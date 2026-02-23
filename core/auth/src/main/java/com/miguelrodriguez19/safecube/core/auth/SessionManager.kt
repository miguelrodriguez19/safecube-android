package com.miguelrodriguez19.safecube.core.auth

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SessionManager @Inject constructor(
    private val tokenStorage: TokenStorage,
) {
    private val mutableSessionState = MutableStateFlow(readSessionState())

    val sessionState: Flow<SessionState> = mutableSessionState.asStateFlow()

    fun isLoggedIn(): Boolean = mutableSessionState.value is SessionState.LoggedIn

    fun logout() {
        tokenStorage.clear()
        mutableSessionState.value = SessionState.LoggedOut
    }

    private fun readSessionState(): SessionState {
        val hasAccessToken = !tokenStorage.getAccessToken().isNullOrBlank()
        val hasRefreshToken = !tokenStorage.getRefreshToken().isNullOrBlank()
        return if (hasAccessToken && hasRefreshToken) {
            SessionState.LoggedIn
        } else {
            SessionState.LoggedOut
        }
    }
}
