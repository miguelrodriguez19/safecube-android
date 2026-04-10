package com.miguelrodriguez19.safecube.core.crypto.data.engine

import com.miguelrodriguez19.safecube.core.crypto.domain.model.DecryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.EncryptionRequest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test
import javax.crypto.AEADBadTagException

class AesGcmCryptoEngineTest {
    private val target = AesGcmCryptoEngine()
    private val validKey = ByteArray(32) { index -> (index + 1).toByte() }

    @Test
    fun `encrypt and decrypt when request is valid then returns original plaintext`() {
        val key = ByteArray(32) { index -> index.toByte() }
        val plaintext = "safe cube payload".encodeToByteArray()
        val aad = "accountId:11111111-2222-3333-4444-555555555555|purpose:kek".encodeToByteArray()

        val encrypted = target.encrypt(
            EncryptionRequest(
                plaintext = plaintext,
                keyMaterial = key,
                aad = aad,
            )
        )

        val decrypted = target.decrypt(
            DecryptionRequest(
                ciphertext = encrypted.ciphertext,
                keyMaterial = key,
                iv = encrypted.iv,
                aad = aad,
                authTag = encrypted.authTag,
            )
        )

        assertEquals(12, encrypted.iv.size)
        assertEquals(16, encrypted.authTag.size)
        assertFalse(encrypted.ciphertext.contentEquals(plaintext))
        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun `decrypt when aad is incorrect then throws bad tag exception`() {
        val plaintext = "payload for aad validation".encodeToByteArray()

        val encrypted = target.encrypt(
            EncryptionRequest(
                plaintext = plaintext,
                keyMaterial = validKey,
                aad = "accountId:abc|purpose:kek".encodeToByteArray(),
            )
        )

        assertThrows(AEADBadTagException::class.java) {
            target.decrypt(
                DecryptionRequest(
                    ciphertext = encrypted.ciphertext,
                    keyMaterial = validKey,
                    iv = encrypted.iv,
                    aad = "accountId:abc|purpose:wrong".encodeToByteArray(),
                    authTag = encrypted.authTag,
                )
            )
        }
    }

    @Test
    fun `encrypt and decrypt when aad is absent then returns original plaintext`() {
        val plaintext = "payload without aad".encodeToByteArray()

        val encrypted = target.encrypt(
            EncryptionRequest(
                plaintext = plaintext,
                keyMaterial = validKey,
                aad = null,
            ),
        )

        val decrypted = target.decrypt(
            DecryptionRequest(
                ciphertext = encrypted.ciphertext,
                keyMaterial = validKey,
                iv = encrypted.iv,
                aad = null,
                authTag = encrypted.authTag,
            ),
        )

        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun `encrypt when key length is invalid then throws illegal argument exception`() {
        val request = EncryptionRequest(
            plaintext = "x".encodeToByteArray(),
            keyMaterial = ByteArray(16),
            aad = null,
        )

        assertThrows(IllegalArgumentException::class.java) {
            target.encrypt(request)
        }
    }

    @Test
    fun `decrypt when key length is invalid then throws illegal argument exception`() {
        val encrypted = target.encrypt(
            EncryptionRequest(
                plaintext = "x".encodeToByteArray(),
                keyMaterial = validKey,
                aad = null,
            ),
        )
        val request = DecryptionRequest(
            ciphertext = encrypted.ciphertext,
            keyMaterial = ByteArray(16),
            iv = encrypted.iv,
            aad = null,
            authTag = encrypted.authTag,
        )

        assertThrows(IllegalArgumentException::class.java) {
            target.decrypt(request)
        }
    }

    @Test
    fun `decrypt when iv length is invalid then throws illegal argument exception`() {
        val encrypted = target.encrypt(
            EncryptionRequest(
                plaintext = "x".encodeToByteArray(),
                keyMaterial = validKey,
                aad = null,
            ),
        )
        val request = DecryptionRequest(
            ciphertext = encrypted.ciphertext,
            keyMaterial = validKey,
            iv = ByteArray(8),
            aad = null,
            authTag = encrypted.authTag,
        )

        assertThrows(IllegalArgumentException::class.java) {
            target.decrypt(request)
        }
    }

    @Test
    fun `decrypt when auth tag length is invalid then throws illegal argument exception`() {
        val encrypted = target.encrypt(
            EncryptionRequest(
                plaintext = "x".encodeToByteArray(),
                keyMaterial = validKey,
                aad = null,
            ),
        )
        val request = DecryptionRequest(
            ciphertext = encrypted.ciphertext,
            keyMaterial = validKey,
            iv = encrypted.iv,
            aad = null,
            authTag = ByteArray(8),
        )

        assertThrows(IllegalArgumentException::class.java) {
            target.decrypt(request)
        }
    }
}
