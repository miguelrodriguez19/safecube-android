package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import org.junit.Assert.assertEquals
import org.junit.Test

class NavigationBackPolicyTest {
    @Test
    fun `settings back returns to vault`() {
        val calls = mutableListOf<String>()

        handleBackNavigation(
            currentRoute = Routes.Settings,
            moveToVaultFromAppSection = { calls += "vault" },
            onVaultBackPressed = { calls += "exit" },
            popBackStack = { calls += "pop" },
        )

        assertEquals(listOf("vault"), calls)
    }

    @Test
    fun `vault back delegates to protected exit policy`() {
        val calls = mutableListOf<String>()

        handleBackNavigation(
            currentRoute = Routes.Vault,
            moveToVaultFromAppSection = { calls += "vault" },
            onVaultBackPressed = { calls += "exit" },
            popBackStack = { calls += "pop" },
        )

        assertEquals(listOf("exit"), calls)
    }

    @Test
    fun `editor back pops its route`() {
        val calls = mutableListOf<String>()

        handleBackNavigation(
            currentRoute = Routes.CreatePassword,
            moveToVaultFromAppSection = { calls += "vault" },
            onVaultBackPressed = { calls += "exit" },
            popBackStack = { calls += "pop" },
        )

        assertEquals(listOf("pop"), calls)
    }
}
