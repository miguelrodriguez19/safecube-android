package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier

sealed interface SecureItemRemoteResult<out T> {
    data class Success<T>(
        val value: T,
    ) : SecureItemRemoteResult<T>

    data class Error(
        val error: SecureItemRemoteError,
    ) : SecureItemRemoteResult<Nothing>
}

sealed interface SecureItemRemoteError {
    data object Unauthorized : SecureItemRemoteError {
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(401)
    }

    data object Forbidden : SecureItemRemoteError {
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(403)
    }

    data object ItemNotFound : SecureItemRemoteError {
        override val failure: NetworkFailure = NetworkFailureClassifier.unknown(404)
    }

    data object PreconditionFailed : SecureItemRemoteError {
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(412)
    }

    data object PreconditionRequired : SecureItemRemoteError {
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(428)
    }

    data object IdempotencyConflict : SecureItemRemoteError {
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(409)
    }

    data class ValidationFailed(
        val fields: Set<String>,
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(400),
    ) : SecureItemRemoteError {
        constructor(fields: Map<String, String>) : this(fields = fields.keys)
    }

    data class HttpError(
        override val failure: NetworkFailure,
    ) : SecureItemRemoteError {
        @Suppress("UNUSED_PARAMETER")
        constructor(statusCode: Int, errorBody: String?) : this(
            NetworkFailureClassifier.fromHttpStatus(statusCode),
        )
    }

    data class NetworkError(
        override val failure: NetworkFailure,
    ) : SecureItemRemoteError {
        constructor(throwable: Throwable) : this(NetworkFailureClassifier.fromThrowable(throwable))
    }

    val failure: NetworkFailure
}
