package com.miguelrodriguez19.safecube.core.crypto.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CryptoRequestModelsTest {
    @Test
    fun `encryption request when compared with equivalent value then returns equal and same hash code`() {
        // Arrange
        val target = EncryptionRequest(
            plaintext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            aad = byteArrayOf(7, 8),
        )
        val equivalent = EncryptionRequest(
            plaintext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            aad = byteArrayOf(7, 8),
        )

        // Act

        // Assert
        assertEquals(target, target)
        assertNotEquals(target, Any())
        assertEquals(target, equivalent)
        assertEquals(target.hashCode(), target.copy().hashCode())
    }

    @Test
    fun `encryption request when plaintext changes then returns not equal`() {
        // Arrange
        val target = EncryptionRequest(
            plaintext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            aad = byteArrayOf(7, 8),
        )

        // Act
        val result = target == target.copy(plaintext = byteArrayOf(9))

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun `encryption request when key material changes then returns not equal`() {
        // Arrange
        val target = EncryptionRequest(
            plaintext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            aad = byteArrayOf(7, 8),
        )

        // Act
        val result = target == target.copy(keyMaterial = byteArrayOf(9))

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun `encryption request when aad changes then returns not equal`() {
        // Arrange
        val target = EncryptionRequest(
            plaintext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            aad = byteArrayOf(7, 8),
        )

        // Act
        val result = target == target.copy(aad = byteArrayOf(9))

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun `encryption request when aad is null on both values then returns equal`() {
        // Arrange
        val target = EncryptionRequest(
            plaintext = byteArrayOf(1),
            keyMaterial = byteArrayOf(2),
            aad = null,
        )
        val equivalent = EncryptionRequest(
            plaintext = byteArrayOf(1),
            keyMaterial = byteArrayOf(2),
            aad = null,
        )

        // Act

        // Assert
        assertEquals(target, equivalent)
        assertEquals(target.hashCode(), target.copy().hashCode())
    }

    @Test
    fun `decryption request when compared with equivalent value then returns equal and same hash code`() {
        // Arrange
        val target = DecryptionRequest(
            ciphertext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            iv = byteArrayOf(7, 8, 9),
            aad = byteArrayOf(10, 11),
            authTag = byteArrayOf(12, 13),
        )
        val equivalent = DecryptionRequest(
            ciphertext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            iv = byteArrayOf(7, 8, 9),
            aad = byteArrayOf(10, 11),
            authTag = byteArrayOf(12, 13),
        )

        // Act

        // Assert
        assertEquals(target, target)
        assertNotEquals(target, Any())
        assertEquals(target, equivalent)
        assertEquals(target.hashCode(), target.copy().hashCode())
    }

    @Test
    fun `decryption request when ciphertext changes then returns not equal`() {
        // Arrange
        val target = DecryptionRequest(
            ciphertext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            iv = byteArrayOf(7, 8, 9),
            aad = byteArrayOf(10, 11),
            authTag = byteArrayOf(12, 13),
        )

        // Act
        val result = target == target.copy(ciphertext = byteArrayOf(99))

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun `decryption request when key material changes then returns not equal`() {
        // Arrange
        val target = DecryptionRequest(
            ciphertext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            iv = byteArrayOf(7, 8, 9),
            aad = byteArrayOf(10, 11),
            authTag = byteArrayOf(12, 13),
        )

        // Act
        val result = target == target.copy(keyMaterial = byteArrayOf(99))

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun `decryption request when iv changes then returns not equal`() {
        // Arrange
        val target = DecryptionRequest(
            ciphertext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            iv = byteArrayOf(7, 8, 9),
            aad = byteArrayOf(10, 11),
            authTag = byteArrayOf(12, 13),
        )

        // Act
        val result = target == target.copy(iv = byteArrayOf(99))

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun `decryption request when aad changes then returns not equal`() {
        // Arrange
        val target = DecryptionRequest(
            ciphertext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            iv = byteArrayOf(7, 8, 9),
            aad = byteArrayOf(10, 11),
            authTag = byteArrayOf(12, 13),
        )

        // Act
        val result = target == target.copy(aad = byteArrayOf(99))

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun `decryption request when auth tag changes then returns not equal`() {
        // Arrange
        val target = DecryptionRequest(
            ciphertext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            iv = byteArrayOf(7, 8, 9),
            aad = byteArrayOf(10, 11),
            authTag = byteArrayOf(12, 13),
        )

        // Act
        val result = target == target.copy(authTag = byteArrayOf(99))

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun `decryption request when aad is null on both values then returns equal`() {
        // Arrange
        val target = DecryptionRequest(
            ciphertext = byteArrayOf(1),
            keyMaterial = byteArrayOf(2),
            iv = byteArrayOf(3),
            aad = null,
            authTag = byteArrayOf(4),
        )
        val equivalent = DecryptionRequest(
            ciphertext = byteArrayOf(1),
            keyMaterial = byteArrayOf(2),
            iv = byteArrayOf(3),
            aad = null,
            authTag = byteArrayOf(4),
        )

        // Act

        // Assert
        assertEquals(target, equivalent)
        assertEquals(target.hashCode(), target.copy().hashCode())
    }

    @Test
    fun `encryption result when compared with equivalent value then returns equal and same hash code`() {
        // Arrange
        val target = EncryptionResult(
            ciphertext = byteArrayOf(1, 2),
            iv = byteArrayOf(3, 4),
            authTag = byteArrayOf(5, 6),
        )
        val equivalent = EncryptionResult(
            ciphertext = byteArrayOf(1, 2),
            iv = byteArrayOf(3, 4),
            authTag = byteArrayOf(5, 6),
        )

        // Act

        // Assert
        assertEquals(target, target)
        assertNotEquals(target, Any())
        assertEquals(target, equivalent)
        assertEquals(target.hashCode(), target.copy().hashCode())
    }

    @Test
    fun `encryption result when ciphertext changes then returns not equal`() {
        // Arrange
        val target = EncryptionResult(
            ciphertext = byteArrayOf(1, 2),
            iv = byteArrayOf(3, 4),
            authTag = byteArrayOf(5, 6),
        )

        // Act
        val result = target == target.copy(ciphertext = byteArrayOf(9))

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun `encryption result when iv changes then returns not equal`() {
        // Arrange
        val target = EncryptionResult(
            ciphertext = byteArrayOf(1, 2),
            iv = byteArrayOf(3, 4),
            authTag = byteArrayOf(5, 6),
        )

        // Act
        val result = target == target.copy(iv = byteArrayOf(9))

        // Assert
        assertEquals(false, result)
    }

    @Test
    fun `encryption result when auth tag changes then returns not equal`() {
        // Arrange
        val target = EncryptionResult(
            ciphertext = byteArrayOf(1, 2),
            iv = byteArrayOf(3, 4),
            authTag = byteArrayOf(5, 6),
        )

        // Act
        val result = target == target.copy(authTag = byteArrayOf(9))

        // Assert
        assertEquals(false, result)
    }
}
