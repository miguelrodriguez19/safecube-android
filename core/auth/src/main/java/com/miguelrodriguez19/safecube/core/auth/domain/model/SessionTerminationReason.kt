package com.miguelrodriguez19.safecube.core.auth.domain.model

enum class SessionTerminationReason {
    ManualLogout,
    SessionExpired,
    RefreshCredentialsRejected,
    LocalIntegrityFailure,
}
