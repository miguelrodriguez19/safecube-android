package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import kotlinx.coroutines.launch

@Composable
fun NavigationWrapper() {
    val backStack = rememberNavBackStack(Routes.Splash)
    val context = LocalContext.current
    val activity = remember(context) { context as? Activity }
    val dependencies = rememberNavigationDependencies()
    val sessionState by dependencies.sessionManager.sessionState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    var pendingRecoveryKey by rememberSaveable { mutableStateOf<String?>(null) }

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
        currentRoute = backStack.lastOrNull() as? Routes,
        setRoot = setRoot,
    )

    NavDisplay(
        backStack = backStack,
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
            pendingRecoveryKey = pendingRecoveryKey,
            onPendingRecoveryKeyChanged = { pendingRecoveryKey = it },
            onLogout = onLogout,
            showSessionExpiredMessage = shouldShowSessionExpiredMessage(sessionState),
        )
    )
}
