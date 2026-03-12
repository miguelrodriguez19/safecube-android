package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.VaultUnlocker
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class VaultSessionManagerImpl @Inject constructor(
    private val vaultUnlocker: VaultUnlocker,
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
) : VaultSessionManager {
    private val state = MutableStateFlow(initialVaultState())
    private var inMemoryKek: ByteArray? = null

    override val vaultState: StateFlow<VaultState> = state.asStateFlow()

    override fun unlockWithPassphrase(passphrase: String): VaultUnlockError? {
        val result = vaultUnlocker.unlockWithPassphrase(passphrase)
        return handleUnlockResult(result)
    }

    override fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockError? {
        val result = vaultUnlocker.unlockWithRecoveryKey(recoveryKey)
        return handleUnlockResult(result)
    }

    override fun lock() {
        clearInMemoryKek()
        state.value = VaultState.Locked
    }

    override fun onLogout() {
        lock()
    }

    private fun handleUnlockResult(result: VaultUnlockResult): VaultUnlockError? = when (result) {
        is VaultUnlockResult.Unlocked -> {
            replaceInMemoryKek(result.keyring.kek)
            state.value = VaultState.Unlocked
            null
        }

        is VaultUnlockResult.Error -> {
            state.value = if (result.reason == VaultUnlockError.KeyMaterialUnavailable) {
                VaultState.NotInitialized
            } else {
                VaultState.Locked
            }
            result.reason
        }
    }

    private fun replaceInMemoryKek(newKek: ByteArray) {
        clearInMemoryKek()
        inMemoryKek = newKek.copyOf()
    }

    private fun clearInMemoryKek() {
        inMemoryKek?.fill(0)
        inMemoryKek = null
    }

    private fun initialVaultState(): VaultState = if (vaultKeyMaterialLocalRepository.get() == null) {
        VaultState.NotInitialized
    } else {
        VaultState.Locked
    }
}
