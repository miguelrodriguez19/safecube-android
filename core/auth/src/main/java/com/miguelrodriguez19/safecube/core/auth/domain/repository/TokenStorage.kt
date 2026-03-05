package com.miguelrodriguez19.safecube.core.auth.domain.repository

import java.time.OffsetDateTime

interface TokenStorage {
    fun saveTokens(
        accessToken: String,
        refreshToken: String,
        issuedAt: OffsetDateTime?,
    )

    fun getAccessToken(): String?
    fun getRefreshToken(): String?
    fun getIssuedAt(): OffsetDateTime?
    fun clear()
}
