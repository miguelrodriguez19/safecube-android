package com.miguelrodriguez19.safecube.core.auth.data.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.repository.TokenStorage
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import javax.inject.Provider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthTokenRefreshHandlerTest {
    private val authRepositoryProvider = mockk<Provider<AuthRepository>>()
    private val authRepository = mockk<AuthRepository>()
    private val tokenStorage = mockk<TokenStorage>()
    private val accountSessionLifecycle = mockk<AccountSessionLifecycle>()
    private val target = AuthTokenRefreshHandler(
        authRepositoryProvider = authRepositoryProvider,
        tokenStorage = tokenStorage,
        accountSessionLifecycle = accountSessionLifecycle,
    )

    @Test
    fun `stale failed token returns latest access token without touching session`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "fresh-access"

        val result = target.refreshAccessToken("stale-access")

        assertEquals("fresh-access", result)
        coVerify(exactly = 0) { accountSessionLifecycle.refreshSession(any()) }
        coVerify(exactly = 0) { accountSessionLifecycle.terminateSession(any()) }
    }

    @Test
    fun `missing refresh token terminates account session`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns null
        coEvery {
            accountSessionLifecycle.terminateSession(SessionTerminationReason.SessionExpired)
        } returns AccountSessionResult.Success

        val result = target.refreshAccessToken("expired-access")

        assertNull(result)
        coVerify(exactly = 1) {
            accountSessionLifecycle.terminateSession(SessionTerminationReason.SessionExpired)
        }
    }

    @Test
    fun `successful refresh replaces tokens without activating a fresh session`() = runBlocking {
        val tokens = tokens()
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery {
            authRepository.refresh("refresh-token")
        } returns AuthResult.Success(tokens)
        coJustRun { accountSessionLifecycle.refreshSession(tokens) }

        val result = target.refreshAccessToken("expired-access")

        assertEquals(tokens.accessToken, result)
        coVerify(exactly = 1) { accountSessionLifecycle.refreshSession(tokens) }
        coVerify(exactly = 0) { accountSessionLifecycle.terminateSession(any()) }
    }

    @Test
    fun `definitive refresh rejection terminates account session`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery {
            authRepository.refresh("refresh-token")
        } returns AuthResult.Error(AuthError.InvalidCredentials)
        coEvery {
            accountSessionLifecycle.terminateSession(
                SessionTerminationReason.RefreshCredentialsRejected,
            )
        } returns AccountSessionResult.Success

        val result = target.refreshAccessToken("expired-access")

        assertNull(result)
        coVerify(exactly = 1) {
            accountSessionLifecycle.terminateSession(
                SessionTerminationReason.RefreshCredentialsRejected,
            )
        }
        coVerify(exactly = 0) { accountSessionLifecycle.refreshSession(any()) }
    }

    @Test
    fun `validation refresh rejection terminates account session`() = runBlocking {
        assertTerminalRefresh(AuthError.ValidationFailed())
    }

    @Test
    fun `forbidden refresh rejection terminates account session`() = runBlocking {
        assertTerminalRefresh(AuthError.Forbidden)
    }

    @Test
    fun `transient refresh failure keeps current local session`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery {
            authRepository.refresh("refresh-token")
        } returns AuthResult.Error(AuthError.Unknown(code = 503))

        val result = target.refreshAccessToken("expired-access")

        assertNull(result)
        coVerify(exactly = 0) { accountSessionLifecycle.terminateSession(any()) }
        coVerify(exactly = 0) { accountSessionLifecycle.refreshSession(any()) }
    }

    private fun tokens() = AuthTokens(
        accessToken = "new-access",
        refreshToken = "new-refresh",
        issuedAt = Instant.parse("2026-07-28T10:00:00Z"),
    )

    private suspend fun assertTerminalRefresh(error: AuthError) {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery {
            authRepository.refresh("refresh-token")
        } returns AuthResult.Error(error)
        coEvery {
            accountSessionLifecycle.terminateSession(
                SessionTerminationReason.RefreshCredentialsRejected,
            )
        } returns AccountSessionResult.Success

        val result = target.refreshAccessToken("expired-access")

        assertNull(result)
        coVerify(exactly = 1) {
            accountSessionLifecycle.terminateSession(
                SessionTerminationReason.RefreshCredentialsRejected,
            )
        }
    }
}
