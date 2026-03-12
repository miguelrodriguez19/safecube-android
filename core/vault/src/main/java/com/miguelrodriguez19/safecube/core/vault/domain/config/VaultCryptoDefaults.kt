package com.miguelrodriguez19.safecube.core.vault.domain.config

object VaultCryptoDefaults {
    const val KDF_ALGORITHM = "argon2id"
    const val KDF_SALT_LENGTH_BYTES = 16
    const val KDF_MEMORY_KIB = 65536
    const val KDF_ITERATIONS = 3
    const val KDF_PARALLELISM = 1
    const val KDF_OUTPUT_LEN = 32
    const val CRYPTO_VERSION = "v1"
    const val KEY_LENGTH_BYTES = 32
}