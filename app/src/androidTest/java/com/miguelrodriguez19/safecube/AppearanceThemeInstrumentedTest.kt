package com.miguelrodriguez19.safecube

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.miguelrodriguez19.safecube.app.appearance.AppearanceBootstrapState
import com.miguelrodriguez19.safecube.app.entrypoint.AppearanceRoot
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeDarkColorScheme
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeLightColorScheme
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@SmallTest
@RunWith(AndroidJUnit4::class)
class AppearanceThemeInstrumentedTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    @SdkSuppress(maxSdkVersion = 30)
    fun api30DynamicColorOptInUsesSafeCubeBrandPalette() {
        assertPrimaryColor { SafeCubeLightColorScheme.primary }
    }

    @Test
    @SdkSuppress(minSdkVersion = 31)
    fun api31OrNewerDynamicColorOptInUsesPlatformPalette() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext

        assertPrimaryColor { dynamicLightColorScheme(context).primary }
    }

    @Test
    fun rootRendersNoContentUntilResolvedAndFirstContentUsesResolvedTheme() {
        val state = mutableStateOf<AppearanceBootstrapState>(AppearanceBootstrapState.Loading)

        composeRule.setContent {
            AppearanceRoot(state.value) {
                val primaryColor = MaterialTheme.colorScheme.primary
                Box(
                    modifier = Modifier
                        .testTag(ROOT_CONTENT_TAG)
                        .semantics { this[PrimaryColorKey] = primaryColor },
                )
            }
        }

        composeRule.onAllNodesWithTag(ROOT_CONTENT_TAG).assertCountEquals(0)

        composeRule.runOnIdle {
            state.value = AppearanceBootstrapState.Ready(
                AppearanceSettings(
                    mode = AppearanceMode.Dark,
                    dynamicColorEnabled = false,
                ),
            )
        }

        composeRule.onNodeWithTag(ROOT_CONTENT_TAG).assert(
            SemanticsMatcher.expectValue(PrimaryColorKey, SafeCubeDarkColorScheme.primary),
        )
    }

    private var composedExpectedPrimary: Color? = null

    private fun assertPrimaryColor(expected: @Composable () -> Color) {
        composeRule.setContent {
            composedExpectedPrimary = expected()
            SafeCubeTheme(
                appearance = AppearanceSettings(
                    mode = AppearanceMode.Light,
                    dynamicColorEnabled = true,
                ),
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                Box(
                    modifier = Modifier
                        .testTag(COLOR_PROBE_TAG)
                        .semantics {
                            this[PrimaryColorKey] = primaryColor
                        },
                )
            }
        }

        composeRule.waitForIdle()
        composeRule.onNodeWithTag(COLOR_PROBE_TAG).assert(
            SemanticsMatcher.expectValue(
                PrimaryColorKey,
                requireNotNull(composedExpectedPrimary),
            ),
        )
    }

    private companion object {
        val PrimaryColorKey = SemanticsPropertyKey<Color>("SafeCubePrimaryColor")
        const val COLOR_PROBE_TAG = "safe_cube_primary_color_probe"
        const val ROOT_CONTENT_TAG = "safe_cube_root_content"
    }
}
