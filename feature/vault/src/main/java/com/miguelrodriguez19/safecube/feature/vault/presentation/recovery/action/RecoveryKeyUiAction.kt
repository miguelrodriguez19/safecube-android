package com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action

sealed interface RecoveryKeyUiAction {
    data class SetRecoveryKey(val value: String?) : RecoveryKeyUiAction
    data object Continue : RecoveryKeyUiAction
}
