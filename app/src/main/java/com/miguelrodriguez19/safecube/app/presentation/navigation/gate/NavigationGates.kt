package com.miguelrodriguez19.safecube.app.presentation.navigation.gate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.event.PostLoginGateUiEvent
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.viewmodel.PostLoginGateViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitializationStatus
import com.miguelrodriguez19.safecube.feature.auth.presentation.gate.ui.PostLoginGateScreen

@Composable
fun PostLoginGateRoute(
    onCreateVault: () -> Unit,
    onRecoveryKey: () -> Unit,
    onUnlockVault: () -> Unit,
    onHome: () -> Unit,
    viewModel: PostLoginGateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PostLoginGateUiEvent.CreateVault -> onCreateVault()
                PostLoginGateUiEvent.RecoveryKey -> onRecoveryKey()
                PostLoginGateUiEvent.UnlockVault -> onUnlockVault()
                PostLoginGateUiEvent.Home -> onHome()
            }
        }
    }

    PostLoginGateScreen(
        isLoading = uiState.isLoading,
        messageRes = uiState.messageRes,
        onRetry = viewModel::retry,
    )
}

internal fun resolveGateDestination(
    vaultState: VaultState,
    pendingInitializationStatus: PendingVaultInitializationStatus =
        PendingVaultInitializationStatus.None,
): GateDestination {
    if (vaultState == VaultState.AuthenticationRequired) {
        return GateDestination.AuthenticationRequired
    }

    return when (pendingInitializationStatus) {
        PendingVaultInitializationStatus.None -> resolveVaultStateDestination(vaultState)
        PendingVaultInitializationStatus.AwaitingRemoteConfirmation -> GateDestination.CreateVault
        PendingVaultInitializationStatus.RemoteConfirmed -> GateDestination.RecoveryKey
        PendingVaultInitializationStatus.Corrupted -> GateDestination.PendingInitializationError
    }
}

private fun resolveVaultStateDestination(vaultState: VaultState): GateDestination = when (vaultState) {
    VaultState.InitialLoading -> GateDestination.Stay
    VaultState.NotInitialized -> GateDestination.CreateVault
    VaultState.Locked -> GateDestination.UnlockVault
    VaultState.Unlocked -> GateDestination.Home
    is VaultState.RetryableRemoteFailure ->
        if (vaultState.hasValidLocalKeyMaterial) {
            GateDestination.UnlockVault
        } else {
            GateDestination.Stay
        }
    VaultState.CorruptedLocalKeyMaterial,
    is VaultState.TerminalRemoteFailure,
        -> GateDestination.Stay
    VaultState.AuthenticationRequired -> GateDestination.AuthenticationRequired
}

internal enum class GateDestination {
    Stay,
    CreateVault,
    RecoveryKey,
    UnlockVault,
    Home,
    AuthenticationRequired,
    PendingInitializationError,
}

internal fun resolveGateMessage(
    vaultState: VaultState,
    pendingInitializationStatus: PendingVaultInitializationStatus?,
): Int = when (pendingInitializationStatus) {
    PendingVaultInitializationStatus.Corrupted -> UiR.string.vault_error_material_corrupted
    PendingVaultInitializationStatus.AwaitingRemoteConfirmation,
    PendingVaultInitializationStatus.RemoteConfirmed,
    PendingVaultInitializationStatus.None,
    null,
        -> resolveVaultStateMessage(vaultState)
}

private fun resolveVaultStateMessage(vaultState: VaultState): Int = when (vaultState) {
    VaultState.InitialLoading,
    VaultState.AuthenticationRequired,
    VaultState.NotInitialized,
    VaultState.Locked,
    VaultState.Unlocked,
        -> UiR.string.vault_bootstrap_loading

    is VaultState.RetryableRemoteFailure -> if (vaultState.hasValidLocalKeyMaterial) {
        UiR.string.vault_bootstrap_loading
    } else {
        UiR.string.vault_bootstrap_retryable_error
    }
    VaultState.CorruptedLocalKeyMaterial -> UiR.string.vault_bootstrap_corrupted_error
    is VaultState.TerminalRemoteFailure -> UiR.string.vault_bootstrap_terminal_error
}

internal fun shouldShowGateLoading(
    vaultState: VaultState,
    pendingInitializationStatus: PendingVaultInitializationStatus? =
        PendingVaultInitializationStatus.None,
): Boolean = when (pendingInitializationStatus) {
    PendingVaultInitializationStatus.AwaitingRemoteConfirmation,
    PendingVaultInitializationStatus.RemoteConfirmed,
        -> true

    PendingVaultInitializationStatus.Corrupted -> false
    PendingVaultInitializationStatus.None,
    null,
        -> when (vaultState) {
            VaultState.InitialLoading,
            VaultState.NotInitialized,
            VaultState.Locked,
            VaultState.Unlocked,
            VaultState.AuthenticationRequired,
                -> true

            is VaultState.RetryableRemoteFailure -> vaultState.hasValidLocalKeyMaterial
            VaultState.CorruptedLocalKeyMaterial,
            is VaultState.TerminalRemoteFailure,
                -> false
        }
}
