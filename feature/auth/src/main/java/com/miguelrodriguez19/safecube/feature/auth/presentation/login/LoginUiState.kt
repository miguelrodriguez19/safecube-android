package com.miguelrodriguez19.safecube.feature.auth.presentation.login

import androidx.annotation.StringRes

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,

    @param:StringRes val emailErrorRes: Int? = null,
    @param:StringRes val passwordErrorRes: Int? = null,
    @param:StringRes val errorMessageRes: Int? = null,

    val loginSucceeded: Boolean = false,
)
