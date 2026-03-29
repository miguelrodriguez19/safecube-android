package com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault

import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.vault.domain.model.UnlockedKeyring
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VaultUnlockUseCase @Inject constructor(
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
    private val kdfEngine: KdfEngine,
    private val keyWrapping: KeyWrapping,
) : VaultUnlocker {
    override fun unlockWithPassphrase(passphrase: String): VaultUnlockResult {
        val cachedVaultKeyMaterial = vaultKeyMaterialLocalRepository.get()
            ?: return VaultUnlockResult.Error(VaultUnlockError.KeyMaterialUnavailable)
        val passphraseBytes = passphrase.toByteArray(StandardCharsets.UTF_8)
        var masterKey = byteArrayOf()

        return try {
            masterKey = deriveMasterKey(
                passphraseBytes = passphraseBytes,
                cachedVaultKeyMaterial = cachedVaultKeyMaterial,
            )
            val kek = unwrapKek(
                envelope = cachedVaultKeyMaterial.kekEncMaster,
                wrappingKey = masterKey,
            )
            VaultUnlockResult.Unlocked(
                keyring = UnlockedKeyring(kek = kek),
            )
        } catch (_: IllegalArgumentException) {
            VaultUnlockResult.Error(VaultUnlockError.InvalidCachedKeyMaterial)
        } catch (_: Throwable) {
            VaultUnlockResult.Error(VaultUnlockError.InvalidCredential)
        } finally {
            passphraseBytes.fill(0)
            masterKey.fill(0)
        }
    }

    override fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockResult {
        val cachedVaultKeyMaterial = vaultKeyMaterialLocalRepository.get()
            ?: return VaultUnlockResult.Error(VaultUnlockError.KeyMaterialUnavailable)

        val recoveryKeyCopy = recoveryKey.copyOf()

        return try {
            val kek = unwrapKek(
                envelope = cachedVaultKeyMaterial.kekEncRecovery,
                wrappingKey = recoveryKeyCopy,
            )
            VaultUnlockResult.Unlocked(
                keyring = UnlockedKeyring(kek = kek),
            )
        } catch (_: IllegalArgumentException) {
            VaultUnlockResult.Error(VaultUnlockError.InvalidCachedKeyMaterial)
        } catch (_: Throwable) {
            VaultUnlockResult.Error(VaultUnlockError.InvalidCredential)
        } finally {
            recoveryKeyCopy.fill(0)
        }
    }

    private fun deriveMasterKey(
        passphraseBytes: ByteArray,
        cachedVaultKeyMaterial: VaultKeyMaterial,
    ): ByteArray = kdfEngine.deriveKey(
        request = KdfRequest(
            secret = passphraseBytes,
            salt = cachedVaultKeyMaterial.kdfSalt,
            iterations = cachedVaultKeyMaterial.kdfIterations,
            memoryKib = cachedVaultKeyMaterial.kdfMemoryKib,
            parallelism = cachedVaultKeyMaterial.kdfParallelism,
            outputLengthBytes = cachedVaultKeyMaterial.kdfOutputLen,
        ),
    )

    private fun unwrapKek(
        envelope: ByteArray,
        wrappingKey: ByteArray,
    ): ByteArray = keyWrapping.unwrapKey(
        request = KeyUnwrapRequest(
            wrappedKey = envelope,
            wrappingKey = wrappingKey,
        ),
    )
}
