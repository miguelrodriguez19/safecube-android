package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultDataCleaner
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException

@Singleton
internal class LocalVaultDataCleanerImpl @Inject constructor(
    private val vaultInMemoryKekStore: VaultInMemoryKekStore,
    private val vaultKeyMaterialLocalRepository: VaultKeyMaterialLocalRepository,
    private val pendingVaultInitializationRepository: PendingVaultInitializationRepository,
    private val secureItemRepository: SecureItemRepository,
    private val quickUnlockManager: QuickUnlockManager,
    private val pendingQuickUnlockEnrollmentStore: PendingQuickUnlockEnrollmentStore =
        PendingQuickUnlockEnrollmentStore(),
) : LocalVaultDataCleaner {
    override suspend fun clear(): LocalVaultCleanupResult {
        pendingQuickUnlockEnrollmentStore.clear()
        vaultInMemoryKekStore.clear()
        vaultKeyMaterialLocalRepository.clear()
        val quickUnlockCleared = quickUnlockManager.clearAllEnrollments() == QuickUnlockCleanupResult.Cleared
        val pendingInitializationCleared = runCatching {
            pendingVaultInitializationRepository.clear()
        }.getOrDefault(false)

        return try {
            secureItemRepository.clearAllLocalData()
            if (pendingInitializationCleared && quickUnlockCleared) {
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
