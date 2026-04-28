package com.miguelrodriguez19.safecube.core.auth.data.remote

import com.miguelrodriguez19.safecube.core.network.data.client.NetworkClientFactory
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthTokensResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthenticateAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RefreshTokenRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountResult
import java.time.Instant
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemoteAuthDataSourceIntegrationTest {

    private lateinit var authServer: MockWebServer
    private lateinit var refreshServer: MockWebServer
    private lateinit var target: RemoteAuthDataSource

    @Before
    fun setUp() {
        authServer = MockWebServer().also(MockWebServer::start)
        refreshServer = MockWebServer().also(MockWebServer::start)
        target = RemoteAuthDataSource(
            authControllerApi = createAuthApi(authServer),
            refreshAuthControllerApi = createAuthApi(refreshServer),
        )
    }

    @After
    fun tearDown() {
        authServer.shutdown()
        refreshServer.shutdown()
    }

    @Test
    fun `login when auth api returns tokens then returns success preserving code body and request payload`() = runBlocking {
        val issuedAtRaw = "2026-03-06T12:11:35.524804768Z"
        authServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "accessToken":"access-token",
                      "refreshToken":"refresh-token",
                      "issuedAt":"$issuedAtRaw"
                    }
                    """.trimIndent(),
                ),
        )

        val result = target.login(
            request = AuthenticateAccountRequest(
                email = "test@example.com",
                password = "password",
            ),
        )

        assertEquals(
            NetworkResult.Success(
                httpCode = 200,
                body = AuthTokensResponse(
                    accessToken = "access-token",
                    refreshToken = "refresh-token",
                    issuedAt = Instant.parse(issuedAtRaw),
                ),
            ),
            result,
        )

        val request = authServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertEquals("/auth/login", request.path)
        assertEquals("POST", request.method)
        assertTrue(requestBody.contains("\"email\":\"test@example.com\""))
        assertTrue(requestBody.isNotEmpty())
    }

    @Test
    fun `register when auth api returns http error then preserves code null body and raw error body`() = runBlocking {
        val errorJson = """{"error":"Validation failed","fields":{"email":"invalid"}}"""
        authServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody(errorJson),
        )

        val result = target.register(
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

        val request = authServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertEquals("/auth/register", request.path)
        assertEquals("POST", request.method)
        assertTrue(requestBody.contains("\"email\":\"invalid-email\""))
    }

    @Test
    fun `refresh when refresh api returns tokens then uses refresh client and returns success`() = runBlocking {
        val issuedAtRaw = "2026-03-06T12:11:35.524804768Z"
        refreshServer.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "accessToken":"rotated-access",
                      "refreshToken":"rotated-refresh",
                      "issuedAt":"$issuedAtRaw"
                    }
                    """.trimIndent(),
                ),
        )

        val result = target.refresh(
            request = RefreshTokenRequest(refreshToken = "old-refresh"),
        )

        assertEquals(
            NetworkResult.Success(
                httpCode = 200,
                body = AuthTokensResponse(
                    accessToken = "rotated-access",
                    refreshToken = "rotated-refresh",
                    issuedAt = Instant.parse(issuedAtRaw),
                ),
            ),
            result,
        )
        assertEquals(0, authServer.requestCount)

        val request = refreshServer.takeRequest()
        val requestBody = request.body.readUtf8()
        assertEquals("/auth/refresh", request.path)
        assertEquals("POST", request.method)
        assertTrue(requestBody.contains("\"refreshToken\":\"old-refresh\""))
    }

    @Test
    fun `logout when auth api returns success then returns success preserving status code`() = runBlocking {
        authServer.enqueue(
            MockResponse()
                .setResponseCode(200),
        )

        val result = target.logout()

        assertEquals(
            NetworkResult.Success<Unit>(
                httpCode = 200,
                body = Unit,
            ),
            result,
        )

        val request = authServer.takeRequest()
        assertEquals("/auth/logout", request.path)
        assertEquals("POST", request.method)
    }

    private fun createAuthApi(server: MockWebServer): AuthControllerApi =
        NetworkClientFactory.createService(
            config = NetworkConfig(
                baseUrl = server.url("/").toString(),
                isDebug = false,
            ),
        )
}
