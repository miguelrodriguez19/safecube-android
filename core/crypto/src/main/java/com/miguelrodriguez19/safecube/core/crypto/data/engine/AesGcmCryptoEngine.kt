package com.miguelrodriguez19.safecube.core.crypto.data.engine

import com.miguelrodriguez19.safecube.core.crypto.domain.port.CryptoEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.model.DecryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.EncryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.EncryptionResult
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * AES-256-GCM implementation of [CryptoEngine].
 */
@Singleton
class AesGcmCryptoEngine @Inject constructor() : CryptoEngine {
    private val secureRandom = SecureRandom()

    private companion object {
        private const val KEY_ALGORITHM = "AES"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val KEY_SIZE_BYTES = 32
        private const val NONCE_SIZE_BYTES = 12
        private const val TAG_SIZE_BYTES = 16
        private const val TAG_SIZE_BITS = 128
    }

    /**
     * Encrypts plaintext with AES-256-GCM.
     *
     * @param request Encryption request.
     * @return Encryption result.
     */
    override fun encrypt(request: EncryptionRequest): EncryptionResult {
        require(request.keyMaterial.size == KEY_SIZE_BYTES) {
            "Encryption key material must be exactly 32 bytes for AES-256-GCM."
        }

        val keyCopy = request.keyMaterial.copyOf()
        val iv = ByteArray(NONCE_SIZE_BYTES).also(secureRandom::nextBytes)

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val key = SecretKeySpec(keyCopy, KEY_ALGORITHM)
            val spec = GCMParameterSpec(TAG_SIZE_BITS, iv)
            cipher.init(Cipher.ENCRYPT_MODE, key, spec)
            request.aad?.let(cipher::updateAAD)

            val cipherAndTag = cipher.doFinal(request.plaintext)
            val splitIndex = cipherAndTag.size - TAG_SIZE_BYTES

            EncryptionResult(
                ciphertext = cipherAndTag.copyOfRange(0, splitIndex),
                iv = iv,
                authTag = cipherAndTag.copyOfRange(splitIndex, cipherAndTag.size),
            )
        } finally {
            keyCopy.fill(0)
        }
    }

    /**
     * Decrypts ciphertext with AES-256-GCM.
     *
     * @param request Decryption request.
     * @return Decrypted plaintext.
     */
    override fun decrypt(request: DecryptionRequest): ByteArray {
        require(request.keyMaterial.size == KEY_SIZE_BYTES) {
            "Decryption key material must be exactly 32 bytes for AES-256-GCM."
        }
        require(request.iv.size == NONCE_SIZE_BYTES) {
            "Nonce must be exactly 12 bytes for AES-256-GCM."
        }
        require(request.authTag.size == TAG_SIZE_BYTES) {
            "Auth tag must be exactly 16 bytes for AES-256-GCM."
        }

        val keyCopy = request.keyMaterial.copyOf()
        val input = ByteArray(request.ciphertext.size + request.authTag.size)

        request.ciphertext.copyInto(input, destinationOffset = 0)
        request.authTag.copyInto(input, destinationOffset = request.ciphertext.size)

        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            val key = SecretKeySpec(keyCopy, KEY_ALGORITHM)
            val spec = GCMParameterSpec(TAG_SIZE_BITS, request.iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)
            request.aad?.let(cipher::updateAAD)

            cipher.doFinal(input)
        } finally {
            keyCopy.fill(0)
            input.fill(0)
        }
    }
}
