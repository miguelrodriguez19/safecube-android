package com.miguelrodriguez19.safecube.core.auth

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
