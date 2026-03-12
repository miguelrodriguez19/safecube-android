package com.miguelrodriguez19.safecube.core.auth.domain.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.repository.TokenStorage
import com.miguelrodriguez19.safecube.core.network.domain.port.TokenProvider
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SessionManagerImpl @Inject constructor(
    private val tokenStorage: TokenStorage,
) : SessionManager, TokenProvider {
    private val mutableSessionState = MutableStateFlow(readSessionState())

    override val sessionState: StateFlow<SessionState> = mutableSessionState.asStateFlow()

    override fun isLoggedIn(): Boolean =
        mutableSessionState.value is SessionState.LoggedInVaultLocked

    override fun onLoginSuccess(tokens: AuthTokens) {
        tokenStorage.saveTokens(
            accessToken = tokens.accessToken,
            refreshToken = tokens.refreshToken,
            issuedAt = tokens.issuedAt,
        )
        mutableSessionState.value = SessionState.LoggedInVaultLocked
    }

    override fun forceLogout() {
        tokenStorage.clear()
        mutableSessionState.value = SessionState.LoggedOut
    }

    override fun getAccessToken(): String? = tokenStorage.getAccessToken()

    private fun readSessionState(): SessionState {
        val hasAccessToken = !tokenStorage.getAccessToken().isNullOrBlank()
        val hasRefreshToken = !tokenStorage.getRefreshToken().isNullOrBlank()
        return if (hasAccessToken && hasRefreshToken) {
            SessionState.LoggedInVaultLocked
        } else {
            SessionState.LoggedOut
        }
    }
}
