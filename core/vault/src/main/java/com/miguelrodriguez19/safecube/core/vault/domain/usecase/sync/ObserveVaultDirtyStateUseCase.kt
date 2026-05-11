package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync

import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged

@Singleton
class ObserveVaultDirtyStateUseCase @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
    private val secureItemDraftRepository: SecureItemDraftRepository,
) {
    operator fun invoke(): Flow<Boolean> = combine(
        secureItemRepository.observeActiveItems(),
        secureItemDraftRepository.observeDrafts(),
    ) { items, drafts ->
        items.any { it.syncState.isPendingPushState() } || drafts.isNotEmpty()
    }.distinctUntilChanged()
}
