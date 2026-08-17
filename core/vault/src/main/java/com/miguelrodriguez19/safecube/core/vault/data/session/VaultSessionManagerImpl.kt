package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure
import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalReadResult
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
        clearInMemoryKek()
        state.value = VaultState.InitialLoading
        val localReadResult = readLocalKeyMaterial()

        when (val result = vaultKeyMaterialRemoteRepository.getKeyMaterial()) {
            is VaultKeyMaterialRemoteResult.Success -> {
                runCatching {
                    vaultKeyMaterialLocalRepository.save(result.value)
                }.onSuccess {
                    state.value = VaultState.Locked
                }.onFailure {
                    state.value = VaultState.CorruptedLocalKeyMaterial
                }
            }

            is VaultKeyMaterialRemoteResult.Error -> {
                when (result.error) {
                    VaultKeyMaterialRemoteError.VaultNotInitialized -> {
                        vaultKeyMaterialLocalRepository.clear()
                        state.value = VaultState.NotInitialized
                    }

                    VaultKeyMaterialRemoteError.Unauthorized -> {
                        state.value = VaultState.AuthenticationRequired
                    }

                    VaultKeyMaterialRemoteError.Forbidden,
                    is VaultKeyMaterialRemoteError.HttpError,
                    is VaultKeyMaterialRemoteError.NetworkError,
                        -> {
                        state.value = resolveStateForRemoteFailure(
                            failure = result.error.failure,
                            localReadResult = localReadResult,
                        )
                    }

                    VaultKeyMaterialRemoteError.VaultAlreadyInitialized -> {
                        state.value = VaultState.TerminalRemoteFailure(result.error.failure)
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
            state.value = when (result.reason) {
                VaultUnlockError.KeyMaterialUnavailable -> when (readLocalKeyMaterial()) {
                    VaultKeyMaterialLocalReadResult.Absent -> VaultState.Locked
                    VaultKeyMaterialLocalReadResult.Corrupted ->
                        VaultState.CorruptedLocalKeyMaterial
                    is VaultKeyMaterialLocalReadResult.Present -> VaultState.Locked
                }

                VaultUnlockError.InvalidCachedKeyMaterial ->
                    VaultState.CorruptedLocalKeyMaterial

                VaultUnlockError.InvalidCredential -> VaultState.Locked
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

    private fun initialVaultState(): VaultState = VaultState.InitialLoading

    private fun resolveStateForRemoteFailure(
        failure: NetworkFailure,
        localReadResult: VaultKeyMaterialLocalReadResult,
    ): VaultState = when {
        localReadResult is VaultKeyMaterialLocalReadResult.Corrupted ->
            VaultState.CorruptedLocalKeyMaterial

        failure.decision == RetryDecision.Retryable ->
            VaultState.RetryableRemoteFailure(
                failure = failure,
                hasValidLocalKeyMaterial = localReadResult is VaultKeyMaterialLocalReadResult.Present,
            )

        else -> VaultState.TerminalRemoteFailure(failure)
    }

    private fun readLocalKeyMaterial(): VaultKeyMaterialLocalReadResult = runCatching {
        vaultKeyMaterialLocalRepository.read()
    }.getOrDefault(VaultKeyMaterialLocalReadResult.Corrupted)
}
