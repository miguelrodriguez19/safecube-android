package com.miguelrodriguez19.safecube.core.auth.domain.model

sealed interface SessionState {
    data object LoggedInVaultLocked : SessionState
    data object LoggedOut : SessionState
}
