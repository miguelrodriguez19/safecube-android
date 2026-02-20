package com.miguelrodriguez19.safecube.core.network

import okhttp3.Interceptor

fun interface AccessTokenProvider {
    fun getAccessToken(): String?
}

object AuthInterceptorFactory {
    fun bearer(tokenProvider: AccessTokenProvider): Interceptor = Interceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        tokenProvider.getAccessToken()
            ?.takeIf { it.isNotBlank() }
            ?.let { requestBuilder.header("Authorization", "Bearer $it") }
        chain.proceed(requestBuilder.build())
    }
}
