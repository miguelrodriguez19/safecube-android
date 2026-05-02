package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result

sealed interface SecureItemRemoteResult<out T> {
    data class Success<T>(
        val value: T,
    ) : SecureItemRemoteResult<T>

    data class Error(
        val error: SecureItemRemoteError,
    ) : SecureItemRemoteResult<Nothing>
}

sealed interface SecureItemRemoteError {
    data object Unauthorized : SecureItemRemoteError

    data object ItemNotFound : SecureItemRemoteError

    data object Conflict : SecureItemRemoteError

    data class HttpError(
        val statusCode: Int,
        val errorBody: String?,
    ) : SecureItemRemoteError

    data class NetworkError(
        val throwable: Throwable,
    ) : SecureItemRemoteError
}
