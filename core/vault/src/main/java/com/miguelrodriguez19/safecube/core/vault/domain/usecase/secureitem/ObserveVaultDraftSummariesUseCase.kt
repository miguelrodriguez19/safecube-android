package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemDraftSummary
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class ObserveVaultDraftSummariesUseCase @Inject constructor(
    private val secureItemDraftRepository: SecureItemDraftRepository,
) {
    operator fun invoke(): Flow<List<VaultItemDraftSummary>> {
        return secureItemDraftRepository.observeDrafts().map { drafts ->
            drafts.map { draft ->
                VaultItemDraftSummary(
                    logicalItemId = draft.logicalItemId,
                    draftType = draft.draftType,
                    lastPublishError = draft.lastPublishError,
                )
            }
        }
    }
}
