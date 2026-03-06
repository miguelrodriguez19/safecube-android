package com.miguelrodriguez19.safecube.core.network

import com.miguelrodriguez19.safecube.core.network.generated.model.AuthTokensResponse
import java.time.OffsetDateTime
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.Authenticator
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.Serializable
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Call
import retrofit2.http.GET

class NetworkClientFactoryTest {
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
    fun `createRetrofit initializes network client`() {
        val config = NetworkConfig(
            baseUrl = server.url("/").toString(),
            isDebug = false,
        )

        val retrofit = NetworkClientFactory.createRetrofit(config)

        assertNotNull(retrofit.callFactory())
        assertEquals(server.url("/").toString(), retrofit.baseUrl().toString())
    }

    @Test
    fun `createService executes request and parses json`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"status":"ok"}"""),
        )
        val config = NetworkConfig(
            baseUrl = server.url("/").toString(),
            isDebug = false,
        )

        val api = NetworkClientFactory.createService<PingApi>(config)
        val response = api.ping().execute()
        val request = server.takeRequest()

        assertTrue(response.isSuccessful)
        assertEquals("ok", response.body()?.status)
        assertEquals("/ping", request.path)
    }

    @Test
    fun `createOkHttpClient adds logging interceptor only in debug`() {
        val baseConfig = NetworkConfig(baseUrl = server.url("/").toString())

        val debugClient = NetworkClientFactory.createOkHttpClient(baseConfig.copy(isDebug = true))
        val releaseClient = NetworkClientFactory.createOkHttpClient(baseConfig.copy(isDebug = false))

        assertTrue(debugClient.interceptors.any { it is HttpLoggingInterceptor })
        assertFalse(releaseClient.interceptors.any { it is HttpLoggingInterceptor })
    }

    @Test
    fun `createOkHttpClient sets authenticator when provided`() {
        val config = NetworkConfig(baseUrl = server.url("/").toString())
        val authenticator = Authenticator { _, _ -> null }

        val client = NetworkClientFactory.createOkHttpClient(
            config = config,
            authenticator = authenticator,
        )

        assertTrue(client.authenticator === authenticator)
    }

    @Test
    fun `auth interceptor adds bearer token when available`() {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"status":"ok"}"""),
        )
        val config = NetworkConfig(
            baseUrl = server.url("/").toString(),
            isDebug = false,
        )
        val authInterceptor = AuthInterceptor { "token-123" }

        val api = NetworkClientFactory.createService<PingApi>(
            config = config,
            authInterceptor = authInterceptor,
        )
        api.ping().execute()
        val request = server.takeRequest()

        assertEquals("Bearer token-123", request.getHeader("Authorization"))
    }

    @Test
    fun `createJson parses generated OffsetDateTime fields`() {
        val json = NetworkClientFactory.createJson()
        val issuedAtRaw = "2026-03-06T12:11:35.524804768Z"
        val payload = """
            {
              "accessToken":"access-token",
              "refreshToken":"refresh-token",
              "issuedAt":"$issuedAtRaw"
            }
        """.trimIndent()

        val parsed = json.decodeFromString<AuthTokensResponse>(payload)

        assertEquals("access-token", parsed.accessToken)
        assertEquals("refresh-token", parsed.refreshToken)
        assertEquals(OffsetDateTime.parse(issuedAtRaw), parsed.issuedAt)
    }
}

private interface PingApi {
    @GET("ping")
    fun ping(): Call<PingResponse>
}

@Serializable
private data class PingResponse(
    val status: String,
)
