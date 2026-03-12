package com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.event

sealed interface RecoveryKeyUiEvent {
    data object ContinueToUnlock : RecoveryKeyUiEvent
}
