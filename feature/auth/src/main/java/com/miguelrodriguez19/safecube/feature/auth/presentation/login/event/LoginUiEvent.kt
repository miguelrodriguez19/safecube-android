package com.miguelrodriguez19.safecube.feature.auth.presentation.login.event

sealed interface LoginUiEvent {
    data object LoginSucceeded : LoginUiEvent
}
