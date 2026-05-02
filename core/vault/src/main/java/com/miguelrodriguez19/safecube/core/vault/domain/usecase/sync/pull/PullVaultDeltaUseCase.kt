package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteListVaultItemsRequestParams
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
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
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PullVaultDeltaUseCase @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
    private val secureItemRemoteRepository: SecureItemRemoteRepository,
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
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
            if (localItem != null && localItem.syncState.blocksRemotePullOverwrite()) {
                secureItemRepository.markConflict(
                    logicalItemId = localItem.logicalItemId,
                    lastSyncError = "Remote pull skipped: local item has pending changes.",
                )
                counters.skippedDirtyOrConflict++
                continue
            }

            if (summary.deletedAt != null) {
                val deleted = secureItemRepository.applyRemoteDelete(
                    remoteItemId = summary.itemId,
                    deletedAt = summary.deletedAt,
                    lastSyncedAt = summary.updatedAt,
                )
                if (deleted) {
                    counters.appliedDeletes++
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

            val upserted = secureItemRepository.applyRemoteUpsert(
                item = detail.toLocalSecureItem(
                    logicalItemId = localItem?.logicalItemId ?: UUID.randomUUID(),
                    itemType = itemType,
                    createdAt = localItem?.createdAt ?: detail.updatedAt,
                ),
                lastSyncedAt = summary.updatedAt,
            )
            if (!upserted) {
                return DeltaApplyResult.Error(
                    PullVaultDeltaError.LocalApplyFailed(
                        itemId = summary.itemId,
                        operation = "UPSERT",
                    ),
                )
            }

            counters.appliedUpserts++
        }

        return DeltaApplyResult.Success(counters)
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
