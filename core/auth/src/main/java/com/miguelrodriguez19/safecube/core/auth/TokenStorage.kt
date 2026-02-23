package com.miguelrodriguez19.safecube.core.auth

interface TokenStorage {
    fun saveAccessToken(token: String)
    fun saveRefreshToken(token: String)
    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun clear()
}
