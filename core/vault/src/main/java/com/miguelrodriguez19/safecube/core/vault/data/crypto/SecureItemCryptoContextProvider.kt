package com.miguelrodriguez19.safecube.core.vault.data.crypto

import com.miguelrodriguez19.safecube.core.vault.data.session.VaultInMemoryKekStore
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

internal sealed interface SecureItemCryptoContextResult {
    data class Available(
        val accountId: UUID,
        val kek: ByteArray,
    ) : SecureItemCryptoContextResult

    data object VaultLocked : SecureItemCryptoContextResult

    data object AccountIdUnavailable : SecureItemCryptoContextResult
}

@Singleton
internal class SecureItemCryptoContextProvider @Inject constructor(
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
    private val vaultInMemoryKekStore: VaultInMemoryKekStore,
) {
    fun get(): SecureItemCryptoContextResult {
        val kek = vaultInMemoryKekStore.snapshot()
            ?: return SecureItemCryptoContextResult.VaultLocked
        val accountId = vaultKeyMaterialLocalRepository.get()?.accountId

        return if (accountId == null) {
            kek.fill(0)
            SecureItemCryptoContextResult.AccountIdUnavailable
        } else {
            SecureItemCryptoContextResult.Available(
                accountId = accountId,
                kek = kek,
            )
        }
    }
}
