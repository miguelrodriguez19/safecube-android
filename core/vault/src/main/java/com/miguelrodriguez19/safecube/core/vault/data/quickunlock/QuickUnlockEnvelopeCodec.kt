package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

/** Binary v1 envelope: [0x01][12-byte nonce][ciphertext||16-byte GCM tag]. */
internal class QuickUnlockEnvelopeCodec @javax.inject.Inject constructor() {
    fun encode(nonce: ByteArray, ciphertextAndTag: ByteArray): ByteArray? {
        if (nonce.size != NONCE_LENGTH || ciphertextAndTag.size != CIPHERTEXT_AND_TAG_LENGTH) return null
        return byteArrayOf(VERSION) + nonce + ciphertextAndTag
    }

    fun decode(envelope: ByteArray): QuickUnlockEnvelopeDecodeResult {
        if (envelope.size != ENVELOPE_LENGTH || envelope.firstOrNull() != VERSION) {
            return QuickUnlockEnvelopeDecodeResult.Invalid
        }
        return QuickUnlockEnvelopeDecodeResult.Valid(
            nonce = envelope.copyOfRange(1, 1 + NONCE_LENGTH),
            ciphertextAndTag = envelope.copyOfRange(1 + NONCE_LENGTH, envelope.size),
        )
    }

    private companion object {
        const val VERSION: Byte = 0x01
        const val NONCE_LENGTH = 12
        const val TAG_LENGTH = 16
        const val KEK_LENGTH = 32
        const val CIPHERTEXT_AND_TAG_LENGTH = KEK_LENGTH + TAG_LENGTH
        const val ENVELOPE_LENGTH = 1 + NONCE_LENGTH + CIPHERTEXT_AND_TAG_LENGTH
    }
}

internal sealed interface QuickUnlockEnvelopeDecodeResult {
    data class Valid(
        val nonce: ByteArray,
        val ciphertextAndTag: ByteArray,
    ) : QuickUnlockEnvelopeDecodeResult

    data object Invalid : QuickUnlockEnvelopeDecodeResult
}
