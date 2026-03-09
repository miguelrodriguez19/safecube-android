package com.miguelrodriguez19.safecube.core.crypto

data class KdfRequest(
    val secret: ByteArray,
    val salt: ByteArray,
    val iterations: Int,
    val memoryKib: Int,
    val parallelism: Int,
    val outputLengthBytes: Int = 32,
    val contextInfo: ByteArray? = null,
)
