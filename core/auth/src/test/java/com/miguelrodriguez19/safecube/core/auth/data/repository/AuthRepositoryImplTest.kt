package com.miguelrodriguez19.safecube.core.auth.data.repository

import com.miguelrodriguez19.safecube.core.auth.data.mapper.AuthErrorMapper
import com.miguelrodriguez19.safecube.core.auth.data.remote.RemoteAuthDataSource
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.RegisteredAccount
import com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthTokensResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthenticateAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RefreshTokenRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountResult
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.Response

class AuthRepositoryImplTest {
    private val mapper = AuthErrorMapper()

    @Test
    fun `login maps successful response into domain auth tokens`() = runBlocking {
        val issuedAt = OffsetDateTime.parse("2026-03-05T00:00:00Z")
        val expected = AuthTokens(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            issuedAt = issuedAt,
        )
        val repository = repositoryWithApi(
            authControllerApi = AuthRepositoryFakeAuthControllerApi(
                loginResponse = Response.success(
                    AuthTokensResponse(
                        accessToken = expected.accessToken,
                        refreshToken = expected.refreshToken,
                        issuedAt = issuedAt,
                    ),
                ),
            ),
        )

        val result = repository.login(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(AuthResult.Success(expected), result)
    }

    @Test
    fun `register maps successful response into domain account`() = runBlocking {
        val accountId = UUID.fromString("8a2d2abf-9db9-4b6a-bf67-6d638665a501")
        val createdAt = OffsetDateTime.parse("2026-03-05T00:00:00Z")
        val repository = repositoryWithApi(
            authControllerApi = AuthRepositoryFakeAuthControllerApi(
                registerResponse = Response.success(
                    RegisterAccountResult(
                        accountId = accountId,
                        createdAt = createdAt,
                    ),
                ),
            ),
        )

        val result = repository.register(
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
    }

    @Test
    fun `register maps 409 into account already exists`() = runBlocking {
        val repository = repositoryWithApi(
            authControllerApi = AuthRepositoryFakeAuthControllerApi(
                registerResponse = Response.error(
                    409,
                    """{"error":"Account already exists"}"""
                        .toResponseBody("application/json".toMediaType()),
                ),
            ),
        )

        val result = repository.register(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(AuthResult.Error(AuthError.AccountAlreadyExists), result)
    }

    @Test
    fun `refresh maps 401 into invalid credentials`() = runBlocking {
        val repository = repositoryWithApi(
            authControllerApi = AuthRepositoryFakeAuthControllerApi(
                refreshResponse = Response.error(
                    401,
                    """{"error":"Invalid credentials"}"""
                        .toResponseBody("application/json".toMediaType()),
                ),
            ),
        )

        val result = repository.refresh(
            refreshToken = "refresh-token",
        )

        assertEquals(AuthResult.Error(AuthError.InvalidCredentials), result)
    }

    @Test
    fun `logout maps transport failure into unknown auth error`() = runBlocking {
        val repository = repositoryWithApi(
            authControllerApi = AuthRepositoryFakeAuthControllerApi(
                logoutThrowable = IllegalStateException("network down"),
            ),
        )

        val result = repository.logout()

        assertEquals(
            AuthResult.Error(
                AuthError.Unknown(
                    code = null,
                    message = "network down",
                ),
            ),
            result,
        )
    }

    private fun repositoryWithApi(
        authControllerApi: AuthControllerApi,
    ): AuthRepositoryImpl = AuthRepositoryImpl(
        remoteAuthDataSource = RemoteAuthDataSource(authControllerApi),
        authErrorMapper = mapper,
    )
}

private class AuthRepositoryFakeAuthControllerApi(
    private val registerResponse: Response<RegisterAccountResult> = Response.success(RegisterAccountResult()),
    private val loginResponse: Response<AuthTokensResponse> = Response.success(AuthTokensResponse()),
    private val refreshResponse: Response<AuthTokensResponse> = Response.success(AuthTokensResponse()),
    private val logoutResponse: Response<Unit> = Response.success(Unit),
    private val registerThrowable: Throwable? = null,
    private val loginThrowable: Throwable? = null,
    private val refreshThrowable: Throwable? = null,
    private val logoutThrowable: Throwable? = null,
) : AuthControllerApi {
    override suspend fun login(authenticateAccountRequest: AuthenticateAccountRequest): Response<AuthTokensResponse> {
        loginThrowable?.let { throw it }
        return loginResponse
    }

    override suspend fun logout(): Response<Unit> {
        logoutThrowable?.let { throw it }
        return logoutResponse
    }

    override suspend fun refresh(refreshTokenRequest: RefreshTokenRequest): Response<AuthTokensResponse> {
        refreshThrowable?.let { throw it }
        return refreshResponse
    }

    override suspend fun register(registerAccountRequest: RegisterAccountRequest): Response<RegisterAccountResult> {
        registerThrowable?.let { throw it }
        return registerResponse
    }
}
