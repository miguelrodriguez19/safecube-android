package com.miguelrodriguez19.safecube.core.crypto

import java.security.SecureRandom
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SaltGenerator @Inject constructor() {
    private val secureRandom = SecureRandom()

    fun generate(lengthBytes: Int): ByteArray {
        require(lengthBytes > 0) { "Salt length must be > 0 bytes." }

        return ByteArray(lengthBytes).also { secureRandom.nextBytes(it) }
    }
}
