package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync

import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncVaultNowUseCase @Inject constructor(
    private val vaultSyncUseCase: VaultSyncUseCase,
) {
    suspend operator fun invoke(): VaultSyncResult = vaultSyncUseCase()
}
