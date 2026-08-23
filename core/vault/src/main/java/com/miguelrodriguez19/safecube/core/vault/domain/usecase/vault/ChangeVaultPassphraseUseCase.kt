package com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase.ChangeVaultPassphraseError
import com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase.ChangeVaultPassphraseResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalReadResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultKekProvider
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class ChangeVaultPassphraseUseCase @Inject constructor(
    private val vaultSessionManager: VaultSessionManager,
    private val vaultKekProvider: VaultKekProvider,
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
    private val vaultKeyMaterialRemoteRepository: VaultKeyMaterialRemoteRepository,
    private val kdfEngine: KdfEngine,
    private val keyWrapping: KeyWrapping,
) {
    private val changeMutex = Mutex()

    suspend operator fun invoke(
        currentPassphrase: String,
        newPassphrase: String,
    ): ChangeVaultPassphraseResult = changeMutex.withLock {
        changePassphrase(
            currentPassphrase = currentPassphrase,
            newPassphrase = newPassphrase,
        )
    }

    private suspend fun changePassphrase(
        currentPassphrase: String,
        newPassphrase: String,
    ): ChangeVaultPassphraseResult {
        if (!vaultSessionManager.isUnlocked()) {
            return ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.InvalidVaultState(
                    currentState = vaultSessionManager.vaultState.value,
                ),
            )
        }

        if (newPassphrase.isEmpty()) {
            return ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.InvalidNewPassphrase,
            )
        }

        var cachedMaterial: VaultKeyMaterial? = null
        var activeKek = byteArrayOf()
        var currentPassphraseBytes = byteArrayOf()
        var newPassphraseBytes = byteArrayOf()
        var currentMasterKey = byteArrayOf()
        var newMasterKey = byteArrayOf()
        var unwrappedKek = byteArrayOf()
        var newKekEncMaster = byteArrayOf()

        return try {
            val material = readValidLocalMaterial()
                ?: return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.InvalidLocalKeyMaterial,
                )
            cachedMaterial = material

            activeKek = vaultKekProvider.snapshot()
                ?: return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.ActiveKekUnavailable,
                )

            if (activeKek.isEmpty()) {
                return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.ActiveKekUnavailable,
                )
            }

            currentPassphraseBytes = currentPassphrase.toByteArray(StandardCharsets.UTF_8)
            newPassphraseBytes = newPassphrase.toByteArray(StandardCharsets.UTF_8)

            currentMasterKey = deriveMasterKey(
                passphraseBytes = currentPassphraseBytes,
                material = material,
            ) ?: return ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.InvalidLocalKeyMaterial,
            )

            unwrappedKek = try {
                keyWrapping.unwrapKey(
                    request = KeyUnwrapRequest(
                        wrappedKey = material.kekEncMaster,
                        wrappingKey = currentMasterKey,
                    ),
                )
            } catch (_: IllegalArgumentException) {
                return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.InvalidLocalKeyMaterial,
                )
            } catch (_: Throwable) {
                return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.InvalidCurrentPassphrase,
                )
            }

            if (!constantTimeEquals(unwrappedKek, activeKek)) {
                return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.InvalidCurrentPassphrase,
                )
            }

            newMasterKey = deriveMasterKey(
                passphraseBytes = newPassphraseBytes,
                material = material,
            ) ?: return ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.CryptoFailure,
            )

            newKekEncMaster = try {
                keyWrapping.wrapKey(
                    request = KeyWrapRequest(
                        keyToWrap = activeKek,
                        wrappingKey = newMasterKey,
                    ),
                )
            } catch (_: Throwable) {
                return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.CryptoFailure,
                )
            }

            if (newKekEncMaster.isEmpty() ||
                newKekEncMaster.contentEquals(material.kekEncMaster)
            ) {
                return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.CryptoFailure,
                )
            }

            when (
                val updateResult = vaultKeyMaterialRemoteRepository.updateMasterWrappedKek(
                    newKekEncMaster = newKekEncMaster.copyOf(),
                )
            ) {
                is VaultKeyMaterialRemoteResult.Success -> persistConfirmedWrapper(
                    newKekEncMaster = newKekEncMaster,
                )

                is VaultKeyMaterialRemoteResult.Error -> when (updateResult.error) {
                    is VaultKeyMaterialRemoteError.NetworkError -> reconcileLostResponse(
                        cachedMaterial = material,
                        newKekEncMaster = newKekEncMaster,
                    )

                    else -> ChangeVaultPassphraseResult.Error(
                        ChangeVaultPassphraseError.RemoteFailure(updateResult.error),
                    )
                }
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (_: Throwable) {
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.CryptoFailure)
        } finally {
            currentPassphraseBytes.fill(0)
            newPassphraseBytes.fill(0)
            currentMasterKey.fill(0)
            newMasterKey.fill(0)
            unwrappedKek.fill(0)
            activeKek.fill(0)
            newKekEncMaster.fill(0)
            cachedMaterial?.let(::zeroize)
        }
    }

    private fun readValidLocalMaterial(): VaultKeyMaterial? = when (
        val result = runCatching { vaultKeyMaterialLocalRepository.read() }
            .getOrNull()
    ) {
        is VaultKeyMaterialLocalReadResult.Present -> result.value
            .takeIf(::hasValidKeyMaterial)
            ?.copy(
                kekEncMaster = result.value.kekEncMaster.copyOf(),
                kekEncRecovery = result.value.kekEncRecovery.copyOf(),
                kdfSalt = result.value.kdfSalt.copyOf(),
            )

        VaultKeyMaterialLocalReadResult.Absent,
        VaultKeyMaterialLocalReadResult.Corrupted,
        null,
            -> null
    }

    private fun deriveMasterKey(
        passphraseBytes: ByteArray,
        material: VaultKeyMaterial,
    ): ByteArray? = runCatching {
        kdfEngine.deriveKey(
            request = KdfRequest(
                secret = passphraseBytes,
                salt = material.kdfSalt,
                iterations = material.kdfIterations,
                memoryKib = material.kdfMemoryKib,
                parallelism = material.kdfParallelism,
                outputLengthBytes = material.kdfOutputLen,
            ),
        )
    }.getOrNull()

    private suspend fun reconcileLostResponse(
        cachedMaterial: VaultKeyMaterial,
        newKekEncMaster: ByteArray,
    ): ChangeVaultPassphraseResult = try {
        when (val getResult = vaultKeyMaterialRemoteRepository.getKeyMaterial()) {
            is VaultKeyMaterialRemoteResult.Success -> when {
                matchesRemoteMaterial(
                    remoteMaterial = getResult.value,
                    expectedKekEncMaster = newKekEncMaster,
                    cachedMaterial = cachedMaterial,
                ) -> persistConfirmedWrapper(newKekEncMaster)

                matchesRemoteMaterial(
                    remoteMaterial = getResult.value,
                    expectedKekEncMaster = cachedMaterial.kekEncMaster,
                    cachedMaterial = cachedMaterial,
                ) -> ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.RemoteChangeNotApplied,
                )

                else -> failClosedAfterUncertainResult()
            }

            is VaultKeyMaterialRemoteResult.Error -> failClosedAfterUncertainResult()
        }
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (_: Throwable) {
        failClosedAfterUncertainResult()
    }

    private fun persistConfirmedWrapper(
        newKekEncMaster: ByteArray,
    ): ChangeVaultPassphraseResult = try {
        vaultKeyMaterialLocalRepository.updateMasterWrappedKek(newKekEncMaster.copyOf())
        ChangeVaultPassphraseResult.Success
    } catch (_: Throwable) {
        failClosedAfterUncertainResult()
    }

    private fun failClosedAfterUncertainResult(): ChangeVaultPassphraseResult {
        runCatching { vaultKeyMaterialLocalRepository.clearMasterWrappedKek() }
        runCatching { vaultSessionManager.lock() }
        return ChangeVaultPassphraseResult.Error(
            ChangeVaultPassphraseError.ReconciliationRequired,
        )
    }

    private fun matchesRemoteMaterial(
        remoteMaterial: VaultKeyMaterial,
        expectedKekEncMaster: ByteArray,
        cachedMaterial: VaultKeyMaterial,
    ): Boolean = hasValidKeyMaterial(remoteMaterial) &&
            remoteMaterial.accountId == cachedMaterial.accountId &&
        constantTimeEquals(remoteMaterial.kekEncMaster, expectedKekEncMaster) &&
        constantTimeEquals(remoteMaterial.kekEncRecovery, cachedMaterial.kekEncRecovery) &&
        remoteMaterial.kdfAlgorithm == cachedMaterial.kdfAlgorithm &&
        constantTimeEquals(remoteMaterial.kdfSalt, cachedMaterial.kdfSalt) &&
        remoteMaterial.kdfMemoryKib == cachedMaterial.kdfMemoryKib &&
        remoteMaterial.kdfIterations == cachedMaterial.kdfIterations &&
        remoteMaterial.kdfParallelism == cachedMaterial.kdfParallelism &&
        remoteMaterial.kdfOutputLen == cachedMaterial.kdfOutputLen &&
        remoteMaterial.cryptoVersion == cachedMaterial.cryptoVersion

    private fun hasValidKeyMaterial(material: VaultKeyMaterial): Boolean =
        material.accountId != null &&
            material.kekEncMaster.isNotEmpty() &&
            material.kekEncRecovery.isNotEmpty() &&
            material.kdfAlgorithm.isNotBlank() &&
            material.kdfSalt.isNotEmpty() &&
            material.kdfMemoryKib > 0 &&
            material.kdfIterations > 0 &&
            material.kdfParallelism > 0 &&
            material.kdfOutputLen > 0 &&
            material.cryptoVersion.isNotBlank()

    private fun constantTimeEquals(left: ByteArray, right: ByteArray): Boolean =
        MessageDigest.isEqual(left, right)

    private fun zeroize(material: VaultKeyMaterial) {
        material.kekEncMaster.fill(0)
        material.kekEncRecovery.fill(0)
        material.kdfSalt.fill(0)
    }
}
