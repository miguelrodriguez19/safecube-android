package com.miguelrodriguez19.safecube.feature.auth.presentation.login.action

sealed interface LoginUiAction {
    data class EmailChanged(val value: String) : LoginUiAction
    data class PasswordChanged(val value: String) : LoginUiAction
    data object Submit : LoginUiAction
}
