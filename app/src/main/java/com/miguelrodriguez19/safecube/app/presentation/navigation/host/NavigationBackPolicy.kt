package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import android.app.Activity
import android.os.SystemClock
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes

@Composable
internal fun rememberVaultBackPressHandler(activity: Activity?): () -> Unit {
    val context = LocalContext.current
    var lastBackPressTimestamp by rememberSaveable { mutableLongStateOf(0L) }

    return {
        val now = SystemClock.elapsedRealtime()
        if (now - lastBackPressTimestamp <= DOUBLE_BACK_PRESS_WINDOW_MS) {
            activity?.finish()
        } else {
            lastBackPressTimestamp = now
            Toast.makeText(
                context,
                BACK_PRESS_EXIT_MESSAGE,
                Toast.LENGTH_SHORT,
            ).show()
        }
    }
}

internal fun handleBackNavigation(
    currentRoute: Routes?,
    moveToVaultFromAppSection: () -> Unit,
    moveToSettingsFromProfile: () -> Unit,
    onVaultBackPressed: () -> Unit,
    popBackStack: () -> Unit,
) {
    when (currentRoute) {
        Routes.VaultFolders,
        Routes.Settings,
        Routes.App,
        -> moveToVaultFromAppSection()

        Routes.Profile -> moveToSettingsFromProfile()

        Routes.Vault -> onVaultBackPressed()

        Routes.Welcome,
        Routes.Login,
        Routes.Splash,
        Routes.PostLoginGate,
        Routes.UnlockVault,
        Routes.CreateVault,
        -> Unit

        else -> popBackStack()
    }
}

private const val DOUBLE_BACK_PRESS_WINDOW_MS = 1500L
private const val BACK_PRESS_EXIT_MESSAGE = "Pulsa de nuevo para cerrar"
