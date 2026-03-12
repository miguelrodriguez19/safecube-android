package com.miguelrodriguez19.safecube.feature.auth.presentation.signup.action

sealed interface SignupUiAction {
    data class EmailChanged(val value: String) : SignupUiAction
    data class PasswordChanged(val value: String) : SignupUiAction
    data class ConfirmPasswordChanged(val value: String) : SignupUiAction
    data object Submit : SignupUiAction
}
