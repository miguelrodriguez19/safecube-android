package com.miguelrodriguez19.safecube.core.vault.domain.session

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import kotlinx.coroutines.flow.StateFlow

interface VaultSessionManager {
    val vaultState: StateFlow<VaultState>

    suspend fun refreshVaultState()

    fun unlockWithPassphrase(passphrase: String): VaultUnlockError?

    fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockError?

    fun lock()

    fun onLogout()
}
