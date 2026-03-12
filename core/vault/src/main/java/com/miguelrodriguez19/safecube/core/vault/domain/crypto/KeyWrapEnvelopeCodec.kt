package com.miguelrodriguez19.safecube.core.vault.domain.crypto

import com.miguelrodriguez19.safecube.core.crypto.EncryptionResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KeyWrapEnvelopeCodec @Inject constructor() {
    fun encode(encryptionResult: EncryptionResult): ByteArray {
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

    fun decode(envelope: ByteArray): DecodedKeyWrapEnvelope {
        if (envelope.size <= MIN_ENVELOPE_LENGTH_BYTES) {
            throw MalformedKeyWrapEnvelopeException()
        }
        if (envelope[0] != KEY_WRAP_ENVELOPE_VERSION) {
            throw MalformedKeyWrapEnvelopeException()
        }

        val ivStart = ENVELOPE_VERSION_SIZE_BYTES
        val ivEndExclusive = ivStart + IV_SIZE_BYTES
        val authTagStart = envelope.size - AUTH_TAG_SIZE_BYTES

        if (authTagStart <= ivEndExclusive) {
            throw MalformedKeyWrapEnvelopeException()
        }

        return DecodedKeyWrapEnvelope(
            iv = envelope.copyOfRange(ivStart, ivEndExclusive),
            ciphertext = envelope.copyOfRange(ivEndExclusive, authTagStart),
            authTag = envelope.copyOfRange(authTagStart, envelope.size),
        )
    }

    private companion object {
        const val ENVELOPE_VERSION_SIZE_BYTES = 1
        const val IV_SIZE_BYTES = 12
        const val AUTH_TAG_SIZE_BYTES = 16
        const val MIN_ENVELOPE_LENGTH_BYTES =
            ENVELOPE_VERSION_SIZE_BYTES + IV_SIZE_BYTES + AUTH_TAG_SIZE_BYTES
        const val KEY_WRAP_ENVELOPE_VERSION: Byte = 1
    }
}

data class DecodedKeyWrapEnvelope(
    val iv: ByteArray,
    val ciphertext: ByteArray,
    val authTag: ByteArray,
)

class MalformedKeyWrapEnvelopeException : IllegalStateException()
