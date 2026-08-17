package com.miguelrodriguez19.safecube.app.presentation.navigation.gate

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationGatesTest {
    @Test
    fun `resolveGateDestination_whenInitialLoading_thenStaysAtGate`() {
        assertEquals(
            GateDestination.Stay,
            resolveGateDestination(VaultState.InitialLoading),
        )
    }

    @Test
    fun `resolveGateDestination_whenVaultIsNotInitialized_thenCreatesVault`() {
        assertEquals(
            GateDestination.CreateVault,
            resolveGateDestination(VaultState.NotInitialized),
        )
    }

    @Test
    fun `resolveGateDestination_whenVaultIsLocked_thenUnlocksVault`() {
        assertEquals(
            GateDestination.UnlockVault,
            resolveGateDestination(VaultState.Locked),
        )
    }

    @Test
    fun `resolveGateDestination_whenVaultIsUnlocked_thenOpensHome`() {
        assertEquals(
            GateDestination.Home,
            resolveGateDestination(VaultState.Unlocked),
        )
    }

    @Test
    fun `resolveGateDestination_whenRemoteFailureHasNoLocalMaterial_thenStaysAtGate`() {
        assertEquals(
            GateDestination.Stay,
            resolveGateDestination(
                VaultState.RetryableRemoteFailure(
                    failure = NetworkFailureClassifier.fromHttpStatus(503),
                    hasValidLocalKeyMaterial = false,
                ),
            ),
        )
    }

    @Test
    fun `resolveGateDestination_whenRemoteFailureHasValidLocalMaterial_thenUnlocksVaultOffline`() {
        assertEquals(
            GateDestination.UnlockVault,
            resolveGateDestination(
                VaultState.RetryableRemoteFailure(
                    failure = NetworkFailureClassifier.fromHttpStatus(503),
                    hasValidLocalKeyMaterial = true,
                ),
            ),
        )
    }

    @Test
    fun `resolveGateDestination_whenLocalMaterialIsCorrupted_thenStaysAtGate`() {
        assertEquals(
            GateDestination.Stay,
            resolveGateDestination(VaultState.CorruptedLocalKeyMaterial),
        )
    }

    @Test
    fun `resolveGateDestination_whenRemoteFailureIsTerminal_thenStaysAtGate`() {
        assertEquals(
            GateDestination.Stay,
            resolveGateDestination(
                VaultState.TerminalRemoteFailure(
                    failure = NetworkFailureClassifier.fromHttpStatus(403),
                ),
            ),
        )
    }

    @Test
    fun `resolveGateDestination_whenAuthenticationIsRequired_thenDelegatesToSessionLifecycle`() {
        assertEquals(
            GateDestination.AuthenticationRequired,
            resolveGateDestination(VaultState.AuthenticationRequired),
        )
    }
}
