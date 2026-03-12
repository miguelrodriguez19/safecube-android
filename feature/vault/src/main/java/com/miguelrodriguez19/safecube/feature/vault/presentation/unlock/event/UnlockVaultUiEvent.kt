package com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.event

sealed interface UnlockVaultUiEvent {
    data object NavigateToApp : UnlockVaultUiEvent
}
