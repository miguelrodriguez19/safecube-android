package com.miguelrodriguez19.safecube

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.miguelrodriguez19.safecube.app.navigation.NavigationWrapper
import com.miguelrodriguez19.safecube.ui.theme.SafecubeandroidTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        splashScreen.setKeepOnScreenCondition {
            // TODO: ping to backend (start koyeb)
            // TODO: check if is first time in app. Send to welcome if not send to login
            false
        }

        enableEdgeToEdge()
        setContent {
            SafecubeandroidTheme {
                NavigationWrapper()
            }
        }
    }
}
