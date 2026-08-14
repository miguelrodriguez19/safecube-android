package com.miguelrodriguez19.safecube.core.auth.domain.model

sealed interface SessionState {
    data object LoggedInVaultLocked : SessionState
    data class LoggedOut(
        val reason: SessionTerminationReason? = null,
    ) : SessionState
}
