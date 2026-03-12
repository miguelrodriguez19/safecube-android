package com.miguelrodriguez19.safecube.core.crypto.domain.model

/**
 * Input payload for key derivation.
 *
 * @property secret Secret input (for example, passphrase bytes).
 * @property salt Salt bytes.
 * @property iterations Time-cost parameter.
 * @property memoryKib Memory-cost parameter in KiB.
 * @property parallelism Parallelism parameter.
 * @property outputLengthBytes Requested derived-key length.
 * @property contextInfo Optional context-specific bytes authenticated by the KDF.
 */
data class KdfRequest(
    val secret: ByteArray,
    val salt: ByteArray,
    val iterations: Int,
    val memoryKib: Int,
    val parallelism: Int,
    val outputLengthBytes: Int = 32,
    val contextInfo: ByteArray? = null,
)
