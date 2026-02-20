package com.miguelrodriguez19.safecube.core.crypto

data class DecryptionRequest(
    val ciphertext: ByteArray,
    val keyMaterial: ByteArray,
    val iv: ByteArray,
    val aad: ByteArray? = null,
    val authTag: ByteArray? = null,
)
