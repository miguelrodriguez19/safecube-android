package com.miguelrodriguez19.safecube.app.appearance

import androidx.datastore.preferences.core.mutablePreferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import org.junit.Assert.assertEquals
import org.junit.Test

class AppearanceSettingsCodecTest {
    @Test
    fun `missing values use the safe defaults`() {
        assertEquals(AppearanceSettings(), AppearanceSettingsCodec.decode(mutablePreferencesOf()))
    }

    @Test
    fun `known modes use stable serialized values`() {
        assertEquals("system", AppearanceSettingsCodec.encodeMode(AppearanceMode.System))
        assertEquals("light", AppearanceSettingsCodec.encodeMode(AppearanceMode.Light))
        assertEquals("dark", AppearanceSettingsCodec.encodeMode(AppearanceMode.Dark))
    }

    @Test
    fun `unknown mode falls back to system`() {
        val preferences = mutablePreferencesOf(
            AppearanceSettingsCodec.modeKey to "future",
            AppearanceSettingsCodec.dynamicColorEnabledKey to true,
        )

        assertEquals(
            AppearanceSettings(mode = AppearanceMode.System, dynamicColorEnabled = true),
            AppearanceSettingsCodec.decode(preferences),
        )
    }

    @Test
    fun `wrong preference type falls back to all defaults`() {
        val preferences = mutablePreferencesOf(
            stringPreferencesKey("dynamic_color_enabled") to "true",
        )

        assertEquals(AppearanceSettings(), AppearanceSettingsCodec.decode(preferences))
    }
}
