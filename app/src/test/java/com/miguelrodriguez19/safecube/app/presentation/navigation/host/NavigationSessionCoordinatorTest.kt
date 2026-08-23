package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
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

    @Test
    fun `locked vault redirects protected content to unlock root`() {
        assertEquals(
            Routes.UnlockVault,
            resolveVaultRedirectTarget(VaultState.Locked, Routes.Settings),
        )
        assertEquals(
            Routes.UnlockVault,
            resolveVaultRedirectTarget(VaultState.Locked, Routes.ChangePassphrase),
        )
    }

    @Test
    fun `locked vault does not redirect the post-login gate`() {
        assertEquals(
            null,
            resolveVaultRedirectTarget(VaultState.Locked, Routes.PostLoginGate),
        )
    }

    @Test
    fun `activity recreation while vault remains unlocked does not redirect`() {
        assertEquals(
            null,
            resolveVaultRedirectTarget(VaultState.Unlocked, Routes.Vault),
        )
        assertEquals(
            null,
            resolveVaultRedirectTarget(VaultState.Unlocked, Routes.ChangePassphrase),
        )
    }
}
