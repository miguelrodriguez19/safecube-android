package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PrepareSecureItemDraftForSyncUseCase @Inject constructor(
    private val secureItemDraftRepository: SecureItemDraftRepository,
    private val secureItemDraftSyncCoordinator: SecureItemDraftSyncCoordinator,
) {
    suspend operator fun invoke(logicalItemId: UUID): PrepareSecureItemDraftForSyncResult {
        val draft = secureItemDraftRepository.getDraft(logicalItemId)
            ?: return PrepareSecureItemDraftForSyncResult.Error(
                PrepareSecureItemDraftForSyncError.DraftNotFound(logicalItemId),
            )
        if (draft.draftType == SecureItemDraftType.CREATE || draft.draftSyncStatus != SecureItemDraftSyncStatus.CONFLICT) {
            return PrepareSecureItemDraftForSyncResult.Error(
                PrepareSecureItemDraftForSyncError.DraftNotPublishable(
                    logicalItemId = logicalItemId,
                    draftType = draft.draftType,
                ),
            )
        }

        return if (secureItemDraftSyncCoordinator.prepareDraftForSync(logicalItemId)) {
            PrepareSecureItemDraftForSyncResult.Success(
                logicalItemId = logicalItemId,
                draftType = draft.draftType,
            )
        } else {
            PrepareSecureItemDraftForSyncResult.Error(
                PrepareSecureItemDraftForSyncError.LocalStateUpdateFailed(
                    logicalItemId = draft.logicalItemId,
                    operation = "PREPARE_DRAFT_FOR_SYNC",
                ),
            )
        }
    }
}
