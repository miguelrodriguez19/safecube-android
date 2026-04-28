package com.miguelrodriguez19.safecube.core.auth.domain.model

import java.time.Instant

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val issuedAt: Instant?,
)
