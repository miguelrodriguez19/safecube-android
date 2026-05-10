package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushProgressCounters
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftPolicyCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull.toLocalSecureItem
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushLocalVaultChangesUseCase @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
    private val secureItemRemoteRepository: SecureItemRemoteRepository,
    private val secureItemDraftPolicyCoordinator: SecureItemDraftPolicyCoordinator,
) {
    suspend operator fun invoke(): PushLocalVaultChangesResult {
        val pendingItems = secureItemRepository.getPendingSyncItemsOrdered()
        return processPendingItems(pendingItems)
    }

    suspend operator fun invoke(logicalItemId: UUID): PushLocalVaultChangesResult {
        val item = secureItemRepository.getItem(logicalItemId)
            ?.takeIf { it.syncState.isPendingPushState() }
            ?: return PushLocalVaultChangesResult.Success(
                processedCount = 0,
                syncedCount = 0,
                conflictCount = 0,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            )

        return processPendingItems(listOf(item))
    }

    private suspend fun processPendingItems(items: List<SecureItem>): PushLocalVaultChangesResult {
        val counters = PushProgressCounters()

        for (item in items) {
            val result = processPendingItem(item)
            counters.processedCount++
            when (result) {
                PushItemResult.Synced -> counters.syncedCount++
                PushItemResult.Conflict -> counters.conflictCount++
                PushItemResult.KeptPending -> counters.keptPendingCount++
                PushItemResult.LocallyResolvedDelete -> {
                    counters.locallyResolvedDeleteCount++
                    counters.syncedCount++
                }

                is PushItemResult.Fatal -> {
                    return PushLocalVaultChangesResult.Error(result.error)
                }
            }
        }

        return PushLocalVaultChangesResult.Success(
            processedCount = counters.processedCount,
            syncedCount = counters.syncedCount,
            conflictCount = counters.conflictCount,
            keptPendingCount = counters.keptPendingCount,
            locallyResolvedDeleteCount = counters.locallyResolvedDeleteCount,
        )
    }

    private suspend fun processPendingItem(item: SecureItem): PushItemResult =
        when (item.syncState) {
            SecureItemSyncState.PENDING_CREATE -> processPendingCreate(item)
            SecureItemSyncState.PENDING_UPDATE -> processPendingUpdate(item)
            SecureItemSyncState.PENDING_DELETE -> processPendingDelete(item)
            SecureItemSyncState.SYNCED,
            SecureItemSyncState.CONFLICT,
                -> PushItemResult.KeptPending
        }

    private suspend fun processPendingCreate(item: SecureItem): PushItemResult {
        val remoteItemId = item.remoteItemId
        if (remoteItemId != null) {
            return markConflictOrFatal(
                item = item,
                message = "Invalid push state: PENDING_CREATE item already has remoteItemId.",
            )
        }

        return when (val remoteResult =
            secureItemRemoteRepository.createVaultItem(item.toRemoteCreateRequest())) {
            is SecureItemRemoteResult.Success -> {
                val createdAt = remoteResult.value.createdAt
                markSyncedOrFatal(
                    item = item,
                    remoteItemId = remoteResult.value.itemId,
                    payloadVersion = item.payloadVersion,
                    updatedAt = createdAt,
                    deletedAt = null,
                    lastSyncedAt = createdAt,
                    operation = "CREATE",
                )
            }

            is SecureItemRemoteResult.Error -> when (remoteResult.error) {
                SecureItemRemoteError.Conflict -> markConflictOrFatal(
                    item = item,
                    message = "409 Conflict on create. Resolve by reopening the item and saving again.",
                )

                SecureItemRemoteError.ItemNotFound,
                SecureItemRemoteError.Unauthorized,
                is SecureItemRemoteError.HttpError,
                is SecureItemRemoteError.NetworkError
                    -> PushItemResult.KeptPending
            }
        }
    }

    private suspend fun processPendingUpdate(item: SecureItem): PushItemResult {
        val remoteItemId = item.remoteItemId
            ?: return markConflictOrFatal(
                item = item,
                message = "Invalid push state: PENDING_UPDATE item has no remoteItemId.",
            )

        return when (
            val remoteResult = secureItemRemoteRepository.updateVaultItem(
                remoteItemId = remoteItemId,
                request = item.toRemoteUpdateRequest(),
            )
        ) {
            is SecureItemRemoteResult.Success -> {
                markSyncedOrFatal(
                    item = item,
                    remoteItemId = remoteItemId,
                    payloadVersion = remoteResult.value.payloadVersion,
                    updatedAt = remoteResult.value.updatedAt,
                    deletedAt = null,
                    lastSyncedAt = remoteResult.value.updatedAt,
                    operation = "UPDATE",
                )
            }

            is SecureItemRemoteResult.Error -> {
                when (remoteResult.error) {
                    SecureItemRemoteError.Conflict -> resolveUpdateConflictAsDraftOrConflict(item)
                    SecureItemRemoteError.ItemNotFound -> resolveMissingRemoteItemOnUpdate(item)
                    SecureItemRemoteError.Unauthorized,
                    is SecureItemRemoteError.HttpError,
                    is SecureItemRemoteError.NetworkError
                        -> PushItemResult.KeptPending
                }
            }
        }
    }

    private suspend fun processPendingDelete(item: SecureItem): PushItemResult {
        val remoteItemId = item.remoteItemId
        if (remoteItemId == null) {
            val deletedAt = item.localDeleteTimestamp()
            return when (
                secureItemRepository.markSynced(
                    logicalItemId = item.logicalItemId,
                    remoteItemId = null,
                    payloadVersion = item.payloadVersion,
                    updatedAt = deletedAt,
                    deletedAt = deletedAt,
                    lastSyncedAt = deletedAt,
                )
            ) {
                true -> PushItemResult.LocallyResolvedDelete
                false -> PushItemResult.Fatal(
                    PushLocalVaultChangesError.LocalStateUpdateFailed(
                        logicalItemId = item.logicalItemId,
                        operation = "LOCAL_DELETE_RESOLUTION",
                    ),
                )
            }
        }

        return when (val remoteResult = secureItemRemoteRepository.deleteVaultItem(remoteItemId)) {
            is SecureItemRemoteResult.Success -> {
                val deletedAt = remoteResult.value.deletedAt
                markSyncedOrFatal(
                    item = item,
                    remoteItemId = remoteItemId,
                    payloadVersion = item.payloadVersion,
                    updatedAt = deletedAt,
                    deletedAt = deletedAt,
                    lastSyncedAt = deletedAt,
                    operation = "DELETE",
                )
            }

            is SecureItemRemoteResult.Error -> when (remoteResult.error) {
                SecureItemRemoteError.ItemNotFound -> {
                    resolveMissingRemoteItemOnDelete(item)
                }

                SecureItemRemoteError.Conflict -> resolveDeleteConflictAsDraftOrConflict(item)

                SecureItemRemoteError.Unauthorized,
                is SecureItemRemoteError.HttpError,
                is SecureItemRemoteError.NetworkError,
                    -> PushItemResult.KeptPending
            }
        }
    }

    private suspend fun resolveUpdateConflictAsDraftOrConflict(item: SecureItem): PushItemResult {
        val remoteOfficialItem = fetchRemoteOfficialItemForConflict(item)
            ?: return markConflictOrFatal(
                item = item,
                message = "409 Conflict on update. Remote version changed.",
            )

        return if (
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = item,
                remoteItem = remoteOfficialItem,
                draftType = SecureItemDraftType.UPDATE,
                lastSyncedAt = remoteOfficialItem.updatedAt,
            )
        ) {
            PushItemResult.Conflict
        } else {
            PushItemResult.Fatal(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    logicalItemId = item.logicalItemId,
                    operation = "UPDATE_CONFLICT_DRAFT_RESOLUTION",
                ),
            )
        }
    }

    private suspend fun resolveDeleteConflictAsDraftOrConflict(item: SecureItem): PushItemResult {
        val remoteOfficialItem = fetchRemoteOfficialItemForConflict(item)
            ?: return markConflictOrFatal(
                item = item,
                message = "409 Conflict on delete. Remote state changed.",
            )

        return if (
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = item,
                remoteItem = remoteOfficialItem,
                draftType = SecureItemDraftType.DELETE,
                lastSyncedAt = remoteOfficialItem.updatedAt,
            )
        ) {
            PushItemResult.Conflict
        } else {
            PushItemResult.Fatal(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    logicalItemId = item.logicalItemId,
                    operation = "DELETE_CONFLICT_DRAFT_RESOLUTION",
                ),
            )
        }
    }

    private suspend fun resolveMissingRemoteItemOnUpdate(item: SecureItem): PushItemResult {
        val remoteItemId = item.remoteItemId ?: return markConflictOrFatal(
            item = item,
            message = "404 Not Found on update. Remote item no longer exists.",
        )
        val deletedAt = item.localDeleteTimestamp()

        return if (
            secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                logicalItemId = item.logicalItemId,
                remoteItemId = remoteItemId,
                deletedAt = deletedAt,
                lastSyncedAt = deletedAt,
            )
        ) {
            PushItemResult.Conflict
        } else {
            PushItemResult.Fatal(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    logicalItemId = item.logicalItemId,
                    operation = "UPDATE_NOT_FOUND_DELETE_RESOLUTION",
                ),
            )
        }
    }

    private suspend fun resolveMissingRemoteItemOnDelete(item: SecureItem): PushItemResult {
        val remoteItemId = item.remoteItemId
            ?: return PushItemResult.KeptPending
        val deletedAt = item.localDeleteTimestamp()

        return if (
            secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                logicalItemId = item.logicalItemId,
                remoteItemId = remoteItemId,
                deletedAt = deletedAt,
                lastSyncedAt = deletedAt,
            )
        ) {
            PushItemResult.Synced
        } else {
            PushItemResult.Fatal(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    logicalItemId = item.logicalItemId,
                    operation = "DELETE_NOT_FOUND_RESOLUTION",
                ),
            )
        }
    }

    private suspend fun fetchRemoteOfficialItemForConflict(item: SecureItem): SecureItem? {
        val remoteItemId = item.remoteItemId ?: return null

        return when (val remoteResult = secureItemRemoteRepository.getVaultItem(remoteItemId)) {
            is SecureItemRemoteResult.Success -> {
                val itemType =
                    SecureItemType.fromWireName(remoteResult.value.itemType) ?: return null

                remoteResult.value.toLocalSecureItem(
                    logicalItemId = item.logicalItemId,
                    itemType = itemType,
                    createdAt = item.createdAt,
                )
            }

            is SecureItemRemoteResult.Error -> null
        }
    }

    private suspend fun markSyncedOrFatal(
        item: SecureItem,
        remoteItemId: UUID?,
        payloadVersion: Long,
        updatedAt: java.time.Instant,
        deletedAt: java.time.Instant?,
        lastSyncedAt: java.time.Instant,
        operation: String,
    ): PushItemResult = when (
        secureItemRepository.markSynced(
            logicalItemId = item.logicalItemId,
            remoteItemId = remoteItemId,
            payloadVersion = payloadVersion,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
            lastSyncedAt = lastSyncedAt,
        )
    ) {
        true -> PushItemResult.Synced
        false -> PushItemResult.Fatal(
            PushLocalVaultChangesError.LocalStateUpdateFailed(
                logicalItemId = item.logicalItemId,
                operation = operation,
            ),
        )
    }

    private suspend fun markConflictOrFatal(
        item: SecureItem,
        message: String,
    ): PushItemResult = when (
        secureItemRepository.markConflict(
            logicalItemId = item.logicalItemId,
            lastSyncError = message,
        )
    ) {
        true -> PushItemResult.Conflict
        false -> PushItemResult.Fatal(
            PushLocalVaultChangesError.LocalStateUpdateFailed(
                logicalItemId = item.logicalItemId,
                operation = "MARK_CONFLICT",
            ),
        )
    }
}
