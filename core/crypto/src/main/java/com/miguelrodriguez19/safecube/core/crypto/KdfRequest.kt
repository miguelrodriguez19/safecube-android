package com.miguelrodriguez19.safecube.core.crypto

data class KdfRequest(
    val secret: ByteArray,
    val salt: ByteArray,
    val outputLengthBytes: Int,
    val contextInfo: ByteArray? = null,
)
