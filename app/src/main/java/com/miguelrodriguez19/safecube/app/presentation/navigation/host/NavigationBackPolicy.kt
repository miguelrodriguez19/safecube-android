package com.miguelrodriguez19.safecube.app.presentation.navigation.host

import android.app.Activity
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.miguelrodriguez19.safecube.R
import com.miguelrodriguez19.safecube.app.presentation.navigation.route.Routes

@Composable
internal fun rememberVaultBackPressHandler(
    activity: Activity?,
    enabled: Boolean,
): () -> Unit {
    val context = LocalContext.current
    val message = stringResource(R.string.shell_back_exit_notice)
    val lifecycleOwner = LocalLifecycleOwner.current
    val prompt = remember { ExitToast() }
    val policy = remember(context, activity, message, prompt) {
        VaultExitBackPolicy(
            showPrompt = { onShown, onHidden ->
                val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
                prompt.current = toast
                toast.addCallback(object : Toast.Callback() {
                    override fun onToastShown() {
                        if (prompt.current === toast) onShown()
                    }
                    override fun onToastHidden() {
                        if (prompt.current === toast) onHidden()
                    }
                })
                toast.show()
            },
            cancelPrompt = prompt::cancel,
            onExit = { activity?.finish() },
        )
    }

    DisposableEffect(policy, lifecycleOwner, enabled) {
        policy.reset()
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) policy.reset()
        }
        if (enabled) lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            policy.reset()
        }
    }

    return { if (enabled) policy.onBack() }
}

private class ExitToast {
    var current: Toast? = null

    fun cancel() {
        val toast = current
        current = null
        toast?.cancel()
    }
}

internal fun handleBackNavigation(
    currentRoute: Routes?,
    moveToVaultFromAppSection: () -> Unit,
    onVaultBackPressed: () -> Unit,
    popBackStack: () -> Unit,
) {
    when (currentRoute) {
        Routes.Settings,
        -> moveToVaultFromAppSection()

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
