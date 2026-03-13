package com.miguelrodriguez19.safecube.app.presentation.navigation.gate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.feature.auth.presentation.gate.ui.PostLoginGateScreen
import dagger.hilt.android.EntryPointAccessors



@Composable
fun PostLoginGateRoute(
    onCreateVault: () -> Unit,
    onUnlockVault: () -> Unit,
    onHome: () -> Unit,
) {
    val entryPoint = rememberNavigationGatesEntryPoint()
    val vaultSessionManager = remember(entryPoint) { entryPoint.vaultSessionManager() }
    val vaultState by vaultSessionManager.vaultState.collectAsState()
    var hasRefreshedOnce by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        vaultSessionManager.refreshVaultState()
        hasRefreshedOnce = true
    }

    LaunchedEffect(vaultState, hasRefreshedOnce) {
        if (!hasRefreshedOnce) return@LaunchedEffect
        when (resolveGateDestination(vaultState)) {
            GateDestination.None -> Unit
            GateDestination.CreateVault -> onCreateVault()
            GateDestination.UnlockVault -> onUnlockVault()
            GateDestination.Home -> onHome()
        }
    }

    PostLoginGateScreen()
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

private fun resolveGateDestination(vaultState: VaultState): GateDestination = when (vaultState) {
    VaultState.NotInitialized -> GateDestination.CreateVault
    VaultState.Locked -> GateDestination.UnlockVault
    VaultState.Unlocked -> GateDestination.Home
    else -> GateDestination.UnlockVault
}

private enum class GateDestination {
    None,
    CreateVault,
    UnlockVault,
    Home,
}
