package com.miguelrodriguez19.safecube

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.core.view.WindowCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.miguelrodriguez19.safecube.app.appearance.AppearanceBootstrapState
import com.miguelrodriguez19.safecube.app.entrypoint.AppearanceRoot
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ShellSystemBarsTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun systemBarIconsFollowResolvedAppearanceChanges() {
        val appearance = mutableStateOf(AppearanceMode.Dark)
        composeRule.setContent {
            AppearanceRoot(
                AppearanceBootstrapState.Ready(
                    AppearanceSettings(mode = appearance.value, dynamicColorEnabled = false),
                ),
            ) {}
        }

        assertDarkIcons(false)
        composeRule.runOnIdle { appearance.value = AppearanceMode.Light }
        assertDarkIcons(true)
        composeRule.runOnIdle { appearance.value = AppearanceMode.Dark }
        assertDarkIcons(false)
    }

    private fun assertDarkIcons(expected: Boolean) {
        composeRule.runOnIdle {
            val window = composeRule.activity.window
            val controller = WindowCompat.getInsetsController(window, window.decorView)
            assertEquals(expected, controller.isAppearanceLightStatusBars)
            assertEquals(expected, controller.isAppearanceLightNavigationBars)
        }
    }
}
