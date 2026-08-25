package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class QuickUnlockEnvelopeCodecTest {
    private val target = QuickUnlockEnvelopeCodec()

    @Test
    fun `encode then decode preserves v1 nonce and ciphertext tag`() {
        val nonce = ByteArray(12) { it.toByte() }
        val ciphertextAndTag = ByteArray(48) { (it + 12).toByte() }

        val encoded = target.encode(nonce, ciphertextAndTag)
        val decoded = target.decode(requireNotNull(encoded)) as QuickUnlockEnvelopeDecodeResult.Valid

        assertEquals(0x01, encoded.first().toInt())
        assertArrayEquals(nonce, decoded.nonce)
        assertArrayEquals(ciphertextAndTag, decoded.ciphertextAndTag)
    }

    @Test
    fun `decode whenVersionLengthOrTagIsInvalid returnsInvalid`() {
        assertEquals(QuickUnlockEnvelopeDecodeResult.Invalid, target.decode(byteArrayOf(0x02)))
        assertEquals(QuickUnlockEnvelopeDecodeResult.Invalid, target.decode(ByteArray(60)))
        assertEquals(QuickUnlockEnvelopeDecodeResult.Invalid, target.decode(ByteArray(0)))
    }

    @Test
    fun `encode rejects nonce and ciphertext lengths other than a 32 byte kek envelope`() {
        assertNull(target.encode(ByteArray(11), ByteArray(48)))
        assertNull(target.encode(ByteArray(12), ByteArray(47)))
        assertNull(target.encode(ByteArray(12), ByteArray(49)))
    }
}
