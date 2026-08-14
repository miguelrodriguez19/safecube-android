package com.miguelrodriguez19.safecube.core.auth.data.remote

import com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthTokensResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthenticateAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RefreshTokenRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.concurrent.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import retrofit2.Response

class RemoteAuthDataSourceTest {

    private val authControllerApi = mockk<AuthControllerApi>()
    private val refreshAuthControllerApi = mockk<AuthControllerApi>()

    private val target = RemoteAuthDataSource(
        authControllerApi = authControllerApi,
        refreshAuthControllerApi = refreshAuthControllerApi,
    )

    @Test
    fun `register when api returns validation error then returns sanitized classification`() = runBlocking {
        val response = mockk<Response<RegisterAccountResult>>()
        coEvery { authControllerApi.register(any()) } returns response
        every { response.isSuccessful } returns false
        every { response.code() } returns 400
        every { response.body() } returns null

        val result = target.register(
            request = RegisterAccountRequest(
                email = "invalid-email",
                password = "password",
            ),
        )

        assertEquals(
            NetworkResult.HttpError<RegisterAccountResult>(
                failure = NetworkFailureClassifier.fromHttpStatus(400),
            ),
            result,
        )
        coVerify(exactly = 1) {
            authControllerApi.register(
                match { request ->
                    request.email == "invalid-email" &&
                        request.password == "password"
                },
            )
        }
        verify(exactly = 1) { response.isSuccessful }
        verify(exactly = 1) { response.code() }
        confirmVerified(authControllerApi, refreshAuthControllerApi, response)
    }

    @Test
    fun `login when api returns success then returns success with response body`() = runBlocking {
        val response = mockk<Response<AuthTokensResponse>>()
        val body = AuthTokensResponse(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            issuedAt = Instant.parse("2026-03-06T12:11:35.524804768Z"),
        )
        coEvery { authControllerApi.login(any()) } returns response
        every { response.isSuccessful } returns true
        every { response.code() } returns 200
        every { response.body() } returns body

        val result = target.login(
            request = AuthenticateAccountRequest(
                email = "user@example.com",
                password = "password",
            ),
        )

        assertEquals(
            NetworkResult.Success(
                httpCode = 200,
                body = body,
            ),
            result,
        )
        coVerify(exactly = 1) { authControllerApi.login(any()) }
        verify(exactly = 1) { response.isSuccessful }
        verify(exactly = 1) { response.code() }
        verify(exactly = 1) { response.body() }
        confirmVerified(authControllerApi, refreshAuthControllerApi, response)
    }

    @Test
    fun `login when api throws cancellation exception then rethrows cancellation exception`() = runBlocking {
        coEvery { authControllerApi.login(any()) } throws CancellationException("cancelled")

        assertThrows(CancellationException::class.java) {
            runBlocking {
                target.login(
                    request = AuthenticateAccountRequest(
                        email = "user@example.com",
                        password = "password",
                    ),
                )
            }
        }

        coVerify(exactly = 1) {
            authControllerApi.login(
                match { request ->
                    request.email == "user@example.com" &&
                        request.password == "password"
                },
            )
        }
        confirmVerified(authControllerApi, refreshAuthControllerApi)
    }

    @Test
    fun `refresh when api throws unexpected exception then returns failure preserving throwable`() = runBlocking {
        val failure = IllegalStateException("network down")
        coEvery { refreshAuthControllerApi.refresh(any()) } throws failure

        val result = target.refresh(
            request = RefreshTokenRequest(refreshToken = "refresh-token"),
        )

        assertEquals(
            NetworkResult.Failure(NetworkFailureClassifier.fromThrowable(failure)),
            result,
        )
        coVerify(exactly = 1) {
            refreshAuthControllerApi.refresh(
                match { request -> request.refreshToken == "refresh-token" },
            )
        }
        confirmVerified(authControllerApi, refreshAuthControllerApi)
    }

    @Test
    fun `logout when api returns forbidden then returns sanitized classification`() = runBlocking {
        val response = mockk<Response<Unit>>()
        coEvery { authControllerApi.logout() } returns response
        every { response.isSuccessful } returns false
        every { response.code() } returns 403
        every { response.body() } returns null

        val result = target.logout()

        assertEquals(
            NetworkResult.HttpError<Unit>(
                failure = NetworkFailureClassifier.fromHttpStatus(403),
            ),
            result,
        )
        coVerify(exactly = 1) { authControllerApi.logout() }
        verify(exactly = 1) { response.isSuccessful }
        verify(exactly = 1) { response.code() }
        confirmVerified(authControllerApi, refreshAuthControllerApi, response)
    }
}
