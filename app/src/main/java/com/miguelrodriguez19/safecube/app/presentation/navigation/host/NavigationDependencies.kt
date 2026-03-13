package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.NavigationGatesEntryPoint
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import dagger.hilt.android.EntryPointAccessors

internal data class NavigationDependencies(
    val authRepository: AuthRepository,
    val sessionManager: SessionManager,
    val vaultSessionManager: VaultSessionManager,
)

@Composable
internal fun rememberNavigationDependencies(): NavigationDependencies {
    val context = LocalContext.current
    val entryPoint = remember(context) {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            NavigationGatesEntryPoint::class.java,
        )
    }

    return remember(entryPoint) {
        NavigationDependencies(
            authRepository = entryPoint.authRepository(),
            sessionManager = entryPoint.sessionManager(),
            vaultSessionManager = entryPoint.vaultSessionManager(),
        )
    }
}
