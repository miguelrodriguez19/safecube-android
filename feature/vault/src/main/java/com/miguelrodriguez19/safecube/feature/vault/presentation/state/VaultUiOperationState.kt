package com.miguelrodriguez19.safecube.feature.vault.presentation.state

sealed interface VaultUiOperationState {
    data object Idle : VaultUiOperationState

    data object Loading : VaultUiOperationState

    data object Success : VaultUiOperationState

    data object RetryableError : VaultUiOperationState

    data object TerminalError : VaultUiOperationState
}

val VaultUiOperationState.isLoading: Boolean
    get() = this == VaultUiOperationState.Loading

val VaultUiOperationState.isRetryable: Boolean
    get() = this == VaultUiOperationState.RetryableError
