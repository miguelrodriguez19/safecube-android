package com.miguelrodriguez19.safecube.core.network

import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

object NetworkClientFactory {
    fun createJson(): Json = Json {
        ignoreUnknownKeys = true
        explicitNulls = false
    }

    fun createOkHttpClient(
        config: NetworkConfig,
        authInterceptor: Interceptor? = null,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS)

        authInterceptor?.let(builder::addInterceptor)

        if (config.isDebug) {
            builder.addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                },
            )
        }

        return builder.build()
    }

    fun createRetrofit(
        config: NetworkConfig,
        authInterceptor: Interceptor? = null,
        json: Json = createJson(),
        okHttpClient: OkHttpClient = createOkHttpClient(config, authInterceptor),
    ): Retrofit = Retrofit.Builder()
        .baseUrl(config.baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

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
