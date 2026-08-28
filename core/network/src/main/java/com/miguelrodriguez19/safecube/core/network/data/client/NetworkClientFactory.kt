package com.miguelrodriguez19.safecube.core.network.data.client

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.generated.infrastructure.Serializer
import com.miguelrodriguez19.safecube.core.network.serialization.Base64ByteArraySerializer
import com.miguelrodriguez19.safecube.core.network.serialization.InstantIso8601Serializer
import java.time.Instant
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.plus
import okhttp3.Authenticator
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

/**
 * Factory for configured Json, OkHttp and Retrofit instances.
 */
object NetworkClientFactory {
    /**
     * Creates the Json configuration used by generated API models.
     */
    fun createJson(): Json = Json(Serializer.kotlinxSerializationJson) {

        ignoreUnknownKeys = true
        explicitNulls = false

        val appSerializers = SerializersModule {
            contextual(ByteArray::class, Base64ByteArraySerializer)
            contextual(Instant::class, InstantIso8601Serializer)
        }

        serializersModule = Serializer.kotlinxSerializationJson.serializersModule + appSerializers
    }

    /**
     * Builds an OkHttp client configured with timeouts and optional auth components.
     */
    fun createOkHttpClient(
        config: NetworkConfig,
        authInterceptor: Interceptor? = null,
        authenticator: Authenticator? = null,
        httpLoggingInterceptor: Interceptor? = null,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS)

        authInterceptor?.let(builder::addInterceptor)
        httpLoggingInterceptor?.let(builder::addInterceptor)
        authenticator?.let(builder::authenticator)

        return builder.build()
    }

    /**
     * Creates temporary full HTTP diagnostics for local debug builds.
     *
     * Phase 9 removes this logger and replaces it with redacted structured observability.
     */
    fun createDebugHttpLoggingInterceptor(
        enabled: Boolean,
        logger: (String) -> Unit,
    ): Interceptor? = if (enabled) {
        HttpLoggingInterceptor(logger).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    } else {
        null
    }

    /**
     * Builds Retrofit with the provided [config], JSON converter, and HTTP client.
     */
    fun createRetrofit(
        config: NetworkConfig,
        authInterceptor: Interceptor? = null,
        json: Json = createJson(),
        okHttpClient: OkHttpClient = createOkHttpClient(
            config = config,
            authInterceptor = authInterceptor,
        ),
    ): Retrofit = Retrofit.Builder()
        .baseUrl(config.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    /**
     * Creates a typed Retrofit service using [createRetrofit].
     */
    inline fun <reified T : Any> createService(
        config: NetworkConfig,
        authInterceptor: Interceptor? = null,
        json: Json = createJson(),
    ): T = createRetrofit(
        config = config,
        authInterceptor = authInterceptor,
        json = json,
    ).create(T::class.java)
}
