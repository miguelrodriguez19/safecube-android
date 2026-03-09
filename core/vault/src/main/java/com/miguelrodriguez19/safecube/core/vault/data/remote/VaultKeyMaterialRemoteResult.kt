package com.miguelrodriguez19.safecube.core.vault.data.remote

sealed interface VaultKeyMaterialRemoteResult<out T> {
    data class Success<T>(
        val value: T,
    ) : VaultKeyMaterialRemoteResult<T>

    data class Error(
        val error: VaultKeyMaterialRemoteError,
    ) : VaultKeyMaterialRemoteResult<Nothing>
}

sealed interface VaultKeyMaterialRemoteError {
    data object VaultNotInitialized : VaultKeyMaterialRemoteError

    data object VaultAlreadyInitialized : VaultKeyMaterialRemoteError

    data object Unauthorized : VaultKeyMaterialRemoteError

    data class HttpError(
        val statusCode: Int,
        val errorBody: String?,
    ) : VaultKeyMaterialRemoteError

    data class NetworkError(
        val throwable: Throwable,
    ) : VaultKeyMaterialRemoteError
}
