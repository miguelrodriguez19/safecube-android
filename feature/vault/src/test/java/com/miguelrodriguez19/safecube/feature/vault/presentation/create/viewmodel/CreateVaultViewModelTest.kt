@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.miguelrodriguez19.safecube.feature.vault.presentation.create.viewmodel

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeError
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultInitializeUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.action.CreateVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.event.CreateVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
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

class CreateVaultViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val vaultInitializeUseCase = mockk<VaultInitializeUseCase>()
    private val vaultSessionManager = mockk<VaultSessionManager>()

    @Test
    fun `submit when initialization is retryable then exposes retry without a second request`() = runTest {
        val passphrase = sensitiveValue()
        coEvery { vaultInitializeUseCase(passphrase) } returns VaultInitializeResult.Error(
            VaultInitializeError.Remote(
                VaultKeyMaterialRemoteError.HttpError(
                    failure = NetworkFailureClassifier.fromHttpStatus(503),
                ),
            ),
        )
        val target = CreateVaultViewModel(vaultInitializeUseCase, vaultSessionManager)

        target.onAction(CreateVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(CreateVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.RetryableError, target.uiState.value.operationState)
        assertTrue(target.uiState.value.isRetryable)
        assertEquals(passphrase, target.uiState.value.passphrase)
        coVerify(exactly = 1) { vaultInitializeUseCase(passphrase) }
    }

    @Test
    fun `retry when initialization becomes successful then emits one recovery navigation event`() = runTest {
        val passphrase = sensitiveValue()
        coEvery { vaultInitializeUseCase(passphrase) } returnsMany listOf(
            VaultInitializeResult.Error(
                VaultInitializeError.Remote(
                    VaultKeyMaterialRemoteError.NetworkError(
                        NetworkFailureClassifier.fromHttpStatus(503),
                    ),
                ),
            ),
            VaultInitializeResult.Initialized(recoveryKeyValue()),
        )
        every { vaultSessionManager.lock() } just runs
        val target = CreateVaultViewModel(vaultInitializeUseCase, vaultSessionManager)
        val event = async { target.events.first() }

        target.onAction(CreateVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(CreateVaultUiAction.Submit)
        advanceUntilIdle()
        target.onAction(CreateVaultUiAction.Retry)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.Success, target.uiState.value.operationState)
        assertEquals(CreateVaultUiEvent.NavigateToRecoveryKey, event.await())
        coVerify(exactly = 2) { vaultInitializeUseCase(passphrase) }
        verify(exactly = 1) { vaultSessionManager.lock() }
    }

    @Test
    fun `submit when initialization is terminal then clears passphrase and exposes terminal error`() = runTest {
        val passphrase = sensitiveValue()
        coEvery { vaultInitializeUseCase(passphrase) } returns VaultInitializeResult.Error(
            VaultInitializeError.Remote(VaultKeyMaterialRemoteError.Forbidden),
        )
        val target = CreateVaultViewModel(vaultInitializeUseCase, vaultSessionManager)

        target.onAction(CreateVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(CreateVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.TerminalError, target.uiState.value.operationState)
        assertEquals(UiR.string.vault_error_forbidden, target.uiState.value.errorMessageRes)
        assertTrue(target.uiState.value.passphrase.isEmpty())
    }

    @Test
    fun `submit twice while initialization is active then sends one request`() = runTest {
        val passphrase = sensitiveValue()
        val result = kotlinx.coroutines.CompletableDeferred<VaultInitializeResult>()
        coEvery { vaultInitializeUseCase(passphrase) } coAnswers { result.await() }
        val target = CreateVaultViewModel(vaultInitializeUseCase, vaultSessionManager)

        target.onAction(CreateVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(CreateVaultUiAction.Submit)
        target.onAction(CreateVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.Loading, target.uiState.value.operationState)
        coVerify(exactly = 1) { vaultInitializeUseCase(passphrase) }
        result.complete(VaultInitializeResult.AlreadyInitialized)
        advanceUntilIdle()
    }

    private fun sensitiveValue(): String = UUID.randomUUID().toString()

    private fun recoveryKeyValue(): ByteArray = UUID.randomUUID().toString().toByteArray()
}
