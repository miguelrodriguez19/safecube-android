package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushProgressCounters
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftSyncCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull.toLocalSecureItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PushLocalVaultChangesUseCase @Inject constructor(
    private val secureItemDraftRepository: SecureItemDraftRepository,
    private val secureItemRemoteRepository: SecureItemRemoteRepository,
    private val secureItemDraftSyncCoordinator: SecureItemDraftSyncCoordinator,
) {
    suspend operator fun invoke(): PushLocalVaultChangesResult {
        val pendingDrafts = secureItemDraftRepository.getSyncableDraftsOrdered()
        val counters = PushProgressCounters()

        for (draft in pendingDrafts) {
            val result = processDraft(draft)
            counters.processedCount++
            when (result) {
                PushItemResult.Synced -> counters.syncedCount++
                PushItemResult.Conflict -> counters.conflictCount++
                PushItemResult.KeptPending -> counters.keptPendingCount++
                PushItemResult.LocallyResolvedDelete -> counters.syncedCount++
                is PushItemResult.Fatal -> return PushLocalVaultChangesResult.Error(result.error)
            }
        }

        return PushLocalVaultChangesResult.Success(
            processedCount = counters.processedCount,
            syncedCount = counters.syncedCount,
            conflictCount = counters.conflictCount,
            keptPendingCount = counters.keptPendingCount,
            locallyResolvedDeleteCount = 0,
        )
    }

    private suspend fun processDraft(draft: SecureItemSyncDraft): PushItemResult = when (draft.draftType) {
        SecureItemDraftType.CREATE -> processCreateDraft(draft)
        SecureItemDraftType.UPDATE -> processUpdateDraft(draft)
        SecureItemDraftType.DELETE -> processDeleteDraft(draft)
    }

    private suspend fun processCreateDraft(draft: SecureItemSyncDraft): PushItemResult = when (
        val remoteResult = secureItemRemoteRepository.createVaultItem(draft.toRemoteCreateRequest())
    ) {
        is SecureItemRemoteResult.Success -> {
            if (secureItemDraftSyncCoordinator.officializeCreatedDraft(
                    draft = draft,
                    result = remoteResult.value,
                )
            ) {
                PushItemResult.Synced
            } else {
                integrityFailure(draft, "CREATE_RESPONSE")
            }
        }

        is SecureItemRemoteResult.Error -> when (remoteResult.error) {
            SecureItemRemoteError.IdempotencyConflict -> integrityFailure(draft, "CREATE_IDEMPOTENCY")
            is SecureItemRemoteError.ValidationFailed -> integrityFailure(draft, "CREATE_VALIDATION")
            SecureItemRemoteError.PreconditionFailed,
            SecureItemRemoteError.PreconditionRequired,
                -> integrityFailure(draft, "CREATE_PRECONDITION")

            SecureItemRemoteError.ItemNotFound,
            SecureItemRemoteError.Unauthorized,
            is SecureItemRemoteError.HttpError,
            is SecureItemRemoteError.NetworkError,
                -> PushItemResult.KeptPending
        }
    }

    private suspend fun processUpdateDraft(draft: SecureItemSyncDraft): PushItemResult {
        val remoteItemId = draft.remoteItemId ?: return fatal(
            draft = draft,
            operation = "UPDATE_MISSING_REMOTE_ID",
        )

        return when (
            val remoteResult = secureItemRemoteRepository.updateVaultItem(
                remoteItemId = remoteItemId,
                request = draft.toRemoteUpdateRequest(),
            )
        ) {
            is SecureItemRemoteResult.Success -> {
                if (secureItemDraftSyncCoordinator.officializeUpdatedDraft(
                        draft = draft,
                        result = remoteResult.value,
                    )
                ) {
                    PushItemResult.Synced
                } else {
                    integrityFailure(draft, "UPDATE_RESPONSE")
                }
            }

            is SecureItemRemoteResult.Error -> when (remoteResult.error) {
                SecureItemRemoteError.PreconditionFailed -> resolveUpdateConflict(draft)
                SecureItemRemoteError.ItemNotFound -> resolveUpdateRemoteDelete(draft)
                SecureItemRemoteError.IdempotencyConflict,
                SecureItemRemoteError.PreconditionRequired,
                    -> integrityFailure(draft, "UPDATE_PROTOCOL")
                is SecureItemRemoteError.ValidationFailed ->
                    integrityFailure(draft, "UPDATE_VALIDATION")
                SecureItemRemoteError.Unauthorized,
                is SecureItemRemoteError.HttpError,
                is SecureItemRemoteError.NetworkError,
                    -> PushItemResult.KeptPending
            }
        }
    }

    private suspend fun processDeleteDraft(draft: SecureItemSyncDraft): PushItemResult {
        val remoteItemId = draft.remoteItemId ?: return fatal(
            draft = draft,
            operation = "DELETE_MISSING_REMOTE_ID",
        )

        return when (
            val remoteResult = secureItemRemoteRepository.deleteVaultItem(
                remoteItemId = remoteItemId,
                request = draft.toRemoteDeleteRequest(),
            )
        ) {
            is SecureItemRemoteResult.Success -> {
                if (secureItemDraftSyncCoordinator.officializeDeletedDraft(
                        draft = draft,
                        result = remoteResult.value,
                    )
                ) {
                    PushItemResult.Synced
                } else {
                    integrityFailure(draft, "DELETE_RESPONSE")
                }
            }

            is SecureItemRemoteResult.Error -> when (remoteResult.error) {
                SecureItemRemoteError.ItemNotFound -> {
                    if (secureItemDraftSyncCoordinator.resolveAlreadyDeletedDraft(draft)) {
                        PushItemResult.LocallyResolvedDelete
                    } else {
                        PushItemResult.KeptPending
                    }
                }

                SecureItemRemoteError.PreconditionFailed -> resolveDeleteConflict(draft)

                SecureItemRemoteError.IdempotencyConflict,
                SecureItemRemoteError.PreconditionRequired,
                    -> integrityFailure(draft, "DELETE_PROTOCOL")
                is SecureItemRemoteError.ValidationFailed ->
                    integrityFailure(draft, "DELETE_VALIDATION")

                SecureItemRemoteError.Unauthorized,
                is SecureItemRemoteError.HttpError,
                is SecureItemRemoteError.NetworkError,
                    -> PushItemResult.KeptPending
            }
        }
    }

    private suspend fun resolveUpdateConflict(draft: SecureItemSyncDraft): PushItemResult {
        val remoteOfficialItem = fetchRemoteOfficialItem(draft) ?: return markConflictWithoutRemote(
            draft = draft,
            operation = "UPDATE_CONFLICT",
            message = "Update conflicted with backend state.",
        )

        return if (secureItemDraftSyncCoordinator.replaceOfficialWithRemoteAndConflictedDraft(
                draft = draft,
                remoteItem = remoteOfficialItem,
                lastSyncedAt = remoteOfficialItem.updatedAt,
                lastSyncError = "Update conflicted with backend state.",
            )
        ) {
            PushItemResult.Conflict
        } else {
            fatal(draft, "UPDATE_CONFLICT_RESOLUTION")
        }
    }

    private suspend fun resolveDeleteConflict(draft: SecureItemSyncDraft): PushItemResult {
        val remoteOfficialItem = fetchRemoteOfficialItem(draft) ?: return markConflictWithoutRemote(
            draft = draft,
            operation = "DELETE_CONFLICT",
            message = "Delete conflicted with backend state.",
        )

        return if (secureItemDraftSyncCoordinator.replaceOfficialWithRemoteAndConflictedDraft(
                draft = draft,
                remoteItem = remoteOfficialItem,
                lastSyncedAt = remoteOfficialItem.updatedAt,
                lastSyncError = "Delete conflicted with backend state.",
            )
        ) {
            PushItemResult.Conflict
        } else {
            fatal(draft, "DELETE_CONFLICT_RESOLUTION")
        }
    }

    private suspend fun resolveUpdateRemoteDelete(draft: SecureItemSyncDraft): PushItemResult {
        if (draft.remoteItemId == null) return fatal(draft, "UPDATE_REMOTE_DELETE")
        return if (
            secureItemDraftSyncCoordinator.markDraftConflict(
                logicalItemId = draft.logicalItemId,
                lastSyncError = "Item was deleted remotely while local draft existed.",
            )
        ) {
            PushItemResult.Conflict
        } else {
            fatal(draft, "UPDATE_REMOTE_DELETE_RESOLUTION")
        }
    }

    private suspend fun fetchRemoteOfficialItem(draft: SecureItemSyncDraft) =
        draft.remoteItemId?.let { remoteItemId ->
            when (val remoteResult = secureItemRemoteRepository.getVaultItem(remoteItemId)) {
                is SecureItemRemoteResult.Success -> remoteResult.value.toLocalSecureItem(
                    logicalItemId = draft.logicalItemId,
                    itemType = draft.itemType,
                    createdAt = draft.createdAt,
                )

                is SecureItemRemoteResult.Error -> null
            }
        }

    private suspend fun markConflictWithoutRemote(
        draft: SecureItemSyncDraft,
        operation: String,
        message: String,
    ): PushItemResult = if (secureItemDraftSyncCoordinator.markDraftConflict(
            logicalItemId = draft.logicalItemId,
            lastSyncError = message,
        )
    ) {
        PushItemResult.Conflict
    } else {
        fatal(draft, operation)
    }

    private fun fatal(
        draft: SecureItemSyncDraft,
        operation: String,
    ): PushItemResult.Fatal = PushItemResult.Fatal(
        PushLocalVaultChangesError.LocalStateUpdateFailed(
            logicalItemId = draft.logicalItemId,
            operation = operation,
        ),
    )

    private fun integrityFailure(
        draft: SecureItemSyncDraft,
        operation: String,
    ): PushItemResult.Fatal = PushItemResult.Fatal(
        PushLocalVaultChangesError.ProtocolIntegrityFailed(
            logicalItemId = draft.logicalItemId,
            operation = operation,
        ),
    )
}
