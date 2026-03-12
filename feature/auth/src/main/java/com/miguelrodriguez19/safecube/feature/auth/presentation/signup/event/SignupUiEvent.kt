package com.miguelrodriguez19.safecube.feature.auth.presentation.signup.event

sealed interface SignupUiEvent {
    data object SignupSucceeded : SignupUiEvent
}
