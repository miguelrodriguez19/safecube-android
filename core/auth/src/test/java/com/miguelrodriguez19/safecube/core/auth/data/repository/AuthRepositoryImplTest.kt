package com.miguelrodriguez19.safecube.core.auth.data.repository

import com.miguelrodriguez19.safecube.core.auth.data.mapper.AuthErrorMapper
import com.miguelrodriguez19.safecube.core.auth.data.remote.NetworkResult
import com.miguelrodriguez19.safecube.core.auth.data.remote.RemoteAuthDataSource
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthOperation
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.RegisteredAccount
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthTokensResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class AuthRepositoryImplTest {

    private val remoteAuthDataSource = mockk<RemoteAuthDataSource>()
    private val authErrorMapper = mockk<AuthErrorMapper>()

    private val target = AuthRepositoryImpl(
        remoteAuthDataSource = remoteAuthDataSource,
        authErrorMapper = authErrorMapper,
    )

    @Test
    fun `register when data source returns success then maps registered account into domain result`() = runBlocking {
        val accountId = UUID.fromString("7ecf3225-6f88-4b95-a4be-2cc6fbf9a1f8")
        val createdAt = Instant.parse("2026-03-09T09:30:00Z")
        coEvery { remoteAuthDataSource.register(any()) } returns NetworkResult.Success(
            httpCode = 200,
            body = RegisterAccountResult(
                accountId = accountId,
                createdAt = createdAt,
            ),
        )

        val result = target.register(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(
            AuthResult.Success(
                RegisteredAccount(
                    accountId = accountId,
                    createdAt = createdAt,
                ),
            ),
            result,
        )
        coVerify(exactly = 1) {
            remoteAuthDataSource.register(
                match { request ->
                    request.email == "user@example.com" &&
                        request.password == "password"
                },
            )
        }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `register when data source returns http error then delegates mapping using signup operation`() = runBlocking {
        val errorBody = """{"error":"Account already exists"}"""
        val mappedError = AuthError.AccountAlreadyExists
        coEvery { remoteAuthDataSource.register(any()) } returns NetworkResult.HttpError(
            httpCode = 409,
            body = null,
            errorBody = errorBody,
        )
        every {
            authErrorMapper.map(
                statusCode = 409,
                errorBody = errorBody,
                operation = AuthOperation.SIGNUP,
            )
        } returns mappedError

        val result = target.register(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(AuthResult.Error(mappedError), result)
        coVerify(exactly = 1) { remoteAuthDataSource.register(any()) }
        verify(exactly = 1) {
            authErrorMapper.map(
                statusCode = 409,
                errorBody = errorBody,
                operation = AuthOperation.SIGNUP,
            )
        }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `register when data source returns failure then returns unknown error preserving throwable message`() = runBlocking {
        coEvery { remoteAuthDataSource.register(any()) } returns NetworkResult.Failure(
            throwable = IllegalStateException("network down"),
        )

        val result = target.register(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(
            AuthResult.Error(
                AuthError.Unknown(
                    code = null,
                    message = "network down",
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { remoteAuthDataSource.register(any()) }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `login when data source returns success with valid body then maps auth tokens into domain result`() = runBlocking {
        val issuedAt = Instant.parse("2026-03-06T12:11:35.524804768Z")
        coEvery { remoteAuthDataSource.login(any()) } returns NetworkResult.Success(
            httpCode = 200,
            body = AuthTokensResponse(
                accessToken = "access-token",
                refreshToken = "refresh-token",
                issuedAt = issuedAt,
            ),
        )

        val result = target.login(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(
            AuthResult.Success(
                AuthTokens(
                    accessToken = "access-token",
                    refreshToken = "refresh-token",
                    issuedAt = issuedAt,
                ),
            ),
            result,
        )
        coVerify(exactly = 1) {
            remoteAuthDataSource.login(
                match { request ->
                    request.email == "user@example.com" &&
                        request.password == "password"
                },
            )
        }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `login when success body is missing then returns unknown error with successful code`() = runBlocking {
        coEvery { remoteAuthDataSource.login(any()) } returns NetworkResult.Success(
            httpCode = 200,
            body = null,
        )

        val result = target.login(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(
            AuthResult.Error(
                AuthError.Unknown(
                    code = 200,
                    message = "Missing token payload in successful auth response.",
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { remoteAuthDataSource.login(any()) }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `login when success body has blank access token then returns unknown error`() = runBlocking {
        coEvery { remoteAuthDataSource.login(any()) } returns NetworkResult.Success(
            httpCode = 200,
            body = AuthTokensResponse(
                accessToken = "",
                refreshToken = "refresh-token",
                issuedAt = Instant.parse("2026-03-09T09:30:00Z"),
            ),
        )

        val result = target.login(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(
            AuthResult.Error(
                AuthError.Unknown(
                    code = 200,
                    message = "Missing token payload in successful auth response.",
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { remoteAuthDataSource.login(any()) }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `login when success body has blank refresh token then returns unknown error`() = runBlocking {
        coEvery { remoteAuthDataSource.login(any()) } returns NetworkResult.Success(
            httpCode = 200,
            body = AuthTokensResponse(
                accessToken = "access-token",
                refreshToken = "",
                issuedAt = Instant.parse("2026-03-09T09:30:00Z"),
            ),
        )

        val result = target.login(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(
            AuthResult.Error(
                AuthError.Unknown(
                    code = 200,
                    message = "Missing token payload in successful auth response.",
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { remoteAuthDataSource.login(any()) }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `login when data source returns http error then delegates mapping using login operation`() = runBlocking {
        val errorBody = """{"error":"Invalid credentials"}"""
        coEvery { remoteAuthDataSource.login(any()) } returns NetworkResult.HttpError(
            httpCode = 401,
            body = null,
            errorBody = errorBody,
        )
        every {
            authErrorMapper.map(
                statusCode = 401,
                errorBody = errorBody,
                operation = AuthOperation.LOGIN,
            )
        } returns AuthError.InvalidCredentials

        val result = target.login(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(AuthResult.Error(AuthError.InvalidCredentials), result)
        coVerify(exactly = 1) { remoteAuthDataSource.login(any()) }
        verify(exactly = 1) {
            authErrorMapper.map(
                statusCode = 401,
                errorBody = errorBody,
                operation = AuthOperation.LOGIN,
            )
        }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `login when data source returns failure then returns unknown error preserving throwable message`() = runBlocking {
        coEvery { remoteAuthDataSource.login(any()) } returns NetworkResult.Failure(
            throwable = IllegalStateException("network down"),
        )

        val result = target.login(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(
            AuthResult.Error(
                AuthError.Unknown(
                    code = null,
                    message = "network down",
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { remoteAuthDataSource.login(any()) }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `refresh when data source returns http error then delegates mapping using refresh operation`() = runBlocking {
        val errorBody = """{"error":"Refresh token conflict"}"""
        val mappedError = AuthError.Conflict(message = "Refresh token conflict")
        coEvery { remoteAuthDataSource.refresh(any()) } returns NetworkResult.HttpError(
            httpCode = 409,
            body = null,
            errorBody = errorBody,
        )
        every {
            authErrorMapper.map(
                statusCode = 409,
                errorBody = errorBody,
                operation = AuthOperation.REFRESH,
            )
        } returns mappedError

        val result = target.refresh(
            refreshToken = "refresh-token",
        )

        assertEquals(AuthResult.Error(mappedError), result)
        coVerify(exactly = 1) {
            remoteAuthDataSource.refresh(
                match { request -> request.refreshToken == "refresh-token" },
            )
        }
        verify(exactly = 1) {
            authErrorMapper.map(
                statusCode = 409,
                errorBody = errorBody,
                operation = AuthOperation.REFRESH,
            )
        }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `refresh when data source returns success with valid body then maps auth tokens into domain result`() = runBlocking {
        val issuedAt = Instant.parse("2026-03-06T12:11:35.524804768Z")
        coEvery { remoteAuthDataSource.refresh(any()) } returns NetworkResult.Success(
            httpCode = 200,
            body = AuthTokensResponse(
                accessToken = "access-token",
                refreshToken = "refresh-token",
                issuedAt = issuedAt,
            ),
        )

        val result = target.refresh(
            refreshToken = "refresh-token",
        )

        assertEquals(
            AuthResult.Success(
                AuthTokens(
                    accessToken = "access-token",
                    refreshToken = "refresh-token",
                    issuedAt = issuedAt,
                ),
            ),
            result,
        )
        coVerify(exactly = 1) {
            remoteAuthDataSource.refresh(
                match { request -> request.refreshToken == "refresh-token" },
            )
        }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `refresh when success body is missing then returns unknown error with successful code`() = runBlocking {
        coEvery { remoteAuthDataSource.refresh(any()) } returns NetworkResult.Success(
            httpCode = 200,
            body = null,
        )

        val result = target.refresh(
            refreshToken = "refresh-token",
        )

        assertEquals(
            AuthResult.Error(
                AuthError.Unknown(
                    code = 200,
                    message = "Missing token payload in successful auth response.",
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { remoteAuthDataSource.refresh(any()) }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `refresh when data source returns failure then returns unknown error preserving throwable message`() = runBlocking {
        coEvery { remoteAuthDataSource.refresh(any()) } returns NetworkResult.Failure(
            throwable = IllegalStateException("network down"),
        )

        val result = target.refresh(
            refreshToken = "refresh-token",
        )

        assertEquals(
            AuthResult.Error(
                AuthError.Unknown(
                    code = null,
                    message = "network down",
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { remoteAuthDataSource.refresh(any()) }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `logout when data source returns success then returns success unit`() = runBlocking {
        coEvery { remoteAuthDataSource.logout() } returns NetworkResult.Success(
            httpCode = 200,
            body = Unit,
        )

        val result = target.logout()

        assertEquals(AuthResult.Success(Unit), result)
        coVerify(exactly = 1) { remoteAuthDataSource.logout() }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `logout when data source returns http error then delegates mapping using logout operation`() = runBlocking {
        val errorBody = """{"error":"Forbidden"}"""
        coEvery { remoteAuthDataSource.logout() } returns NetworkResult.HttpError(
            httpCode = 403,
            body = null,
            errorBody = errorBody,
        )
        every {
            authErrorMapper.map(
                statusCode = 403,
                errorBody = errorBody,
                operation = AuthOperation.LOGOUT,
            )
        } returns AuthError.Forbidden

        val result = target.logout()

        assertEquals(AuthResult.Error(AuthError.Forbidden), result)
        coVerify(exactly = 1) { remoteAuthDataSource.logout() }
        verify(exactly = 1) {
            authErrorMapper.map(
                statusCode = 403,
                errorBody = errorBody,
                operation = AuthOperation.LOGOUT,
            )
        }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }

    @Test
    fun `logout when data source returns failure then returns unknown error with throwable message`() = runBlocking {
        coEvery { remoteAuthDataSource.logout() } returns NetworkResult.Failure(
            throwable = IllegalStateException("network down"),
        )

        val result = target.logout()

        assertEquals(
            AuthResult.Error(
                AuthError.Unknown(
                    code = null,
                    message = "network down",
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { remoteAuthDataSource.logout() }
        verify(exactly = 0) { authErrorMapper.map(any(), any(), any()) }
        confirmVerified(remoteAuthDataSource, authErrorMapper)
    }
}
