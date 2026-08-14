package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultDataCleaner
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException

@Singleton
internal class LocalVaultDataCleanerImpl @Inject constructor(
    private val vaultInMemoryKekStore: VaultInMemoryKekStore,
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
    private val secureItemRepository: SecureItemRepository,
) : LocalVaultDataCleaner {
    override suspend fun clear(): LocalVaultCleanupResult {
        vaultInMemoryKekStore.clear()
        vaultKeyMaterialLocalRepository.clear()

        return try {
            secureItemRepository.clearAllLocalData()
            LocalVaultCleanupResult.Success
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            LocalVaultCleanupResult.Failure
        }
    }
}
