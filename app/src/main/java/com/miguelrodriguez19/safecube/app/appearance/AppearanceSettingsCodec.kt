package com.miguelrodriguez19.safecube.app.appearance

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings

internal object AppearanceSettingsCodec {
    val modeKey = stringPreferencesKey("mode")
    val dynamicColorEnabledKey = booleanPreferencesKey("dynamic_color_enabled")

    fun decode(preferences: Preferences): AppearanceSettings {
        return try {
            AppearanceSettings(
                mode = decodeMode(preferences[modeKey]),
                dynamicColorEnabled = preferences[dynamicColorEnabledKey] ?: false,
            )
        } catch (_: Exception) {
            AppearanceSettings()
        }
    }

    fun encodeMode(mode: AppearanceMode): String = when (mode) {
        AppearanceMode.System -> SYSTEM_MODE
        AppearanceMode.Light -> LIGHT_MODE
        AppearanceMode.Dark -> DARK_MODE
    }

    private fun decodeMode(value: String?): AppearanceMode = when (value) {
        LIGHT_MODE -> AppearanceMode.Light
        DARK_MODE -> AppearanceMode.Dark
        SYSTEM_MODE, null -> AppearanceMode.System
        else -> AppearanceMode.System
    }

    private const val SYSTEM_MODE = "system"
    private const val LIGHT_MODE = "light"
    private const val DARK_MODE = "dark"
}
