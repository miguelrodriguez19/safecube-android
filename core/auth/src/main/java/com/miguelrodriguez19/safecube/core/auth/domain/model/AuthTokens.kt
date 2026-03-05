package com.miguelrodriguez19.safecube.core.auth.domain.model

import java.time.OffsetDateTime

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val issuedAt: OffsetDateTime?,
)
