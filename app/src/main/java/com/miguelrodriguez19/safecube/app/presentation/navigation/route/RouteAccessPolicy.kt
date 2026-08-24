package com.miguelrodriguez19.safecube.app.presentation.navigation.route

/**
 * Defines the minimum state required before a route may be restored.
 *
 * New routes are protected by default. A route is explicitly exempted only when it is part of
 * authentication or vault bootstrap and does not render protected vault contents.
 */
internal enum class RouteAccess {
    Public,
    SessionOnly,
    VaultUnlocked,
}

internal val Routes.access: RouteAccess
    get() = when (this) {
        Routes.Splash,
        Routes.Welcome,
        Routes.Login,
        Routes.Signup,
        Routes.Error,
            -> RouteAccess.Public

        Routes.CreateVault,
        Routes.RecoveryKey,
        Routes.UnlockVault,
        Routes.PostLoginGate,
            -> RouteAccess.SessionOnly

        else -> RouteAccess.VaultUnlocked
    }

internal fun Routes?.requiresUnlockedVault(): Boolean = this?.access == RouteAccess.VaultUnlocked
