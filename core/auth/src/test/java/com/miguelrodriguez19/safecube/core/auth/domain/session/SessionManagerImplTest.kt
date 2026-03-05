package com.miguelrodriguez19.safecube.core.auth.domain.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.repository.TokenStorage
import java.time.OffsetDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionManagerImplTest {
    @Test
    fun `cold start with tokens starts as logged in vault locked`() {
        val storage = FakeTokenStorage(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            issuedAt = OffsetDateTime.parse("2026-03-05T00:00:00Z"),
        )

        val sessionManager = SessionManagerImpl(storage)

        assertEquals(SessionState.LoggedInVaultLocked, sessionManager.sessionState.value)
        assertTrue(sessionManager.isLoggedIn())
    }

    @Test
    fun `cold start without tokens starts as logged out`() {
        val sessionManager = SessionManagerImpl(FakeTokenStorage())

        assertEquals(SessionState.LoggedOut, sessionManager.sessionState.value)
        assertFalse(sessionManager.isLoggedIn())
    }

    @Test
    fun `onLoginSuccess persists tokens and updates session state`() {
        val storage = FakeTokenStorage()
        val sessionManager = SessionManagerImpl(storage)
        val issuedAt = OffsetDateTime.parse("2026-03-05T00:00:00Z")

        sessionManager.onLoginSuccess(
            tokens = AuthTokens(
                accessToken = "access-token",
                refreshToken = "refresh-token",
                issuedAt = issuedAt,
            ),
        )

        assertEquals("access-token", storage.storedAccessToken)
        assertEquals("refresh-token", storage.storedRefreshToken)
        assertEquals(issuedAt, storage.storedIssuedAt)
        assertEquals(SessionState.LoggedInVaultLocked, sessionManager.sessionState.value)
        assertTrue(sessionManager.isLoggedIn())
    }

    @Test
    fun `forceLogout clears tokens and sets logged out`() {
        val storage = FakeTokenStorage(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            issuedAt = OffsetDateTime.parse("2026-03-05T00:00:00Z"),
        )
        val sessionManager = SessionManagerImpl(storage)

        sessionManager.forceLogout()

        assertEquals(null, storage.storedAccessToken)
        assertEquals(null, storage.storedRefreshToken)
        assertEquals(null, storage.storedIssuedAt)
        assertEquals(SessionState.LoggedOut, sessionManager.sessionState.value)
        assertFalse(sessionManager.isLoggedIn())
    }

    @Test
    fun `getAccessToken delegates to token storage`() {
        val storage = FakeTokenStorage(accessToken = "access-token")
        val sessionManager = SessionManagerImpl(storage)

        assertEquals("access-token", sessionManager.getAccessToken())
    }
}

private class FakeTokenStorage(
    accessToken: String? = null,
    refreshToken: String? = null,
    issuedAt: OffsetDateTime? = null,
) : TokenStorage {
    var storedAccessToken: String? = accessToken
        private set
    var storedRefreshToken: String? = refreshToken
        private set
    var storedIssuedAt: OffsetDateTime? = issuedAt
        private set

    override fun saveTokens(
        accessToken: String,
        refreshToken: String,
        issuedAt: OffsetDateTime?,
    ) {
        storedAccessToken = accessToken
        storedRefreshToken = refreshToken
        storedIssuedAt = issuedAt
    }

    override fun getAccessToken(): String? = storedAccessToken

    override fun getRefreshToken(): String? = storedRefreshToken

    override fun getIssuedAt(): OffsetDateTime? = storedIssuedAt

    override fun clear() {
        storedAccessToken = null
        storedRefreshToken = null
        storedIssuedAt = null
    }
}
