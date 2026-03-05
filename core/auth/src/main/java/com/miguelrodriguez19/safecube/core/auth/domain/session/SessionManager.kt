package com.miguelrodriguez19.safecube.core.auth.domain.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.repository.TokenStorage
import com.miguelrodriguez19.safecube.core.network.TokenProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SessionManager @Inject constructor(
    private val tokenStorage: TokenStorage,
) : TokenProvider {
    private val mutableSessionState = MutableStateFlow(readSessionState())

    val sessionState: Flow<SessionState> = mutableSessionState.asStateFlow()

    fun isLoggedIn(): Boolean = mutableSessionState.value is SessionState.LoggedIn

    fun logout() {
        tokenStorage.clear()
        mutableSessionState.value = SessionState.LoggedOut
    }

    override fun getAccessToken(): String? = tokenStorage.getAccessToken()

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
