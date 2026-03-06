package com.miguelrodriguez19.safecube.feature.auth.presentation.signup

import androidx.annotation.StringRes

data class SignupUiState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    @param:StringRes val emailErrorRes: Int? = null,
    @param:StringRes val passwordErrorRes: Int? = null,
    @param:StringRes val confirmPasswordErrorRes: Int? = null,
    @param:StringRes val errorMessageRes: Int? = null,
    val signupSucceeded: Boolean = false,
)
