package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushProgressCounters
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushLocalVaultChangesUseCase @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
    private val secureItemRemoteRepository: SecureItemRemoteRepository,
) {
    suspend operator fun invoke(): PushLocalVaultChangesResult {
        val pendingItems = secureItemRepository.getPendingSyncItemsOrdered()
        val counters = PushProgressCounters()

        for (item in pendingItems) {
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

    private suspend fun processPendingItem(item: SecureItem): PushItemResult = when (item.syncState) {
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

        return when (val remoteResult = secureItemRemoteRepository.createVaultItem(item.toRemoteCreateRequest())) {
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
                is SecureItemRemoteError.NetworkError,
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

            is SecureItemRemoteResult.Error -> when (remoteResult.error) {
                SecureItemRemoteError.Conflict,
                SecureItemRemoteError.ItemNotFound,
                -> markConflictOrFatal(
                    item = item,
                    message = when (remoteResult.error) {
                        SecureItemRemoteError.Conflict ->
                            "409 Conflict on update. Remote version changed."
                        SecureItemRemoteError.ItemNotFound ->
                            "404 Not Found on update. Remote item no longer exists."
                    },
                )

                SecureItemRemoteError.Unauthorized,
                is SecureItemRemoteError.HttpError,
                is SecureItemRemoteError.NetworkError,
                -> PushItemResult.KeptPending
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
                    val deletedAt = item.localDeleteTimestamp()
                    markSyncedOrFatal(
                        item = item,
                        remoteItemId = remoteItemId,
                        payloadVersion = item.payloadVersion,
                        updatedAt = deletedAt,
                        deletedAt = deletedAt,
                        lastSyncedAt = deletedAt,
                        operation = "DELETE_NOT_FOUND_RESOLUTION",
                    )
                }

                SecureItemRemoteError.Conflict -> markConflictOrFatal(
                    item = item,
                    message = "409 Conflict on delete. Remote state changed.",
                )

                SecureItemRemoteError.Unauthorized,
                is SecureItemRemoteError.HttpError,
                is SecureItemRemoteError.NetworkError,
                -> PushItemResult.KeptPending
            }
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
