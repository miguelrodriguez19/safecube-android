package com.miguelrodriguez19.safecube.feature.vault.presentation.settings.event

import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptRequest

sealed interface SettingsUiEvent {
    data class LaunchQuickUnlockPrompt(val request: QuickUnlockPromptRequest) : SettingsUiEvent
}
