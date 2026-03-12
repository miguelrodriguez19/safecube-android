package com.miguelrodriguez19.safecube.app.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.feature.auth.screens.PostLoginGateScreen
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

@Composable
fun SplashGateScreen() {
    LoadingGateScreen(message = "Loading session...")
}

@Composable
fun PostLoginGateRoute(
    onCreateVault: () -> Unit,
    onUnlockVault: () -> Unit,
    onHome: () -> Unit,
) {
    val entryPoint = rememberNavigationGatesEntryPoint()
    val vaultSessionManager = remember(entryPoint) { entryPoint.vaultSessionManager() }
    val vaultState by vaultSessionManager.vaultState.collectAsState()

    LaunchedEffect(Unit) {
        vaultSessionManager.refreshVaultState()
    }

    LaunchedEffect(vaultState) {
        when (vaultState) {
            VaultState.Unknown -> Unit
            VaultState.NotInitialized -> onCreateVault()
            VaultState.Locked -> onUnlockVault()
            VaultState.Unlocked -> onHome()
        }
    }

    PostLoginGateScreen()
}

@Composable
private fun LoadingGateScreen(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
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

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NavigationGatesEntryPoint {
    fun authRepository(): AuthRepository
    fun sessionManager(): SessionManager
    fun vaultSessionManager(): VaultSessionManager
}
