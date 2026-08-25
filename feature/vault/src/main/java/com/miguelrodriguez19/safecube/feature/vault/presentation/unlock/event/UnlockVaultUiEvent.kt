package com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.event

import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptRequest

sealed interface UnlockVaultUiEvent {
    data object NavigateToApp : UnlockVaultUiEvent
    data class LaunchQuickUnlockPrompt(val request: QuickUnlockPromptRequest) : UnlockVaultUiEvent
}
