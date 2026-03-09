package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class FakeVaultSessionManagerTest {
    @Test
    fun `starts locked and transitions unlocked then locked`() = runBlocking {
        val manager = FakeVaultSessionManager()

        assertEquals(VaultState.Locked, manager.vaultState.first())

        manager.markVaultUnlocked()
        assertEquals(VaultState.Unlocked, manager.vaultState.first())

        manager.markVaultLocked()
        assertEquals(VaultState.Locked, manager.vaultState.first())
    }
}
