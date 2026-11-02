package com.miguelrodriguez19.safecube.app.entrypoint

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
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
            SafeCubeTheme(appearance = state.settings, content = content)
        }
    }
}
