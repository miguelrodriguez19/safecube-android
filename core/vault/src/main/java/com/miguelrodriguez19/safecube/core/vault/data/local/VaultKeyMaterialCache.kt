package com.miguelrodriguez19.safecube.core.vault.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import java.util.Base64
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultKeyMaterialCache @Inject constructor(
    @param:EncryptedVaultKeyMaterialPrefs private val encryptedPreferences: SharedPreferences,
) : VaultKeyMaterialLocalRepository {

    private companion object {
        const val KEY_ACCOUNT_ID = "account_id"
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

    override fun save(vaultKeyMaterial: VaultKeyMaterial) {
        encryptedPreferences.edit {
            putString(KEY_ACCOUNT_ID, vaultKeyMaterial.accountId.toString())
            putString(KEY_KEK_ENC_MASTER, encode(vaultKeyMaterial.kekEncMaster))
            putString(KEY_KEK_ENC_RECOVERY, encode(vaultKeyMaterial.kekEncRecovery))
            putString(KEY_KDF_ALGORITHM, vaultKeyMaterial.kdfAlgorithm)
            putString(KEY_KDF_SALT, encode(vaultKeyMaterial.kdfSalt))
            putInt(KEY_KDF_MEMORY_KIB, vaultKeyMaterial.kdfMemoryKib)
            putInt(KEY_KDF_ITERATIONS, vaultKeyMaterial.kdfIterations)
            putInt(KEY_KDF_PARALLELISM, vaultKeyMaterial.kdfParallelism)
            putInt(KEY_KDF_OUTPUT_LEN, vaultKeyMaterial.kdfOutputLen)
            putString(KEY_CRYPTO_VERSION, vaultKeyMaterial.cryptoVersion)
        }
    }

    override fun get(): VaultKeyMaterial? {
        val accountId = encryptedPreferences.getString(KEY_ACCOUNT_ID, null)
            ?.toUuidOrNull()
            ?: return null

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

        return VaultKeyMaterial(
            accountId = accountId,
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

    override fun clear() {
        encryptedPreferences.edit {
            clear()
        }
    }

    private fun encode(value: ByteArray): String = Base64.getEncoder().encodeToString(value)

    private fun decode(value: String): ByteArray? =
        runCatching { Base64.getDecoder().decode(value) }.getOrNull()

    private fun Int.takeIfPositive(): Int? = takeIf { it > 0 }

    private fun String.toUuidOrNull(): UUID? =
        takeIf { it.isNotBlank() }
            ?.let { runCatching { UUID.fromString(it) }.getOrNull() }
}
