package com.miguelrodriguez19.safecube.core.auth.data.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.RegisteredAccount
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.repository.TokenStorage
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import java.time.OffsetDateTime
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthTokenRefreshHandlerTest {
    @Test
    fun `refresh success returns new access token and updates session`() = runBlocking {
        val newTokens = AuthTokens(
            accessToken = "new-access",
            refreshToken = "new-refresh",
            issuedAt = OffsetDateTime.parse("2026-03-05T00:00:00Z"),
        )
        val tokenStorage = FakeTokenStorage(
            accessToken = "expired-access",
            refreshToken = "current-refresh",
        )
        val sessionManager = FakeSessionManager()
        val handler = AuthTokenRefreshHandler(
            authRepository = FakeAuthRepository(
                refreshResult = AuthResult.Success(newTokens),
            ),
            tokenStorage = tokenStorage,
            sessionManager = sessionManager,
        )

        val refreshedAccessToken = handler.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertEquals("new-access", refreshedAccessToken)
        assertEquals(1, sessionManager.onLoginSuccessCalls)
        assertEquals(newTokens, sessionManager.lastTokens)
        assertEquals(0, sessionManager.forceLogoutCalls)
    }

    @Test
    fun `refresh auth failure forces logout`() = runBlocking {
        val sessionManager = FakeSessionManager()
        val handler = AuthTokenRefreshHandler(
            authRepository = FakeAuthRepository(
                refreshResult = AuthResult.Error(AuthError.InvalidCredentials),
            ),
            tokenStorage = FakeTokenStorage(refreshToken = "current-refresh"),
            sessionManager = sessionManager,
        )

        val refreshedAccessToken = handler.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(refreshedAccessToken)
        assertEquals(1, sessionManager.forceLogoutCalls)
    }

    @Test
    fun `refresh transport failure does not force logout`() = runBlocking {
        val sessionManager = FakeSessionManager()
        val handler = AuthTokenRefreshHandler(
            authRepository = FakeAuthRepository(
                refreshResult = AuthResult.Error(
                    AuthError.Unknown(
                        code = null,
                        message = "network down",
                    ),
                ),
            ),
            tokenStorage = FakeTokenStorage(refreshToken = "current-refresh"),
            sessionManager = sessionManager,
        )

        val refreshedAccessToken = handler.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(refreshedAccessToken)
        assertEquals(0, sessionManager.forceLogoutCalls)
    }
}

private class FakeAuthRepository(
    private val refreshResult: AuthResult<AuthTokens>,
) : AuthRepository {
    override suspend fun register(
        email: String,
        password: String,
    ): AuthResult<RegisteredAccount> = error("not needed")

    override suspend fun login(
        email: String,
        password: String,
    ): AuthResult<AuthTokens> = error("not needed")

    override suspend fun refresh(
        refreshToken: String,
    ): AuthResult<AuthTokens> = refreshResult

    override suspend fun logout(): AuthResult<Unit> = error("not needed")
}

private class FakeTokenStorage(
    private var accessToken: String? = null,
    private var refreshToken: String? = null,
    private var issuedAt: OffsetDateTime? = null,
) : TokenStorage {
    override fun saveTokens(
        accessToken: String,
        refreshToken: String,
        issuedAt: OffsetDateTime?,
    ) {
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.issuedAt = issuedAt
    }

    override fun getAccessToken(): String? = accessToken

    override fun getRefreshToken(): String? = refreshToken

    override fun getIssuedAt(): OffsetDateTime? = issuedAt

    override fun clear() {
        accessToken = null
        refreshToken = null
        issuedAt = null
    }
}

private class FakeSessionManager : SessionManager {
    private val mutableState = MutableStateFlow<SessionState>(SessionState.LoggedOut)

    override val sessionState: StateFlow<SessionState> = mutableState

    var forceLogoutCalls: Int = 0
        private set
    var onLoginSuccessCalls: Int = 0
        private set
    var lastTokens: AuthTokens? = null
        private set

    override fun isLoggedIn(): Boolean = sessionState.value is SessionState.LoggedInVaultLocked

    override fun onLoginSuccess(tokens: AuthTokens) {
        onLoginSuccessCalls++
        lastTokens = tokens
        mutableState.value = SessionState.LoggedInVaultLocked
    }

    override fun forceLogout() {
        forceLogoutCalls++
        mutableState.value = SessionState.LoggedOut
    }
}
