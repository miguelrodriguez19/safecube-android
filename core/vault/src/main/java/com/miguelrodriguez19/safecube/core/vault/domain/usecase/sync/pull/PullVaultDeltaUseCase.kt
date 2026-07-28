package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.ApplyDeltaCounters
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemPayloadIdentityReader
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PullVaultDeltaUseCase @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
    private val secureItemDraftRepository: SecureItemDraftRepository,
    private val secureItemRemoteRepository: SecureItemRemoteRepository,
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
    private val secureItemPayloadIdentityReader: SecureItemPayloadIdentityReader,
    private val secureItemCryptoService: SecureItemCryptoService,
) {
    suspend operator fun invoke(limit: Int? = null): PullVaultDeltaResult {
        val accountId = vaultKeyMaterialLocalRepository.get().accountIdOrNull()
            ?: return PullVaultDeltaResult.Error(PullVaultDeltaError.AccountIdUnavailable)
        val pageLimit = limit ?: DEFAULT_PAGE_LIMIT
        var cursor = secureItemRepository.getSyncCheckpoint(accountId) ?: INITIAL_CURSOR
        var hasMore = true
        val counters = ApplyDeltaCounters()
        var processedCount = 0
        var checkpointUpdated = false

        while (hasMore) {
            val page = when (
                val result = secureItemRemoteRepository.listVaultItemChanges(
                    after = cursor,
                    limit = pageLimit,
                )
            ) {
                is SecureItemRemoteResult.Success -> result.value
                is SecureItemRemoteResult.Error -> {
                    return PullVaultDeltaResult.Error(PullVaultDeltaError.RemoteListFailed(result.error))
                }
            }
            if (page.nextCursor < cursor) {
                return PullVaultDeltaResult.Error(
                    PullVaultDeltaError.LocalApplyFailed(
                        itemId = ZERO_UUID,
                        operation = "NON_MONOTONIC_CHANGE_CURSOR",
                    ),
                )
            }
            if (page.items.isEmpty()) {
                if (page.hasMore) {
                    return PullVaultDeltaResult.Error(
                        PullVaultDeltaError.LocalApplyFailed(
                            itemId = ZERO_UUID,
                            operation = "EMPTY_CHANGE_PAGE_WITH_MORE_RESULTS",
                        ),
                    )
                }
                break
            }
            val pageSequences = page.items.map(RemoteSecureItem::changeSequence)
            if (
                pageSequences.first() <= cursor ||
                pageSequences.zipWithNext().any { (current, next) -> next <= current } ||
                pageSequences.last() != page.nextCursor
            ) {
                return PullVaultDeltaResult.Error(
                    PullVaultDeltaError.LocalApplyFailed(
                        itemId = ZERO_UUID,
                        operation = "INVALID_CHANGE_PAGE_ORDER",
                    ),
                )
            }

            val preparedPage = when (val preparation = preparePage(page.items)) {
                is PagePreparationResult.Success -> preparation.page
                is PagePreparationResult.Error -> return PullVaultDeltaResult.Error(preparation.error)
            }
            val lastSyncedAt = page.items.maxOf(RemoteSecureItem::updatedAt)

            val applied = secureItemRepository.applyRemotePage(
                accountId = accountId,
                items = preparedPage.officialItems,
                conflictedDrafts = preparedPage.conflictedDrafts,
                draftsToDelete = preparedPage.draftsToDelete,
                lastAppliedChangeSequence = page.nextCursor,
                lastSyncedAt = lastSyncedAt,
            )
            if (!applied) {
                return PullVaultDeltaResult.Error(
                    PullVaultDeltaError.LocalApplyFailed(
                        itemId = preparedPage.officialItems.lastOrNull()?.remoteItemId ?: ZERO_UUID,
                        operation = "APPLY_CHANGE_PAGE",
                    ),
                )
            }

            processedCount += page.items.size
            counters.appliedUpserts += preparedPage.officialItems.count { it.deletedAt == null }
            counters.appliedDeletes += preparedPage.officialItems.count { it.deletedAt != null }
            counters.skippedDirtyOrConflict += preparedPage.conflictedDrafts.size
            cursor = page.nextCursor
            checkpointUpdated = true
            hasMore = page.hasMore
        }

        return PullVaultDeltaResult.Success(
            processedSummaryCount = processedCount,
            appliedUpsertCount = counters.appliedUpserts,
            appliedDeleteCount = counters.appliedDeletes,
            skippedDirtyOrConflictCount = counters.skippedDirtyOrConflict,
            checkpointUpdatedTo = cursor.takeIf { checkpointUpdated },
        )
    }

    private suspend fun preparePage(items: List<RemoteSecureItem>): PagePreparationResult {
        val officialItems = mutableListOf<SecureItem>()
        val conflictedDrafts = mutableListOf<SecureItemSyncDraft>()
        val draftsToDelete = mutableSetOf<UUID>()

        for (remote in items) {
            val itemType = SecureItemType.fromWireName(remote.itemType)
            if (itemType == null) {
                return PagePreparationResult.Error(
                    PullVaultDeltaError.UnsupportedRemoteItemType(
                        itemId = remote.itemId,
                        wireType = remote.itemType,
                    ),
                )
            }
            val payloadLogicalItemId = secureItemPayloadIdentityReader.readLogicalItemId(remote.payload)
            if (payloadLogicalItemId == null) {
                return PagePreparationResult.Error(
                    PullVaultDeltaError.LocalApplyFailed(
                        itemId = remote.itemId,
                        operation = "READ_PAYLOAD_IDENTITY",
                    ),
                )
            }
            val localOfficial = secureItemRepository.findByRemoteItemId(remote.itemId)
                ?: secureItemRepository.getItem(payloadLogicalItemId)
            val localDraft = secureItemDraftRepository.findByRemoteItemId(remote.itemId)
                ?: secureItemDraftRepository.getDraft(payloadLogicalItemId)
            val logicalItemId = localOfficial?.logicalItemId
                ?: localDraft?.logicalItemId
                ?: payloadLogicalItemId
            val remoteOfficial = remote.toLocalSecureItem(
                logicalItemId = logicalItemId,
                itemType = itemType,
                createdAt = localOfficial?.createdAt ?: localDraft?.createdAt ?: remote.updatedAt,
            )
            if (secureItemCryptoService.decrypt(remoteOfficial) !is SecureItemDecryptionResult.Success) {
                return PagePreparationResult.Error(
                    PullVaultDeltaError.LocalApplyFailed(
                        itemId = remote.itemId,
                        operation = "DECRYPT_REMOTE_SNAPSHOT",
                    ),
                )
            }

            officialItems += remoteOfficial
            if (localDraft == null) {
                continue
            }
            when {
                remoteConfirmsDraft(localDraft, remoteOfficial) -> draftsToDelete += localDraft.logicalItemId
                localDraft.baseItemRevision == remote.itemRevision -> Unit
                else -> conflictedDrafts += localDraft.copy(
                    remoteItemId = remote.itemId,
                    draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
                    lastSyncError = conflictMessage(localDraft, remoteOfficial),
                )
            }
        }
        return PagePreparationResult.Success(
            PreparedPage(
                officialItems = officialItems,
                conflictedDrafts = conflictedDrafts,
                draftsToDelete = draftsToDelete,
            ),
        )
    }

    private fun remoteConfirmsDraft(
        draft: SecureItemSyncDraft,
        remote: SecureItem,
    ): Boolean = when (draft.draftType) {
        SecureItemDraftType.CREATE,
        SecureItemDraftType.UPDATE,
            -> remote.deletedAt == null &&
                remote.payloadVersion == draft.payloadVersion &&
                remote.payload.contentEquals(draft.payload)

        SecureItemDraftType.DELETE -> remote.deletedAt != null
    }

    private fun conflictMessage(
        draft: SecureItemSyncDraft,
        remote: SecureItem,
    ): String = when {
        draft.draftType == SecureItemDraftType.UPDATE && remote.deletedAt != null ->
            "Item was deleted remotely. Save the local proposal as a new item or discard it."

        draft.draftType == SecureItemDraftType.DELETE ->
            "Item changed remotely before the local deletion could be applied."

        else -> "Item changed remotely while a local proposal existed."
    }

    private data class PreparedPage(
        val officialItems: List<SecureItem>,
        val conflictedDrafts: List<SecureItemSyncDraft>,
        val draftsToDelete: Set<UUID>,
    )

    private sealed interface PagePreparationResult {
        data class Success(val page: PreparedPage) : PagePreparationResult
        data class Error(val error: PullVaultDeltaError) : PagePreparationResult
    }

    private companion object {
        private const val DEFAULT_PAGE_LIMIT = 100
        private const val INITIAL_CURSOR = 0L
        private val ZERO_UUID: UUID = UUID(0, 0)
    }
}
