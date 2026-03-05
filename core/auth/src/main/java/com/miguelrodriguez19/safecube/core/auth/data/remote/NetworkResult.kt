package com.miguelrodriguez19.safecube.core.auth.data.remote

sealed interface NetworkResult<out T> {
    data class Success<T>(
        val httpCode: Int,
        val body: T?,
    ) : NetworkResult<T>

    data class HttpError<T>(
        val httpCode: Int,
        val body: T?,
        val errorBody: String?,
    ) : NetworkResult<T>

    data class Failure(
        val throwable: Throwable,
    ) : NetworkResult<Nothing>
}
