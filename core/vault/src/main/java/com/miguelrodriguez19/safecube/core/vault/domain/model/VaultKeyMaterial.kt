package com.miguelrodriguez19.safecube.core.vault.domain.model

import java.util.UUID

data class VaultKeyMaterial(
    val accountId: UUID? = null,
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
