package com.miguelrodriguez19.safecube.core.ui.theme

import kotlinx.coroutines.flow.Flow

enum class AppearanceMode {
    System,
    Light,
    Dark,
}

data class AppearanceSettings(
    val mode: AppearanceMode = AppearanceMode.System,
    val dynamicColorEnabled: Boolean = false,
)

interface AppearanceRepository {
    val settings: Flow<AppearanceSettings>

    suspend fun setMode(mode: AppearanceMode): AppearanceUpdateResult

    suspend fun setDynamicColorEnabled(enabled: Boolean): AppearanceUpdateResult
}

sealed interface AppearanceUpdateResult {
    data object Updated : AppearanceUpdateResult

    data object Failed : AppearanceUpdateResult
}

enum class DynamicColorAvailability {
    Available,
    RequiresAndroid12,
}

fun dynamicColorAvailability(sdkInt: Int): DynamicColorAvailability =
    if (sdkInt >= 31) {
        DynamicColorAvailability.Available
    } else {
        DynamicColorAvailability.RequiresAndroid12
    }
