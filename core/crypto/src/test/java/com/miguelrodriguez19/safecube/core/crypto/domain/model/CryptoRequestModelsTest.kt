package com.miguelrodriguez19.safecube.core.crypto.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class CryptoRequestModelsTest {
    @Test
    fun `encryption request equals and hashcode cover branches`() {
        val base = EncryptionRequest(
            plaintext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            aad = byteArrayOf(7, 8),
        )

        assertEquals(base, base)
        assertNotEquals(base, Any())
        assertEquals(
            base,
            EncryptionRequest(
                plaintext = byteArrayOf(1, 2, 3),
                keyMaterial = byteArrayOf(4, 5, 6),
                aad = byteArrayOf(7, 8),
            ),
        )
        assertEquals(base.hashCode(), base.copy().hashCode())

        assertNotEquals(base, base.copy(plaintext = byteArrayOf(9)))
        assertNotEquals(base, base.copy(keyMaterial = byteArrayOf(9)))
        assertNotEquals(base, base.copy(aad = byteArrayOf(9)))

        val nullAad = EncryptionRequest(
            plaintext = byteArrayOf(1),
            keyMaterial = byteArrayOf(2),
            aad = null,
        )
        assertEquals(
            nullAad,
            EncryptionRequest(
                plaintext = byteArrayOf(1),
                keyMaterial = byteArrayOf(2),
                aad = null,
            ),
        )
        assertEquals(nullAad.hashCode(), nullAad.copy().hashCode())
    }

    @Test
    fun `decryption request equals and hashcode cover branches`() {
        val base = DecryptionRequest(
            ciphertext = byteArrayOf(1, 2, 3),
            keyMaterial = byteArrayOf(4, 5, 6),
            iv = byteArrayOf(7, 8, 9),
            aad = byteArrayOf(10, 11),
            authTag = byteArrayOf(12, 13),
        )

        assertEquals(base, base)
        assertNotEquals(base, Any())
        assertEquals(
            base,
            DecryptionRequest(
                ciphertext = byteArrayOf(1, 2, 3),
                keyMaterial = byteArrayOf(4, 5, 6),
                iv = byteArrayOf(7, 8, 9),
                aad = byteArrayOf(10, 11),
                authTag = byteArrayOf(12, 13),
            ),
        )
        assertEquals(base.hashCode(), base.copy().hashCode())

        assertNotEquals(base, base.copy(ciphertext = byteArrayOf(99)))
        assertNotEquals(base, base.copy(keyMaterial = byteArrayOf(99)))
        assertNotEquals(base, base.copy(iv = byteArrayOf(99)))
        assertNotEquals(base, base.copy(aad = byteArrayOf(99)))
        assertNotEquals(base, base.copy(authTag = byteArrayOf(99)))

        val nullAad = DecryptionRequest(
            ciphertext = byteArrayOf(1),
            keyMaterial = byteArrayOf(2),
            iv = byteArrayOf(3),
            aad = null,
            authTag = byteArrayOf(4),
        )
        assertEquals(
            nullAad,
            DecryptionRequest(
                ciphertext = byteArrayOf(1),
                keyMaterial = byteArrayOf(2),
                iv = byteArrayOf(3),
                aad = null,
                authTag = byteArrayOf(4),
            ),
        )
        assertEquals(nullAad.hashCode(), nullAad.copy().hashCode())
    }

    @Test
    fun `encryption result equals and hashcode cover branches`() {
        val base = EncryptionResult(
            ciphertext = byteArrayOf(1, 2),
            iv = byteArrayOf(3, 4),
            authTag = byteArrayOf(5, 6),
        )

        assertEquals(base, base)
        assertNotEquals(base, Any())
        assertEquals(
            base,
            EncryptionResult(
                ciphertext = byteArrayOf(1, 2),
                iv = byteArrayOf(3, 4),
                authTag = byteArrayOf(5, 6),
            ),
        )
        assertEquals(base.hashCode(), base.copy().hashCode())

        assertNotEquals(base, base.copy(ciphertext = byteArrayOf(9)))
        assertNotEquals(base, base.copy(iv = byteArrayOf(9)))
        assertNotEquals(base, base.copy(authTag = byteArrayOf(9)))
    }
}
