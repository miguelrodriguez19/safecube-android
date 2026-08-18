package com.miguelrodriguez19.safecube.feature.vault.presentation.create.state

import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.isLoading
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.isRetryable

data class CreateVaultUiState(
    val passphrase: String = "",
    val passphraseErrorRes: Int? = null,
    val operationState: VaultUiOperationState = VaultUiOperationState.Idle,
    val errorMessageRes: Int? = null,
) {
    val isLoading: Boolean
        get() = operationState.isLoading

    val isRetryable: Boolean
        get() = operationState.isRetryable
}
