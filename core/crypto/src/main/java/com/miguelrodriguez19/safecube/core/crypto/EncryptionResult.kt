package com.miguelrodriguez19.safecube.core.crypto

data class EncryptionResult(
    val ciphertext: ByteArray,
    val authTag: ByteArray? = null,
)
