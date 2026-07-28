package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync

import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Singleton
class ObserveVaultDirtyStateUseCase @Inject constructor(
    private val secureItemDraftRepository: SecureItemDraftRepository,
) {
    operator fun invoke(): Flow<Boolean> = secureItemDraftRepository.observeDrafts()
        .map { drafts ->
            drafts.any { it.draftSyncStatus == SecureItemDraftSyncStatus.READY_TO_SYNC }
        }
        .distinctUntilChanged()
}
