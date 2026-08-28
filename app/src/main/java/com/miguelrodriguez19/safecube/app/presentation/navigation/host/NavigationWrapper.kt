package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.app.presentation.ui.SplashGateScreen
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

    val setRoot: (Routes) -> Unit = setRoot@{ route ->
        if (backStack.size == 1 && backStack.lastOrNull() == route) return@setRoot
        while (backStack.isNotEmpty()) {
            backStack.removeLastOrNull()
        }
        backStack.add(route)
    }

    val replaceCurrent: (Routes) -> Unit = { route ->
        backStack.removeLastOrNull()
        backStack.add(route)
    }

    val addRoute: (Routes) -> Unit = { route ->
        backStack.add(route)
    }

    val moveToVaultFromAppSection: () -> Unit = {
        while (backStack.isNotEmpty() && backStack.lastOrNull() != Routes.Vault) {
            backStack.removeLastOrNull()
        }
        if (backStack.lastOrNull() != Routes.Vault) {
            backStack.add(Routes.Vault)
        }
    }

    val moveToSettingsFromProfile: () -> Unit = {
        while (backStack.isNotEmpty() && backStack.lastOrNull() != Routes.Settings) {
            backStack.removeLastOrNull()
        }
        if (backStack.lastOrNull() != Routes.Settings) {
            backStack.add(Routes.Settings)
        }
    }

    val popBackStack: () -> Unit = {
        backStack.removeLastOrNull()
    }

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

    val onVaultBackPressed = rememberVaultBackPressHandler(activity = activity)

    ObserveSessionRedirect(
        sessionState = sessionState,
        vaultState = vaultState,
        currentRoute = currentRoute,
        setRoot = setRoot,
    )
    ObserveVaultRedirect(
        vaultState = vaultState,
        currentRoute = currentRoute,
        setRoot = setRoot,
    )

    if (shouldGuardRestoredNavigation(
            sessionState = sessionState,
            vaultState = vaultState,
            currentRoute = currentRoute,
        )
    ) {
        SplashGateScreen()
    } else {
        NavDisplay(
            backStack = backStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            onBack = {
                handleBackNavigation(
                    currentRoute = backStack.lastOrNull() as? Routes,
                    moveToVaultFromAppSection = moveToVaultFromAppSection,
                    moveToSettingsFromProfile = moveToSettingsFromProfile,
                    onVaultBackPressed = onVaultBackPressed,
                    popBackStack = popBackStack,
                )
            },
            entryProvider = navigationEntryProvider(
                setRoot = setRoot,
                addRoute = addRoute,
                replaceCurrent = replaceCurrent,
                popBackStack = popBackStack,
                onLogout = onLogout,
                onLockNow = {
                    dependencies.vaultAutoLockController.lockNow()
                    setRoot(Routes.UnlockVault)
                },
            )
        )
    }

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
