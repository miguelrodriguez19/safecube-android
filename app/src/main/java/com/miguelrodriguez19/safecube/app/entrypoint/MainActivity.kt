package com.miguelrodriguez19.safecube.app.entrypoint

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.miguelrodriguez19.safecube.app.appearance.AppearanceBootstrapState
import com.miguelrodriguez19.safecube.app.appearance.AppearanceBootstrapViewModel
import com.miguelrodriguez19.safecube.app.presentation.navigation.host.NavigationWrapper
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    private val appearanceBootstrapViewModel: AppearanceBootstrapViewModel by lazy {
        ViewModelProvider(this)[AppearanceBootstrapViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition(::shouldKeepSplashOnScreen)

        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        setAppContent()
    }

    private fun shouldKeepSplashOnScreen(): Boolean =
        appearanceBootstrapViewModel.state.value is AppearanceBootstrapState.Loading

    private fun setAppContent() {
        setContent {
            val bootstrapState by appearanceBootstrapViewModel.state.collectAsState()
            AppearanceRoot(bootstrapState) {
                NavigationWrapper()
            }
        }
    }
}

@Composable
internal fun AppearanceRoot(
    state: AppearanceBootstrapState,
    content: @Composable () -> Unit,
) {
    when (state) {
        AppearanceBootstrapState.Loading -> Unit
        is AppearanceBootstrapState.Ready -> {
            SafeCubeTheme(appearance = state.settings) {
                val activity = LocalContext.current.findActivity()
                val darkIcons = MaterialTheme.colorScheme.surface.luminance() > 0.5f
                SideEffect {
                    activity?.window?.let { window ->
                        WindowCompat.getInsetsController(window, window.decorView).apply {
                            isAppearanceLightStatusBars = darkIcons
                            isAppearanceLightNavigationBars = darkIcons
                        }
                    }
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                    content = content,
                )
            }
        }
    }
}

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
