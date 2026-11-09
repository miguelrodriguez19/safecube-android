package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.navigation3.runtime.NavKey
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes

/** Mutations retain the Phase 7 stack contract; animation never owns a navigation operation. */
internal class NavigationController(private val backStack: MutableList<NavKey>) {
    var instantTransition: Boolean by mutableStateOf(true)
        private set

    fun setRoot(route: Routes) {
        if (backStack.size == 1 && backStack.lastOrNull() == route) return
        instantTransition = true
        backStack.clear()
        backStack.add(route)
    }

    fun replaceCurrent(route: Routes) {
        instantTransition = true
        backStack.removeLastOrNull()
        backStack.add(route)
    }

    fun addRoute(route: Routes) {
        instantTransition = false
        backStack.add(route)
    }

    fun popBackStack() {
        instantTransition = false
        backStack.removeLastOrNull()
    }

    fun moveToVaultFromAppSection() {
        instantTransition = false
        while (backStack.isNotEmpty() && backStack.lastOrNull() != Routes.Vault) {
            backStack.removeLastOrNull()
        }
        if (backStack.lastOrNull() != Routes.Vault) {
            backStack.add(Routes.Vault)
        }
    }
}
