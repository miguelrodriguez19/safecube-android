package com.miguelrodriguez19.safecube.core.auth

sealed interface SessionState {
    data object LoggedIn : SessionState
    data object LoggedOut : SessionState
}
