package com.miguelrodriguez19.safecube.core.network.domain.port

/**
 * Boundary for refreshing expired access tokens.
 */
fun interface TokenRefreshHandler {
    /**
     * Attempts to refresh the access token after a failed authenticated request.
     *
     * @param failedAccessToken Access token that produced the 401 response.
     * @return New access token when refresh succeeds; null otherwise.
     */
    suspend fun refreshAccessToken(
        failedAccessToken: String?,
    ): String?
}
