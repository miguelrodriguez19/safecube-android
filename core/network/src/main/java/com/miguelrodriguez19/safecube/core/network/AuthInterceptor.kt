package com.miguelrodriguez19.safecube.core.network

import javax.inject.Inject
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.Response

@Singleton
class AuthInterceptor @Inject constructor(
    private val tokenProvider: TokenProvider,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        tokenProvider.getAccessToken()
            ?.takeIf { it.isNotBlank() }
            ?.let { requestBuilder.header("Authorization", "Bearer $it") }
        return chain.proceed(requestBuilder.build())
    }
}
