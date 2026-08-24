package com.miguelrodriguez19.safecube.core.auth.data.remote

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier

sealed interface NetworkResult<out T> {
    data class Success<T>(
        val httpCode: Int,
        val body: T?,
    ) : NetworkResult<T>

    data class HttpError<T>(
        val failure: NetworkFailure,
    ) : NetworkResult<T>

    data class Failure(
        val failure: NetworkFailure,
    ) : NetworkResult<Nothing> {

        constructor(throwable: Throwable) : this(NetworkFailureClassifier.fromThrowable(throwable))
    }
}
