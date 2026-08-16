package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultCleanupResult
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class LocalVaultDataCleanerImplTest {
    private val vaultInMemoryKekStore = mockk<VaultInMemoryKekStore>()
    private val vaultKeyMaterialLocalRepository = mockk<VaultKeyMaterialLocalRepository>()
    private val pendingVaultInitializationRepository =
        mockk<PendingVaultInitializationRepository>()
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val target = LocalVaultDataCleanerImpl(
        vaultInMemoryKekStore = vaultInMemoryKekStore,
        vaultKeyMaterialLocalRepository = vaultKeyMaterialLocalRepository,
        pendingVaultInitializationRepository = pendingVaultInitializationRepository,
        secureItemRepository = secureItemRepository,
    )

    @Test
    fun `clear removes keys before transactional vault data`() = runBlocking {
        justRun { vaultInMemoryKekStore.clear() }
        justRun { vaultKeyMaterialLocalRepository.clear() }
        every { pendingVaultInitializationRepository.clear() } returns true
        coJustRun { secureItemRepository.clearAllLocalData() }

        val result = target.clear()

        assertEquals(LocalVaultCleanupResult.Success, result)
        coVerifyOrder {
            vaultInMemoryKekStore.clear()
            vaultKeyMaterialLocalRepository.clear()
            pendingVaultInitializationRepository.clear()
            secureItemRepository.clearAllLocalData()
        }
    }

    @Test
    fun `clear returns failure after keys are removed when Room fails`() = runBlocking {
        justRun { vaultInMemoryKekStore.clear() }
        justRun { vaultKeyMaterialLocalRepository.clear() }
        every { pendingVaultInitializationRepository.clear() } returns true
        coEvery { secureItemRepository.clearAllLocalData() } throws IllegalStateException("Room unavailable")

        val result = target.clear()

        assertEquals(LocalVaultCleanupResult.Failure, result)
        coVerifyOrder {
            vaultInMemoryKekStore.clear()
            vaultKeyMaterialLocalRepository.clear()
            pendingVaultInitializationRepository.clear()
            secureItemRepository.clearAllLocalData()
        }
    }

    @Test
    fun `clear returns failure when pending initialization cannot be verified as removed`() = runBlocking {
        justRun { vaultInMemoryKekStore.clear() }
        justRun { vaultKeyMaterialLocalRepository.clear() }
        every { pendingVaultInitializationRepository.clear() } returns false
        coJustRun { secureItemRepository.clearAllLocalData() }

        val result = target.clear()

        assertEquals(LocalVaultCleanupResult.Failure, result)
        coVerifyOrder {
            vaultInMemoryKekStore.clear()
            vaultKeyMaterialLocalRepository.clear()
            pendingVaultInitializationRepository.clear()
            secureItemRepository.clearAllLocalData()
        }
    }
}
