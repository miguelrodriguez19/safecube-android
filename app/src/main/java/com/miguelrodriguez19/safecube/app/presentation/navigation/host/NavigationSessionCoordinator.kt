package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason

@Composable
internal fun ObserveSessionRedirect(
    sessionState: SessionState,
    currentRoute: Routes?,
    setRoot: (Routes) -> Unit,
) {
    LaunchedEffect(sessionState) {
        resolveSessionRedirectTarget(sessionState, currentRoute)?.let(setRoot)
    }
}

internal fun resolveSessionRedirectTarget(
    sessionState: SessionState,
    currentRoute: Routes?,
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
        destination.takeUnless { it == currentRoute }
    }

    SessionState.LoggedInVaultLocked ->
        Routes.PostLoginGate.takeIf { currentRoute in PRE_AUTH_ROUTES }
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
