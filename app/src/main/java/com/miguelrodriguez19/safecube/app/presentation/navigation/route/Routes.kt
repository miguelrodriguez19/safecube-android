package com.miguelrodriguez19.safecube.app.presentation.navigation.route

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Routes : NavKey {
    @Serializable
    data object Splash : Routes()

    @Serializable
    data object Welcome : Routes()

    @Serializable
    data object Login : Routes()

    @Serializable
    data object Signup : Routes()

    @Serializable
    data object App : Routes()

    @Serializable
    data object Vault : Routes()

    @Serializable
    data object VaultFolders : Routes()

    @Serializable
    data object Settings : Routes()

    @Serializable
    data object Profile : Routes()

    @Serializable
    data object CreateVault : Routes()

    @Serializable
    data object RecoveryKey : Routes()

    @Serializable
    data object UnlockVault : Routes()

    @Serializable
    data object PostLoginGate : Routes()

    @Serializable
    data object Error : Routes()
}

val allRoutes = listOf(
    Routes.Splash,
    Routes.Welcome,
    Routes.Login,
    Routes.Signup,
    Routes.App,
    Routes.Vault,
    Routes.VaultFolders,
    Routes.Settings,
    Routes.Profile,
    Routes.CreateVault,
    Routes.RecoveryKey,
    Routes.UnlockVault,
    Routes.PostLoginGate,
)
