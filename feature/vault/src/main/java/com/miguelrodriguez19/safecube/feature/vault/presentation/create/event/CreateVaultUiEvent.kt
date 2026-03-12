package com.miguelrodriguez19.safecube.feature.vault.presentation.create.event

sealed interface CreateVaultUiEvent {
    data class NavigateToRecoveryKey(val recoveryKeyBase64: String) : CreateVaultUiEvent
    data object NavigateToUnlock : CreateVaultUiEvent
}
