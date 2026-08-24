package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier

sealed interface VaultKeyMaterialRemoteResult<out T> {
    data class Success<T>(
        val value: T,
    ) : VaultKeyMaterialRemoteResult<T>

    data class Error(
        val error: VaultKeyMaterialRemoteError,
    ) : VaultKeyMaterialRemoteResult<Nothing>
}

sealed interface VaultKeyMaterialRemoteError {
    data object VaultNotInitialized : VaultKeyMaterialRemoteError {
        override val failure: NetworkFailure = NetworkFailureClassifier.unknown(404)
    }

    data object VaultAlreadyInitialized : VaultKeyMaterialRemoteError {
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(409)
    }

    data object Unauthorized : VaultKeyMaterialRemoteError {
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(401)
    }

    data object Forbidden : VaultKeyMaterialRemoteError {
        override val failure: NetworkFailure = NetworkFailureClassifier.fromHttpStatus(403)
    }

    data class HttpError(
        override val failure: NetworkFailure,
    ) : VaultKeyMaterialRemoteError

    data class NetworkError(
        override val failure: NetworkFailure,
    ) : VaultKeyMaterialRemoteError {
        constructor(throwable: Throwable) : this(NetworkFailureClassifier.fromThrowable(throwable))
    }

    val failure: NetworkFailure
}
