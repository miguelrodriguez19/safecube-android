package com.miguelrodriguez19.safecube.app.presentation.navigation.gate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitializationStatus
import com.miguelrodriguez19.safecube.feature.auth.presentation.gate.ui.PostLoginGateScreen
import dagger.hilt.android.EntryPointAccessors



@Composable
fun PostLoginGateRoute(
    onCreateVault: () -> Unit,
    onRecoveryKey: () -> Unit,
    onUnlockVault: () -> Unit,
    onHome: () -> Unit,
) {
    val entryPoint = rememberNavigationGatesEntryPoint()
    val vaultSessionManager = remember(entryPoint) { entryPoint.vaultSessionManager() }
    val vaultInitializeUseCase = remember(entryPoint) { entryPoint.vaultInitializeUseCase() }
    val accountSessionLifecycle = remember(entryPoint) { entryPoint.accountSessionLifecycle() }
    val vaultState by vaultSessionManager.vaultState.collectAsState()
    var refreshAttempt by remember { mutableIntStateOf(0) }
    var isResolving by remember { mutableStateOf(true) }
    var pendingInitializationStatus by remember {
        mutableStateOf<PendingVaultInitializationStatus?>(null)
    }

    LaunchedEffect(refreshAttempt) {
        isResolving = true
        pendingInitializationStatus = null
        try {
            pendingInitializationStatus = vaultInitializeUseCase.readPendingInitializationStatus()
            vaultSessionManager.refreshVaultState()
        } finally {
            isResolving = false
        }
    }

    LaunchedEffect(vaultState, pendingInitializationStatus, isResolving) {
        if (isResolving || pendingInitializationStatus == null) return@LaunchedEffect

        when (resolveGateDestination(vaultState, pendingInitializationStatus!!)) {
            GateDestination.Stay -> Unit
            GateDestination.CreateVault -> onCreateVault()
            GateDestination.RecoveryKey -> onRecoveryKey()
            GateDestination.UnlockVault -> onUnlockVault()
            GateDestination.Home -> onHome()
            GateDestination.PendingInitializationError -> Unit
            GateDestination.AuthenticationRequired -> accountSessionLifecycle.terminateSession(
                reason = SessionTerminationReason.SessionExpired,
            )
        }
    }

    PostLoginGateScreen(
        isLoading = isResolving || shouldShowGateLoading(vaultState, pendingInitializationStatus),
        messageRes = resolveGateMessage(vaultState, pendingInitializationStatus),
        onRetry = { refreshAttempt += 1 },
    )
}

@Composable
private fun rememberNavigationGatesEntryPoint(): NavigationGatesEntryPoint {
    val context = LocalContext.current
    return remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            NavigationGatesEntryPoint::class.java,
        )
    }
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

private fun resolveGateMessage(
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

private fun shouldShowGateLoading(
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
