package com.miguelrodriguez19.safecube.app.presentation.navigation.gate.event

sealed interface PostLoginGateUiEvent {
    data object CreateVault : PostLoginGateUiEvent
    data object RecoveryKey : PostLoginGateUiEvent
    data object UnlockVault : PostLoginGateUiEvent
    data object Home : PostLoginGateUiEvent
}
