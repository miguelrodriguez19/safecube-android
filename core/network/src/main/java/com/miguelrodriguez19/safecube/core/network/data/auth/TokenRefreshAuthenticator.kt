package com.miguelrodriguez19.safecube.core.network.data.auth

import com.miguelrodriguez19.safecube.core.network.domain.port.TokenProvider
import com.miguelrodriguez19.safecube.core.network.domain.port.TokenRefreshHandler
import java.util.Optional
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Handles `401 Unauthorized` responses by attempting a token refresh and retrying once.
 */
@Singleton
class TokenRefreshAuthenticator @Inject constructor(
    private val tokenProvider: TokenProvider,
    private val tokenRefreshHandler: Optional<TokenRefreshHandler>,
) : Authenticator {
    private val refreshMutex = Mutex()

    /**
     * Returns a retried request with a refreshed bearer token, or null when retry is not possible.
     */
    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {
        if (response.code != HTTP_UNAUTHORIZED) return null
        if (isRefreshRequest(response.request)) return null
        if (responseCount(response) >= MAX_AUTH_ATTEMPTS) return null

        val failedAccessToken = response.request
            .header(HEADER_AUTHORIZATION)
            .extractBearerToken()

        val refreshedAccessToken = runBlocking {
            refreshMutex.withLock {
                tokenProvider.getAccessToken()
                    ?.takeIf { it.isNotBlank() }
                    ?.takeIf { currentAccessToken ->
                        !failedAccessToken.isNullOrBlank() && currentAccessToken != failedAccessToken
                    }
                    ?: tokenRefreshHandler
                        .orElse(null)
                        ?.refreshAccessToken(failedAccessToken)
            }
        }?.takeIf { it.isNotBlank() }
            ?: return null

        if (refreshedAccessToken == failedAccessToken) return null

        return response.request.newBuilder()
            .header(HEADER_AUTHORIZATION, "Bearer $refreshedAccessToken")
            .build()
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }

    private fun isRefreshRequest(request: Request): Boolean =
        request.url.encodedPath.endsWith(REFRESH_PATH_SUFFIX)

    private fun String?.extractBearerToken(): String? = this
        ?.takeIf { it.startsWith(BEARER_PREFIX) }
        ?.removePrefix(BEARER_PREFIX)
        ?.trim()

    private companion object {
        const val HTTP_UNAUTHORIZED = 401
        const val MAX_AUTH_ATTEMPTS = 2
        const val HEADER_AUTHORIZATION = "Authorization"
        const val BEARER_PREFIX = "Bearer "
        const val REFRESH_PATH_SUFFIX = "/auth/refresh"
    }
}
