package com.miguelrodriguez19.safecube.core.vault.domain.model

data class VaultKeyMaterial(
    val kekEncMaster: ByteArray,
    val kekEncRecovery: ByteArray,
    val kdfAlgorithm: String,
    val kdfSalt: ByteArray,
    val kdfMemoryKib: Int,
    val kdfIterations: Int,
    val kdfParallelism: Int,
    val kdfOutputLen: Int,
    val cryptoVersion: String,
)
