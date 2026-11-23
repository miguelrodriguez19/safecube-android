package com.miguelrodriguez19.safecube.core.network.data.auth

import com.miguelrodriguez19.safecube.core.network.data.client.NetworkClientFactory
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.domain.port.TokenProvider
import com.miguelrodriguez19.safecube.core.network.domain.port.TokenRefreshHandler
import com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthenticateAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RefreshTokenRequest
import java.util.Optional
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class TokenRefreshAuthenticatorFlowIntegrationTest {
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `401 when refresh succeeds then retries original request`() {
        val session = MutableSession(
            accessToken = "expired-access",
            refreshToken = "refresh-1",
        )
        server.dispatcher = refreshSuccessDispatcher(
            expiredAccessToken = "expired-access",
            refreshedAccessToken = "refreshed-access",
            refreshedRefreshToken = "refresh-2",
        )
        val client = createAuthenticatedClient(session)

        val response = client.newCall(
            Request.Builder()
                .url(server.url("/protected"))
                .get()
                .build(),
        ).execute()

        response.use {
            assertEquals(200, it.code)
            assertEquals("ok", it.body.string())
        }
        assertEquals("refreshed-access", session.accessToken)
        assertEquals("refresh-2", session.refreshToken)
        assertEquals(0, session.forceLogoutCalls.get())

        val firstProtected = server.takeRequest()
        assertEquals("/protected", firstProtected.requestUrl?.encodedPath)
        assertEquals("Bearer expired-access", firstProtected.getHeader("Authorization"))

        val refreshCall = server.takeRequest()
        assertEquals("/auth/refresh", refreshCall.requestUrl?.encodedPath)
        assertTrue(refreshCall.body.readUtf8().contains("\"refreshToken\":\"refresh-1\""))

        val retriedProtected = server.takeRequest()
        assertEquals("/protected", retriedProtected.requestUrl?.encodedPath)
        assertEquals("Bearer refreshed-access", retriedProtected.getHeader("Authorization"))
    }

    @Test
    fun `401 when refresh returns auth failure then forces logout and returns unauthorized response`() {
        val session = MutableSession(
            accessToken = "expired-access",
            refreshToken = "refresh-1",
        )
        server.dispatcher = refreshUnauthorizedDispatcher(
            expiredAccessToken = "expired-access",
        )
        val client = createAuthenticatedClient(session)

        val response = client.newCall(
            Request.Builder()
                .url(server.url("/protected"))
                .get()
                .build(),
        ).execute()

        response.use {
            assertEquals(401, it.code)
        }
        assertNull(session.accessToken)
        assertNull(session.refreshToken)
        assertEquals(1, session.forceLogoutCalls.get())

        assertEquals(2, server.requestCount)
        val firstProtected = server.takeRequest()
        assertEquals("/protected", firstProtected.requestUrl?.encodedPath)
        assertEquals("Bearer expired-access", firstProtected.getHeader("Authorization"))

        val refreshCall = server.takeRequest()
        assertEquals("/auth/refresh", refreshCall.requestUrl?.encodedPath)
        assertTrue(refreshCall.body.readUtf8().contains("\"refreshToken\":\"refresh-1\""))
    }

    @Test
    fun `401 when refresh returns server failure keeps session and returns unauthorized response`() {
        val session = MutableSession(
            accessToken = "expired-access",
            refreshToken = "refresh-1",
        )
        server.dispatcher = refreshUnavailableDispatcher()
        val client = createAuthenticatedClient(session)

        val response = client.newCall(
            Request.Builder()
                .url(server.url("/protected"))
                .get()
                .build(),
        ).execute()

        response.use {
            assertEquals(401, it.code)
        }
        assertEquals("expired-access", session.accessToken)
        assertEquals("refresh-1", session.refreshToken)
        assertEquals(0, session.forceLogoutCalls.get())
        assertEquals(2, server.requestCount)
    }

    @Test
    fun `401 on generated login and register routes with base path prefix does not refresh or terminate session`() = runBlocking {
        val baseUrlPath = "/api/v1/"
        val session = MutableSession(
            accessToken = "stale-access",
            refreshToken = null,
        )
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse =
                when (request.requestUrl?.encodedPath) {
                    "/api/v1/auth/login",
                    "/api/v1/auth/register" -> MockResponse().setResponseCode(401)

                    else -> MockResponse().setResponseCode(404)
                }
        }
        val config = NetworkConfig(baseUrl = server.url(baseUrlPath).toString())
        val client = createAuthenticatedClient(session, baseUrlPath)
        val authApi: AuthControllerApi = NetworkClientFactory.createRetrofit(
            config = config,
            okHttpClient = client,
        ).create(AuthControllerApi::class.java)

        val loginResponse = authApi.login(
            AuthenticateAccountRequest(email = "missing@example.com", password = "wrong-password"),
        )
        val registerResponse = authApi.register(
            RegisterAccountRequest(email = "existing@example.com", password = "password"),
        )

        assertEquals(401, loginResponse.code())
        assertEquals(401, registerResponse.code())
        loginResponse.errorBody()?.close()
        registerResponse.errorBody()?.close()
        assertEquals(2, session.accessTokenReads.get())
        assertEquals(0, session.refreshHandlerCalls.get())
        assertEquals(0, session.forceLogoutCalls.get())
        assertEquals(2, server.requestCount)

        val loginRequest = server.takeRequest()
        assertEquals("/api/v1/auth/login", loginRequest.requestUrl?.encodedPath)
        assertEquals("Bearer stale-access", loginRequest.getHeader("Authorization"))

        val registerRequest = server.takeRequest()
        assertEquals("/api/v1/auth/register", registerRequest.requestUrl?.encodedPath)
        assertEquals("Bearer stale-access", registerRequest.getHeader("Authorization"))
    }

    @Test
    fun `401 on protected request without refresh token still terminates session`() {
        val session = MutableSession(
            accessToken = "expired-access",
            refreshToken = null,
        )
        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse =
                if (request.requestUrl?.encodedPath == "/protected") {
                    MockResponse().setResponseCode(401)
                } else {
                    MockResponse().setResponseCode(404)
                }
        }
        val client = createAuthenticatedClient(session)

        val response = client.newCall(
            Request.Builder()
                .url(server.url("/protected"))
                .get()
                .build(),
        ).execute()

        response.use {
            assertEquals(401, it.code)
        }
        assertEquals(1, session.refreshHandlerCalls.get())
        assertEquals(1, session.forceLogoutCalls.get())
        assertNull(session.accessToken)
        assertNull(session.refreshToken)
        assertEquals(1, server.requestCount)
    }

    private fun createAuthenticatedClient(
        session: MutableSession,
        baseUrlPath: String = "/",
    ): OkHttpClient {
        val config = NetworkConfig(
            baseUrl = server.url(baseUrlPath).toString(),
        )
        val refreshApi: AuthControllerApi = NetworkClientFactory.createService(config = config)
        val tokenProvider = TokenProvider {
            session.accessTokenReads.incrementAndGet()
            session.accessToken
        }
        val refreshHandler = TokenRefreshHandler {
            session.refreshHandlerCalls.incrementAndGet()
            val currentRefreshToken = session.refreshToken
                ?.takeIf { token -> token.isNotBlank() }
                ?: run {
                    session.forceLogout()
                    return@TokenRefreshHandler null
                }

            val response = refreshApi.refresh(
                refreshTokenRequest = RefreshTokenRequest(refreshToken = currentRefreshToken),
            )
            if (!response.isSuccessful) {
                if (response.code() in AUTH_FAILURE_CODES) {
                    session.forceLogout()
                }
                return@TokenRefreshHandler null
            }

            val payload = response.body() ?: return@TokenRefreshHandler null
            val newAccessToken = payload.accessToken
                .takeIf { token -> token.isNotBlank() }
                ?: return@TokenRefreshHandler null
            val newRefreshToken = payload.refreshToken
                .takeIf { token -> token.isNotBlank() }
                ?: return@TokenRefreshHandler null

            session.accessToken = newAccessToken
            session.refreshToken = newRefreshToken
            newAccessToken
        }

        val authenticator = TokenRefreshAuthenticator(
            tokenProvider = tokenProvider,
            tokenRefreshHandler = Optional.of(refreshHandler),
        )

        return NetworkClientFactory.createOkHttpClient(
            config = config,
            authInterceptor = AuthInterceptor(tokenProvider),
            authenticator = authenticator,
        )
    }

    private fun refreshSuccessDispatcher(
        expiredAccessToken: String,
        refreshedAccessToken: String,
        refreshedRefreshToken: String,
    ): Dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse =
            when (request.requestUrl?.encodedPath) {
                "/protected" -> {
                    val authHeader = request.getHeader("Authorization")
                    if (authHeader == "Bearer $expiredAccessToken") {
                        MockResponse().setResponseCode(401)
                    } else if (authHeader == "Bearer $refreshedAccessToken") {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody("ok")
                    } else {
                        MockResponse().setResponseCode(401)
                    }
                }

                "/auth/refresh" -> MockResponse()
                    .setResponseCode(200)
                    .addHeader("Content-Type", "application/json")
                    .setBody(
                        """
                    {
                      "accessToken":"$refreshedAccessToken",
                      "refreshToken":"$refreshedRefreshToken",
                      "issuedAt":"2026-03-06T12:11:35.524804768Z"
                    }
                    """.trimIndent(),
                    )

                else -> MockResponse().setResponseCode(404)
            }
    }

    private fun refreshUnauthorizedDispatcher(
        expiredAccessToken: String,
    ): Dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse =
            when (request.requestUrl?.encodedPath) {
                "/protected" -> {
                    if (request.getHeader("Authorization") == "Bearer $expiredAccessToken") {
                        MockResponse().setResponseCode(401)
                    } else {
                        MockResponse().setResponseCode(404)
                    }
                }

                "/auth/refresh" -> MockResponse()
                    .setResponseCode(401)
                    .addHeader("Content-Type", "application/json")
                    .setBody("""{"error":"Invalid credentials"}""")

                else -> MockResponse().setResponseCode(404)
            }
    }

    private fun refreshUnavailableDispatcher(): Dispatcher = object : Dispatcher() {
        override fun dispatch(request: RecordedRequest): MockResponse =
            when (request.requestUrl?.encodedPath) {
                "/protected" -> MockResponse().setResponseCode(401)
                "/auth/refresh" -> MockResponse().setResponseCode(503)
                else -> MockResponse().setResponseCode(404)
            }
    }

    private class MutableSession(
        accessToken: String?,
        refreshToken: String?,
    ) {
        @Volatile
        var accessToken: String? = accessToken

        @Volatile
        var refreshToken: String? = refreshToken

        val accessTokenReads = AtomicInteger(0)
        val refreshHandlerCalls = AtomicInteger(0)
        val forceLogoutCalls = AtomicInteger(0)

        fun forceLogout() {
            accessToken = null
            refreshToken = null
            forceLogoutCalls.incrementAndGet()
        }
    }

    private companion object {
        val AUTH_FAILURE_CODES = setOf(400, 401, 403, 409)
    }
}
