package com.miguelrodriguez19.safecube.core.network

fun interface TokenRefreshHandler {
    suspend fun refreshAccessToken(
        failedAccessToken: String?,
    ): String?
}
