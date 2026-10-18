package com.miguelrodriguez19.safecube.core.ui.theme

import androidx.compose.ui.graphics.Color
import org.junit.Assert.assertEquals
import org.junit.Test

class SafeCubeThemeTest {
    @Test
    fun appearanceSettingsDefaultsToSystemWithDynamicColorDisabled() {
        assertEquals(
            AppearanceSettings(
                mode = AppearanceMode.System,
                dynamicColorEnabled = false,
            ),
            AppearanceSettings(),
        )
    }

    @Test
    fun lightModeAlwaysUsesTheLightBrandPalette() {
        val result = resolveSafeCubeColorScheme(
            settings = AppearanceSettings(AppearanceMode.Light),
            systemDarkTheme = true,
            sdkInt = 31,
        )

        assertEquals(SafeCubeLightColorScheme, result)
    }

    @Test
    fun darkModeAlwaysUsesTheDarkBrandPalette() {
        val result = resolveSafeCubeColorScheme(
            settings = AppearanceSettings(AppearanceMode.Dark),
            systemDarkTheme = false,
            sdkInt = 30,
        )

        assertEquals(SafeCubeDarkColorScheme, result)
    }

    @Test
    fun systemModeFollowsSystemTheme() {
        val lightResult = resolveSafeCubeColorScheme(
            settings = AppearanceSettings(AppearanceMode.System),
            systemDarkTheme = false,
            sdkInt = 30,
        )
        val darkResult = resolveSafeCubeColorScheme(
            settings = AppearanceSettings(AppearanceMode.System),
            systemDarkTheme = true,
            sdkInt = 30,
        )

        assertEquals(SafeCubeLightColorScheme, lightResult)
        assertEquals(SafeCubeDarkColorScheme, darkResult)
    }

    @Test
    fun dynamicColorIsIgnoredBelowAndroid12() {
        val dynamicLight = SafeCubeLightColorScheme.copy(primary = Color.Magenta)

        val result = resolveSafeCubeColorScheme(
            settings = AppearanceSettings(
                mode = AppearanceMode.Light,
                dynamicColorEnabled = true,
            ),
            systemDarkTheme = false,
            sdkInt = 30,
            dynamicLightScheme = dynamicLight,
        )

        assertEquals(SafeCubeLightColorScheme, result)
    }

    @Test
    fun dynamicColorIsAppliedOnlyWhenOptedInOnAndroid12OrNewer() {
        val dynamicLight = SafeCubeLightColorScheme.copy(primary = Color.Magenta)

        val enabledResult = resolveSafeCubeColorScheme(
            settings = AppearanceSettings(
                mode = AppearanceMode.Light,
                dynamicColorEnabled = true,
            ),
            systemDarkTheme = false,
            sdkInt = 31,
            dynamicLightScheme = dynamicLight,
        )
        val disabledResult = resolveSafeCubeColorScheme(
            settings = AppearanceSettings(AppearanceMode.Light),
            systemDarkTheme = false,
            sdkInt = 31,
            dynamicLightScheme = dynamicLight,
        )

        assertEquals(dynamicLight, enabledResult)
        assertEquals(SafeCubeLightColorScheme, disabledResult)
    }

    @Test
    fun dynamicColorAvailabilityUsesAndroid12AsTheCapabilityBoundary() {
        assertEquals(
            DynamicColorAvailability.RequiresAndroid12,
            dynamicColorAvailability(30),
        )
        assertEquals(
            DynamicColorAvailability.Available,
            dynamicColorAvailability(31),
        )
    }
}
