package com.miguelrodriguez19.safecube.core.auth.domain.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import kotlinx.coroutines.flow.StateFlow

interface SessionManager {
    val sessionState: StateFlow<SessionState>

    fun isLoggedIn(): Boolean

    fun onLoginSuccess(tokens: AuthTokens)

    fun forceLogout(reason: SessionTerminationReason = SessionTerminationReason.ManualLogout)

    fun acknowledgeTermination(reason: SessionTerminationReason)
}
