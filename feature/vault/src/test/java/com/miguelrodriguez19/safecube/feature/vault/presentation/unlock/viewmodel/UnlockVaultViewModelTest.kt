@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.miguel.rodriguez19.safecube.feature.vault.presentation.unlock.viewmodel

import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.action.UnlockVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.event.UnlockVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.viewmodel.UnlockVaultViewModel
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class UnlockVaultViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val vaultSessionManager = mockk<VaultSessionManager>()

    @Test
    fun `submit with invalid passphrase then exposes terminal error and clears passphrase`() = runTest {
        val passphrase = sensitiveValue()
        every { vaultSessionManager.unlockWithPassphrase(passphrase) } returns
            VaultUnlockError.InvalidCredential
        val target = UnlockVaultViewModel(vaultSessionManager)

        target.onAction(UnlockVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(UnlockVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.TerminalError, target.uiState.value.operationState)
        assertEquals(UiR.string.vault_error_invalid_passphrase, target.uiState.value.errorMessageRes)
        assertTrue(target.uiState.value.passphrase.isEmpty())
    }

    @Test
    fun `submit with unavailable material then exposes a distinct terminal error`() = runTest {
        val passphrase = sensitiveValue()
        every { vaultSessionManager.unlockWithPassphrase(passphrase) } returns
            VaultUnlockError.KeyMaterialUnavailable
        val target = UnlockVaultViewModel(vaultSessionManager)

        target.onAction(UnlockVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(UnlockVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(UiR.string.vault_error_material_unavailable, target.uiState.value.errorMessageRes)
        assertEquals(VaultUiOperationState.TerminalError, target.uiState.value.operationState)
    }

    @Test
    fun `submit with corrupted material then exposes a distinct terminal error`() = runTest {
        val passphrase = sensitiveValue()
        every { vaultSessionManager.unlockWithPassphrase(passphrase) } returns
            VaultUnlockError.InvalidCachedKeyMaterial
        val target = UnlockVaultViewModel(vaultSessionManager)

        target.onAction(UnlockVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(UnlockVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(UiR.string.vault_error_material_corrupted, target.uiState.value.errorMessageRes)
        assertEquals(VaultUiOperationState.TerminalError, target.uiState.value.operationState)
    }

    @Test
    fun `submit when vault is locked during operation then exposes retryable error and retry repeats`() = runTest {
        val passphrase = sensitiveValue()
        every { vaultSessionManager.unlockWithPassphrase(passphrase) } returns null
        every { vaultSessionManager.isUnlocked() } returnsMany listOf(false, true)
        val target = UnlockVaultViewModel(vaultSessionManager)
        val event = async { target.events.first() }

        target.onAction(UnlockVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(UnlockVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.RetryableError, target.uiState.value.operationState)
        assertEquals(passphrase, target.uiState.value.passphrase)

        target.onAction(UnlockVaultUiAction.Retry)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.Success, target.uiState.value.operationState)
        assertEquals(UnlockVaultUiEvent.NavigateToApp, event.await())
        verify(exactly = 2) { vaultSessionManager.unlockWithPassphrase(passphrase) }
    }

    @Test
    fun `submit when vault unlocks successfully then emits one navigation event`() = runTest {
        val passphrase = sensitiveValue()
        every { vaultSessionManager.unlockWithPassphrase(passphrase) } returns null
        every { vaultSessionManager.isUnlocked() } returns true
        val target = UnlockVaultViewModel(vaultSessionManager)
        val event = async { target.events.first() }

        target.onAction(UnlockVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(UnlockVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.Success, target.uiState.value.operationState)
        assertTrue(target.uiState.value.passphrase.isEmpty())
        assertEquals(UnlockVaultUiEvent.NavigateToApp, event.await())
    }

    private fun sensitiveValue(): String = UUID.randomUUID().toString()
}
