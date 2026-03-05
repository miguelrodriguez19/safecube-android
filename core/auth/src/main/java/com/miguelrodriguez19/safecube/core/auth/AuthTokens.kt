package com.miguelrodriguez19.safecube.core.auth

import java.time.OffsetDateTime

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val issuedAt: OffsetDateTime?,
)
