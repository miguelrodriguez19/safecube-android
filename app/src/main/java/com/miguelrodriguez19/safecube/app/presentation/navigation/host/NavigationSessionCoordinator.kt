package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState

@Composable
internal fun ObserveSessionRedirect(
    sessionState: SessionState,
    currentRoute: Routes?,
    setRoot: (Routes) -> Unit,
) {
    LaunchedEffect(sessionState) {
        when (sessionState) {
            SessionState.LoggedOut -> {
                if (currentRoute !in LOGGED_OUT_ROUTES) {
                    setRoot(Routes.Welcome)
                }
            }

            SessionState.LoggedInVaultLocked -> {
                if (currentRoute in PRE_AUTH_ROUTES) {
                    setRoot(Routes.PostLoginGate)
                }
            }
        }
    }
}

private val PRE_AUTH_ROUTES = setOf(
    Routes.Splash,
    Routes.Welcome,
    Routes.Login,
    Routes.Signup,
)

private val LOGGED_OUT_ROUTES = setOf(
    Routes.Welcome,
    Routes.Login,
    Routes.Signup,
)
