package com.miguelrodriguez19.safecube.core.crypto

data class KeyWrapRequest(
    val keyToWrap: ByteArray,
    val wrappingKey: ByteArray,
    val aad: ByteArray? = null,
)
