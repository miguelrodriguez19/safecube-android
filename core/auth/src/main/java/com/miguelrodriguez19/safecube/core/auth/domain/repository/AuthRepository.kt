package com.miguelrodriguez19.safecube.core.auth.domain.repository

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.RegisteredAccount

interface AuthRepository {
    suspend fun register(
        email: String,
        password: String,
    ): AuthResult<RegisteredAccount>

    suspend fun login(
        email: String,
        password: String,
    ): AuthResult<AuthTokens>

    suspend fun refresh(
        refreshToken: String,
    ): AuthResult<AuthTokens>

    suspend fun logout(): AuthResult<Unit>
}
