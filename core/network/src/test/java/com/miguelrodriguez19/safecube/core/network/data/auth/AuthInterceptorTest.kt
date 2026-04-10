package com.miguelrodriguez19.safecube.core.network.data.auth

import com.miguelrodriguez19.safecube.core.network.domain.port.TokenProvider
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.confirmVerified
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthInterceptorTest {

    private val tokenProvider = mockk<TokenProvider>()
    private val chain = mockk<Interceptor.Chain>()

    private val target = AuthInterceptor(tokenProvider)

    @Test
    fun `intercept when token exists then adds bearer header`() {
        every { tokenProvider.getAccessToken() } returns "access-token"
        val originalRequest = Request.Builder()
            .url("https://example.com/protected")
            .build()
        every { chain.request() } returns originalRequest

        val requestSlot = slot<Request>()
        every { chain.proceed(capture(requestSlot)) } answers {
            Response.Builder()
                .request(requestSlot.captured)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("{}".toResponseBody())
                .build()
        }

        target.intercept(chain)

        assertEquals("Bearer access-token", requestSlot.captured.header("Authorization"))
        verify(exactly = 1) { tokenProvider.getAccessToken() }
        verify(exactly = 1) { chain.request() }
        verify(exactly = 1) { chain.proceed(any()) }
        confirmVerified(tokenProvider, chain)
    }

    @Test
    fun `intercept when token is blank then does not add bearer header`() {
        every { tokenProvider.getAccessToken() } returns "   "
        val originalRequest = Request.Builder()
            .url("https://example.com/protected")
            .build()
        every { chain.request() } returns originalRequest

        val requestSlot = slot<Request>()
        every { chain.proceed(capture(requestSlot)) } answers {
            Response.Builder()
                .request(requestSlot.captured)
                .protocol(Protocol.HTTP_1_1)
                .code(200)
                .message("OK")
                .body("{}".toResponseBody())
                .build()
        }

        target.intercept(chain)

        assertNull(requestSlot.captured.header("Authorization"))
        verify(exactly = 1) { tokenProvider.getAccessToken() }
        verify(exactly = 1) { chain.request() }
        verify(exactly = 1) { chain.proceed(any()) }
        confirmVerified(tokenProvider, chain)
    }
}
