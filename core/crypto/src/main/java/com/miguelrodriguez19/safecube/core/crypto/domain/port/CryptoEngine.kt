package com.miguelrodriguez19.safecube.core.crypto.domain.port

import com.miguelrodriguez19.safecube.core.crypto.domain.model.DecryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.EncryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.EncryptionResult

/**
 * Abstraction for symmetric authenticated encryption and decryption operations.
 */
interface CryptoEngine {
    /**
     * Encrypts the given [request] and returns ciphertext material plus metadata required to decrypt.
     */
    fun encrypt(request: EncryptionRequest): EncryptionResult

    /**
     * Decrypts the given [request] and returns plaintext bytes.
     */
    fun decrypt(request: DecryptionRequest): ByteArray
}
