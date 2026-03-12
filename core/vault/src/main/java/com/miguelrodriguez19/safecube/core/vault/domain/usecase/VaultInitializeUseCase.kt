package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.crypto.CryptoEngine
import com.miguelrodriguez19.safecube.core.crypto.EncryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.SaltGenerator
import com.miguelrodriguez19.safecube.core.network.generated.model.InitVaultKeyMaterialRequest
import com.miguelrodriguez19.safecube.core.vault.data.local.CachedVaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.data.local.VaultKeyMaterialCache
import com.miguelrodriguez19.safecube.core.vault.data.remote.VaultKeyMaterialDataSource
import com.miguelrodriguez19.safecube.core.vault.data.remote.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.data.remote.VaultKeyMaterialRemoteResult
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException

sealed interface VaultInitializeResult {
    data class Initialized(
        val recoveryKey: ByteArray,
    ) : VaultInitializeResult

    data object AlreadyInitialized : VaultInitializeResult

    data class Error(
        val reason: VaultInitializeError,
    ) : VaultInitializeResult
}

sealed interface VaultInitializeError {
    data class Remote(
        val error: VaultKeyMaterialRemoteError,
    ) : VaultInitializeError

    data class Crypto(
        val throwable: Throwable,
    ) : VaultInitializeError
}

@Singleton
class VaultInitializeUseCase @Inject constructor(
    private val vaultKeyMaterialDataSource: VaultKeyMaterialDataSource,
    private val vaultKeyMaterialCache: VaultKeyMaterialCache,
    private val kdfEngine: KdfEngine,
    private val cryptoEngine: CryptoEngine,
    private val saltGenerator: SaltGenerator,
) {

    suspend operator fun invoke(passphrase: String): VaultInitializeResult {
        when (val getResult = vaultKeyMaterialDataSource.getKeyMaterial()) {
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
            val kdfSalt = saltGenerator.generate(lengthBytes = KDF_SALT_LENGTH_BYTES)
            val derivedMasterKey = kdfEngine.deriveKey(
                request = KdfRequest(
                    secret = passphraseBytes,
                    salt = kdfSalt,
                    iterations = KDF_ITERATIONS,
                    memoryKib = KDF_MEMORY_KIB,
                    parallelism = KDF_PARALLELISM,
                    outputLengthBytes = KDF_OUTPUT_LEN,
                ),
            )
            masterKey = derivedMasterKey

            val generatedKek = saltGenerator.generate(lengthBytes = KEY_LENGTH_BYTES)
            kek = generatedKek

            val generatedRecoveryKey = saltGenerator.generate(lengthBytes = KEY_LENGTH_BYTES)
            recoveryKey = generatedRecoveryKey

            val kekEncMaster = wrapKek(
                kek = generatedKek,
                wrappingKey = derivedMasterKey,
            )
            val kekEncRecovery = wrapKek(
                kek = generatedKek,
                wrappingKey = generatedRecoveryKey,
            )

            val request = InitVaultKeyMaterialRequest(
                kekEncMaster = kekEncMaster,
                kekEncRecovery = kekEncRecovery,
                kdfAlgorithm = KDF_ALGORITHM,
                kdfSalt = kdfSalt,
                cryptoVersion = CRYPTO_VERSION,
                kdfMemoryKib = KDF_MEMORY_KIB,
                kdfIterations = KDF_ITERATIONS,
                kdfParallelism = KDF_PARALLELISM,
                kdfOutputLen = KDF_OUTPUT_LEN,
            )

            when (val initResult = vaultKeyMaterialDataSource.initKeyMaterial(request)) {
                is VaultKeyMaterialRemoteResult.Success -> {
                    vaultKeyMaterialCache.save(
                        cachedVaultKeyMaterial = CachedVaultKeyMaterial(
                            kekEncMaster = request.kekEncMaster,
                            kekEncRecovery = request.kekEncRecovery,
                            kdfAlgorithm = request.kdfAlgorithm,
                            kdfSalt = request.kdfSalt,
                            kdfMemoryKib = KDF_MEMORY_KIB,
                            kdfIterations = KDF_ITERATIONS,
                            kdfParallelism = KDF_PARALLELISM,
                            kdfOutputLen = KDF_OUTPUT_LEN,
                            cryptoVersion = request.cryptoVersion,
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

    private fun wrapKek(
        kek: ByteArray,
        wrappingKey: ByteArray,
    ): ByteArray {
        val encrypted = cryptoEngine.encrypt(
            request = EncryptionRequest(
                plaintext = kek,
                keyMaterial = wrappingKey,
            ),
        )

        var offset = 0
        return ByteArray(
            ENVELOPE_VERSION_SIZE_BYTES +
                encrypted.iv.size +
                encrypted.ciphertext.size +
                encrypted.authTag.size,
        ).also { output ->
            output[offset] = KEY_WRAP_ENVELOPE_VERSION
            offset += ENVELOPE_VERSION_SIZE_BYTES

            encrypted.iv.copyInto(
                destination = output,
                destinationOffset = offset,
            )
            offset += encrypted.iv.size

            encrypted.ciphertext.copyInto(
                destination = output,
                destinationOffset = offset,
            )
            offset += encrypted.ciphertext.size

            encrypted.authTag.copyInto(
                destination = output,
                destinationOffset = offset,
            )
        }
    }

    private companion object {
        const val KDF_ALGORITHM = "argon2id"
        const val KDF_SALT_LENGTH_BYTES = 16
        const val KDF_MEMORY_KIB = 65536
        const val KDF_ITERATIONS = 3
        const val KDF_PARALLELISM = 1
        const val KDF_OUTPUT_LEN = 32
        const val CRYPTO_VERSION = "v1"
        const val KEY_LENGTH_BYTES = 32
        const val ENVELOPE_VERSION_SIZE_BYTES = 1
        const val KEY_WRAP_ENVELOPE_VERSION: Byte = 1
    }
}
