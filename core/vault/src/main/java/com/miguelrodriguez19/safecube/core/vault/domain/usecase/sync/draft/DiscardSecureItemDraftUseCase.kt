package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiscardSecureItemDraftUseCase @Inject constructor(
    private val secureItemDraftRepository: SecureItemDraftRepository,
    private val secureItemDraftSyncCoordinator: SecureItemDraftSyncCoordinator,
) {
    suspend operator fun invoke(logicalItemId: UUID): DiscardSecureItemDraftResult {
        val draft = secureItemDraftRepository.getDraft(logicalItemId)
            ?: return DiscardSecureItemDraftResult.Error(
                DiscardSecureItemDraftError.DraftNotFound(logicalItemId),
            )

        return if (secureItemDraftSyncCoordinator.discardDraft(draft.logicalItemId)) {
            DiscardSecureItemDraftResult.Success(logicalItemId)
        } else {
            DiscardSecureItemDraftResult.Error(
                DiscardSecureItemDraftError.LocalStateUpdateFailed(
                    logicalItemId = logicalItemId,
                    operation = "DISCARD_DRAFT",
                ),
            )
        }
    }
}
