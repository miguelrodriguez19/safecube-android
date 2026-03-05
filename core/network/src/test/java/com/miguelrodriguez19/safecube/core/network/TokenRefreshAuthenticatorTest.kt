package com.miguelrodriguez19.safecube.core.network

import java.util.Optional
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class TokenRefreshAuthenticatorTest {
    @Test
    fun `authenticate retries request with refreshed bearer token`() {
        val authenticator = TokenRefreshAuthenticator(
            tokenProvider = TokenProvider { "expired-token" },
            tokenRefreshHandler = Optional.of(
                TokenRefreshHandler { "new-token" },
            ),
        )
        val response = unauthorizedResponse(
            authorizationHeader = "Bearer expired-token",
        )

        val retriedRequest = authenticator.authenticate(
            route = null,
            response = response,
        )

        assertNotNull(retriedRequest)
        assertEquals("Bearer new-token", retriedRequest?.header("Authorization"))
    }

    @Test
    fun `authenticate returns null when no handler binding exists`() {
        val authenticator = TokenRefreshAuthenticator(
            tokenProvider = TokenProvider { "expired-token" },
            tokenRefreshHandler = Optional.empty(),
        )

        val retriedRequest = authenticator.authenticate(
            route = null,
            response = unauthorizedResponse(
                authorizationHeader = "Bearer expired-token",
            ),
        )

        assertNull(retriedRequest)
    }

    @Test
    fun `authenticate returns null when request already retried`() {
        val authenticator = TokenRefreshAuthenticator(
            tokenProvider = TokenProvider { "expired-token" },
            tokenRefreshHandler = Optional.of(
                TokenRefreshHandler { "new-token" },
            ),
        )
        val priorResponse = unauthorizedResponse(
            authorizationHeader = "Bearer old-token",
        )
        val response = unauthorizedResponse(
            authorizationHeader = "Bearer expired-token",
            priorResponse = priorResponse,
        )

        val retriedRequest = authenticator.authenticate(
            route = null,
            response = response,
        )

        assertNull(retriedRequest)
    }

    private fun unauthorizedResponse(
        authorizationHeader: String,
        priorResponse: Response? = null,
    ): Response {
        val request = Request.Builder()
            .url("https://example.com/api/items")
            .header("Authorization", authorizationHeader)
            .build()
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(401)
            .message("Unauthorized")
            .body("{}".toResponseBody())
            .apply {
                priorResponse?.let(::priorResponse)
            }
            .build()
    }
}
