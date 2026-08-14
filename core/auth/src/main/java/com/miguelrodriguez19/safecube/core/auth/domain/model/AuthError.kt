package com.miguelrodriguez19.safecube.core.auth.domain.model

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier

sealed interface AuthError {
    data class ValidationFailed(
        val fields: Set<String> = emptySet(),
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(400),
    ) : AuthError {
        @Suppress("UNUSED_PARAMETER")
        constructor(fields: Map<String, String>?, message: String?) : this(
            fields = fields?.keys.orEmpty(),
        )
    }

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
    ) : AuthError {
        @Suppress("UNUSED_PARAMETER")
        constructor(message: String?) : this()
    }

    data class Unknown(
        val code: Int? = null,
        override val failure: NetworkFailure = code?.let(NetworkFailureClassifier::fromHttpStatus)
            ?: NetworkFailureClassifier.unknown(),
    ) : AuthError {
        @Suppress("UNUSED_PARAMETER")
        constructor(code: Int? = null, message: String? = null) : this(
            code = code,
            failure = code?.let(NetworkFailureClassifier::fromHttpStatus)
                ?: NetworkFailureClassifier.unknown(),
        )
    }

    val failure: NetworkFailure
}
