package com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state

import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.isLoading
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.isRetryable

data class RecoveryKeyUiState(
    val recoveryKey: String = "",
    val operationState: VaultUiOperationState = VaultUiOperationState.Loading,
    val isConfirmed: Boolean = false,
    val errorMessageRes: Int? = null,
) {
    val isLoading: Boolean
        get() = operationState.isLoading

    val isRetryable: Boolean
        get() = operationState.isRetryable

    val canContinue: Boolean
        get() = operationState == VaultUiOperationState.Success &&
            recoveryKey.isNotBlank() &&
            isConfirmed
}
