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
    fun `logged out session keeps the selected pre-auth route`() {
        assertEquals(
            null,
            resolveSessionRedirectTarget(
                sessionState = SessionState.LoggedOut(),
                currentRoute = Routes.Login,
            ),
        )
    }

    @Test
    fun `logged out session resolves splash to welcome`() {
        assertEquals(
            Routes.Welcome,
            resolveSessionRedirectTarget(
                sessionState = SessionState.LoggedOut(),
                currentRoute = Routes.Splash,
            ),
        )
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
    fun `process state recreation redirects restored protected route to bootstrap gate`() {
        assertEquals(
            Routes.PostLoginGate,
            resolveSessionRedirectTarget(
                sessionState = SessionState.LoggedInVaultLocked,
                currentRoute = Routes.Vault,
                vaultState = VaultState.InitialLoading,
            ),
        )
        assertTrue(
            shouldGuardRestoredNavigation(
                sessionState = SessionState.LoggedInVaultLocked,
                vaultState = VaultState.InitialLoading,
                currentRoute = Routes.Vault,
            ),
        )
    }

    @Test
    fun `activity recreation while vault remains unlocked keeps restored protected route`() {
        assertFalse(
            shouldGuardRestoredNavigation(
                sessionState = SessionState.LoggedInVaultLocked,
                vaultState = VaultState.Unlocked,
                currentRoute = Routes.Vault,
            ),
        )
        assertEquals(
            null,
            resolveSessionRedirectTarget(
                sessionState = SessionState.LoggedInVaultLocked,
                currentRoute = Routes.Vault,
                vaultState = VaultState.Unlocked,
            ),
        )
    }

    @Test
    fun `locked restored protected route is guarded while unlock root is safe`() {
        assertTrue(
            shouldGuardRestoredNavigation(
                sessionState = SessionState.LoggedInVaultLocked,
                vaultState = VaultState.Locked,
                currentRoute = Routes.Settings,
            ),
        )
        assertFalse(
            shouldGuardRestoredNavigation(
                sessionState = SessionState.LoggedInVaultLocked,
                vaultState = VaultState.Locked,
                currentRoute = Routes.UnlockVault,
            ),
        )
    }

    @Test
    fun `logged out restored protected route is guarded`() {
        assertTrue(
            shouldGuardRestoredNavigation(
                sessionState = SessionState.LoggedOut(),
                vaultState = VaultState.InitialLoading,
                currentRoute = Routes.Vault,
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
    fun `activity recreation helper does not redirect an unlocked vault`() {
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
