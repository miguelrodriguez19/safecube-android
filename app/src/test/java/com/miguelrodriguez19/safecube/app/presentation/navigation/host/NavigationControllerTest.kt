package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import androidx.navigation3.runtime.NavKey
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class NavigationControllerTest {
    @Test
    fun `set root replaces the entire protected stack immediately and is idempotent`() {
        val stack = mutableListOf<NavKey>(Routes.Vault, Routes.Settings, Routes.CreateNote)
        val target = NavigationController(stack)

        target.setRoot(Routes.UnlockVault)
        target.setRoot(Routes.UnlockVault)

        assertEquals(listOf(Routes.UnlockVault), stack)
        assertTrue(target.instantTransition)
    }

    @Test
    fun `replacement preserves earlier entries and suppresses outgoing animation`() {
        val stack = mutableListOf<NavKey>(Routes.Login, Routes.CreateVault)
        val target = NavigationController(stack)

        target.replaceCurrent(Routes.RecoveryKey)

        assertEquals(listOf(Routes.Login, Routes.RecoveryKey), stack)
        assertTrue(target.instantTransition)
    }

    @Test
    fun `ordinary navigation preserves existing tab callback stack semantics`() {
        val stack = mutableListOf<NavKey>(Routes.Vault)
        val target = NavigationController(stack)

        target.addRoute(Routes.Settings)
        target.addRoute(Routes.Vault)

        assertEquals(listOf(Routes.Vault, Routes.Settings, Routes.Vault), stack)
        assertFalse(target.instantTransition)
    }

    @Test
    fun `settings back returns to the nearest vault root without leaving section entries`() {
        val stack = mutableListOf<NavKey>(Routes.Vault, Routes.Settings, Routes.Settings)
        val target = NavigationController(stack)

        target.moveToVaultFromAppSection()

        assertEquals(listOf(Routes.Vault), stack)
        assertFalse(target.instantTransition)
    }

    @Test
    fun `restored settings without a vault entry falls back to vault`() {
        val stack = mutableListOf<NavKey>(Routes.Settings)
        val target = NavigationController(stack)

        target.moveToVaultFromAppSection()

        assertEquals(listOf(Routes.Vault), stack)
    }

    @Test
    fun `editor back removes only the current entry`() {
        val stack = mutableListOf<NavKey>(Routes.Vault, Routes.CreateNote)
        val target = NavigationController(stack)

        target.popBackStack()

        assertEquals(listOf(Routes.Vault), stack)
        assertFalse(target.instantTransition)
    }
}
