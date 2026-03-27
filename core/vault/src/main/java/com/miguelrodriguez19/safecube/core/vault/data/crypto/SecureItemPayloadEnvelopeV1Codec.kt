package com.miguelrodriguez19.safecube.core.vault.data.crypto

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SecureItemPayloadEnvelopeV1Codec @Inject constructor() {
    fun encode(
        logicalItemId: UUID,
        wrappedDek: ByteArray,
        nonce: ByteArray,
        ciphertext: ByteArray,
        authTag: ByteArray,
    ): ByteArray {
        require(wrappedDek.size <= UShort.MAX_VALUE.toInt()) {
            "wrappedDek exceeds payload envelope v1 maximum length."
        }
        require(nonce.size == NONCE_BYTES) { "nonce must be exactly 12 bytes." }
        require(authTag.size == AUTH_TAG_BYTES) { "authTag must be exactly 16 bytes." }
        require(ciphertext.isNotEmpty()) { "ciphertext must not be empty." }

        val output = ByteArray(
            ENVELOPE_VERSION_BYTES +
                LOGICAL_ITEM_ID_BYTES +
                WRAPPED_DEK_LENGTH_BYTES +
                wrappedDek.size +
                nonce.size +
                ciphertext.size +
                authTag.size,
        )
        var offset = 0

        output[offset] = ENVELOPE_VERSION
        offset += ENVELOPE_VERSION_BYTES

        uuidToBytes(logicalItemId).copyInto(output, destinationOffset = offset)
        offset += LOGICAL_ITEM_ID_BYTES

        output[offset] = (wrappedDek.size ushr 8).toByte()
        output[offset + 1] = wrappedDek.size.toByte()
        offset += WRAPPED_DEK_LENGTH_BYTES

        wrappedDek.copyInto(output, destinationOffset = offset)
        offset += wrappedDek.size

        nonce.copyInto(output, destinationOffset = offset)
        offset += nonce.size

        ciphertext.copyInto(output, destinationOffset = offset)
        offset += ciphertext.size

        authTag.copyInto(output, destinationOffset = offset)

        return output
    }

    fun decode(payload: ByteArray): DecodedSecureItemPayloadEnvelope {
        require(payload.size >= MIN_ENVELOPE_BYTES) { "SecureItem payload envelope is malformed." }
        require(payload[0] == ENVELOPE_VERSION) { "SecureItem payload envelope version is not supported." }

        val logicalItemIdStart = ENVELOPE_VERSION_BYTES
        val wrappedDekLengthStart = logicalItemIdStart + LOGICAL_ITEM_ID_BYTES
        val wrappedDekLength = (
            ((payload[wrappedDekLengthStart].toInt() and 0xFF) shl 8) or
                (payload[wrappedDekLengthStart + 1].toInt() and 0xFF)
            )
        val wrappedDekStart = wrappedDekLengthStart + WRAPPED_DEK_LENGTH_BYTES
        val wrappedDekEnd = wrappedDekStart + wrappedDekLength
        val nonceEnd = wrappedDekEnd + NONCE_BYTES
        val authTagStart = payload.size - AUTH_TAG_BYTES

        require(wrappedDekLength > 0) { "wrappedDek must not be empty." }
        require(wrappedDekEnd <= payload.size) { "wrappedDek length exceeds payload envelope bounds." }
        require(nonceEnd < authTagStart) { "SecureItem payload envelope ciphertext is malformed." }

        return DecodedSecureItemPayloadEnvelope(
            logicalItemId = bytesToUuid(payload, logicalItemIdStart),
            wrappedDek = payload.copyOfRange(wrappedDekStart, wrappedDekEnd),
            nonce = payload.copyOfRange(wrappedDekEnd, nonceEnd),
            ciphertext = payload.copyOfRange(nonceEnd, authTagStart),
            authTag = payload.copyOfRange(authTagStart, payload.size),
        )
    }

    private fun uuidToBytes(uuid: UUID): ByteArray = ByteBuffer.allocate(LOGICAL_ITEM_ID_BYTES)
        .order(ByteOrder.BIG_ENDIAN)
        .putLong(uuid.mostSignificantBits)
        .putLong(uuid.leastSignificantBits)
        .array()

    private fun bytesToUuid(source: ByteArray, offset: Int): UUID {
        val buffer = ByteBuffer.wrap(source, offset, LOGICAL_ITEM_ID_BYTES).order(ByteOrder.BIG_ENDIAN)
        return UUID(buffer.long, buffer.long)
    }

    private companion object {
        private const val ENVELOPE_VERSION: Byte = 0x01
        private const val ENVELOPE_VERSION_BYTES = 1
        private const val LOGICAL_ITEM_ID_BYTES = 16
        private const val WRAPPED_DEK_LENGTH_BYTES = 2
        private const val NONCE_BYTES = 12
        private const val AUTH_TAG_BYTES = 16
        private const val MIN_ENVELOPE_BYTES =
            ENVELOPE_VERSION_BYTES +
                LOGICAL_ITEM_ID_BYTES +
                WRAPPED_DEK_LENGTH_BYTES +
                NONCE_BYTES +
                AUTH_TAG_BYTES +
                1
    }
}

internal data class DecodedSecureItemPayloadEnvelope(
    val logicalItemId: UUID,
    val wrappedDek: ByteArray,
    val nonce: ByteArray,
    val ciphertext: ByteArray,
    val authTag: ByteArray,
)
