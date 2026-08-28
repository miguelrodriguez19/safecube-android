package com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase.ChangeVaultPassphraseError
import com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase.ChangeVaultPassphraseResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.MasterWrapperUpdateConfirmation
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalReadResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultKekProvider
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.session.QuickUnlockPromptMode
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
        var baseMaterial: VaultKeyMaterial? = null
        var activeKek = byteArrayOf()
        var currentPassphraseBytes = byteArrayOf()
        var newPassphraseBytes = byteArrayOf()
        var currentMasterKey = byteArrayOf()
        var newMasterKey = byteArrayOf()
        var unwrappedKek = byteArrayOf()
        var newKekEncMaster = byteArrayOf()

        return try {
            activeKek = vaultKekProvider.snapshot()
                ?: return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.ActiveKekUnavailable,
                )

            if (activeKek.isEmpty()) {
                return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.ActiveKekUnavailable,
                )
            }

            val localMaterial = readValidLocalMaterial()
                ?: return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.InvalidLocalKeyMaterial,
                )
            cachedMaterial = localMaterial

            val versionedMaterial = when (
                val result = vaultKeyMaterialRemoteRepository.getVersionedKeyMaterial()
            ) {
                is VaultKeyMaterialRemoteResult.Success -> result.value
                is VaultKeyMaterialRemoteResult.Error -> return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.RemoteFailure(result.error),
                )
            }

            val material = copyMaterial(versionedMaterial.material)
                .takeIf { hasValidKeyMaterial(it) && hasValidStrongOpaqueEtag(versionedMaterial.etag) }
                ?: return ChangeVaultPassphraseResult.Error(
                    ChangeVaultPassphraseError.InvalidLocalKeyMaterial,
                )
            baseMaterial = material
            val etagBase = versionedMaterial.etag
            if (!hasSameNonMasterMaterial(material, localMaterial)) {
                return failClosedAfterUncertainResult()
            }
            val remoteSupersedesCachedMaster = !constantTimeEquals(
                material.kekEncMaster,
                localMaterial.kekEncMaster,
            )

            currentPassphraseBytes = currentPassphrase.toByteArray(StandardCharsets.UTF_8)
            newPassphraseBytes = newPassphrase.toByteArray(StandardCharsets.UTF_8)

            currentMasterKey = deriveMasterKey(
                passphraseBytes = currentPassphraseBytes,
                material = material,
            ) ?: return credentialFailureAfterRemoteRefresh(
                remoteSupersedesCachedMaster = remoteSupersedesCachedMaster,
                remoteMaterial = material,
                fallback = ChangeVaultPassphraseError.InvalidLocalKeyMaterial,
            )

            unwrappedKek = try {
                keyWrapping.unwrapKey(
                    request = KeyUnwrapRequest(
                        wrappedKey = material.kekEncMaster,
                        wrappingKey = currentMasterKey,
                    ),
                )
            } catch (_: IllegalArgumentException) {
                return credentialFailureAfterRemoteRefresh(
                    remoteSupersedesCachedMaster = remoteSupersedesCachedMaster,
                    remoteMaterial = material,
                    fallback = ChangeVaultPassphraseError.InvalidLocalKeyMaterial,
                )
            } catch (_: Throwable) {
                return credentialFailureAfterRemoteRefresh(
                    remoteSupersedesCachedMaster = remoteSupersedesCachedMaster,
                    remoteMaterial = material,
                    fallback = ChangeVaultPassphraseError.InvalidCurrentPassphrase,
                )
            }

            if (!constantTimeEquals(unwrappedKek, activeKek)) {
                return credentialFailureAfterRemoteRefresh(
                    remoteSupersedesCachedMaster = remoteSupersedesCachedMaster,
                    remoteMaterial = material,
                    fallback = ChangeVaultPassphraseError.InvalidCurrentPassphrase,
                )
            }

            if (remoteSupersedesCachedMaster) {
                val synchronizationResult = persistMasterWrapper(material.kekEncMaster)
                if (synchronizationResult !is ChangeVaultPassphraseResult.Success) {
                    return synchronizationResult
                }
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
                    ifMatch = etagBase,
                )
            ) {
                is VaultKeyMaterialRemoteResult.Success -> if (
                    isValidConfirmation(updateResult.value, etagBase)
                ) {
                    persistMasterWrapper(newKekEncMaster)
                } else {
                    reconcileUncertainResult(
                        baseMaterial = material,
                        etagBase = etagBase,
                        wrapperCandidate = newKekEncMaster,
                    )
                }

                is VaultKeyMaterialRemoteResult.Error -> when (updateResult.error) {
                    VaultKeyMaterialRemoteError.MasterKeyRevisionConflict,
                    VaultKeyMaterialRemoteError.ContractViolation,
                    -> reconcileUncertainResult(
                        baseMaterial = material,
                        etagBase = etagBase,
                        wrapperCandidate = newKekEncMaster,
                    )

                    else -> if (updateResult.error.failure.decision == RetryDecision.Retryable) {
                        reconcileUncertainResult(
                            baseMaterial = material,
                            etagBase = etagBase,
                            wrapperCandidate = newKekEncMaster,
                        )
                    } else {
                        ChangeVaultPassphraseResult.Error(
                            ChangeVaultPassphraseError.RemoteFailure(updateResult.error),
                        )
                    }
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
            zeroizeIfPresent(cachedMaterial)
            zeroizeIfPresent(baseMaterial)
        }
    }

    private fun readValidLocalMaterial(): VaultKeyMaterial? = when (
        val result = runCatching { vaultKeyMaterialLocalRepository.read() }.getOrNull()
    ) {
        is VaultKeyMaterialLocalReadResult.Present -> result.value
            .takeIf(::hasValidKeyMaterial)
            ?.let(::copyMaterial)

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

    private suspend fun reconcileUncertainResult(
        baseMaterial: VaultKeyMaterial,
        etagBase: String,
        wrapperCandidate: ByteArray,
    ): ChangeVaultPassphraseResult = try {
        when (val getResult = vaultKeyMaterialRemoteRepository.getVersionedKeyMaterial()) {
            is VaultKeyMaterialRemoteResult.Success -> {
                val remoteVersion = getResult.value
                val remoteMaterial = copyMaterial(remoteVersion.material)
                try {
                    reconcileRemoteVersion(
                        baseMaterial = baseMaterial,
                        etagBase = etagBase,
                        wrapperCandidate = wrapperCandidate,
                        remoteMaterial = remoteMaterial,
                        remoteEtag = remoteVersion.etag,
                    )
                } finally {
                    zeroize(remoteMaterial)
                }
            }

            is VaultKeyMaterialRemoteResult.Error -> failClosedAfterUncertainResult()
        }
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (_: Throwable) {
        failClosedAfterUncertainResult()
    }

    private fun reconcileRemoteVersion(
        baseMaterial: VaultKeyMaterial,
        etagBase: String,
        wrapperCandidate: ByteArray,
        remoteMaterial: VaultKeyMaterial,
        remoteEtag: String,
    ): ChangeVaultPassphraseResult {
        if (!hasValidKeyMaterial(remoteMaterial) ||
            !hasValidStrongOpaqueEtag(remoteEtag) ||
            !hasSameNonMasterMaterial(remoteMaterial, baseMaterial)
        ) {
            return failClosedAfterUncertainResult()
        }

        return when {
            constantTimeEquals(remoteMaterial.kekEncMaster, wrapperCandidate) &&
                remoteEtag != etagBase -> persistMasterWrapper(
                kekEncMaster = wrapperCandidate,
            )

            constantTimeEquals(remoteMaterial.kekEncMaster, baseMaterial.kekEncMaster) &&
                remoteEtag == etagBase -> ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.RemoteChangeNotApplied,
            )

            remoteEtag != etagBase &&
                !constantTimeEquals(remoteMaterial.kekEncMaster, baseMaterial.kekEncMaster) &&
                !constantTimeEquals(remoteMaterial.kekEncMaster, wrapperCandidate) ->
                persistConcurrentRemoteMaterial(remoteMaterial)

            else -> failClosedAfterUncertainResult()
        }
    }

    private fun persistMasterWrapper(
        kekEncMaster: ByteArray,
    ): ChangeVaultPassphraseResult {
        val persistedWrapper = kekEncMaster.copyOf()
        return try {
            vaultKeyMaterialLocalRepository.updateMasterWrappedKek(persistedWrapper)
            ChangeVaultPassphraseResult.Success
        } catch (_: Throwable) {
            failClosedAfterUncertainResult()
        } finally {
            persistedWrapper.fill(0)
        }
    }

    private fun persistConcurrentRemoteMaterial(
        remoteMaterial: VaultKeyMaterial,
    ): ChangeVaultPassphraseResult {
        val persisted = persistMasterWrapper(remoteMaterial.kekEncMaster)
        return if (persisted is ChangeVaultPassphraseResult.Success) {
            lockAfterRemoteChange(ChangeVaultPassphraseError.ConcurrentRemoteChange)
        } else {
            persisted
        }
    }

    private fun credentialFailureAfterRemoteRefresh(
        remoteSupersedesCachedMaster: Boolean,
        remoteMaterial: VaultKeyMaterial,
        fallback: ChangeVaultPassphraseError,
    ): ChangeVaultPassphraseResult = if (remoteSupersedesCachedMaster) {
        persistConcurrentRemoteMaterial(remoteMaterial)
    } else {
        ChangeVaultPassphraseResult.Error(fallback)
    }

    private fun lockAfterRemoteChange(
        error: ChangeVaultPassphraseError,
    ): ChangeVaultPassphraseResult {
        runCatching { vaultSessionManager.clearQuickUnlockEnrollment() }
        runCatching {
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
        }
        return ChangeVaultPassphraseResult.Error(error)
    }

    private fun failClosedAfterUncertainResult(): ChangeVaultPassphraseResult {
        runCatching { vaultKeyMaterialLocalRepository.clearMasterWrappedKek() }
        runCatching { vaultSessionManager.clearQuickUnlockEnrollment() }
        runCatching {
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
        }
        return ChangeVaultPassphraseResult.Error(
            ChangeVaultPassphraseError.ReconciliationRequired,
        )
    }

    private fun copyMaterial(material: VaultKeyMaterial): VaultKeyMaterial = material.copy(
        kekEncMaster = material.kekEncMaster.copyOf(),
        kekEncRecovery = material.kekEncRecovery.copyOf(),
        kdfSalt = material.kdfSalt.copyOf(),
    )

    private fun hasSameNonMasterMaterial(
        left: VaultKeyMaterial,
        right: VaultKeyMaterial,
    ): Boolean = left.accountId == right.accountId &&
        constantTimeEquals(left.kekEncRecovery, right.kekEncRecovery) &&
        left.kdfAlgorithm == right.kdfAlgorithm &&
        constantTimeEquals(left.kdfSalt, right.kdfSalt) &&
        left.kdfMemoryKib == right.kdfMemoryKib &&
        left.kdfIterations == right.kdfIterations &&
        left.kdfParallelism == right.kdfParallelism &&
        left.kdfOutputLen == right.kdfOutputLen &&
        left.cryptoVersion == right.cryptoVersion

    private fun isValidConfirmation(
        confirmation: MasterWrapperUpdateConfirmation,
        etagBase: String,
    ): Boolean = hasValidStrongOpaqueEtag(confirmation.etag) && confirmation.etag != etagBase

    private fun hasValidStrongOpaqueEtag(etag: String): Boolean =
        etag.length >= 2 &&
            !etag.startsWith("W/") &&
            etag != "*" &&
            etag.first() == '"' &&
            etag.last() == '"' &&
            !etag.contains(',') &&
            !etag.contains('\r') &&
            !etag.contains('\n')

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

    private fun zeroizeIfPresent(material: VaultKeyMaterial?) {
        if (material != null) {
            zeroize(material)
        }
    }
}
