package com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action

sealed interface RecoveryKeyUiAction {
    data object Retry : RecoveryKeyUiAction
    data class ConfirmationChanged(val isConfirmed: Boolean) : RecoveryKeyUiAction
    data object Continue : RecoveryKeyUiAction
}
