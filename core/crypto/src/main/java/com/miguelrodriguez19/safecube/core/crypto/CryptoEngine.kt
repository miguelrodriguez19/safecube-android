package com.miguelrodriguez19.safecube.core.crypto

interface CryptoEngine {
    fun encrypt(request: EncryptionRequest): EncryptionResult
    fun decrypt(request: DecryptionRequest): ByteArray
}
