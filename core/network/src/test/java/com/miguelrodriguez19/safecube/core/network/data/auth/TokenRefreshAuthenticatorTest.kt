package com.miguelrodriguez19.safecube.core.network.data.auth

import com.miguelrodriguez19.safecube.core.network.domain.port.TokenProvider
import com.miguelrodriguez19.safecube.core.network.domain.port.TokenRefreshHandler
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.Optional
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class TokenRefreshAuthenticatorTest {
    private val tokenProvider = mockk<TokenProvider>()
    private val refreshHandler = mockk<TokenRefreshHandler>()

    @Test
    fun `authenticate when response is not unauthorized then returns null`() {
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = target.authenticate(
            route = null,
            response = response(code = 200),
        )

        assertNull(retriedRequest)
        verify(exactly = 0) { tokenProvider.getAccessToken() }
        coVerify(exactly = 0) { refreshHandler.refreshAccessToken(any()) }
        confirmVerified(tokenProvider, refreshHandler)
    }

    @Test
    fun `authenticate when protected response is forbidden then does not refresh`() {
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = target.authenticate(
            route = null,
            response = response(code = 403),
        )

        assertNull(retriedRequest)
        verify(exactly = 0) { tokenProvider.getAccessToken() }
        coVerify(exactly = 0) { refreshHandler.refreshAccessToken(any()) }
        confirmVerified(tokenProvider, refreshHandler)
    }

    @Test
    fun `authenticate when unauthorized response comes from refresh endpoint then returns null`() {
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = target.authenticate(
            route = null,
            response = response(code = 401, path = "/auth/refresh"),
        )

        assertNull(retriedRequest)
        verify(exactly = 0) { tokenProvider.getAccessToken() }
        coVerify(exactly = 0) { refreshHandler.refreshAccessToken(any()) }
        confirmVerified(tokenProvider, refreshHandler)
    }

    @Test
    fun `authenticate when refresh succeeds then retries request with refreshed token`() {
        every { tokenProvider.getAccessToken() } returns "expired-token"
        coEvery { refreshHandler.refreshAccessToken("expired-token") } returns "new-token"
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = target.authenticate(
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
        confirmVerified(tokenProvider, refreshHandler)
    }

    @Test
    fun `authenticate when token is already rotated in memory then skips refresh handler`() {
        every { tokenProvider.getAccessToken() } returns "already-rotated-token"
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = target.authenticate(
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
        confirmVerified(tokenProvider, refreshHandler)
    }

    @Test
    fun `authenticate concurrent unauthorized responses perform one effective refresh`() = runBlocking {
        val accessToken = AtomicReference("expired-token")
        every { tokenProvider.getAccessToken() } answers { accessToken.get() }
        coEvery { refreshHandler.refreshAccessToken("expired-token") } answers {
            Thread.sleep(50)
            accessToken.set("new-token")
            "new-token"
        }
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequests = withContext(Dispatchers.Default) {
            (1..8).map {
                async {
                    target.authenticate(
                        route = null,
                        response = response(
                            code = 401,
                            authorizationHeader = "Bearer expired-token",
                        ),
                    )
                }
            }.awaitAll()
        }

        assertEquals(8, retriedRequests.count { it != null })
        coVerify(exactly = 1) { refreshHandler.refreshAccessToken("expired-token") }
    }

    @Test
    fun `authenticate when request has no bearer token then refreshes with null failed token`() {
        every { tokenProvider.getAccessToken() } returns null
        coEvery { refreshHandler.refreshAccessToken(null) } returns "new-token"
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = target.authenticate(
            route = null,
            response = response(
                code = 401,
                authorizationHeader = null,
            ),
        )

        assertNotNull(retriedRequest)
        assertEquals("Bearer new-token", retriedRequest?.header("Authorization"))
        verify(exactly = 1) { tokenProvider.getAccessToken() }
        coVerify(exactly = 1) { refreshHandler.refreshAccessToken(null) }
        confirmVerified(tokenProvider, refreshHandler)
    }

    @Test
    fun `authenticate when in memory token is blank then delegates refresh handler`() {
        every { tokenProvider.getAccessToken() } returns "   "
        coEvery { refreshHandler.refreshAccessToken("expired-token") } returns "new-token"
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = target.authenticate(
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
        confirmVerified(tokenProvider, refreshHandler)
    }

    @Test
    fun `authenticate when refresh handler binding does not exist then returns null`() {
        every { tokenProvider.getAccessToken() } returns "expired-token"
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.empty(),
        )

        val retriedRequest = target.authenticate(
            route = null,
            response = response(
                code = 401,
                authorizationHeader = "Bearer expired-token",
            ),
        )

        assertNull(retriedRequest)
        verify(exactly = 1) { tokenProvider.getAccessToken() }
        confirmVerified(tokenProvider, refreshHandler)
    }

    @Test
    fun `authenticate when refresh returns blank token then returns null`() {
        every { tokenProvider.getAccessToken() } returns "expired-token"
        coEvery { refreshHandler.refreshAccessToken("expired-token") } returns " "
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = target.authenticate(
            route = null,
            response = response(
                code = 401,
                authorizationHeader = "Bearer expired-token",
            ),
        )

        assertNull(retriedRequest)
        verify(exactly = 1) { tokenProvider.getAccessToken() }
        coVerify(exactly = 1) { refreshHandler.refreshAccessToken("expired-token") }
        confirmVerified(tokenProvider, refreshHandler)
    }

    @Test
    fun `authenticate when refresh returns same token then returns null`() {
        every { tokenProvider.getAccessToken() } returns "expired-token"
        coEvery { refreshHandler.refreshAccessToken("expired-token") } returns "expired-token"
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val retriedRequest = target.authenticate(
            route = null,
            response = response(
                code = 401,
                authorizationHeader = "Bearer expired-token",
            ),
        )

        assertNull(retriedRequest)
        verify(exactly = 1) { tokenProvider.getAccessToken() }
        coVerify(exactly = 1) { refreshHandler.refreshAccessToken("expired-token") }
        confirmVerified(tokenProvider, refreshHandler)
    }

    @Test
    fun `authenticate when request was already retried then returns null`() {
        val target = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        val priorResponse = response(
            code = 401,
            authorizationHeader = "Bearer prior-token",
        )

        val retriedRequest = target.authenticate(
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
        confirmVerified(tokenProvider, refreshHandler)
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
