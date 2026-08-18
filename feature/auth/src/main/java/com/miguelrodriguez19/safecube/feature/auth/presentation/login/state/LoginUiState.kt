package com.miguelrodriguez19.safecube.feature.auth.presentation.login.state

import androidx.annotation.StringRes
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.AuthUiOperationState
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.isRetryable

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val operationState: AuthUiOperationState = AuthUiOperationState.Idle,

    @param:StringRes val emailErrorRes: Int? = null,
    @param:StringRes val passwordErrorRes: Int? = null,
    @param:StringRes val errorMessageRes: Int? = null,
) {
    val isLoading: Boolean
        get() = operationState == AuthUiOperationState.Loading

    val isRetryable: Boolean
        get() = operationState.isRetryable()
}
