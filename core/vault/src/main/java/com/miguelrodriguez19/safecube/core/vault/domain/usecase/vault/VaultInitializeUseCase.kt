package com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.crypto.domain.service.SaltGenerator
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.vault.domain.config.VaultCryptoDefaults
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitialization
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultRecoveryKeyResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitializationState
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeError
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationReadResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

@Singleton
class VaultInitializeUseCase @Inject constructor(
    private val vaultKeyMaterialRemoteRepository: VaultKeyMaterialRemoteRepository,
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
    private val pendingVaultInitializationRepository: PendingVaultInitializationRepository,
    private val kdfEngine: KdfEngine,
    private val keyWrapping: KeyWrapping,
    private val saltGenerator: SaltGenerator,
) {
    private val initializationMutex = Mutex()

    suspend operator fun invoke(passphrase: String): VaultInitializeResult =
        initializationMutex.withLock {
            initialize(passphrase)
        }

    /**
     * Removes the recovery key only after the recovery screen has explicitly confirmed that it
     * was saved by the user.
     */
    fun confirmRecoveryKeySaved(): Boolean = runCatching {
        pendingVaultInitializationRepository.clear()
    }.getOrDefault(false)

    fun readPendingRecoveryKey(): PendingVaultRecoveryKeyResult {
        val pendingInitialization = when (val pendingResult = readPendingInitialization()) {
            PendingVaultInitializationReadResult.Empty -> return PendingVaultRecoveryKeyResult.Unavailable
            is PendingVaultInitializationReadResult.Present -> pendingResult.value
            PendingVaultInitializationReadResult.Corrupted ->
                return PendingVaultRecoveryKeyResult.Corrupted
        }

        return try {
            PendingVaultRecoveryKeyResult.Available(pendingInitialization.recoveryKey.copyOf())
        } finally {
            zeroize(pendingInitialization)
        }
    }

    private suspend fun initialize(passphrase: String): VaultInitializeResult {
        val pendingInitialization = when (
            val pendingResult = readPendingInitialization()
        ) {
            PendingVaultInitializationReadResult.Empty -> null
            is PendingVaultInitializationReadResult.Present -> pendingResult.value
            PendingVaultInitializationReadResult.Corrupted -> {
                return VaultInitializeResult.Error(
                    reason = VaultInitializeError.LocalStorage(
                        operation = VaultInitializeError.LocalStorageOperation.Read,
                    ),
                )
            }
        }

        return if (pendingInitialization == null) {
            initializeWithoutPendingRecord(passphrase)
        } else {
            try {
                reconcilePendingInitialization(pendingInitialization)
            } finally {
                zeroize(pendingInitialization)
            }
        }
    }

    private fun readPendingInitialization(): PendingVaultInitializationReadResult = try {
        pendingVaultInitializationRepository.read()
    } catch (_: Throwable) {
        PendingVaultInitializationReadResult.Corrupted
    }

    private suspend fun initializeWithoutPendingRecord(passphrase: String): VaultInitializeResult {
        when (val getResult = vaultKeyMaterialRemoteRepository.getKeyMaterial()) {
            is VaultKeyMaterialRemoteResult.Success -> return VaultInitializeResult.AlreadyInitialized
            is VaultKeyMaterialRemoteResult.Error -> {
                if (getResult.error != VaultKeyMaterialRemoteError.VaultNotInitialized) {
                    return VaultInitializeResult.Error(
                        reason = VaultInitializeError.Remote(getResult.error),
                    )
                }
            }
        }

        val passphraseBytes = passphrase.toByteArray(StandardCharsets.UTF_8)
        var masterKey = byteArrayOf()
        var kek = byteArrayOf()
        var recoveryKey = byteArrayOf()
        var pendingInitialization: PendingVaultInitialization? = null

        return try {
            val kdfSalt = saltGenerator.generate(
                lengthBytes = VaultCryptoDefaults.KDF_SALT_LENGTH_BYTES,
            )
            val derivedMasterKey = kdfEngine.deriveKey(
                request = KdfRequest(
                    secret = passphraseBytes,
                    salt = kdfSalt,
                    iterations = VaultCryptoDefaults.KDF_ITERATIONS,
                    memoryKib = VaultCryptoDefaults.KDF_MEMORY_KIB,
                    parallelism = VaultCryptoDefaults.KDF_PARALLELISM,
                    outputLengthBytes = VaultCryptoDefaults.KDF_OUTPUT_LEN,
                ),
            )
            masterKey = derivedMasterKey

            val generatedKek = saltGenerator.generate(
                lengthBytes = VaultCryptoDefaults.KEY_LENGTH_BYTES,
            )
            kek = generatedKek

            val generatedRecoveryKey = saltGenerator.generate(
                lengthBytes = VaultCryptoDefaults.KEY_LENGTH_BYTES,
            )
            recoveryKey = generatedRecoveryKey

            val candidate = VaultKeyMaterial(
                kekEncMaster = wrapKek(
                    kek = generatedKek,
                    wrappingKey = derivedMasterKey,
                ),
                kekEncRecovery = wrapKek(
                    kek = generatedKek,
                    wrappingKey = generatedRecoveryKey,
                ),
                kdfAlgorithm = VaultCryptoDefaults.KDF_ALGORITHM,
                kdfSalt = kdfSalt,
                cryptoVersion = VaultCryptoDefaults.CRYPTO_VERSION,
                kdfMemoryKib = VaultCryptoDefaults.KDF_MEMORY_KIB,
                kdfIterations = VaultCryptoDefaults.KDF_ITERATIONS,
                kdfParallelism = VaultCryptoDefaults.KDF_PARALLELISM,
                kdfOutputLen = VaultCryptoDefaults.KDF_OUTPUT_LEN,
            )
            pendingInitialization = PendingVaultInitialization(
                candidate = candidate,
                recoveryKey = generatedRecoveryKey.copyOf(),
                state = PendingVaultInitializationState.AwaitingRemoteConfirmation,
            )

            if (!savePendingInitialization(pendingInitialization)) {
                VaultInitializeResult.Error(
                    reason = VaultInitializeError.LocalStorage(
                        operation = VaultInitializeError.LocalStorageOperation.Persist,
                    ),
                )
            } else {
                submitPendingInitialization(pendingInitialization)
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (_: Throwable) {
            VaultInitializeResult.Error(
                reason = VaultInitializeError.Crypto(NetworkFailureClassifier.unknown()),
            )
        } finally {
            passphraseBytes.fill(0)
            masterKey.fill(0)
            kek.fill(0)
            recoveryKey.fill(0)
            pendingInitialization?.let(::zeroize)
        }
    }

    private suspend fun reconcilePendingInitialization(
        pendingInitialization: PendingVaultInitialization,
    ): VaultInitializeResult = when (
        val getResult = vaultKeyMaterialRemoteRepository.getKeyMaterial()
    ) {
        is VaultKeyMaterialRemoteResult.Success -> reconcileRemoteMaterial(
            pendingInitialization = pendingInitialization,
            remoteMaterial = getResult.value,
        )

        is VaultKeyMaterialRemoteResult.Error -> if (
            getResult.error == VaultKeyMaterialRemoteError.VaultNotInitialized
        ) {
            submitPendingInitialization(pendingInitialization)
        } else {
            VaultInitializeResult.Error(
                reason = VaultInitializeError.Remote(getResult.error),
            )
        }
    }

    private suspend fun submitPendingInitialization(
        pendingInitialization: PendingVaultInitialization,
    ): VaultInitializeResult {
        return when (
            val initResult = vaultKeyMaterialRemoteRepository.initKeyMaterial(
                pendingInitialization.candidate,
            )
        ) {
            is VaultKeyMaterialRemoteResult.Success,
            is VaultKeyMaterialRemoteResult.Error
                -> reconcileAfterPost(pendingInitialization, initResult)
        }
    }

    private suspend fun reconcileAfterPost(
        pendingInitialization: PendingVaultInitialization,
        initResult: VaultKeyMaterialRemoteResult<Unit>,
    ): VaultInitializeResult {
        if (initResult is VaultKeyMaterialRemoteResult.Error &&
            initResult.error != VaultKeyMaterialRemoteError.VaultAlreadyInitialized
        ) {
            return VaultInitializeResult.Error(
                reason = VaultInitializeError.Remote(initResult.error),
            )
        }

        return when (
            val getResult = vaultKeyMaterialRemoteRepository.getKeyMaterial()
        ) {
            is VaultKeyMaterialRemoteResult.Success -> reconcileRemoteMaterial(
                pendingInitialization = pendingInitialization,
                remoteMaterial = getResult.value,
            )

            is VaultKeyMaterialRemoteResult.Error -> VaultInitializeResult.Error(
                reason = VaultInitializeError.Remote(getResult.error),
            )
        }
    }

    private fun reconcileRemoteMaterial(
        pendingInitialization: PendingVaultInitialization,
        remoteMaterial: VaultKeyMaterial,
    ): VaultInitializeResult {
        if (!matchesCandidate(pendingInitialization.candidate, remoteMaterial)) {
            return when {
                !saveConfirmedMaterial(remoteMaterial) -> VaultInitializeResult.Error(
                    reason = VaultInitializeError.LocalStorage(
                        operation = VaultInitializeError.LocalStorageOperation.Persist,
                    ),
                )

                !clearPendingInitialization() -> VaultInitializeResult.Error(
                    reason = VaultInitializeError.LocalStorage(
                        operation = VaultInitializeError.LocalStorageOperation.Cleanup,
                    ),
                )

                else -> VaultInitializeResult.AlreadyInitialized
            }
        }

        if (!saveConfirmedMaterial(remoteMaterial)) {
            return VaultInitializeResult.Error(
                reason = VaultInitializeError.LocalStorage(
                    operation = VaultInitializeError.LocalStorageOperation.Persist,
                ),
            )
        }

        val confirmedPending = pendingInitialization.copy(
            candidate = pendingInitialization.candidate.copy(
                accountId = remoteMaterial.accountId,
            ),
            recoveryKey = pendingInitialization.recoveryKey.copyOf(),
            state = PendingVaultInitializationState.RemoteConfirmed,
        )
        if (!savePendingInitialization(confirmedPending)) {
            zeroize(confirmedPending)
            return VaultInitializeResult.Error(
                reason = VaultInitializeError.LocalStorage(
                    operation = VaultInitializeError.LocalStorageOperation.Persist,
                ),
            )
        }
        zeroize(confirmedPending)

        return VaultInitializeResult.Initialized(
            recoveryKey = pendingInitialization.recoveryKey.copyOf(),
        )
    }

    private fun savePendingInitialization(
        pendingInitialization: PendingVaultInitialization,
    ): Boolean = try {
        pendingVaultInitializationRepository.save(pendingInitialization)
    } catch (_: Throwable) {
        false
    }

    private fun clearPendingInitialization(): Boolean = try {
        pendingVaultInitializationRepository.clear()
    } catch (_: Throwable) {
        false
    }

    private fun saveConfirmedMaterial(remoteMaterial: VaultKeyMaterial): Boolean = try {
        vaultKeyMaterialLocalRepository.save(remoteMaterial)
        true
    } catch (_: Throwable) {
        false
    }

    private fun matchesCandidate(
        candidate: VaultKeyMaterial,
        remoteMaterial: VaultKeyMaterial,
    ): Boolean =
        (candidate.accountId == null || candidate.accountId == remoteMaterial.accountId) &&
                constantTimeEquals(candidate.kekEncMaster, remoteMaterial.kekEncMaster) &&
                constantTimeEquals(candidate.kekEncRecovery, remoteMaterial.kekEncRecovery) &&
                candidate.kdfAlgorithm == remoteMaterial.kdfAlgorithm &&
                constantTimeEquals(candidate.kdfSalt, remoteMaterial.kdfSalt) &&
                candidate.kdfMemoryKib == remoteMaterial.kdfMemoryKib &&
                candidate.kdfIterations == remoteMaterial.kdfIterations &&
                candidate.kdfParallelism == remoteMaterial.kdfParallelism &&
                candidate.kdfOutputLen == remoteMaterial.kdfOutputLen &&
                candidate.cryptoVersion == remoteMaterial.cryptoVersion

    private fun constantTimeEquals(left: ByteArray, right: ByteArray): Boolean =
        MessageDigest.isEqual(left, right)

    private fun wrapKek(
        kek: ByteArray,
        wrappingKey: ByteArray,
    ): ByteArray = keyWrapping.wrapKey(
        request = KeyWrapRequest(
            keyToWrap = kek,
            wrappingKey = wrappingKey,
        ),
    )

    private fun zeroize(value: PendingVaultInitialization) {
        value.candidate.kekEncMaster.fill(0)
        value.candidate.kekEncRecovery.fill(0)
        value.candidate.kdfSalt.fill(0)
        value.recoveryKey.fill(0)
    }
}
