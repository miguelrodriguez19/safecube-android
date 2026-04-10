package com.miguelrodriguez19.safecube.core.auth.domain.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionState
import com.miguelrodriguez19.safecube.core.auth.domain.repository.TokenStorage
import io.mockk.Runs
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.time.OffsetDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SessionManagerImplTest {

    private val tokenStorage = mockk<TokenStorage>()

    private lateinit var target: SessionManagerImpl

    @Test
    fun `init when access and refresh tokens exist then starts as logged in vault locked`() {
        every { tokenStorage.getAccessToken() } returns "access-token"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"

        createTarget()

        assertEquals(SessionState.LoggedInVaultLocked, target.sessionState.value)
        assertTrue(target.isLoggedIn())
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        confirmVerified(tokenStorage)
    }

    @Test
    fun `init when tokens are missing then starts as logged out`() {
        every { tokenStorage.getAccessToken() } returns null
        every { tokenStorage.getRefreshToken() } returns null

        createTarget()

        assertEquals(SessionState.LoggedOut, target.sessionState.value)
        assertFalse(target.isLoggedIn())
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        confirmVerified(tokenStorage)
    }

    @Test
    fun `onLoginSuccess when tokens are provided then persists them and updates session state`() {
        val issuedAt = OffsetDateTime.parse("2026-03-05T00:00:00Z")
        every { tokenStorage.getAccessToken() } returns null
        every { tokenStorage.getRefreshToken() } returns null
        every { tokenStorage.saveTokens("access-token", "refresh-token", issuedAt) } just Runs
        createTarget()

        target.onLoginSuccess(
            tokens = AuthTokens(
                accessToken = "access-token",
                refreshToken = "refresh-token",
                issuedAt = issuedAt,
            ),
        )

        assertEquals(SessionState.LoggedInVaultLocked, target.sessionState.value)
        assertTrue(target.isLoggedIn())
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { tokenStorage.saveTokens("access-token", "refresh-token", issuedAt) }
        confirmVerified(tokenStorage)
    }

    @Test
    fun `forceLogout when session exists then clears tokens and updates session state`() {
        every { tokenStorage.getAccessToken() } returns "access-token"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { tokenStorage.clear() } just Runs
        createTarget()

        target.forceLogout()

        assertEquals(SessionState.LoggedOut, target.sessionState.value)
        assertFalse(target.isLoggedIn())
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { tokenStorage.clear() }
        confirmVerified(tokenStorage)
    }

    @Test
    fun `getAccessToken when session manager delegates then returns token storage access token`() {
        every { tokenStorage.getAccessToken() } returnsMany listOf(null, "access-token")
        every { tokenStorage.getRefreshToken() } returns null
        createTarget()

        val result = target.getAccessToken()

        assertEquals("access-token", result)
        verify(exactly = 2) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        confirmVerified(tokenStorage)
    }

    private fun createTarget() {
        target = SessionManagerImpl(tokenStorage)
    }
}
