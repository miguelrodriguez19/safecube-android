package com.miguelrodriguez19.safecube.core.network

data class NetworkConfig(
    val baseUrl: String,
    val isDebug: Boolean = BuildConfig.DEBUG,
    val connectTimeoutSeconds: Long = 15,
    val readTimeoutSeconds: Long = 15,
    val writeTimeoutSeconds: Long = 15,
) {
    init {
        require(baseUrl.endsWith("/")) {
            "baseUrl must end with '/'. Example: https://api.example.com/"
        }
    }
}
