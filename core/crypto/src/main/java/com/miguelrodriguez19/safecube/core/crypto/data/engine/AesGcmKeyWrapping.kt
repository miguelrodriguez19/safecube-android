package com.miguelrodriguez19.safecube.core.crypto.data.engine

import com.miguelrodriguez19.safecube.core.crypto.domain.port.CryptoEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.model.DecryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.EncryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.EncryptionResult
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AES-GCM-based implementation of [KeyWrapping].
 *
 * The wrapped payload is encoded as a versioned envelope containing IV, ciphertext and auth tag.
 */
@Singleton
class AesGcmKeyWrapping @Inject constructor(
    private val cryptoEngine: CryptoEngine,
) : KeyWrapping {
    /**
     * Wraps [KeyWrapRequest.keyToWrap] with [KeyWrapRequest.wrappingKey].
     */
    override fun wrapKey(request: KeyWrapRequest): ByteArray = encodeEnvelope(
        encryptionResult = cryptoEngine.encrypt(
            request = EncryptionRequest(
                plaintext = request.keyToWrap,
                keyMaterial = request.wrappingKey,
                aad = request.aad,
            ),
        ),
    )

    /**
     * Unwraps [KeyUnwrapRequest.wrappedKey] with [KeyUnwrapRequest.wrappingKey].
     */
    override fun unwrapKey(request: KeyUnwrapRequest): ByteArray {
        val envelope = decodeEnvelope(request.wrappedKey)
        return cryptoEngine.decrypt(
            request = DecryptionRequest(
                ciphertext = envelope.ciphertext,
                keyMaterial = request.wrappingKey,
                iv = envelope.iv,
                aad = request.aad,
                authTag = envelope.authTag,
            ),
        )
    }

    /**
     * Encodes encryption output into a versioned wrapped-key envelope.
     */
    private fun encodeEnvelope(encryptionResult: EncryptionResult): ByteArray {
        var offset = 0
        return ByteArray(
            ENVELOPE_VERSION_SIZE_BYTES +
                encryptionResult.iv.size +
                encryptionResult.ciphertext.size +
                encryptionResult.authTag.size,
        ).also { output ->
            output[offset] = KEY_WRAP_ENVELOPE_VERSION
            offset += ENVELOPE_VERSION_SIZE_BYTES

            encryptionResult.iv.copyInto(
                destination = output,
                destinationOffset = offset,
            )
            offset += encryptionResult.iv.size

            encryptionResult.ciphertext.copyInto(
                destination = output,
                destinationOffset = offset,
            )
            offset += encryptionResult.ciphertext.size

            encryptionResult.authTag.copyInto(
                destination = output,
                destinationOffset = offset,
            )
        }
    }

    /**
     * Decodes a wrapped-key envelope.
     */
    private fun decodeEnvelope(envelope: ByteArray): DecodedEnvelope {
        require(envelope.size > MIN_ENVELOPE_LENGTH_BYTES) { "Wrapped key envelope is malformed." }
        require(envelope[0] == KEY_WRAP_ENVELOPE_VERSION) { "Wrapped key envelope version is not supported." }

        val ivStart = ENVELOPE_VERSION_SIZE_BYTES
        val ivEndExclusive = ivStart + IV_SIZE_BYTES
        val authTagStart = envelope.size - AUTH_TAG_SIZE_BYTES

        require(authTagStart > ivEndExclusive) { "Wrapped key envelope is malformed." }

        return DecodedEnvelope(
            iv = envelope.copyOfRange(ivStart, ivEndExclusive),
            ciphertext = envelope.copyOfRange(ivEndExclusive, authTagStart),
            authTag = envelope.copyOfRange(authTagStart, envelope.size),
        )
    }

    private data class DecodedEnvelope(
        val iv: ByteArray,
        val ciphertext: ByteArray,
        val authTag: ByteArray,
    )

    private companion object {
        const val ENVELOPE_VERSION_SIZE_BYTES = 1
        const val IV_SIZE_BYTES = 12
        const val AUTH_TAG_SIZE_BYTES = 16
        const val MIN_ENVELOPE_LENGTH_BYTES =
            ENVELOPE_VERSION_SIZE_BYTES + IV_SIZE_BYTES + AUTH_TAG_SIZE_BYTES
        const val KEY_WRAP_ENVELOPE_VERSION: Byte = 1
    }
}
