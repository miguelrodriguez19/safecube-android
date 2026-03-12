package com.miguelrodriguez19.safecube.core.network.data.client

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.generated.model.AuthTokensResponse
import io.mockk.mockk
import java.time.OffsetDateTime
import kotlinx.serialization.Serializable
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertSame
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
    fun `createOkHttpClient adds logging interceptor only in debug`() {
        val config = NetworkConfig(baseUrl = server.url("/").toString())

        val debugClient = NetworkClientFactory.createOkHttpClient(config.copy(isDebug = true))
        val releaseClient = NetworkClientFactory.createOkHttpClient(config.copy(isDebug = false))

        assertTrue(debugClient.interceptors.any { it is HttpLoggingInterceptor })
        assertFalse(releaseClient.interceptors.any { it is HttpLoggingInterceptor })
    }

    @Test
    fun `createOkHttpClient keeps provided auth components`() {
        val config = NetworkConfig(baseUrl = server.url("/").toString(), isDebug = false)
        val authInterceptor = mockk<Interceptor>(relaxed = true)
        val authenticator = mockk<Authenticator>(relaxed = true)

        val client = NetworkClientFactory.createOkHttpClient(
            config = config,
            authInterceptor = authInterceptor,
            authenticator = authenticator,
        )

        assertTrue(client.interceptors.contains(authInterceptor))
        assertSame(authenticator, client.authenticator)
    }

    @Test
    fun `createRetrofit initializes network client with expected base url`() {
        val config = NetworkConfig(
            baseUrl = server.url("/").toString(),
            isDebug = false,
        )

        val retrofit = NetworkClientFactory.createRetrofit(config)

        assertNotNull(retrofit.callFactory())
        assertEquals(server.url("/").toString(), retrofit.baseUrl().toString())
    }

    @Test
    fun `createRetrofit uses provided okHttpClient`() {
        val config = NetworkConfig(baseUrl = server.url("/").toString(), isDebug = false)
        val client = NetworkClientFactory.createOkHttpClient(config)

        val retrofit = NetworkClientFactory.createRetrofit(
            config = config,
            okHttpClient = client,
        )

        assertSame(client, retrofit.callFactory())
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
