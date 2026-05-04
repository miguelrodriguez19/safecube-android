package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.StateFlow

@Singleton
class ObserveVaultSyncingUseCase @Inject constructor(
    private val vaultSyncExecutionLock: VaultSyncExecutionLock,
) {
    operator fun invoke(): StateFlow<Boolean> = vaultSyncExecutionLock.isSyncing
}
