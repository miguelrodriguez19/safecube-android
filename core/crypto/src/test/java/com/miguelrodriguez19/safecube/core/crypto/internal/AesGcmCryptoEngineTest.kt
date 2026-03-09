package com.miguelrodriguez19.safecube.core.crypto.internal

import com.miguelrodriguez19.safecube.core.crypto.DecryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.EncryptionRequest
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test
import javax.crypto.AEADBadTagException

class AesGcmCryptoEngineTest {
    private val engine = AesGcmCryptoEngine()
    private val validKey = ByteArray(32) { index -> (index + 1).toByte() }

    @Test
    fun encryptThenDecrypt_roundtripSucceeds() {
        val key = ByteArray(32) { index -> index.toByte() }
        val plaintext = "safe cube payload".encodeToByteArray()
        val aad = "accountId:11111111-2222-3333-4444-555555555555|purpose:kek".encodeToByteArray()

        val encrypted = engine.encrypt(
            EncryptionRequest(
                plaintext = plaintext,
                keyMaterial = key,
                aad = aad,
            )
        )

        assertEquals(12, encrypted.iv.size)
        assertEquals(16, encrypted.authTag.size)
        assertFalse(encrypted.ciphertext.contentEquals(plaintext))

        val decrypted = engine.decrypt(
            DecryptionRequest(
                ciphertext = encrypted.ciphertext,
                keyMaterial = key,
                iv = encrypted.iv,
                aad = aad,
                authTag = encrypted.authTag,
            )
        )

        assertArrayEquals(plaintext, decrypted)
    }

    @Test
    fun decrypt_failsWhenAadIsIncorrect() {
        val plaintext = "payload for aad validation".encodeToByteArray()

        val encrypted = engine.encrypt(
            EncryptionRequest(
                plaintext = plaintext,
                keyMaterial = validKey,
                aad = "accountId:abc|purpose:kek".encodeToByteArray(),
            )
        )

        assertThrows(AEADBadTagException::class.java) {
            engine.decrypt(
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
    fun encryptThenDecrypt_roundtripWithoutAadSucceeds() {
        val plaintext = "payload without aad".encodeToByteArray()

        val encrypted = engine.encrypt(
            EncryptionRequest(
                plaintext = plaintext,
                keyMaterial = validKey,
                aad = null,
            ),
        )

        val decrypted = engine.decrypt(
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
    fun encrypt_throwsWhenKeyLengthIsInvalid() {
        assertThrows(IllegalArgumentException::class.java) {
            engine.encrypt(
                EncryptionRequest(
                    plaintext = "x".encodeToByteArray(),
                    keyMaterial = ByteArray(16),
                    aad = null,
                ),
            )
        }
    }

    @Test
    fun decrypt_throwsWhenInputLengthsAreInvalid() {
        val encrypted = engine.encrypt(
            EncryptionRequest(
                plaintext = "x".encodeToByteArray(),
                keyMaterial = validKey,
                aad = null,
            ),
        )

        assertThrows(IllegalArgumentException::class.java) {
            engine.decrypt(
                DecryptionRequest(
                    ciphertext = encrypted.ciphertext,
                    keyMaterial = ByteArray(16),
                    iv = encrypted.iv,
                    aad = null,
                    authTag = encrypted.authTag,
                ),
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            engine.decrypt(
                DecryptionRequest(
                    ciphertext = encrypted.ciphertext,
                    keyMaterial = validKey,
                    iv = ByteArray(8),
                    aad = null,
                    authTag = encrypted.authTag,
                ),
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            engine.decrypt(
                DecryptionRequest(
                    ciphertext = encrypted.ciphertext,
                    keyMaterial = validKey,
                    iv = encrypted.iv,
                    aad = null,
                    authTag = ByteArray(8),
                ),
            )
        }
    }
}
