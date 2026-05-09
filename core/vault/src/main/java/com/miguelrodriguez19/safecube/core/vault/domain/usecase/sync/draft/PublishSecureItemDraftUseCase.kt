package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PublishSecureItemDraftError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PublishSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.toPublishedUpdateRequest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PublishSecureItemDraftUseCase @Inject constructor(
    private val secureItemDraftRepository: SecureItemDraftRepository,
    private val secureItemRemoteRepository: SecureItemRemoteRepository,
    private val secureItemDraftPolicyCoordinator: SecureItemDraftPolicyCoordinator,
) {
    suspend operator fun invoke(logicalItemId: UUID): PublishSecureItemDraftResult {
        val draft = secureItemDraftRepository.getDraft(logicalItemId)
            ?: return PublishSecureItemDraftResult.Error(
                PublishSecureItemDraftError.DraftNotFound(logicalItemId),
            )
        val remoteItemId = draft.remoteItemId ?: return PublishSecureItemDraftResult.Error(
            PublishSecureItemDraftError.MissingRemoteItemId(
                logicalItemId = logicalItemId,
                draftType = draft.draftType,
            ),
        )

        return when (draft.draftType) {
            SecureItemDraftType.UPDATE -> publishUpdateDraft(
                draft = draft, remoteItemId = remoteItemId
            )

            SecureItemDraftType.DELETE -> publishDeleteDraft(
                draft = draft, remoteItemId = remoteItemId
            )
        }
    }

    private suspend fun publishUpdateDraft(
        draft: SecureItemSyncDraft,
        remoteItemId: UUID,
    ): PublishSecureItemDraftResult =
        when (val remoteResult = secureItemRemoteRepository.updateVaultItem(
            remoteItemId = remoteItemId,
            request = draft.toPublishedUpdateRequest(),
        )) {
            is SecureItemRemoteResult.Success -> {
                if (secureItemDraftPolicyCoordinator.finalizePublishedUpdate(
                        draft = draft,
                        remotePayloadVersion = remoteResult.value.payloadVersion,
                        remoteUpdatedAt = remoteResult.value.updatedAt,
                    )
                ) {
                    PublishSecureItemDraftResult.Success(
                        logicalItemId = draft.logicalItemId,
                        draftType = draft.draftType,
                    )
                } else {
                    PublishSecureItemDraftResult.Error(
                        PublishSecureItemDraftError.LocalStateUpdateFailed(
                            logicalItemId = draft.logicalItemId,
                            operation = "PUBLISH_DRAFT_UPDATE",
                        ),
                    )
                }
            }

            is SecureItemRemoteResult.Error -> when (remoteResult.error) {
                SecureItemRemoteError.ItemNotFound -> {
                    if (secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                            logicalItemId = draft.logicalItemId,
                            remoteItemId = remoteItemId,
                            deletedAt = draft.updatedAt,
                            lastSyncedAt = draft.updatedAt,
                        )
                    ) {
                        PublishSecureItemDraftResult.Success(
                            logicalItemId = draft.logicalItemId,
                            draftType = draft.draftType,
                        )
                    } else {
                        PublishSecureItemDraftResult.Error(
                            PublishSecureItemDraftError.LocalStateUpdateFailed(
                                logicalItemId = draft.logicalItemId,
                                operation = "PUBLISH_DRAFT_UPDATE_NOT_FOUND_RESOLUTION",
                            ),
                        )
                    }
                }

                else -> PublishSecureItemDraftResult.Error(
                    PublishSecureItemDraftError.RemoteOperationFailed(
                        logicalItemId = draft.logicalItemId,
                        error = remoteResult.error,
                    ),
                )
            }
        }

    private suspend fun publishDeleteDraft(
        draft: SecureItemSyncDraft,
        remoteItemId: UUID,
    ): PublishSecureItemDraftResult =
        when (val remoteResult = secureItemRemoteRepository.deleteVaultItem(remoteItemId)) {
            is SecureItemRemoteResult.Success -> {
                finalizeDeleteDraft(
                    draft = draft,
                    deletedAt = remoteResult.value.deletedAt,
                    operation = "PUBLISH_DRAFT_DELETE",
                )
            }

            is SecureItemRemoteResult.Error -> when (remoteResult.error) {
                SecureItemRemoteError.ItemNotFound -> finalizeDeleteDraft(
                    draft = draft,
                    deletedAt = draft.updatedAt,
                    operation = "PUBLISH_DRAFT_DELETE_NOT_FOUND_RESOLUTION",
                )

                else -> PublishSecureItemDraftResult.Error(
                    PublishSecureItemDraftError.RemoteOperationFailed(
                        logicalItemId = draft.logicalItemId,
                        error = remoteResult.error,
                    ),
                )
            }
        }

    private suspend fun finalizeDeleteDraft(
        draft: SecureItemSyncDraft,
        deletedAt: java.time.Instant,
        operation: String,
    ): PublishSecureItemDraftResult = if (secureItemDraftPolicyCoordinator.finalizePublishedDelete(
            draft = draft,
            deletedAt = deletedAt,
        )
    ) {
        PublishSecureItemDraftResult.Success(
            logicalItemId = draft.logicalItemId,
            draftType = draft.draftType,
        )
    } else {
        PublishSecureItemDraftResult.Error(
            PublishSecureItemDraftError.LocalStateUpdateFailed(
                logicalItemId = draft.logicalItemId,
                operation = operation,
            ),
        )
    }
}
