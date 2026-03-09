package com.miguelrodriguez19.safecube.core.auth.data.repository

import com.miguelrodriguez19.safecube.core.auth.data.mapper.AuthErrorMapper
import com.miguelrodriguez19.safecube.core.auth.data.remote.RemoteAuthDataSource
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.RegisteredAccount
import com.miguelrodriguez19.safecube.core.network.NetworkClientFactory
import com.miguelrodriguez19.safecube.core.network.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.generated.api.AuthControllerApi
import java.time.OffsetDateTime
import java.util.UUID
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

    @Test
    fun `register maps successful response into RegisteredAccount`() = runBlocking {
        val createdAt = "2026-03-09T09:30:00Z"
        val accountId = "7ecf3225-6f88-4b95-a4be-2cc6fbf9a1f8"
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "accountId":"$accountId",
                      "createdAt":"$createdAt"
                    }
                    """.trimIndent(),
                ),
        )
        val repository = createRepository(server)

        val result = repository.register(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(
            AuthResult.Success(
                RegisteredAccount(
                    accountId = UUID.fromString(accountId),
                    createdAt = OffsetDateTime.parse(createdAt),
                ),
            ),
            result,
        )
    }

    @Test
    fun `register maps 409 into account already exists`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(409)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"error":"Account already exists"}"""),
        )
        val repository = createRepository(server)

        val result = repository.register(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(AuthResult.Error(AuthError.AccountAlreadyExists), result)
    }

    @Test
    fun `login maps successful response without tokens into unknown error`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("{}"),
        )
        val repository = createRepository(server)

        val result = repository.login(
            email = "user@example.com",
            password = "password",
        )

        assertTrue(result is AuthResult.Error)
        val error = (result as AuthResult.Error).error
        assertTrue(error is AuthError.Unknown)
        val unknown = error as AuthError.Unknown
        assertEquals(null, unknown.code)
        assertTrue(unknown.message?.contains("Fields [accessToken, refreshToken, issuedAt]") == true)
    }

    @Test
    fun `login maps blank access token into unknown error`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "accessToken":"",
                      "refreshToken":"refresh-token",
                      "issuedAt":"2026-03-09T09:30:00Z"
                    }
                    """.trimIndent(),
                ),
        )
        val repository = createRepository(server)

        val result = repository.login(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(
            AuthResult.Error(
                AuthError.Unknown(
                    code = 200,
                    message = "Missing token payload in successful auth response.",
                ),
            ),
            result,
        )
    }

    @Test
    fun `login maps blank refresh token into unknown error`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "accessToken":"access-token",
                      "refreshToken":"",
                      "issuedAt":"2026-03-09T09:30:00Z"
                    }
                    """.trimIndent(),
                ),
        )
        val repository = createRepository(server)

        val result = repository.login(
            email = "user@example.com",
            password = "password",
        )

        assertEquals(
            AuthResult.Error(
                AuthError.Unknown(
                    code = 200,
                    message = "Missing token payload in successful auth response.",
                ),
            ),
            result,
        )
    }

    @Test
    fun `refresh maps 409 into conflict`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(409)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"error":"Refresh token conflict"}"""),
        )
        val repository = createRepository(server)

        val result = repository.refresh(
            refreshToken = "refresh-token",
        )

        assertEquals(
            AuthResult.Error(
                AuthError.Conflict(message = "Refresh token conflict"),
            ),
            result,
        )
    }

    @Test
    fun `logout maps 403 into forbidden`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(403)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"error":"Forbidden"}"""),
        )
        val repository = createRepository(server)

        val result = repository.logout()

        assertEquals(AuthResult.Error(AuthError.Forbidden), result)
    }

    @Test
    fun `login maps transport failure into unknown error`() = runBlocking {
        val repository = createRepository(server)
        server.shutdown()

        val result = repository.login(
            email = "user@example.com",
            password = "password",
        )

        assertTrue(result is AuthResult.Error)
        val error = (result as AuthResult.Error).error
        assertTrue(error is AuthError.Unknown)
        assertTrue((error as AuthError.Unknown).message?.isNotBlank() == true)
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
