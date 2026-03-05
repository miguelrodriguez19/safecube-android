package com.miguelrodriguez19.safecube.core.auth.domain.model

sealed interface SessionState {
    data object LoggedIn : SessionState
    data object LoggedOut : SessionState
}
