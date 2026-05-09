package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteListVaultItemsRequestParams
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.ApplyDeltaCounters
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.DeltaApplyResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.DetailsFetchResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.SummaryFetchResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftPolicyCoordinator
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PullVaultDeltaUseCase @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
    private val secureItemRemoteRepository: SecureItemRemoteRepository,
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
    private val secureItemDraftPolicyCoordinator: SecureItemDraftPolicyCoordinator,
) {
    suspend operator fun invoke(limit: Int? = null): PullVaultDeltaResult {
        val accountId = vaultKeyMaterialLocalRepository.get().accountIdOrNull()
            ?: return PullVaultDeltaResult.Error(PullVaultDeltaError.AccountIdUnavailable)

        val summaries =
            when (val summariesResult = fetchSummaries(accountId = accountId, limit = limit)) {
                is SummaryFetchResult.Success -> summariesResult.summaries
                is SummaryFetchResult.Error ->
                    return PullVaultDeltaResult.Error(summariesResult.error)
            }

        val detailsByItemId = when (val detailsResult = fetchRequiredDetails(summaries)) {
            is DetailsFetchResult.Success -> detailsResult.detailsByItemId
            is DetailsFetchResult.Error ->
                return PullVaultDeltaResult.Error(detailsResult.error)
        }

        val counters = when (val applyResult =
            applyRemoteDelta(summaries = summaries, detailsByItemId = detailsByItemId)) {
            is DeltaApplyResult.Success -> applyResult.counters
            is DeltaApplyResult.Error ->
                return PullVaultDeltaResult.Error(applyResult.error)
        }

        val checkpoint = summaries.maxOfOrNull(RemoteSecureItemSummary::updatedAt)
        updateCheckpointIfPresent(accountId = accountId, checkpoint = checkpoint)

        return PullVaultDeltaResult.Success(
            processedSummaryCount = summaries.size,
            appliedUpsertCount = counters.appliedUpserts,
            appliedDeleteCount = counters.appliedDeletes,
            skippedDirtyOrConflictCount = counters.skippedDirtyOrConflict,
            checkpointUpdatedTo = checkpoint,
        )
    }

    private suspend fun fetchSummaries(accountId: UUID, limit: Int?): SummaryFetchResult {
        val lastPulledAt = secureItemRepository.getSyncCheckpoint(accountId)
        return when (val listResult = secureItemRemoteRepository.listVaultItems(
            requestParams = RemoteListVaultItemsRequestParams(
                updatedAfter = lastPulledAt,
                includeDeleted = true,
                limit = limit,
            ),
        )) {
            is SecureItemRemoteResult.Success -> {
                val summaries = listResult.value.deduplicateByItemIdKeepingLatest()
                    .sortedBy(RemoteSecureItemSummary::updatedAt)
                SummaryFetchResult.Success(summaries)
            }

            is SecureItemRemoteResult.Error -> {
                SummaryFetchResult.Error(PullVaultDeltaError.RemoteListFailed(listResult.error))
            }
        }
    }

    private suspend fun fetchRequiredDetails(summaries: List<RemoteSecureItemSummary>): DetailsFetchResult {
        val detailsByItemId = mutableMapOf<UUID, RemoteSecureItem>()

        for (summary in summaries) {
            if (summary.deletedAt != null) continue

            when (val detailResult = secureItemRemoteRepository.getVaultItem(summary.itemId)) {
                is SecureItemRemoteResult.Success -> detailsByItemId[summary.itemId] =
                    detailResult.value

                is SecureItemRemoteResult.Error -> {
                    return DetailsFetchResult.Error(
                        PullVaultDeltaError.RemoteDetailFailed(
                            itemId = summary.itemId,
                            error = detailResult.error,
                        ),
                    )
                }
            }
        }

        return DetailsFetchResult.Success(detailsByItemId)
    }

    private suspend fun applyRemoteDelta(
        summaries: List<RemoteSecureItemSummary>,
        detailsByItemId: Map<UUID, RemoteSecureItem>,
    ): DeltaApplyResult {
        val counters = ApplyDeltaCounters()

        for (summary in summaries) {
            val localItem = secureItemRepository.findByRemoteItemId(summary.itemId)
            if (summary.deletedAt != null) {
                val deleteResult = applyRemoteDeleteSummary(
                    summary = summary,
                    localItem = localItem,
                )
                when (deleteResult) {
                    RemoteDeltaItemResult.Applied -> counters.appliedDeletes++
                    RemoteDeltaItemResult.Skipped -> counters.skippedDirtyOrConflict++
                    RemoteDeltaItemResult.Failed -> {
                        return DeltaApplyResult.Error(
                            PullVaultDeltaError.LocalApplyFailed(
                                itemId = summary.itemId,
                                operation = "DELETE",
                            ),
                        )
                    }
                }
                continue
            }

            val detail = detailsByItemId[summary.itemId] ?: return DeltaApplyResult.Error(
                PullVaultDeltaError.RemoteDetailMissing(summary.itemId)
            )

            val itemType =
                SecureItemType.fromWireName(detail.itemType) ?: return DeltaApplyResult.Error(
                    PullVaultDeltaError.UnsupportedRemoteItemType(
                        itemId = summary.itemId,
                        wireType = detail.itemType,
                    ),
                )

            val applyResult = applyRemoteActiveSummary(
                summary = summary,
                detail = detail,
                itemType = itemType,
                localItem = localItem,
            )
            when (applyResult) {
                RemoteDeltaItemResult.Applied -> counters.appliedUpserts++
                RemoteDeltaItemResult.Skipped -> counters.skippedDirtyOrConflict++
                RemoteDeltaItemResult.Failed -> {
                    return DeltaApplyResult.Error(
                        PullVaultDeltaError.LocalApplyFailed(
                            itemId = summary.itemId,
                            operation = "UPSERT",
                        ),
                    )
                }
            }
        }

        return DeltaApplyResult.Success(counters)
    }

    private suspend fun applyRemoteDeleteSummary(
        summary: RemoteSecureItemSummary,
        localItem: SecureItem?,
    ): RemoteDeltaItemResult {
        val deletedAt = requireNotNull(summary.deletedAt)

        if (localItem == null) {
            secureItemRepository.applyRemoteDelete(
                remoteItemId = summary.itemId,
                deletedAt = deletedAt,
                lastSyncedAt = summary.updatedAt,
            )
            return RemoteDeltaItemResult.Applied
        }

        return when (localItem.syncState) {
            SecureItemSyncState.PENDING_UPDATE,
            SecureItemSyncState.PENDING_DELETE,
                -> appliedOrFailedIf {
                secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                    logicalItemId = localItem.logicalItemId,
                    remoteItemId = summary.itemId,
                    deletedAt = deletedAt,
                    lastSyncedAt = summary.updatedAt,
                )
            }

            SecureItemSyncState.PENDING_CREATE,
            SecureItemSyncState.CONFLICT,
                -> skippedOrFailedIf {
                secureItemRepository.markConflict(
                    logicalItemId = localItem.logicalItemId,
                    lastSyncError = "Pull skipped: local changes cannot be reconciled with remote tombstone automatically.",
                )
            }


            SecureItemSyncState.SYNCED -> appliedOrFailedIf {
                secureItemRepository.applyRemoteDelete(
                    remoteItemId = summary.itemId,
                    deletedAt = deletedAt,
                    lastSyncedAt = summary.updatedAt,
                )
            }
        }
    }

    private suspend fun applyRemoteActiveSummary(
        summary: RemoteSecureItemSummary,
        detail: RemoteSecureItem,
        itemType: SecureItemType,
        localItem: SecureItem?,
    ): RemoteDeltaItemResult {
        val remoteOfficialItem = detail.toLocalSecureItem(
            logicalItemId = localItem?.logicalItemId ?: UUID.randomUUID(),
            itemType = itemType,
            createdAt = localItem?.createdAt ?: detail.updatedAt,
        )

        if (localItem == null) {
            return appliedOrFailedIf {
                secureItemRepository.applyRemoteUpsert(
                    item = remoteOfficialItem,
                    lastSyncedAt = summary.updatedAt,
                )
            }
        }

        return when (localItem.syncState) {
            SecureItemSyncState.PENDING_UPDATE -> appliedOrFailedIf {
                secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                    localItem = localItem,
                    remoteItem = remoteOfficialItem,
                    draftType = SecureItemDraftType.UPDATE,
                    lastSyncedAt = summary.updatedAt,
                )
            }

            SecureItemSyncState.PENDING_DELETE -> appliedOrFailedIf {
                secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                    localItem = localItem,
                    remoteItem = remoteOfficialItem,
                    draftType = SecureItemDraftType.DELETE,
                    lastSyncedAt = summary.updatedAt,
                )
            }

            SecureItemSyncState.PENDING_CREATE,
            SecureItemSyncState.CONFLICT,
                -> skippedOrFailedIf {
                secureItemRepository.markConflict(
                    logicalItemId = localItem.logicalItemId,
                    lastSyncError = "Pull skipped: local changes pending. Push/retry required before applying remote update.",
                )
            }

            SecureItemSyncState.SYNCED -> appliedOrFailedIf {
                secureItemRepository.applyRemoteUpsert(
                    item = remoteOfficialItem,
                    lastSyncedAt = summary.updatedAt,
                )
            }
        }
    }

    private suspend fun appliedOrFailedIf(action: suspend () -> Boolean): RemoteDeltaItemResult =
        if (action()) {
            RemoteDeltaItemResult.Applied
        } else {
            RemoteDeltaItemResult.Failed
        }

    private suspend fun skippedOrFailedIf(action: suspend () -> Boolean): RemoteDeltaItemResult =
        if (action()) {
            RemoteDeltaItemResult.Skipped
        } else {
            RemoteDeltaItemResult.Failed
        }

    private suspend fun updateCheckpointIfPresent(accountId: UUID, checkpoint: Instant?) {
        if (checkpoint != null) {
            secureItemRepository.updateSyncCheckpoint(
                accountId = accountId,
                lastPulledAt = checkpoint
            )
        }
    }
}

private enum class RemoteDeltaItemResult {
    Applied,
    Skipped,
    Failed,
}
