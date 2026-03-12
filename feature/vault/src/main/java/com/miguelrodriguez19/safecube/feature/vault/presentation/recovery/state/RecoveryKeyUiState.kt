package com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state

data class RecoveryKeyUiState(
    val recoveryKey: String = "",
    val errorMessageRes: Int? = null,
)
