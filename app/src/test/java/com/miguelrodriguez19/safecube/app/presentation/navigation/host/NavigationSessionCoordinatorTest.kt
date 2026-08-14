package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationSessionCoordinatorTest {
    @Test
    fun `expired session redirects to login root`() {
        val destination = resolveSessionRedirectTarget(
            sessionState = SessionState.LoggedOut(SessionTerminationReason.SessionExpired),
            currentRoute = Routes.Vault,
        )

        assertEquals(Routes.Login, destination)
        assertTrue(
            shouldShowSessionExpiredMessage(
                SessionState.LoggedOut(SessionTerminationReason.SessionExpired),
            ),
        )
    }

    @Test
    fun `manual logout redirects to welcome without expiry message`() {
        val state = SessionState.LoggedOut(SessionTerminationReason.ManualLogout)

        assertEquals(
            Routes.Welcome,
            resolveSessionRedirectTarget(state, Routes.Settings),
        )
        assertFalse(shouldShowSessionExpiredMessage(state))
    }

    @Test
    fun `logged in session leaves pre-auth routes at post-login gate`() {
        assertEquals(
            Routes.PostLoginGate,
            resolveSessionRedirectTarget(
                SessionState.LoggedInVaultLocked,
                Routes.Login,
            ),
        )
    }
}
