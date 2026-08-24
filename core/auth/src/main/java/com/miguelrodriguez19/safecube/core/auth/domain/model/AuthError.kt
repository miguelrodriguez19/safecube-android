package com.miguelrodriguez19.safecube.core.auth.domain.model

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier

sealed interface AuthError {
    data class ValidationFailed(
        val fields: Set<String> = emptySet(),
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(400),
    ) : AuthError

    data object InvalidCredentials : AuthError {
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(401)
    }

    data object Forbidden : AuthError {
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(403)
    }

    data object AccountNotActive : AuthError {
        override val failure: NetworkFailure = NetworkFailureClassifier.unknown()
    }

    data object AccountAlreadyExists : AuthError {
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(409)
    }

    data class Conflict(
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(409),
    ) : AuthError

    data class Unknown(
        val code: Int? = null,
        override val failure: NetworkFailure = code?.let(NetworkFailureClassifier::fromHttpStatus)
            ?: NetworkFailureClassifier.unknown(),
    ) : AuthError

    val failure: NetworkFailure
}
