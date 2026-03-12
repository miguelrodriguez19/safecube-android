package com.miguelrodriguez19.safecube.feature.vault.presentation.recovery

data class RecoveryKeyUiState(
    val recoveryKey: String = "",
    val errorMessageRes: Int? = null,
    val continueToUnlock: Boolean = false,
)
