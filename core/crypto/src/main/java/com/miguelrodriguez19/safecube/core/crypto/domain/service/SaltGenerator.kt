package com.miguelrodriguez19.safecube.core.crypto.domain.service

import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Generates cryptographically secure random byte arrays used as salts or key material.
 */
@Singleton
class SaltGenerator @Inject constructor() {
    private val secureRandom = SecureRandom()

    /**
     * Generates a random byte array of [lengthBytes].
     */
    fun generate(lengthBytes: Int): ByteArray {
        require(lengthBytes > 0) { "Salt length must be > 0 bytes." }

        return ByteArray(lengthBytes).also { secureRandom.nextBytes(it) }
    }
}
