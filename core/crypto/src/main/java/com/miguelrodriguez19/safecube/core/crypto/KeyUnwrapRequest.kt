package com.miguelrodriguez19.safecube.core.crypto

data class KeyUnwrapRequest(
    val wrappedKey: ByteArray,
    val wrappingKey: ByteArray,
    val aad: ByteArray? = null,
)
