package com.miguelrodriguez19.safecube.core.auth.data.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.repository.TokenStorage
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.verify
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
    private val sessionManager = mockk<SessionManager>()

    private val target = AuthTokenRefreshHandler(
        authRepositoryProvider = authRepositoryProvider,
        tokenStorage = tokenStorage,
        sessionManager = sessionManager,
    )

    @Test
    fun `refreshAccessToken when failed access token is stale then returns latest token without refreshing`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "fresh-access-token"

        val result = target.refreshAccessToken(
            failedAccessToken = "old-access-token",
        )

        assertEquals("fresh-access-token", result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 0) { tokenStorage.getRefreshToken() }
        verify(exactly = 0) { authRepositoryProvider.get() }
        verify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        verify(exactly = 0) { sessionManager.forceLogout() }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when refresh token is missing then forces logout and returns null`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns null
        every { sessionManager.forceLogout() } just Runs

        val result = target.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 0) { authRepositoryProvider.get() }
        verify(exactly = 1) { sessionManager.forceLogout() }
        verify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when refresh succeeds then returns new access token and updates session`() = runBlocking {
        val newTokens = AuthTokens(
            accessToken = "new-access",
            refreshToken = "new-refresh",
            issuedAt = Instant.parse("2026-03-05T00:00:00Z"),
        )
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "current-refresh"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery { authRepository.refresh("current-refresh") } returns AuthResult.Success(newTokens)
        every { sessionManager.onLoginSuccess(newTokens) } just Runs

        val result = target.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertEquals("new-access", result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { authRepositoryProvider.get() }
        coVerify(exactly = 1) { authRepository.refresh("current-refresh") }
        verify(exactly = 1) { sessionManager.onLoginSuccess(newTokens) }
        verify(exactly = 0) { sessionManager.forceLogout() }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when refresh returns invalid credentials then forces logout`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "current-refresh"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery { authRepository.refresh("current-refresh") } returns AuthResult.Error(AuthError.InvalidCredentials)
        every { sessionManager.forceLogout() } just Runs

        val result = target.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { authRepositoryProvider.get() }
        coVerify(exactly = 1) { authRepository.refresh("current-refresh") }
        verify(exactly = 1) { sessionManager.forceLogout() }
        verify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when refresh returns forbidden then forces logout`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery { authRepository.refresh("refresh-token") } returns AuthResult.Error(AuthError.Forbidden)
        every { sessionManager.forceLogout() } just Runs

        val result = target.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { authRepositoryProvider.get() }
        coVerify(exactly = 1) { authRepository.refresh("refresh-token") }
        verify(exactly = 1) { sessionManager.forceLogout() }
        verify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when refresh returns account already exists then forces logout`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery { authRepository.refresh("refresh-token") } returns AuthResult.Error(AuthError.AccountAlreadyExists)
        every { sessionManager.forceLogout() } just Runs

        val result = target.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { authRepositoryProvider.get() }
        coVerify(exactly = 1) { authRepository.refresh("refresh-token") }
        verify(exactly = 1) { sessionManager.forceLogout() }
        verify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when refresh returns conflict then forces logout`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery { authRepository.refresh("refresh-token") } returns AuthResult.Error(
            AuthError.Conflict(message = "conflict"),
        )
        every { sessionManager.forceLogout() } just Runs

        val result = target.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { authRepositoryProvider.get() }
        coVerify(exactly = 1) { authRepository.refresh("refresh-token") }
        verify(exactly = 1) { sessionManager.forceLogout() }
        verify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when failed access token is blank then does not reuse current token and refreshes`() = runBlocking {
        val newTokens = AuthTokens(
            accessToken = "new-access",
            refreshToken = "new-refresh",
            issuedAt = Instant.parse("2026-03-05T00:00:00Z"),
        )
        every { tokenStorage.getAccessToken() } returns "current-access"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery { authRepository.refresh("refresh-token") } returns AuthResult.Success(newTokens)
        every { sessionManager.onLoginSuccess(newTokens) } just Runs

        val result = target.refreshAccessToken(
            failedAccessToken = "",
        )

        assertEquals("new-access", result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { authRepositoryProvider.get() }
        coVerify(exactly = 1) { authRepository.refresh("refresh-token") }
        verify(exactly = 1) { sessionManager.onLoginSuccess(newTokens) }
        verify(exactly = 0) { sessionManager.forceLogout() }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when refresh token is blank then forces logout and returns null`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "   "
        every { sessionManager.forceLogout() } just Runs

        val result = target.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 0) { authRepositoryProvider.get() }
        verify(exactly = 1) { sessionManager.forceLogout() }
        verify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when refresh returns validation failed then forces logout`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery { authRepository.refresh("refresh-token") } returns AuthResult.Error(
            AuthError.ValidationFailed(fields = null, message = "invalid"),
        )
        every { sessionManager.forceLogout() } just Runs

        val result = target.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { authRepositoryProvider.get() }
        coVerify(exactly = 1) { authRepository.refresh("refresh-token") }
        verify(exactly = 1) { sessionManager.forceLogout() }
        verify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when refresh returns account not active then forces logout`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery { authRepository.refresh("refresh-token") } returns AuthResult.Error(AuthError.AccountNotActive)
        every { sessionManager.forceLogout() } just Runs

        val result = target.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { authRepositoryProvider.get() }
        coVerify(exactly = 1) { authRepository.refresh("refresh-token") }
        verify(exactly = 1) { sessionManager.forceLogout() }
        verify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when refresh returns unknown auth failure code then forces logout`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "refresh-token"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery { authRepository.refresh("refresh-token") } returns AuthResult.Error(
            AuthError.Unknown(code = 400, message = "bad request"),
        )
        every { sessionManager.forceLogout() } just Runs

        val result = target.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { authRepositoryProvider.get() }
        coVerify(exactly = 1) { authRepository.refresh("refresh-token") }
        verify(exactly = 1) { sessionManager.forceLogout() }
        verify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }

    @Test
    fun `refreshAccessToken when refresh returns unknown transport failure then does not force logout`() = runBlocking {
        every { tokenStorage.getAccessToken() } returns "expired-access"
        every { tokenStorage.getRefreshToken() } returns "current-refresh"
        every { authRepositoryProvider.get() } returns authRepository
        coEvery { authRepository.refresh("current-refresh") } returns AuthResult.Error(
            AuthError.Unknown(
                code = null,
                message = "network down",
            ),
        )

        val result = target.refreshAccessToken(
            failedAccessToken = "expired-access",
        )

        assertNull(result)
        verify(exactly = 1) { tokenStorage.getAccessToken() }
        verify(exactly = 1) { tokenStorage.getRefreshToken() }
        verify(exactly = 1) { authRepositoryProvider.get() }
        coVerify(exactly = 1) { authRepository.refresh("current-refresh") }
        verify(exactly = 0) { sessionManager.forceLogout() }
        verify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        confirmVerified(authRepositoryProvider, authRepository, tokenStorage, sessionManager)
    }
}
