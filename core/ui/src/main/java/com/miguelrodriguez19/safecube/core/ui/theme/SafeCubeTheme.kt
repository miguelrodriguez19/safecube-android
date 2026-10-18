package com.miguelrodriguez19.safecube.core.ui.theme

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
fun SafeCubeTheme(
    appearance: AppearanceSettings = AppearanceSettings(),
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val systemDarkTheme = isSystemInDarkTheme()
    val darkTheme = resolveDarkTheme(appearance.mode, systemDarkTheme)
    val dynamicColorScheme = if (appearance.dynamicColorEnabled && Build.VERSION.SDK_INT >= 31) {
        remember(appearance.dynamicColorEnabled, darkTheme) {
            dynamicColorSchemeForApi31(context, darkTheme)
        }
    } else {
        null
    }
    val colorScheme = resolveSafeCubeColorScheme(
        settings = appearance,
        systemDarkTheme = systemDarkTheme,
        sdkInt = Build.VERSION.SDK_INT,
        dynamicLightScheme = if (!darkTheme) dynamicColorScheme else null,
        dynamicDarkScheme = if (darkTheme) dynamicColorScheme else null,
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = SafeCubeTypography,
        shapes = SafeCubeShapes,
        content = content,
    )
}

internal fun resolveDarkTheme(
    mode: AppearanceMode,
    systemDarkTheme: Boolean,
): Boolean = when (mode) {
    AppearanceMode.System -> systemDarkTheme
    AppearanceMode.Light -> false
    AppearanceMode.Dark -> true
}

internal fun resolveSafeCubeColorScheme(
    settings: AppearanceSettings,
    systemDarkTheme: Boolean,
    sdkInt: Int,
    dynamicLightScheme: ColorScheme? = null,
    dynamicDarkScheme: ColorScheme? = null,
): ColorScheme {
    val darkTheme = resolveDarkTheme(settings.mode, systemDarkTheme)
    val dynamicScheme = if (settings.dynamicColorEnabled && sdkInt >= 31) {
        if (darkTheme) dynamicDarkScheme else dynamicLightScheme
    } else {
        null
    }
    return dynamicScheme ?: if (darkTheme) SafeCubeDarkColorScheme else SafeCubeLightColorScheme
}

@RequiresApi(31)
private fun dynamicColorSchemeForApi31(
    context: android.content.Context,
    darkTheme: Boolean,
): ColorScheme = if (darkTheme) {
    dynamicDarkColorScheme(context)
} else {
    dynamicLightColorScheme(context)
}
