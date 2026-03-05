package com.miguelrodriguez19.safecube.core.auth.domain.model

sealed interface AuthResult<out T> {
    data class Success<T>(
        val data: T,
    ) : AuthResult<T>

    data class Error(
        val error: AuthError,
    ) : AuthResult<Nothing>
}
