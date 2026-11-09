package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationMotionPolicyTest {
    @Test
    fun `ordinary tab and editor navigation permits subtle fades`() {
        assertTrue(shouldAnimateNavigation(Routes.Vault, Routes.Settings, false))
        assertTrue(shouldAnimateNavigation(Routes.Settings, Routes.Vault, false))
        assertTrue(shouldAnimateNavigation(Routes.Vault, Routes.CreateNote, false))
        assertTrue(shouldAnimateNavigation(Routes.CreateNote, Routes.Vault, false))
    }

    @Test
    fun `root replacement skips animation even between protected routes`() {
        assertFalse(shouldAnimateNavigation(Routes.Settings, Routes.Vault, true))
    }

    @Test
    fun `bootstrap authentication and sensitive recovery boundaries never fade`() {
        val gates = listOf(
            Routes.Splash, Routes.PostLoginGate, Routes.Welcome, Routes.Login, Routes.Signup,
            Routes.CreateVault, Routes.RecoveryKey, Routes.UnlockVault,
        )

        gates.forEach { gate ->
            assertFalse(shouldAnimateNavigation(gate, Routes.Vault, false))
            assertFalse(shouldAnimateNavigation(Routes.Vault, gate, false))
        }
        assertFalse(shouldAnimateNavigation(null, Routes.Vault, false))
        assertFalse(shouldAnimateNavigation(Routes.Vault, null, false))
    }
}
