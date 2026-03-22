package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.crypto.domain.service.SaltGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.config.VaultCryptoDefaults
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeError
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import java.util.UUID

@Singleton
class VaultInitializeUseCase @Inject constructor(
    private val vaultKeyMaterialRemoteRepository: VaultKeyMaterialRemoteRepository,
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
    private val kdfEngine: KdfEngine,
    private val keyWrapping: KeyWrapping,
    private val saltGenerator: SaltGenerator,
) {

    suspend operator fun invoke(passphrase: String): VaultInitializeResult {
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

            val kekEncMaster = wrapKek(
                kek = generatedKek,
                wrappingKey = derivedMasterKey,
            )
            val kekEncRecovery = wrapKek(
                kek = generatedKek,
                wrappingKey = generatedRecoveryKey,
            )

            val vaultKeyMaterial = VaultKeyMaterial(
                kekEncMaster = kekEncMaster,
                kekEncRecovery = kekEncRecovery,
                kdfAlgorithm = VaultCryptoDefaults.KDF_ALGORITHM,
                kdfSalt = kdfSalt,
                cryptoVersion = VaultCryptoDefaults.CRYPTO_VERSION,
                kdfMemoryKib = VaultCryptoDefaults.KDF_MEMORY_KIB,
                kdfIterations = VaultCryptoDefaults.KDF_ITERATIONS,
                kdfParallelism = VaultCryptoDefaults.KDF_PARALLELISM,
                kdfOutputLen = VaultCryptoDefaults.KDF_OUTPUT_LEN,
            )

            when (val initResult = vaultKeyMaterialRemoteRepository.initKeyMaterial(vaultKeyMaterial)) {
                is VaultKeyMaterialRemoteResult.Success -> {
                    vaultKeyMaterialLocalRepository.save(
                        refreshCachedVaultKeyMaterial(
                            initializedVaultKeyMaterial = vaultKeyMaterial,
                        ),
                    )
                    VaultInitializeResult.Initialized(
                        recoveryKey = generatedRecoveryKey.copyOf(),
                    )
                }

                is VaultKeyMaterialRemoteResult.Error -> {
                    if (initResult.error == VaultKeyMaterialRemoteError.VaultAlreadyInitialized) {
                        VaultInitializeResult.AlreadyInitialized
                    } else {
                        VaultInitializeResult.Error(
                            reason = VaultInitializeError.Remote(initResult.error),
                        )
                    }
                }
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            VaultInitializeResult.Error(
                reason = VaultInitializeError.Crypto(throwable),
            )
        } finally {
            passphraseBytes.fill(0)
            masterKey.fill(0)
            kek.fill(0)
            recoveryKey.fill(0)
        }
    }

    private suspend fun refreshCachedVaultKeyMaterial(
        initializedVaultKeyMaterial: VaultKeyMaterial,
    ): VaultKeyMaterial = when (val refreshedResponse = vaultKeyMaterialRemoteRepository.getKeyMaterial()) {
        is VaultKeyMaterialRemoteResult.Success -> refreshedResponse.value
        is VaultKeyMaterialRemoteResult.Error -> initializedVaultKeyMaterial
    }

    private fun wrapKek(
        kek: ByteArray,
        wrappingKey: ByteArray,
    ): ByteArray = keyWrapping.wrapKey(
        request = KeyWrapRequest(
            keyToWrap = kek,
            wrappingKey = wrappingKey,
        ),
    )
}
