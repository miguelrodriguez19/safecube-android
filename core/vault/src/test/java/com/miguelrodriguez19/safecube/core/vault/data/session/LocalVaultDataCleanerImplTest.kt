package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerifyOrder
import io.mockk.justRun
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class LocalVaultDataCleanerImplTest {
    private val vaultSessionManager = mockk<VaultSessionManager>()
    private val vaultKeyMaterialLocalRepository = mockk<VaultKeyMaterialLocalRepository>()
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val target = LocalVaultDataCleanerImpl(
        vaultSessionManager = vaultSessionManager,
        vaultKeyMaterialLocalRepository = vaultKeyMaterialLocalRepository,
        secureItemRepository = secureItemRepository,
    )

    @Test
    fun `clear removes keys before transactional vault data`() = runBlocking {
        justRun { vaultSessionManager.lock() }
        justRun { vaultKeyMaterialLocalRepository.clear() }
        coJustRun { secureItemRepository.clearAllLocalData() }

        val result = target.clear()

        assertEquals(LocalVaultCleanupResult.Success, result)
        coVerifyOrder {
            vaultSessionManager.lock()
            vaultKeyMaterialLocalRepository.clear()
            secureItemRepository.clearAllLocalData()
        }
    }

    @Test
    fun `clear returns failure after keys are removed when Room fails`() = runBlocking {
        val cause = IllegalStateException("Room unavailable")
        justRun { vaultSessionManager.lock() }
        justRun { vaultKeyMaterialLocalRepository.clear() }
        coEvery { secureItemRepository.clearAllLocalData() } throws cause

        val result = target.clear()

        assertSame(cause, (result as LocalVaultCleanupResult.Failure).cause)
        coVerifyOrder {
            vaultSessionManager.lock()
            vaultKeyMaterialLocalRepository.clear()
            secureItemRepository.clearAllLocalData()
        }
    }
}
