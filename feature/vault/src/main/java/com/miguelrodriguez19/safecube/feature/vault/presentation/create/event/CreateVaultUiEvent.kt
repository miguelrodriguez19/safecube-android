package com.miguelrodriguez19.safecube.feature.vault.presentation.create.event

sealed interface CreateVaultUiEvent {
    data object NavigateToRecoveryKey : CreateVaultUiEvent
    data object NavigateToUnlock : CreateVaultUiEvent
}
