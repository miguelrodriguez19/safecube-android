package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationRepository
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
    private val pendingVaultInitializationRepository: PendingVaultInitializationRepository,
    private val secureItemRepository: SecureItemRepository,
) : LocalVaultDataCleaner {
    override suspend fun clear(): LocalVaultCleanupResult {
        vaultInMemoryKekStore.clear()
        vaultKeyMaterialLocalRepository.clear()
        val pendingInitializationCleared = runCatching {
            pendingVaultInitializationRepository.clear()
        }.getOrDefault(false)

        return try {
            secureItemRepository.clearAllLocalData()
            if (pendingInitializationCleared) {
                LocalVaultCleanupResult.Success
            } else {
                LocalVaultCleanupResult.Failure
            }
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            LocalVaultCleanupResult.Failure
        }
    }
}
