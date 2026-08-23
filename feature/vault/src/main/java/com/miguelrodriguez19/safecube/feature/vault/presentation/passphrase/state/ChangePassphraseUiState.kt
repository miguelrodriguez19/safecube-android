package com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.state

enum class ChangePassphraseUiOperationState {
    Idle,
    Loading,
    ValidationError,
    Success,
    InvalidCurrentPassphrase,
    RetryableError,
    SessionRequired,
    UncertainError,
    TerminalError,
}

data class ChangePassphraseUiState(
    val operationState: ChangePassphraseUiOperationState = ChangePassphraseUiOperationState.Idle,
    val currentPassphraseErrorRes: Int? = null,
    val newPassphraseErrorRes: Int? = null,
    val confirmationErrorRes: Int? = null,
    val errorMessageRes: Int? = null,
    val successMessageRes: Int? = null,
) {
    val isLoading: Boolean
        get() = operationState == ChangePassphraseUiOperationState.Loading

    val isRetryable: Boolean
        get() = operationState == ChangePassphraseUiOperationState.RetryableError
}
