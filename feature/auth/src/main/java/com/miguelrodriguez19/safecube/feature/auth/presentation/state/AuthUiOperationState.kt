package com.miguelrodriguez19.safecube.feature.auth.presentation.state

sealed interface AuthUiOperationState {
    data object Idle : AuthUiOperationState
    data object Loading : AuthUiOperationState
    data object ValidationError : AuthUiOperationState
    data object InvalidCredentials : AuthUiOperationState
    data object AccountAlreadyExists : AuthUiOperationState
    data object Forbidden : AuthUiOperationState
    data object OfflineOrTimeout : AuthUiOperationState
    data object ServiceUnavailable : AuthUiOperationState
    data object TerminalError : AuthUiOperationState
}

fun AuthUiOperationState.isRetryable(): Boolean = when (this) {
    AuthUiOperationState.OfflineOrTimeout,
    AuthUiOperationState.ServiceUnavailable,
        -> true

    AuthUiOperationState.Idle,
    AuthUiOperationState.Loading,
    AuthUiOperationState.ValidationError,
    AuthUiOperationState.InvalidCredentials,
    AuthUiOperationState.AccountAlreadyExists,
    AuthUiOperationState.Forbidden,
    AuthUiOperationState.TerminalError,
        -> false
}
