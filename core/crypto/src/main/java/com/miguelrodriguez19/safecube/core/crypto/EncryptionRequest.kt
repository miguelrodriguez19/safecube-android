package com.miguelrodriguez19.safecube.core.crypto

data class EncryptionRequest(
    val plaintext: ByteArray,
    val keyMaterial: ByteArray,
    val iv: ByteArray,
    val aad: ByteArray? = null,
)
