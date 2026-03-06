package com.miguelrodriguez19.safecube.core.auth.data.remote

import com.miguelrodriguez19.safecube.core.network.NetworkClientFactory
import com.miguelrodriguez19.safecube.core.network.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthTokensResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthenticateAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RefreshTokenRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.RegisterAccountResult
import java.time.OffsetDateTime
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RemoteAuthDataSourceTest {
    private lateinit var authServer: MockWebServer
    private lateinit var refreshServer: MockWebServer

    @Before
    fun setUp() {
        authServer = MockWebServer().also(MockWebServer::start)
        refreshServer = MockWebServer().also(MockWebServer::start)
    }

    @After
    fun tearDown() {
        authServer.shutdown()
        refreshServer.shutdown()
    }

    @Test
    fun `login returns success result preserving code and body`() = runBlocking {
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
        val dataSource = RemoteAuthDataSource(
            authControllerApi = createAuthApi(authServer),
            refreshAuthControllerApi = createAuthApi(refreshServer),
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
                body = AuthTokensResponse(
                    accessToken = "access-token",
                    refreshToken = "refresh-token",
                    issuedAt = OffsetDateTime.parse(issuedAtRaw),
                ),
            ),
            result,
        )

        val request = authServer.takeRequest()
        assertEquals("/auth/login", request.path)
        assertEquals("POST", request.method)
        assertTrue(request.body.readUtf8().contains("\"email\":\"test@example.com\""))
    }

    @Test
    fun `register returns http error preserving code and error body`() = runBlocking {
        val errorJson = """{"error":"Validation failed","fields":{"email":"invalid"}}"""
        authServer.enqueue(
            MockResponse()
                .setResponseCode(400)
                .addHeader("Content-Type", "application/json")
                .setBody(errorJson),
        )
        val dataSource = RemoteAuthDataSource(
            authControllerApi = createAuthApi(authServer),
            refreshAuthControllerApi = createAuthApi(refreshServer),
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

        val request = authServer.takeRequest()
        assertEquals("/auth/register", request.path)
        assertEquals("POST", request.method)
    }

    @Test
    fun `refresh uses refresh api client and returns success`() = runBlocking {
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
        val dataSource = RemoteAuthDataSource(
            authControllerApi = createAuthApi(authServer),
            refreshAuthControllerApi = createAuthApi(refreshServer),
        )

        val result = dataSource.refresh(
            request = RefreshTokenRequest(refreshToken = "old-refresh"),
        )

        assertEquals(
            NetworkResult.Success(
                httpCode = 200,
                body = AuthTokensResponse(
                    accessToken = "rotated-access",
                    refreshToken = "rotated-refresh",
                    issuedAt = OffsetDateTime.parse(issuedAtRaw),
                ),
            ),
            result,
        )
        assertEquals(0, authServer.requestCount)

        val refreshRequest = refreshServer.takeRequest()
        assertEquals("/auth/refresh", refreshRequest.path)
        assertEquals("POST", refreshRequest.method)
        assertTrue(refreshRequest.body.readUtf8().contains("\"refreshToken\":\"old-refresh\""))
    }

    private fun createAuthApi(server: MockWebServer): AuthControllerApi =
        NetworkClientFactory.createService(
            config = NetworkConfig(
                baseUrl = server.url("/").toString(),
                isDebug = false,
            ),
        )
}
