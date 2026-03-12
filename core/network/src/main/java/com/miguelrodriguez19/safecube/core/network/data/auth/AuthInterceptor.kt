package com.miguelrodriguez19.safecube.core.network.data.auth

import com.miguelrodriguez19.safecube.core.network.domain.port.TokenProvider
import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Interceptor that appends the bearer token to authenticated requests when available.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider,
) : Interceptor {
    /**
     * Adds `Authorization: Bearer <token>` to the outgoing request when a non-empty token exists.
     */
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        tokenProvider.getAccessToken()
            ?.takeIf { it.isNotBlank() }
            ?.let { requestBuilder.header("Authorization", "Bearer $it") }
        return chain.proceed(requestBuilder.build())
    }
}
