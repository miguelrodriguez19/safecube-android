package com.miguelrodriguez19.safecube.app.entrypoint

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.FragmentActivity
import com.miguelrodriguez19.safecube.app.presentation.navigation.host.NavigationWrapper
import com.miguelrodriguez19.safecube.app.presentation.theme.SafecubeandroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition(::shouldKeepSplashOnScreen)

        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        enableEdgeToEdge()
        setAppContent()
    }

    private fun shouldKeepSplashOnScreen(): Boolean = false

    private fun setAppContent() {
        setContent {
            SafecubeandroidTheme {
                NavigationWrapper()
            }
        }
    }
}
