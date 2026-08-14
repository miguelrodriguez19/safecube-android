package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultUnlocker
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
internal class VaultSessionManagerImpl @Inject constructor(
    private val vaultUnlocker: VaultUnlocker,
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
    private val vaultKeyMaterialRemoteRepository: VaultKeyMaterialRemoteRepository,
    private val vaultInMemoryKekStore: VaultInMemoryKekStore,
) : VaultSessionManager {
    private val state = MutableStateFlow(initialVaultState())

    override val vaultState: StateFlow<VaultState> = state.asStateFlow()

    override fun isUnlocked(): Boolean {
        return state.value == VaultState.Unlocked
    }

    override suspend fun refreshVaultState() {
        state.value = VaultState.Unknown

        when (val result = vaultKeyMaterialRemoteRepository.getKeyMaterial()) {
            is VaultKeyMaterialRemoteResult.Success -> {
                vaultKeyMaterialLocalRepository.save(result.value)
                clearInMemoryKek()
                state.value = VaultState.Locked
            }

            is VaultKeyMaterialRemoteResult.Error -> {
                when (result.error) {
                    VaultKeyMaterialRemoteError.VaultNotInitialized -> {
                        vaultKeyMaterialLocalRepository.clear()
                        clearInMemoryKek()
                        state.value = VaultState.NotInitialized
                    }

                    VaultKeyMaterialRemoteError.Unauthorized -> {
                        clearInMemoryKek()
                        state.value = VaultState.Locked
                    }

                    VaultKeyMaterialRemoteError.Forbidden -> {
                        clearInMemoryKek()
                        state.value = VaultState.Locked
                    }

                    is VaultKeyMaterialRemoteError.HttpError,
                    is VaultKeyMaterialRemoteError.NetworkError,
                        -> {
                        state.value = resolveStateForRemoteFailure()
                    }

                    VaultKeyMaterialRemoteError.VaultAlreadyInitialized -> {
                        state.value = initialVaultState()
                    }
                }
            }
        }
    }

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
        vaultInMemoryKekStore.replace(newKek)
    }

    private fun clearInMemoryKek() {
        vaultInMemoryKekStore.clear()
    }

    private fun initialVaultState(): VaultState =
        if (vaultKeyMaterialLocalRepository.get() == null) {
            VaultState.NotInitialized
        } else {
            VaultState.Locked
        }

    private fun resolveStateForRemoteFailure(): VaultState =
        if (vaultKeyMaterialLocalRepository.get() == null) {
            VaultState.Unknown
        } else {
            VaultState.Locked
        }
}
