package com.miguelrodriguez19.safecube.core.auth.domain.model

sealed interface AuthError {
    data class ValidationFailed(
        val fields: Map<String, String>? = null,
        val message: String? = null,
    ) : AuthError

    data object InvalidCredentials : AuthError

    data object Forbidden : AuthError

    data object AccountNotActive : AuthError

    data object AccountAlreadyExists : AuthError

    data class Conflict(
        val message: String? = null,
    ) : AuthError

    data class Unknown(
        val code: Int? = null,
        val message: String? = null,
    ) : AuthError
}
