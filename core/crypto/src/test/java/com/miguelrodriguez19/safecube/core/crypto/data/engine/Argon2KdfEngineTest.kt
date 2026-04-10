package com.miguelrodriguez19.safecube.core.crypto.data.engine

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test

class Argon2KdfEngineTest {
    private val target = Argon2KdfEngine()

    @Test
    fun `derive key when input and params are the same then returns deterministic output`() {
        // Arrange
        val request = createRequest()

        // Act
        val first = target.deriveKey(request)
        val second = target.deriveKey(request)

        // Assert
        assertArrayEquals(first, second)
        assertEquals(EXPECTED_OUTPUT_HEX, first.toHex())
    }

    @Test
    fun `derive key when salt changes then returns different output`() {
        // Arrange
        val requestA = createRequest(saltHex = "00112233445566778899aabbccddeeff")
        val requestB = createRequest(saltHex = "ffeeddccbbaa99887766554433221100")

        // Act
        val outputA = target.deriveKey(requestA)
        val outputB = target.deriveKey(requestB)

        // Assert
        assertFalse(outputA.contentEquals(outputB))
    }

    @Test
    fun `derive key when context info is absent then returns deterministic output`() {
        // Arrange
        val request = createRequest().copy(contextInfo = null)

        // Act
        val first = target.deriveKey(
            request,
        )
        val second = target.deriveKey(
            request,
        )

        // Assert
        assertArrayEquals(first, second)
        assertEquals(32, first.size)
    }

    @Test
    fun `derive key when secret is empty then throws illegal argument exception`() {
        // Arrange
        val request = createRequest().copy(secret = byteArrayOf())

        // Act
        // Assert
        assertThrows(IllegalArgumentException::class.java) {
            target.deriveKey(request)
        }
    }

    @Test
    fun `derive key when salt is empty then throws illegal argument exception`() {
        // Arrange
        val request = createRequest().copy(salt = byteArrayOf())

        // Act
        // Assert
        assertThrows(IllegalArgumentException::class.java) {
            target.deriveKey(request)
        }
    }

    @Test
    fun `derive key when output length is zero then throws illegal argument exception`() {
        // Arrange
        val request = createRequest().copy(outputLengthBytes = 0)

        // Act
        // Assert
        assertThrows(IllegalArgumentException::class.java) {
            target.deriveKey(request)
        }
    }

    @Test
    fun `derive key when iterations are zero then throws illegal argument exception`() {
        // Arrange
        val request = createRequest().copy(iterations = 0)

        // Act
        // Assert
        assertThrows(IllegalArgumentException::class.java) {
            target.deriveKey(request)
        }
    }

    @Test
    fun `derive key when memory is zero then throws illegal argument exception`() {
        // Arrange
        val request = createRequest().copy(memoryKib = 0)

        // Act
        // Assert
        assertThrows(IllegalArgumentException::class.java) {
            target.deriveKey(request)
        }
    }

    @Test
    fun `derive key when parallelism is zero then throws illegal argument exception`() {
        // Arrange
        val request = createRequest().copy(parallelism = 0)

        // Act
        // Assert
        assertThrows(IllegalArgumentException::class.java) {
            target.deriveKey(request)
        }
    }

    private fun createRequest(saltHex: String = "00112233445566778899aabbccddeeff"): KdfRequest {
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
