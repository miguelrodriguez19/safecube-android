package com.miguelrodriguez19.safecube.core.crypto.internal

import com.miguelrodriguez19.safecube.core.crypto.KdfRequest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class Argon2KdfEngineTest {
    private val engine = Argon2KdfEngine()

    @Test
    fun deriveKey_isDeterministic_forSameInputAndParams() {
        val request = fixedRequest()

        val first = engine.deriveKey(request)
        val second = engine.deriveKey(request)

        assertArrayEquals(first, second)
        assertEquals(EXPECTED_OUTPUT_HEX, first.toHex())
    }

    @Test
    fun deriveKey_changesWhenSaltChanges() {
        val requestA = fixedRequest(saltHex = "00112233445566778899aabbccddeeff")
        val requestB = fixedRequest(saltHex = "ffeeddccbbaa99887766554433221100")

        val outputA = engine.deriveKey(requestA)
        val outputB = engine.deriveKey(requestB)

        assertFalse(outputA.contentEquals(outputB))
    }

    private fun fixedRequest(saltHex: String = "00112233445566778899aabbccddeeff"): KdfRequest {
        return KdfRequest(
            secret = "correct horse battery staple".encodeToByteArray(),
            salt = saltHex.hexToBytes(),
            iterations = 3,
            memoryKib = 65_536,
            parallelism = 1,
            outputLengthBytes = 32,
            contextInfo = "accountId:11111111-2222-3333-4444-555555555555|purpose:kek".encodeToByteArray(),
        )
    }

    private fun ByteArray.toHex(): String = joinToString(separator = "") { byte ->
        "%02x".format(byte)
    }

    private fun String.hexToBytes(): ByteArray {
        require(length % 2 == 0) { "Hex input length must be even." }

        return chunked(2).map { chunk ->
            chunk.toInt(16).toByte()
        }.toByteArray()
    }

    companion object {
        private const val EXPECTED_OUTPUT_HEX = "acd8973486ac61e896ca8fc47958655ef9ac9a5162f00d567a91a51a50872c9e"
    }
}
