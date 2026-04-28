package com.miguelrodriguez19.safecube.core.auth.domain.repository

import java.time.Instant

interface TokenStorage {
    fun saveTokens(
        accessToken: String,
        refreshToken: String,
        issuedAt: Instant?,
    )

    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun getIssuedAt(): Instant?
    fun clear()
}
