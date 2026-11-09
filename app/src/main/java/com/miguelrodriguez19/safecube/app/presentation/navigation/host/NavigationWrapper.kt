package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.rememberNavBackStack
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import kotlinx.coroutines.launch

@Composable
fun NavigationWrapper() {
    val backStack = rememberNavBackStack(Routes.Splash)
    val context = LocalContext.current
    val activity = remember(context) { context as? Activity }
    val dependencies = rememberNavigationDependencies()
    val sessionState by dependencies.sessionManager.sessionState.collectAsState()
    val vaultState by dependencies.vaultSessionManager.vaultState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val currentRoute = backStack.lastOrNull() as? Routes
    val forcedLogoutNotice = resolveForcedLogoutNotice(sessionState)
    val navigation = remember(backStack) { NavigationController(backStack) }

    val onLogout: () -> Unit = {
        coroutineScope.launch {
            try {
                dependencies.authRepository.logout()
            } finally {
                dependencies.accountSessionLifecycle.terminateSession(
                    reason = SessionTerminationReason.ManualLogout,
                )
            }
        }
    }

    val onVaultBackPressed = rememberVaultBackPressHandler(
        activity = activity,
        enabled = currentRoute == Routes.Vault &&
            !shouldGuardRestoredNavigation(sessionState, vaultState, currentRoute),
    )

    ObserveSessionRedirect(
        sessionState = sessionState,
        vaultState = vaultState,
        currentRoute = currentRoute,
        setRoot = navigation::setRoot,
    )
    ObserveVaultRedirect(
        vaultState = vaultState,
        currentRoute = currentRoute,
        setRoot = navigation::setRoot,
    )

    SafeCubeNavigationHost(
        backStack = backStack,
        sessionState = sessionState,
        vaultState = vaultState,
        instantTransition = navigation.instantTransition,
        onBack = {
            handleBackNavigation(
                currentRoute = backStack.lastOrNull() as? Routes,
                moveToVaultFromAppSection = navigation::moveToVaultFromAppSection,
                onVaultBackPressed = onVaultBackPressed,
                popBackStack = navigation::popBackStack,
            )
        },
        entryProvider = navigationEntryProvider(
            setRoot = navigation::setRoot,
            addRoute = navigation::addRoute,
            replaceCurrent = navigation::replaceCurrent,
            popBackStack = navigation::popBackStack,
            onLogout = onLogout,
            onLockNow = {
                dependencies.vaultAutoLockController.lockNow()
                navigation.setRoot(Routes.UnlockVault)
            },
        ),
    )

    if (currentRoute == Routes.Login && forcedLogoutNotice != null) {
        ForcedLogoutNoticeDialog(
            notice = forcedLogoutNotice,
            onAcknowledged = {
                val reason = (sessionState as? SessionState.LoggedOut)?.reason
                if (reason != null) {
                    dependencies.sessionManager.acknowledgeTermination(reason)
                }
            },
        )
    }
}
