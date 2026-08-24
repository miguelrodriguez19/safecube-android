package com.miguelrodriguez19.safecube.app.entrypoint

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.miguelrodriguez19.safecube.app.presentation.navigation.host.NavigationWrapper
import com.miguelrodriguez19.safecube.app.presentation.theme.SafecubeandroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition(::shouldKeepSplashOnScreen)

        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        setAppContent()
    }

    private fun shouldKeepSplashOnScreen(): Boolean {
        // TODO: check if is first time in app. Send to welcome, if not send to login
        return false
    }

    private fun setAppContent() {
        setContent {
            SafecubeandroidTheme {
                NavigationWrapper()
            }
        }
    }
}
