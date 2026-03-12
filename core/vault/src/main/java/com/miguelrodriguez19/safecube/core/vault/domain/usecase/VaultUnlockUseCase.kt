package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.crypto.CryptoEngine
import com.miguelrodriguez19.safecube.core.crypto.DecryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.KdfRequest
import com.miguelrodriguez19.safecube.core.vault.data.local.CachedVaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.data.local.VaultKeyMaterialCache
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

data class UnlockedKeyring(
    val kek: ByteArray,
)

sealed interface VaultUnlockResult {
    data class Unlocked(
        val keyring: UnlockedKeyring,
    ) : VaultUnlockResult

    data class Error(
        val reason: VaultUnlockError,
    ) : VaultUnlockResult
}

sealed interface VaultUnlockError {
    data object KeyMaterialUnavailable : VaultUnlockError

    data object InvalidCredential : VaultUnlockError

    data object InvalidCachedKeyMaterial : VaultUnlockError
}

@Singleton
class VaultUnlockUseCase @Inject constructor(
    private val vaultKeyMaterialCache: VaultKeyMaterialCache,
    private val kdfEngine: KdfEngine,
    private val cryptoEngine: CryptoEngine,
) {
    fun unlockWithPassphrase(passphrase: String): VaultUnlockResult {
        val cachedVaultKeyMaterial = vaultKeyMaterialCache.get()
            ?: return VaultUnlockResult.Error(VaultUnlockError.KeyMaterialUnavailable)
        val passphraseBytes = passphrase.toByteArray(StandardCharsets.UTF_8)
        var masterKey = byteArrayOf()

        return try {
            masterKey = deriveMasterKey(
                passphraseBytes = passphraseBytes,
                cachedVaultKeyMaterial = cachedVaultKeyMaterial,
            )
            val kek = unwrapKek(
                envelope = cachedVaultKeyMaterial.kekEncMaster,
                wrappingKey = masterKey,
            )
            VaultUnlockResult.Unlocked(
                keyring = UnlockedKeyring(kek = kek),
            )
        } catch (_: MalformedEnvelopeException) {
            VaultUnlockResult.Error(VaultUnlockError.InvalidCachedKeyMaterial)
        } catch (_: IllegalArgumentException) {
            VaultUnlockResult.Error(VaultUnlockError.InvalidCachedKeyMaterial)
        } catch (_: Throwable) {
            VaultUnlockResult.Error(VaultUnlockError.InvalidCredential)
        } finally {
            passphraseBytes.fill(0)
            masterKey.fill(0)
        }
    }

    fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockResult {
        val cachedVaultKeyMaterial = vaultKeyMaterialCache.get()
            ?: return VaultUnlockResult.Error(VaultUnlockError.KeyMaterialUnavailable)

        val recoveryKeyCopy = recoveryKey.copyOf()

        return try {
            val kek = unwrapKek(
                envelope = cachedVaultKeyMaterial.kekEncRecovery,
                wrappingKey = recoveryKeyCopy,
            )
            VaultUnlockResult.Unlocked(
                keyring = UnlockedKeyring(kek = kek),
            )
        } catch (malformedEnvelopeException: MalformedEnvelopeException) {
            VaultUnlockResult.Error(VaultUnlockError.InvalidCachedKeyMaterial)
        } catch (illegalArgumentException: IllegalArgumentException) {
            VaultUnlockResult.Error(VaultUnlockError.InvalidCachedKeyMaterial)
        } catch (throwable: Throwable) {
            VaultUnlockResult.Error(VaultUnlockError.InvalidCredential)
        } finally {
            recoveryKeyCopy.fill(0)
        }
    }

    private fun deriveMasterKey(
        passphraseBytes: ByteArray,
        cachedVaultKeyMaterial: CachedVaultKeyMaterial,
    ): ByteArray = kdfEngine.deriveKey(
        request = KdfRequest(
            secret = passphraseBytes,
            salt = cachedVaultKeyMaterial.kdfSalt,
            iterations = cachedVaultKeyMaterial.kdfIterations,
            memoryKib = cachedVaultKeyMaterial.kdfMemoryKib,
            parallelism = cachedVaultKeyMaterial.kdfParallelism,
            outputLengthBytes = cachedVaultKeyMaterial.kdfOutputLen,
        ),
    )

    private fun unwrapKek(
        envelope: ByteArray,
        wrappingKey: ByteArray,
    ): ByteArray {
        val parsedEnvelope = parseEnvelope(envelope)
        return cryptoEngine.decrypt(
            request = DecryptionRequest(
                ciphertext = parsedEnvelope.ciphertext,
                keyMaterial = wrappingKey,
                iv = parsedEnvelope.iv,
                authTag = parsedEnvelope.authTag,
            ),
        )
    }

    private fun parseEnvelope(envelope: ByteArray): ParsedEnvelope {
        if (envelope.size <= MIN_ENVELOPE_LENGTH_BYTES) {
            throw MalformedEnvelopeException()
        }
        if (envelope[0] != KEY_WRAP_ENVELOPE_VERSION) {
            throw MalformedEnvelopeException()
        }

        val ivStart = ENVELOPE_VERSION_SIZE_BYTES
        val ivEndExclusive = ivStart + IV_SIZE_BYTES
        val authTagStart = envelope.size - AUTH_TAG_SIZE_BYTES

        if (authTagStart <= ivEndExclusive) {
            throw MalformedEnvelopeException()
        }

        return ParsedEnvelope(
            iv = envelope.copyOfRange(ivStart, ivEndExclusive),
            ciphertext = envelope.copyOfRange(ivEndExclusive, authTagStart),
            authTag = envelope.copyOfRange(authTagStart, envelope.size),
        )
    }

    private data class ParsedEnvelope(
        val iv: ByteArray,
        val ciphertext: ByteArray,
        val authTag: ByteArray,
    )

    private class MalformedEnvelopeException : IllegalStateException()

    private companion object {
        const val ENVELOPE_VERSION_SIZE_BYTES = 1
        const val IV_SIZE_BYTES = 12
        const val AUTH_TAG_SIZE_BYTES = 16
        const val MIN_ENVELOPE_LENGTH_BYTES =
            ENVELOPE_VERSION_SIZE_BYTES + IV_SIZE_BYTES + AUTH_TAG_SIZE_BYTES
        const val KEY_WRAP_ENVELOPE_VERSION: Byte = 1
    }
}
