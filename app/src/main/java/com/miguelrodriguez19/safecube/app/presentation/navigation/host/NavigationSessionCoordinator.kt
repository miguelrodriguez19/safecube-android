package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.requiresUnlockedVault
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState

@Composable
internal fun ObserveSessionRedirect(
    sessionState: SessionState,
    vaultState: VaultState,
    currentRoute: Routes?,
    setRoot: (Routes) -> Unit,
) {
    LaunchedEffect(sessionState, vaultState, currentRoute) {
        resolveSessionRedirectTarget(
            sessionState = sessionState,
            currentRoute = currentRoute,
            vaultState = vaultState,
        )?.let(setRoot)
    }
}

internal fun resolveSessionRedirectTarget(
    sessionState: SessionState,
    currentRoute: Routes?,
    vaultState: VaultState = VaultState.Unlocked,
): Routes? = when (sessionState) {
    is SessionState.LoggedOut -> {
        val destination = when (sessionState.reason) {
            null,
            SessionTerminationReason.ManualLogout,
                -> Routes.Welcome

            SessionTerminationReason.SessionExpired,
            SessionTerminationReason.RefreshCredentialsRejected,
            SessionTerminationReason.LocalIntegrityFailure,
                -> Routes.Login
        }
        when (currentRoute) {
            Routes.Splash -> destination
            in PRE_AUTH_ROUTES -> null
            else -> destination
        }
    }

    SessionState.LoggedInVaultLocked -> Routes.PostLoginGate.takeIf {
        currentRoute in PRE_AUTH_ROUTES ||
            (vaultState == VaultState.InitialLoading &&
                currentRoute !in setOf(Routes.Splash, Routes.PostLoginGate))
    }
}

internal fun shouldGuardRestoredNavigation(
    sessionState: SessionState,
    vaultState: VaultState,
    currentRoute: Routes?,
): Boolean {
    val route = currentRoute ?: return true

    return when (sessionState) {
        is SessionState.LoggedOut -> route !in PRE_AUTH_ROUTES
        SessionState.LoggedInVaultLocked -> when {
            vaultState == VaultState.InitialLoading ->
                route !in setOf(Routes.Splash, Routes.PostLoginGate)

            vaultState != VaultState.Unlocked -> route.requiresUnlockedVault()
            else -> false
        }
    }
}

internal fun shouldShowSessionExpiredMessage(sessionState: SessionState): Boolean =
    sessionState is SessionState.LoggedOut && sessionState.reason in setOf(
        SessionTerminationReason.SessionExpired,
        SessionTerminationReason.RefreshCredentialsRejected,
    )

private val PRE_AUTH_ROUTES = setOf(
    Routes.Splash,
    Routes.Welcome,
    Routes.Login,
    Routes.Signup,
)
