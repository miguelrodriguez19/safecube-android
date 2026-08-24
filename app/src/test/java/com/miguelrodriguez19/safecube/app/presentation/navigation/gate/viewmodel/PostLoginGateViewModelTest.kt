@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.miguelrodriguez19.safecube.app.presentation.navigation.gate.viewmodel

import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.event.PostLoginGateUiEvent
import com.miguelrodriguez19.safecube.app.test.MainDispatcherRule
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitializationStatus
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultInitializeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class PostLoginGateViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val vaultSessionManager = mockk<VaultSessionManager>()
    private val vaultInitializeUseCase = mockk<VaultInitializeUseCase>()
    private val accountSessionLifecycle = mockk<AccountSessionLifecycle>()

    @Test
    fun `confirmed pending recovery key emits recovery navigation`() = runTest {
        every { vaultInitializeUseCase.readPendingInitializationStatus() } returns
            PendingVaultInitializationStatus.RemoteConfirmed
        val vaultState = stubVaultRefresh(VaultState.Locked)
        val target = PostLoginGateViewModel(
            vaultSessionManager = vaultSessionManager,
            vaultInitializeUseCase = vaultInitializeUseCase,
            accountSessionLifecycle = accountSessionLifecycle,
        )
        val event = async { target.events.first() }

        advanceUntilIdle()

        assertEquals(PostLoginGateUiEvent.RecoveryKey, event.await())
        assertEquals(VaultState.Locked, vaultState.value)
    }

    @Test
    fun `locked vault without pending initialization emits unlock navigation`() = runTest {
        every { vaultInitializeUseCase.readPendingInitializationStatus() } returns
            PendingVaultInitializationStatus.None
        stubVaultRefresh(VaultState.Locked)
        val target = PostLoginGateViewModel(
            vaultSessionManager = vaultSessionManager,
            vaultInitializeUseCase = vaultInitializeUseCase,
            accountSessionLifecycle = accountSessionLifecycle,
        )
        val event = async { target.events.first() }

        advanceUntilIdle()

        assertEquals(PostLoginGateUiEvent.UnlockVault, event.await())
    }

    @Test
    fun `authentication required terminates the account session`() = runTest {
        every { vaultInitializeUseCase.readPendingInitializationStatus() } returns
            PendingVaultInitializationStatus.None
        stubVaultRefresh(VaultState.AuthenticationRequired)
        coEvery {
            accountSessionLifecycle.terminateSession(
                reason = SessionTerminationReason.SessionExpired,
            )
        } returns AccountSessionResult.Success
        PostLoginGateViewModel(
            vaultSessionManager = vaultSessionManager,
            vaultInitializeUseCase = vaultInitializeUseCase,
            accountSessionLifecycle = accountSessionLifecycle,
        )

        advanceUntilIdle()

        coVerify(exactly = 1) {
            accountSessionLifecycle.terminateSession(
                reason = SessionTerminationReason.SessionExpired,
            )
        }
    }

    @Test
    fun `corrupted pending record exposes terminal gate error without navigation`() = runTest {
        every { vaultInitializeUseCase.readPendingInitializationStatus() } returns
            PendingVaultInitializationStatus.Corrupted
        stubVaultRefresh(VaultState.Locked)
        val target = PostLoginGateViewModel(
            vaultSessionManager = vaultSessionManager,
            vaultInitializeUseCase = vaultInitializeUseCase,
            accountSessionLifecycle = accountSessionLifecycle,
        )

        advanceUntilIdle()

        assertEquals(false, target.uiState.value.isLoading)
        assertEquals(UiR.string.vault_error_material_corrupted, target.uiState.value.messageRes)
    }

    private fun stubVaultRefresh(state: VaultState): MutableStateFlow<VaultState> {
        val vaultState = MutableStateFlow<VaultState>(VaultState.InitialLoading)
        every { vaultSessionManager.vaultState } returns vaultState
        coEvery { vaultSessionManager.refreshVaultState() } coAnswers {
            vaultState.value = state
        }
        return vaultState
    }
}
