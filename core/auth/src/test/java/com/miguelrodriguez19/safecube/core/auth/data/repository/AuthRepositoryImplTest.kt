package com.miguelrodriguez19.safecube.core.auth.data.repository

import com.miguelrodriguez19.safecube.core.auth.data.mapper.AuthErrorMapper
import com.miguelrodriguez19.safecube.core.auth.data.remote.RemoteAuthDataSource
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.network.NetworkClientFactory
import com.miguelrodriguez19.safecube.core.network.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi
import java.time.OffsetDateTime
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {
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
    fun `login refresh and logout map successful responses into domain results`() = runBlocking {
        val loginIssuedAtRaw = "2026-03-06T12:11:35.524804768Z"
        val refreshIssuedAtRaw = "2026-03-06T12:12:40.010203040Z"
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "accessToken":"access-token-1",
                      "refreshToken":"refresh-token-1",
                      "issuedAt":"$loginIssuedAtRaw"
                    }
                    """.trimIndent(),
                ),
        )
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "accessToken":"access-token-2",
                      "refreshToken":"refresh-token-2",
                      "issuedAt":"$refreshIssuedAtRaw"
                    }
                    """.trimIndent(),
                ),
        )
        server.enqueue(
            MockResponse()
                .setResponseCode(200),
        )
        val repository = createRepository(server)

        val loginResult = repository.login(
            email = "user@example.com",
            password = "password",
        )
        val refreshResult = repository.refresh(
            refreshToken = "refresh-token-1",
        )
        val logoutResult = repository.logout()

        assertEquals(
            AuthResult.Success(
                AuthTokens(
                    accessToken = "access-token-1",
                    refreshToken = "refresh-token-1",
                    issuedAt = OffsetDateTime.parse(loginIssuedAtRaw),
                ),
            ),
            loginResult,
        )
        assertEquals(
            AuthResult.Success(
                AuthTokens(
                    accessToken = "access-token-2",
                    refreshToken = "refresh-token-2",
                    issuedAt = OffsetDateTime.parse(refreshIssuedAtRaw),
                ),
            ),
            refreshResult,
        )
        assertEquals(AuthResult.Success(Unit), logoutResult)

        val loginRequest = server.takeRequest()
        assertEquals("/auth/login", loginRequest.path)
        assertEquals("POST", loginRequest.method)
        assertTrue(loginRequest.body.readUtf8().contains("\"email\":\"user@example.com\""))

        val refreshRequest = server.takeRequest()
        assertEquals("/auth/refresh", refreshRequest.path)
        assertEquals("POST", refreshRequest.method)
        assertTrue(refreshRequest.body.readUtf8().contains("\"refreshToken\":\"refresh-token-1\""))

        val logoutRequest = server.takeRequest()
        assertEquals("/auth/logout", logoutRequest.path)
        assertEquals("POST", logoutRequest.method)
    }

    private fun createRepository(server: MockWebServer): AuthRepositoryImpl {
        val config = NetworkConfig(
            baseUrl = server.url("/").toString(),
            isDebug = false,
        )
        val authControllerApi: AuthControllerApi = NetworkClientFactory.createService(config = config)

        return AuthRepositoryImpl(
            remoteAuthDataSource = RemoteAuthDataSource(
                authControllerApi = authControllerApi,
                refreshAuthControllerApi = authControllerApi,
            ),
            authErrorMapper = AuthErrorMapper(),
        )
    }
}
