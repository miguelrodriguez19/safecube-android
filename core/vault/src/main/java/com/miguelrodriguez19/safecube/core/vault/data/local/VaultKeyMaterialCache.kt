package com.miguelrodriguez19.safecube.core.vault.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

data class CachedVaultKeyMaterial(
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

@Singleton
class VaultKeyMaterialCache @Inject constructor(
    @param:EncryptedVaultKeyMaterialPrefs private val encryptedPreferences: SharedPreferences,
) {

    private companion object {
        const val KEY_KEK_ENC_MASTER = "kek_enc_master"
        const val KEY_KEK_ENC_RECOVERY = "kek_enc_recovery"
        const val KEY_KDF_ALGORITHM = "kdf_algorithm"
        const val KEY_KDF_SALT = "kdf_salt"
        const val KEY_KDF_MEMORY_KIB = "kdf_memory_kib"
        const val KEY_KDF_ITERATIONS = "kdf_iterations"
        const val KEY_KDF_PARALLELISM = "kdf_parallelism"
        const val KEY_KDF_OUTPUT_LEN = "kdf_output_len"
        const val KEY_CRYPTO_VERSION = "crypto_version"
        const val MISSING_INT = -1
    }

    fun save(cachedVaultKeyMaterial: CachedVaultKeyMaterial) {
        encryptedPreferences.edit {
            putString(KEY_KEK_ENC_MASTER, encode(cachedVaultKeyMaterial.kekEncMaster))
            putString(KEY_KEK_ENC_RECOVERY, encode(cachedVaultKeyMaterial.kekEncRecovery))
            putString(KEY_KDF_ALGORITHM, cachedVaultKeyMaterial.kdfAlgorithm)
            putString(KEY_KDF_SALT, encode(cachedVaultKeyMaterial.kdfSalt))
            putInt(KEY_KDF_MEMORY_KIB, cachedVaultKeyMaterial.kdfMemoryKib)
            putInt(KEY_KDF_ITERATIONS, cachedVaultKeyMaterial.kdfIterations)
            putInt(KEY_KDF_PARALLELISM, cachedVaultKeyMaterial.kdfParallelism)
            putInt(KEY_KDF_OUTPUT_LEN, cachedVaultKeyMaterial.kdfOutputLen)
            putString(KEY_CRYPTO_VERSION, cachedVaultKeyMaterial.cryptoVersion)
        }
    }

    fun get(): CachedVaultKeyMaterial? {
        val kekEncMaster = encryptedPreferences.getString(KEY_KEK_ENC_MASTER, null)
            ?.let(::decode)
            ?: return null
        val kekEncRecovery = encryptedPreferences.getString(KEY_KEK_ENC_RECOVERY, null)
            ?.let(::decode)
            ?: return null
        val kdfAlgorithm = encryptedPreferences.getString(KEY_KDF_ALGORITHM, null)
            ?.takeIf { it.isNotBlank() }
            ?: return null
        val kdfSalt = encryptedPreferences.getString(KEY_KDF_SALT, null)
            ?.let(::decode)
            ?: return null
        val kdfMemoryKib = encryptedPreferences.getInt(KEY_KDF_MEMORY_KIB, MISSING_INT)
            .takeIfPositive()
            ?: return null
        val kdfIterations = encryptedPreferences.getInt(KEY_KDF_ITERATIONS, MISSING_INT)
            .takeIfPositive()
            ?: return null
        val kdfParallelism = encryptedPreferences.getInt(KEY_KDF_PARALLELISM, MISSING_INT)
            .takeIfPositive()
            ?: return null
        val kdfOutputLen = encryptedPreferences.getInt(KEY_KDF_OUTPUT_LEN, MISSING_INT)
            .takeIfPositive()
            ?: return null
        val cryptoVersion = encryptedPreferences.getString(KEY_CRYPTO_VERSION, null)
            ?.takeIf { it.isNotBlank() }
            ?: return null

        return CachedVaultKeyMaterial(
            kekEncMaster = kekEncMaster,
            kekEncRecovery = kekEncRecovery,
            kdfAlgorithm = kdfAlgorithm,
            kdfSalt = kdfSalt,
            kdfMemoryKib = kdfMemoryKib,
            kdfIterations = kdfIterations,
            kdfParallelism = kdfParallelism,
            kdfOutputLen = kdfOutputLen,
            cryptoVersion = cryptoVersion,
        )
    }

    fun clear() {
        encryptedPreferences.edit {
            clear()
        }
    }

    private fun encode(value: ByteArray): String = Base64.getEncoder().encodeToString(value)

    private fun decode(value: String): ByteArray? =
        runCatching { Base64.getDecoder().decode(value) }.getOrNull()

    private fun Int.takeIfPositive(): Int? = takeIf { it > 0 }
}
