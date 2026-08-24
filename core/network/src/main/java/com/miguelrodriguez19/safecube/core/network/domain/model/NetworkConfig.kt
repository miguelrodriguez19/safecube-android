package com.miguelrodriguez19.safecube.core.network.domain.model

/**
 * Immutable configuration for building network clients.
 */
data class NetworkConfig(
    /**
     * Base URL used by Retrofit services. Must end with `/`.
     */
    val baseUrl: String,
    /**
     * Connect timeout in seconds.
     */
    val connectTimeoutSeconds: Long = 15,
    /**
     * Read timeout in seconds.
     */
    val readTimeoutSeconds: Long = 15,
    /**
     * Write timeout in seconds.
     */
    val writeTimeoutSeconds: Long = 15,
) {
    init {
        require(baseUrl.endsWith("/")) {
            "baseUrl must end with '/'. Example: https://api.example.com/"
        }
    }
}
