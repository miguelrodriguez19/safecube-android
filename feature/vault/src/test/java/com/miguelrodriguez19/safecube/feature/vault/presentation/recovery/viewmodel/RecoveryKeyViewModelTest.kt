@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.miguel.rodriguez19.safecube.feature.vault.presentation.recovery.viewmodel

import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultRecoveryKeyResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultInitializeUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.viewmodel.RecoveryKeyViewModel
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action.RecoveryKeyUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.event.RecoveryKeyUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Base64
import java.util.UUID
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RecoveryKeyViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val vaultInitializeUseCase = mockk<VaultInitializeUseCase>()

    @Test
    fun `initialization when pending recovery key exists then exposes success with key`() = runTest {
        val recoveryKey = recoveryKeyValue()
        every { vaultInitializeUseCase.readPendingRecoveryKey() } returns
            PendingVaultRecoveryKeyResult.Available(recoveryKey.copyOf())
        val target = RecoveryKeyViewModel(vaultInitializeUseCase)

        advanceUntilIdle()

        assertEquals(VaultUiOperationState.Success, target.uiState.value.operationState)
        assertEquals(
            Base64.getEncoder().encodeToString(recoveryKey),
            target.uiState.value.recoveryKey,
        )
        assertTrue(!target.uiState.value.isConfirmed)
        verify(exactly = 0) { vaultInitializeUseCase.confirmRecoveryKeySaved() }
    }

    @Test
    fun `initialization when recovery key is unavailable then prevents continuation`() = runTest {
        every { vaultInitializeUseCase.readPendingRecoveryKey() } returns
            PendingVaultRecoveryKeyResult.Unavailable
        val target = RecoveryKeyViewModel(vaultInitializeUseCase)

        advanceUntilIdle()
        target.onAction(RecoveryKeyUiAction.Continue)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.TerminalError, target.uiState.value.operationState)
        assertEquals(UiR.string.vault_recovery_unavailable, target.uiState.value.errorMessageRes)
        verify(exactly = 0) { vaultInitializeUseCase.confirmRecoveryKeySaved() }
    }

    @Test
    fun `continue without explicit confirmation then keeps pending record and shows validation`() = runTest {
        every { vaultInitializeUseCase.readPendingRecoveryKey() } returns
            PendingVaultRecoveryKeyResult.Available(recoveryKeyValue())
        val target = RecoveryKeyViewModel(vaultInitializeUseCase)

        advanceUntilIdle()
        target.onAction(RecoveryKeyUiAction.Continue)

        assertEquals(VaultUiOperationState.Success, target.uiState.value.operationState)
        assertEquals(
            UiR.string.vault_recovery_confirmation_required,
            target.uiState.value.errorMessageRes,
        )
        verify(exactly = 0) { vaultInitializeUseCase.confirmRecoveryKeySaved() }
    }

    @Test
    fun `continue after explicit confirmation then clears pending record and emits one event`() = runTest {
        every { vaultInitializeUseCase.readPendingRecoveryKey() } returns
            PendingVaultRecoveryKeyResult.Available(recoveryKeyValue())
        every { vaultInitializeUseCase.confirmRecoveryKeySaved() } returns true
        val target = RecoveryKeyViewModel(vaultInitializeUseCase)
        val event = async { target.events.first() }

        advanceUntilIdle()
        target.onAction(RecoveryKeyUiAction.ConfirmationChanged(true))
        target.onAction(RecoveryKeyUiAction.Continue)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.Success, target.uiState.value.operationState)
        assertEquals(RecoveryKeyUiEvent.ContinueToUnlock, event.await())
        verify(exactly = 1) { vaultInitializeUseCase.confirmRecoveryKeySaved() }
    }

    @Test
    fun `continue when pending record cleanup fails then keeps key and exposes retryable error`() = runTest {
        every { vaultInitializeUseCase.readPendingRecoveryKey() } returns
            PendingVaultRecoveryKeyResult.Available(recoveryKeyValue())
        every { vaultInitializeUseCase.confirmRecoveryKeySaved() } returns false
        val target = RecoveryKeyViewModel(vaultInitializeUseCase)

        advanceUntilIdle()
        target.onAction(RecoveryKeyUiAction.ConfirmationChanged(true))
        target.onAction(RecoveryKeyUiAction.Continue)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.RetryableError, target.uiState.value.operationState)
        assertTrue(target.uiState.value.recoveryKey.isNotBlank())
        assertTrue(target.uiState.value.isRetryable)
    }

    private fun recoveryKeyValue(): ByteArray = UUID.randomUUID().toString().toByteArray()
}
