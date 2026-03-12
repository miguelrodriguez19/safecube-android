package com.miguelrodriguez19.safecube.core.network.domain.port

/**
 * Read-only access to the current access token.
 */
fun interface TokenProvider {
    /**
     * Returns the current access token, or null when no authenticated session exists.
     */
    fun getAccessToken(): String?
}
