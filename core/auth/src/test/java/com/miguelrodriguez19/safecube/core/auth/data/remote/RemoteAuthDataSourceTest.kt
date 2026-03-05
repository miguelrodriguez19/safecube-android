package com.miguelrodriguez19.safecube.core.auth.data.remote

import com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthTokensResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthenticateAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RefreshTokenRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountResult
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class RemoteAuthDataSourceTest {
    @Test
    fun `login returns success result preserving code and body`() = runBlocking {
        val expectedBody = AuthTokensResponse(
            accessToken = "access-token",
            refreshToken = "refresh-token",
        )
        val dataSource = RemoteAuthDataSource(
            authControllerApi = FakeAuthControllerApi(
                loginResponse = Response.success(expectedBody),
            ),
            refreshAuthControllerApi = FakeAuthControllerApi(),
        )

        val result = dataSource.login(
            request = AuthenticateAccountRequest(
                email = "test@example.com",
                password = "password",
            ),
        )

        assertEquals(
            NetworkResult.Success(
                httpCode = 200,
                body = expectedBody,
            ),
            result,
        )
    }

    @Test
    fun `register returns http error preserving code and error body`() = runBlocking {
        val errorJson = """{"error":"Validation failed","fields":{"email":"invalid"}}"""
        val dataSource = RemoteAuthDataSource(
            authControllerApi = FakeAuthControllerApi(
                registerResponse = Response.error(
                    400,
                    errorJson.toResponseBody("application/json".toMediaType()),
                ),
            ),
            refreshAuthControllerApi = FakeAuthControllerApi(),
        )

        val result = dataSource.register(
            request = RegisterAccountRequest(
                email = "invalid-email",
                password = "password",
            ),
        )

        assertEquals(
            NetworkResult.HttpError<RegisterAccountResult>(
                httpCode = 400,
                body = null,
                errorBody = errorJson,
            ),
            result,
        )
    }

    @Test
    fun `refresh wraps transport exceptions into failure result`() = runBlocking {
        val dataSource = RemoteAuthDataSource(
            authControllerApi = FakeAuthControllerApi(),
            refreshAuthControllerApi = FakeAuthControllerApi(
                refreshThrowable = IllegalStateException("network down"),
            ),
        )

        val result = dataSource.refresh(
            request = RefreshTokenRequest(refreshToken = "refresh"),
        )

        assertTrue(result is NetworkResult.Failure)
        val failure = result as NetworkResult.Failure
        assertEquals("network down", failure.throwable.message)
    }
}

private class FakeAuthControllerApi(
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
