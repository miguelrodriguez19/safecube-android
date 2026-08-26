@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.miguel.rodriguez19.safecube.feature.vault.presentation.unlock.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCompletionResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.PendingQuickUnlockEnrollment
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptOperation
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptRequest
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.action.UnlockVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.event.UnlockVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.viewmodel.UnlockVaultViewModel
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.util.UUID
import com.miguelrodriguez19.safecube.core.ui.R as UiR

class UnlockVaultViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    private val vaultSessionManager = mockk<VaultSessionManager>()

    private lateinit var target: UnlockVaultViewModel


    @Before
    fun setUp() {
        target = UnlockVaultViewModel(vaultSessionManager)

        every { vaultSessionManager.quickUnlockOfferState() } returns QuickUnlockOfferState.AccountUnavailable
    }

    @After
    fun tearDown() {
        PendingQuickUnlockEnrollment.clear()
    }

    @Test
    fun `submit with invalid passphrase then exposes terminal error and clears passphrase`() =
        runTest {
            val passphrase = sensitiveValue()
            every { vaultSessionManager.unlockWithPassphrase(passphrase) } returns
                    VaultUnlockError.InvalidCredential

            target.onAction(UnlockVaultUiAction.PassphraseChanged(passphrase))
            target.onAction(UnlockVaultUiAction.Submit)
            advanceUntilIdle()

            assertEquals(VaultUiOperationState.TerminalError, target.uiState.value.operationState)
            assertEquals(
                UiR.string.vault_error_invalid_passphrase,
                target.uiState.value.errorMessageRes
            )
            assertTrue(target.uiState.value.passphrase.isEmpty())
        }

    @Test
    fun `submit with unavailable material then exposes a distinct terminal error`() = runTest {
        val passphrase = sensitiveValue()
        every { vaultSessionManager.unlockWithPassphrase(passphrase) } returns
                VaultUnlockError.KeyMaterialUnavailable

        target.onAction(UnlockVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(UnlockVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(
            UiR.string.vault_error_material_unavailable,
            target.uiState.value.errorMessageRes
        )
        assertEquals(VaultUiOperationState.TerminalError, target.uiState.value.operationState)
    }

    @Test
    fun `submit with corrupted material then exposes a distinct terminal error`() = runTest {
        val passphrase = sensitiveValue()
        every { vaultSessionManager.unlockWithPassphrase(passphrase) } returns
                VaultUnlockError.InvalidCachedKeyMaterial

        target.onAction(UnlockVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(UnlockVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(
            UiR.string.vault_error_material_corrupted,
            target.uiState.value.errorMessageRes
        )
        assertEquals(VaultUiOperationState.TerminalError, target.uiState.value.operationState)
    }

    @Test
    fun `submit when vault is locked during operation then exposes retryable error and retry repeats`() =
        runTest {
            val passphrase = sensitiveValue()
            every { vaultSessionManager.unlockWithPassphrase(passphrase) } returns null
            every { vaultSessionManager.isUnlocked() } returnsMany listOf(false, true)
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
        val event = async { target.events.first() }

        target.onAction(UnlockVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(UnlockVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(VaultUiOperationState.Success, target.uiState.value.operationState)
        assertTrue(target.uiState.value.passphrase.isEmpty())
        assertEquals(UnlockVaultUiEvent.NavigateToApp, event.await())
    }

    @Test
    fun `screen entry enrolled exposes manual quick unlock and emits one automatic prompt`() =
        runTest {
            every { vaultSessionManager.quickUnlockOfferState() } returns QuickUnlockOfferState.Enrolled
            every { vaultSessionManager.prepareQuickUnlock() } returns
                    QuickUnlockPreparationResult.Ready("quick-operation")
            val event = async { target.events.first() }

            target.onAction(UnlockVaultUiAction.ScreenEntered)
            target.onAction(UnlockVaultUiAction.ScreenEntered)

            assertTrue(target.uiState.value.hasQuickUnlockEnrollment)
            assertEquals(
                UnlockVaultUiEvent.LaunchQuickUnlockPrompt(
                    QuickUnlockPromptRequest("quick-operation", QuickUnlockPromptOperation.Unlock),
                ),
                event.await(),
            )
            verify(exactly = 1) { vaultSessionManager.quickUnlockOfferState() }
            verify(exactly = 1) { vaultSessionManager.prepareQuickUnlock() }
        }

    @Test
    fun `screen entry without quick unlock enrollment does not expose manual quick unlock`() =
        runTest {

            target.onAction(UnlockVaultUiAction.ScreenEntered)

            assertFalse(target.uiState.value.hasQuickUnlockEnrollment)
            verify(exactly = 0) { vaultSessionManager.prepareQuickUnlock() }
        }

    @Test
    fun `manual quick unlock emits a prompt when automatic entry was not delivered`() = runTest {
        every { vaultSessionManager.prepareQuickUnlock() } returns
                QuickUnlockPreparationResult.Ready("quick-operation")
        val event = async { target.events.first() }

        target.onAction(UnlockVaultUiAction.RetryQuickUnlock)

        assertEquals(
            UnlockVaultUiEvent.LaunchQuickUnlockPrompt(
                QuickUnlockPromptRequest("quick-operation", QuickUnlockPromptOperation.Unlock),
            ),
            event.await(),
        )
        verify(exactly = 1) { vaultSessionManager.prepareQuickUnlock() }
    }

    @Test
    fun `quick unlock cancellation keeps the passphrase form available and supports manual retry`() =
        runTest {
            every { vaultSessionManager.quickUnlockOfferState() } returns QuickUnlockOfferState.Enrolled
            every { vaultSessionManager.prepareQuickUnlock() } returnsMany listOf(
                QuickUnlockPreparationResult.Ready("first-operation"),
                QuickUnlockPreparationResult.Ready("second-operation"),
            )
            every { vaultSessionManager.cancelQuickUnlock("first-operation") } returns Unit
            target.onAction(UnlockVaultUiAction.ScreenEntered)
            val firstEvent = async { target.events.first() }
            target.onAction(UnlockVaultUiAction.RetryQuickUnlock)
            firstEvent.await()
            target.onAction(UnlockVaultUiAction.QuickUnlockPromptCancelled("first-operation"))

            assertTrue(target.uiState.value.canRetryQuickUnlock)
            assertEquals(VaultUiOperationState.Idle, target.uiState.value.operationState)
            assertEquals(UiR.string.quick_unlock_error, target.uiState.value.errorMessageRes)
            verify(exactly = 1) { vaultSessionManager.cancelQuickUnlock("first-operation") }
            verify(exactly = 0) { vaultSessionManager.finishQuickUnlock("first-operation") }
            verify(exactly = 0) { vaultSessionManager.lock() }

            val retryEvent = async { target.events.first() }
            target.onAction(UnlockVaultUiAction.RetryQuickUnlock)

            assertEquals(
                UnlockVaultUiEvent.LaunchQuickUnlockPrompt(
                    QuickUnlockPromptRequest("second-operation", QuickUnlockPromptOperation.Unlock),
                ),
                retryEvent.await(),
            )
            verify(exactly = 2) { vaultSessionManager.prepareQuickUnlock() }
        }

    @Test
    fun `manual quick unlock preparation failure keeps unlock screen available`() = runTest {
        every { vaultSessionManager.quickUnlockOfferState() } returns QuickUnlockOfferState.Enrolled
        every { vaultSessionManager.prepareQuickUnlock() } returns
                QuickUnlockPreparationResult.TemporarilyUnavailable

        target.onAction(UnlockVaultUiAction.ScreenEntered)
        target.onAction(UnlockVaultUiAction.RetryQuickUnlock)

        assertEquals(VaultUiOperationState.Idle, target.uiState.value.operationState)
        assertEquals(UiR.string.quick_unlock_error, target.uiState.value.errorMessageRes)
        assertTrue(target.uiState.value.hasQuickUnlockEnrollment)
        assertTrue(target.uiState.value.canRetryQuickUnlock)
        verify(exactly = 0) { vaultSessionManager.lock() }
    }

    @Test
    fun `quick unlock authentication success completes the operation and navigates`() = runTest {
        every { vaultSessionManager.quickUnlockOfferState() } returns QuickUnlockOfferState.Enrolled
        every { vaultSessionManager.prepareQuickUnlock() } returns
                QuickUnlockPreparationResult.Ready("quick-operation")
        every { vaultSessionManager.finishQuickUnlock("quick-operation") } returns
                QuickUnlockCompletionResult.Unlocked
        target.onAction(UnlockVaultUiAction.ScreenEntered)
        val promptEvent = async { target.events.first() }
        target.onAction(UnlockVaultUiAction.RetryQuickUnlock)
        promptEvent.await()
        val navigationEvent = async { target.events.first() }
        target.onAction(UnlockVaultUiAction.QuickUnlockPromptSucceeded("quick-operation"))

        assertEquals(UnlockVaultUiEvent.NavigateToApp, navigationEvent.await())
        verify(exactly = 1) { vaultSessionManager.finishQuickUnlock("quick-operation") }
    }

    @Test
    fun `passphrase unlock offer cancellation discards enrollment and then navigates`() = runTest {
        val passphrase = sensitiveValue()
        every { vaultSessionManager.unlockWithPassphrase(passphrase) } returns null
        every { vaultSessionManager.isUnlocked() } returns true
        every { vaultSessionManager.quickUnlockOfferState() } returns QuickUnlockOfferState.Available
        every { vaultSessionManager.prepareQuickUnlockEnrollment(true) } returns
                QuickUnlockEnrollmentPreparationResult.Ready("enrollment-operation")
        every { vaultSessionManager.cancelQuickUnlock("enrollment-operation") } returns Unit

        target.onAction(UnlockVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(UnlockVaultUiAction.Submit)
        advanceUntilIdle()
        assertTrue(target.uiState.value.showQuickUnlockOffer)

        val promptEvent = async { target.events.first() }
        target.onAction(UnlockVaultUiAction.EnableQuickUnlock)

        assertEquals(
            UnlockVaultUiEvent.LaunchQuickUnlockPrompt(
                QuickUnlockPromptRequest(
                    "enrollment-operation",
                    QuickUnlockPromptOperation.Enrollment
                ),
            ),
            promptEvent.await(),
        )
        val navigationEvent = async { target.events.first() }
        target.onAction(UnlockVaultUiAction.QuickUnlockPromptCancelled("enrollment-operation"))

        assertEquals(UnlockVaultUiEvent.NavigateToApp, navigationEvent.await())
        verify(exactly = 1) { vaultSessionManager.cancelQuickUnlock("enrollment-operation") }
        verify(exactly = 0) { vaultSessionManager.lock() }
    }

    @Test
    fun `pending settings enrollment is consumed by the next passphrase unlock`() = runTest {
        val passphrase = sensitiveValue()
        PendingQuickUnlockEnrollment.request()
        every { vaultSessionManager.unlockWithPassphrase(passphrase) } returns null
        every { vaultSessionManager.isUnlocked() } returns true
        every { vaultSessionManager.prepareQuickUnlockEnrollment(true) } returns
                QuickUnlockEnrollmentPreparationResult.Ready("pending-enrollment")
        val event = async { target.events.first() }

        target.onAction(UnlockVaultUiAction.PassphraseChanged(passphrase))
        target.onAction(UnlockVaultUiAction.Submit)
        advanceUntilIdle()

        assertEquals(
            UnlockVaultUiEvent.LaunchQuickUnlockPrompt(
                QuickUnlockPromptRequest(
                    "pending-enrollment",
                    QuickUnlockPromptOperation.Enrollment
                ),
            ),
            event.await(),
        )
        verify(exactly = 1) { vaultSessionManager.prepareQuickUnlockEnrollment(true) }
    }

    private fun sensitiveValue(): String = UUID.randomUUID().toString()
}
