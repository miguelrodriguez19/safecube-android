package com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase.ChangeVaultPassphraseError
import com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase.ChangeVaultPassphraseResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.ChangeVaultPassphraseUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.action.ChangePassphraseUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.event.ChangePassphraseUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.state.ChangePassphraseUiOperationState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChangePassphraseViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    private val changeVaultPassphraseUseCase = mockk<ChangeVaultPassphraseUseCase>()

    private fun target() = ChangePassphraseViewModel(changeVaultPassphraseUseCase)

    @Before
    fun setUp() {
        kotlinx.coroutines.Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        kotlinx.coroutines.Dispatchers.resetMain()
    }

    @Test
    fun `local validation prevents request when fields are missing`() = runTest {
        val target = target()

        target.onAction(
            ChangePassphraseUiAction.Submit(
                currentPassphrase = "",
                newPassphrase = "new-passphrase",
                confirmation = "new-passphrase",
            ),
        )

        assertEquals(
            ChangePassphraseUiOperationState.ValidationError,
            target.uiState.value.operationState,
        )
        assertEquals(
            com.miguelrodriguez19.safecube.core.ui.R.string.change_passphrase_current_required,
            target.uiState.value.currentPassphraseErrorRes,
        )
        coVerify(exactly = 0) { changeVaultPassphraseUseCase(any(), any()) }
    }

    @Test
    fun `local validation rejects equal new passphrase and mismatched confirmation`() = runTest {
        val target = target()

        target.onAction(
            ChangePassphraseUiAction.Submit(
                currentPassphrase = "same-passphrase",
                newPassphrase = "same-passphrase",
                confirmation = "different-passphrase",
            ),
        )

        assertEquals(
            com.miguelrodriguez19.safecube.core.ui.R.string.change_passphrase_new_must_differ,
            target.uiState.value.newPassphraseErrorRes,
        )
        assertEquals(
            com.miguelrodriguez19.safecube.core.ui.R.string.change_passphrase_confirmation_mismatch,
            target.uiState.value.confirmationErrorRes,
        )
        coVerify(exactly = 0) { changeVaultPassphraseUseCase(any(), any()) }
    }

    @Test
    fun `success clears fields and keeps success feedback`() = runTest {
        val target = target()
        coEvery { changeVaultPassphraseUseCase(any(), any()) } returns
            ChangeVaultPassphraseResult.Success
        val event = async(start = CoroutineStart.UNDISPATCHED) { target.events.first() }

        target.onAction(validSubmit())

        assertEquals(
            ChangePassphraseUiOperationState.Success,
            target.uiState.value.operationState,
        )
        assertEquals(
            com.miguelrodriguez19.safecube.core.ui.R.string.change_passphrase_success,
            target.uiState.value.successMessageRes,
        )
        assertEquals(ChangePassphraseUiEvent.ClearFields, event.await())
        coVerify(exactly = 1) {
            changeVaultPassphraseUseCase(
                currentPassphrase = "current-passphrase",
                newPassphrase = "new-passphrase",
            )
        }
    }

    @Test
    fun `invalid current passphrase is terminal and clears fields`() = runTest {
        val target = target()
        coEvery { changeVaultPassphraseUseCase(any(), any()) } returns
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidCurrentPassphrase)
        val event = async(start = CoroutineStart.UNDISPATCHED) { target.events.first() }

        target.onAction(validSubmit())

        assertEquals(
            ChangePassphraseUiOperationState.InvalidCurrentPassphrase,
            target.uiState.value.operationState,
        )
        assertEquals(ChangePassphraseUiEvent.ClearFields, event.await())
    }

    @Test
    fun `retryable error preserves retry state without terminal feedback`() = runTest {
        val target = target()
        coEvery { changeVaultPassphraseUseCase(any(), any()) } returns
            ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.RemoteFailure(
                    VaultKeyMaterialRemoteError.NetworkError(IOException()),
                ),
            )

        target.onAction(validSubmit())

        assertEquals(
            ChangePassphraseUiOperationState.RetryableError,
            target.uiState.value.operationState,
        )
        assertEquals(
            com.miguelrodriguez19.safecube.core.ui.R.string.change_passphrase_retryable,
            target.uiState.value.errorMessageRes,
        )
        assertNull(target.uiState.value.successMessageRes)
    }

    @Test
    fun `locked vault requires unlock and clears fields`() = runTest {
        val target = target()
        coEvery { changeVaultPassphraseUseCase(any(), any()) } returns
            ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.InvalidVaultState(VaultState.Locked),
            )
        val events = async(start = CoroutineStart.UNDISPATCHED) {
            target.events.take(2).toList()
        }

        target.onAction(validSubmit())

        assertEquals(
            ChangePassphraseUiOperationState.SessionRequired,
            target.uiState.value.operationState,
        )
        assertEquals(
            listOf(
                ChangePassphraseUiEvent.ClearFields,
                ChangePassphraseUiEvent.NavigateToUnlock,
            ),
            events.await(),
        )
    }

    @Test
    fun `unauthorized remote result is exposed as session required`() = runTest {
        val target = target()
        coEvery { changeVaultPassphraseUseCase(any(), any()) } returns
            ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.RemoteFailure(VaultKeyMaterialRemoteError.Unauthorized),
            )
        val event = async(start = CoroutineStart.UNDISPATCHED) { target.events.first() }

        target.onAction(validSubmit())

        assertEquals(
            ChangePassphraseUiOperationState.SessionRequired,
            target.uiState.value.operationState,
        )
        assertEquals(ChangePassphraseUiEvent.ClearFields, event.await())
    }

    @Test
    fun `uncertain result clears fields and is represented separately`() = runTest {
        val target = target()
        coEvery { changeVaultPassphraseUseCase(any(), any()) } returns
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ReconciliationRequired)
        val event = async(start = CoroutineStart.UNDISPATCHED) { target.events.first() }

        target.onAction(validSubmit())

        assertEquals(
            ChangePassphraseUiOperationState.UncertainError,
            target.uiState.value.operationState,
        )
        assertEquals(ChangePassphraseUiEvent.ClearFields, event.await())
    }

    @Test
    fun `fields changed clears previous feedback without storing passphrases in state`() = runTest {
        val target = target()
        coEvery { changeVaultPassphraseUseCase(any(), any()) } returns
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidCurrentPassphrase)

        target.onAction(validSubmit())
        target.onAction(ChangePassphraseUiAction.FieldsChanged)

        assertEquals(ChangePassphraseUiOperationState.Idle, target.uiState.value.operationState)
        assertNull(target.uiState.value.errorMessageRes)
        assertNull(target.uiState.value.successMessageRes)
    }

    private fun validSubmit() = ChangePassphraseUiAction.Submit(
        currentPassphrase = "current-passphrase",
        newPassphrase = "new-passphrase",
        confirmation = "new-passphrase",
    )
}
