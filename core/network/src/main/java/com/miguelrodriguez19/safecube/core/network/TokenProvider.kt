package com.miguelrodriguez19.safecube.core.network

fun interface TokenProvider {
    fun getAccessToken(): String?
}
