package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockManager
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test
import java.util.UUID

class LocalVaultDataCleanerImplTest {
    private val vaultInMemoryKekStore = mockk<VaultInMemoryKekStore>()
    private val vaultKeyMaterialLocalRepository = mockk<VaultKeyMaterialLocalRepository>()
    private val pendingVaultInitializationRepository =
        mockk<PendingVaultInitializationRepository>()
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val quickUnlockManager = mockk<QuickUnlockManager>()
    private val pendingQuickUnlockEnrollmentStore = PendingQuickUnlockEnrollmentStore()

    private val target = LocalVaultDataCleanerImpl(
        vaultInMemoryKekStore = vaultInMemoryKekStore,
        vaultKeyMaterialLocalRepository = vaultKeyMaterialLocalRepository,
        pendingVaultInitializationRepository = pendingVaultInitializationRepository,
        secureItemRepository = secureItemRepository,
        quickUnlockManager = quickUnlockManager,
        pendingQuickUnlockEnrollmentStore = pendingQuickUnlockEnrollmentStore,
    )


    @Test
    fun `clear removes keys before transactional vault data`() = runBlocking {
        val accountId = UUID.randomUUID()
        pendingQuickUnlockEnrollmentStore.request(accountId)
        justRun { vaultInMemoryKekStore.clear() }
        justRun { vaultKeyMaterialLocalRepository.clear() }
        every { pendingVaultInitializationRepository.clear() } returns true
        every { quickUnlockManager.clearAllEnrollments() } returns QuickUnlockCleanupResult.Cleared
        coJustRun { secureItemRepository.clearAllLocalData() }

        val result = target.clear()

        assertEquals(LocalVaultCleanupResult.Success, result)
        assertFalse(pendingQuickUnlockEnrollmentStore.consume(accountId))
        coVerifyOrder {
            vaultInMemoryKekStore.clear()
            vaultKeyMaterialLocalRepository.clear()
            quickUnlockManager.clearAllEnrollments()
            pendingVaultInitializationRepository.clear()
            secureItemRepository.clearAllLocalData()
        }
    }

    @Test
    fun `clear returns failure after keys are removed when Room fails`() = runBlocking {
        justRun { vaultInMemoryKekStore.clear() }
        justRun { vaultKeyMaterialLocalRepository.clear() }
        every { pendingVaultInitializationRepository.clear() } returns true
        every { quickUnlockManager.clearAllEnrollments() } returns QuickUnlockCleanupResult.Cleared
        coEvery { secureItemRepository.clearAllLocalData() } throws IllegalStateException("Room unavailable")

        val result = target.clear()

        assertEquals(LocalVaultCleanupResult.Failure, result)
        coVerifyOrder {
            vaultInMemoryKekStore.clear()
            vaultKeyMaterialLocalRepository.clear()
            quickUnlockManager.clearAllEnrollments()
            pendingVaultInitializationRepository.clear()
            secureItemRepository.clearAllLocalData()
        }
    }

    @Test
    fun `clear returns failure when pending initialization cannot be verified as removed`() = runBlocking {
        justRun { vaultInMemoryKekStore.clear() }
        justRun { vaultKeyMaterialLocalRepository.clear() }
        every { pendingVaultInitializationRepository.clear() } returns false
        every { quickUnlockManager.clearAllEnrollments() } returns QuickUnlockCleanupResult.Cleared
        coJustRun { secureItemRepository.clearAllLocalData() }

        val result = target.clear()

        assertEquals(LocalVaultCleanupResult.Failure, result)
        coVerifyOrder {
            vaultInMemoryKekStore.clear()
            vaultKeyMaterialLocalRepository.clear()
            quickUnlockManager.clearAllEnrollments()
            pendingVaultInitializationRepository.clear()
            secureItemRepository.clearAllLocalData()
        }
    }

    @Test
    fun `clear returns failure when quick unlock cleanup fails`() = runBlocking {
        justRun { vaultInMemoryKekStore.clear() }
        justRun { vaultKeyMaterialLocalRepository.clear() }
        every { quickUnlockManager.clearAllEnrollments() } returns QuickUnlockCleanupResult.Failed
        every { pendingVaultInitializationRepository.clear() } returns true
        coJustRun { secureItemRepository.clearAllLocalData() }

        val result = target.clear()

        assertEquals(LocalVaultCleanupResult.Failure, result)
    }
}
