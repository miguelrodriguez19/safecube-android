package com.miguelrodriguez19.safecube.core.network.data.auth

import com.miguelrodriguez19.safecube.core.network.domain.port.TokenProvider
import com.miguelrodriguez19.safecube.core.network.domain.port.TokenRefreshHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class TokenRefreshAuthenticatorIntegrationTest {

    @Test
    fun `authenticate returns null when response is not unauthorized`() {
        val tokenProvider = mockk<TokenProvider>(relaxed = true)
        val refreshHandler = mockk<TokenRefreshHandler>(relaxed = true)
        val authenticator = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = authenticator.authenticate(
            route = null,
            response = response(code = 200),
        )

        assertNull(retriedRequest)
        verify(exactly = 0) { tokenProvider.getAccessToken() }
        coVerify(exactly = 0) { refreshHandler.refreshAccessToken(any()) }
    }

    @Test
    fun `authenticate returns null when unauthorized response comes from refresh endpoint`() {
        val tokenProvider = mockk<TokenProvider>(relaxed = true)
        val refreshHandler = mockk<TokenRefreshHandler>(relaxed = true)
        val authenticator = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = authenticator.authenticate(
            route = null,
            response = response(code = 401, path = "/auth/refresh"),
        )

        assertNull(retriedRequest)
        verify(exactly = 0) { tokenProvider.getAccessToken() }
        coVerify(exactly = 0) { refreshHandler.refreshAccessToken(any()) }
    }

    @Test
    fun `authenticate retries request with refreshed token when refresh succeeds`() {
        val tokenProvider = mockk<TokenProvider>()
        val refreshHandler = mockk<TokenRefreshHandler>()
        every { tokenProvider.getAccessToken() } returns "expired-token"
        coEvery { refreshHandler.refreshAccessToken("expired-token") } returns "new-token"
        val authenticator = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = authenticator.authenticate(
            route = null,
            response = response(
                code = 401,
                authorizationHeader = "Bearer expired-token",
            ),
        )

        assertNotNull(retriedRequest)
        assertEquals("Bearer new-token", retriedRequest?.header("Authorization"))
        verify(exactly = 1) { tokenProvider.getAccessToken() }
        coVerify(exactly = 1) { refreshHandler.refreshAccessToken("expired-token") }
    }

    @Test
    fun `authenticate uses in-memory rotated token without invoking refresh handler`() {
        val tokenProvider = mockk<TokenProvider>()
        val refreshHandler = mockk<TokenRefreshHandler>()
        every { tokenProvider.getAccessToken() } returns "already-rotated-token"
        val authenticator = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = authenticator.authenticate(
            route = null,
            response = response(
                code = 401,
                authorizationHeader = "Bearer stale-token",
            ),
        )

        assertNotNull(retriedRequest)
        assertEquals("Bearer already-rotated-token", retriedRequest?.header("Authorization"))
        verify(exactly = 1) { tokenProvider.getAccessToken() }
        coVerify(exactly = 0) { refreshHandler.refreshAccessToken(any()) }
    }

    @Test
    fun `authenticate returns null when no refresh handler binding exists`() {
        val tokenProvider = mockk<TokenProvider>()
        every { tokenProvider.getAccessToken() } returns "expired-token"
        val authenticator = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.empty(),
        )

        val retriedRequest = authenticator.authenticate(
            route = null,
            response = response(
                code = 401,
                authorizationHeader = "Bearer expired-token",
            ),
        )

        assertNull(retriedRequest)
        verify(exactly = 1) { tokenProvider.getAccessToken() }
    }

    @Test
    fun `authenticate returns null when refresh returns same token`() {
        val tokenProvider = mockk<TokenProvider>()
        val refreshHandler = mockk<TokenRefreshHandler>()
        every { tokenProvider.getAccessToken() } returns "expired-token"
        coEvery { refreshHandler.refreshAccessToken("expired-token") } returns "expired-token"
        val authenticator = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = authenticator.authenticate(
            route = null,
            response = response(
                code = 401,
                authorizationHeader = "Bearer expired-token",
            ),
        )

        assertNull(retriedRequest)
        verify(exactly = 1) { tokenProvider.getAccessToken() }
        coVerify(exactly = 1) { refreshHandler.refreshAccessToken("expired-token") }
    }

    @Test
    fun `authenticate returns null when request was already retried`() {
        val tokenProvider = mockk<TokenProvider>(relaxed = true)
        val refreshHandler = mockk<TokenRefreshHandler>(relaxed = true)
        val authenticator = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val priorResponse = response(
            code = 401,
            authorizationHeader = "Bearer prior-token",
        )
        val retriedRequest = authenticator.authenticate(
            route = null,
            response = response(
                code = 401,
                authorizationHeader = "Bearer expired-token",
                priorResponse = priorResponse,
            ),
        )

        assertNull(retriedRequest)
        verify(exactly = 0) { tokenProvider.getAccessToken() }
        coVerify(exactly = 0) { refreshHandler.refreshAccessToken(any()) }
    }

    private fun response(
        code: Int,
        path: String = "/api/items",
        authorizationHeader: String? = null,
        priorResponse: Response? = null,
    ): Response {
        val request = Request.Builder()
            .url("https://example.com$path")
            .apply {
                authorizationHeader?.let { header("Authorization", it) }
            }
            .build()

        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code == 401) "Unauthorized" else "OK")
            .body("{}".toResponseBody())
            .apply {
                priorResponse?.let(::priorResponse)
            }
            .build()
    }
}
