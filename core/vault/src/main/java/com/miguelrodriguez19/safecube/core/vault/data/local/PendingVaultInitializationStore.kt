package com.miguelrodriguez19.safecube.core.vault.data.local

import android.content.SharedPreferences
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitialization
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationReadResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PendingVaultInitializationStore @Inject constructor(
    @param:EncryptedVaultInitializationPrefs private val encryptedPreferences: SharedPreferences,
    private val codec: PendingVaultInitializationCodec,
) : PendingVaultInitializationRepository {
    override fun read(): PendingVaultInitializationReadResult {
        val encoded = encryptedPreferences.getString(KEY_PENDING_RECORD, null)
            ?: return PendingVaultInitializationReadResult.Empty
        return codec.decode(encoded)
            ?.let(PendingVaultInitializationReadResult::Present)
            ?: PendingVaultInitializationReadResult.Corrupted
    }

    override fun save(value: PendingVaultInitialization): Boolean {
        val encoded = codec.encode(value)
        val committed = encryptedPreferences.edit()
            .putString(KEY_PENDING_RECORD, encoded)
            .commit()
        if (!committed) return false

        return when (val result = read()) {
            is PendingVaultInitializationReadResult.Present -> result.value.sameAs(value)
            PendingVaultInitializationReadResult.Empty,
            PendingVaultInitializationReadResult.Corrupted,
                -> false
        }
    }

    override fun clear(): Boolean {
        val committed = encryptedPreferences.edit()
            .remove(KEY_PENDING_RECORD)
            .commit()
        return committed && encryptedPreferences.getString(KEY_PENDING_RECORD, null) == null
    }

    private fun PendingVaultInitialization.sameAs(other: PendingVaultInitialization): Boolean =
        state == other.state &&
            candidate.sameAs(other.candidate) &&
            recoveryKey.contentEquals(other.recoveryKey)

    private fun VaultKeyMaterial.sameAs(other: VaultKeyMaterial): Boolean =
        accountId == other.accountId &&
            kekEncMaster.contentEquals(other.kekEncMaster) &&
            kekEncRecovery.contentEquals(other.kekEncRecovery) &&
            kdfAlgorithm == other.kdfAlgorithm &&
            kdfSalt.contentEquals(other.kdfSalt) &&
            kdfMemoryKib == other.kdfMemoryKib &&
            kdfIterations == other.kdfIterations &&
            kdfParallelism == other.kdfParallelism &&
            kdfOutputLen == other.kdfOutputLen &&
            cryptoVersion == other.cryptoVersion

    private companion object {
        const val KEY_PENDING_RECORD = "pending_vault_initialization_record"
    }
}
