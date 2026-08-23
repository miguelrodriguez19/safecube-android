package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState

@Composable
internal fun ObserveVaultRedirect(
    vaultState: VaultState,
    currentRoute: Routes?,
    setRoot: (Routes) -> Unit,
) {
    LaunchedEffect(vaultState, currentRoute) {
        resolveVaultRedirectTarget(vaultState, currentRoute)?.let(setRoot)
    }
}

internal fun resolveVaultRedirectTarget(
    vaultState: VaultState,
    currentRoute: Routes?,
): Routes? = if (vaultState == VaultState.Locked && currentRoute.isVaultContentRoute()) {
    Routes.UnlockVault
} else {
    null
}

private fun Routes?.isVaultContentRoute(): Boolean = when (this) {
    Routes.Vault,
    Routes.CreatePassword,
    is Routes.EditPassword,
    Routes.CreateNote,
    is Routes.EditNote,
    Routes.VaultFolders,
    Routes.Settings,
    Routes.ChangePassphrase,
    Routes.Profile,
        -> true

    else -> false
}
