package com.miguelrodriguez19.safecube.core.network.domain.model

import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.util.concurrent.TimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

enum class NetworkFailureKind {
    Connectivity,
    Timeout,
    RateLimited,
    ServerUnavailable,
    Unauthorized,
    Forbidden,
    Validation,
    Conflict,
    Protocol,
    MalformedResponse,
    Unknown,
}

enum class RetryDecision {
    Retryable,
    Terminal,
}

data class NetworkFailure(
    val kind: NetworkFailureKind,
    val decision: RetryDecision,
    val statusCode: Int? = null,
)

object NetworkFailureClassifier {
    fun fromHttpStatus(statusCode: Int): NetworkFailure = when (statusCode) {
        408 -> retryable(NetworkFailureKind.Timeout, statusCode)
        429 -> retryable(NetworkFailureKind.RateLimited, statusCode)
        in 500..599 -> retryable(NetworkFailureKind.ServerUnavailable, statusCode)
        401 -> terminal(NetworkFailureKind.Unauthorized, statusCode)
        403 -> terminal(NetworkFailureKind.Forbidden, statusCode)
        400 -> terminal(NetworkFailureKind.Validation, statusCode)
        409, 412 -> terminal(NetworkFailureKind.Conflict, statusCode)
        428 -> terminal(NetworkFailureKind.Protocol, statusCode)
        else -> terminal(NetworkFailureKind.Unknown, statusCode)
    }

    fun fromThrowable(throwable: Throwable): NetworkFailure {
        if (throwable is CancellationException) throw throwable

        return when (throwable) {
            is SocketTimeoutException,
            is TimeoutException,
            is InterruptedIOException,
                -> retryable(NetworkFailureKind.Timeout)

            is IOException -> retryable(NetworkFailureKind.Connectivity)
            is SerializationException -> terminal(NetworkFailureKind.MalformedResponse)
            else -> terminal(NetworkFailureKind.Unknown)
        }
    }

    fun malformedResponse(statusCode: Int? = null): NetworkFailure = terminal(
        kind = NetworkFailureKind.MalformedResponse,
        statusCode = statusCode,
    )

    fun unknown(statusCode: Int? = null): NetworkFailure = terminal(
        kind = NetworkFailureKind.Unknown,
        statusCode = statusCode,
    )

    private fun retryable(
        kind: NetworkFailureKind,
        statusCode: Int? = null,
    ): NetworkFailure = NetworkFailure(
        kind = kind,
        decision = RetryDecision.Retryable,
        statusCode = statusCode,
    )

    private fun terminal(
        kind: NetworkFailureKind,
        statusCode: Int? = null,
    ): NetworkFailure = NetworkFailure(
        kind = kind,
        decision = RetryDecision.Terminal,
        statusCode = statusCode,
    )
}
