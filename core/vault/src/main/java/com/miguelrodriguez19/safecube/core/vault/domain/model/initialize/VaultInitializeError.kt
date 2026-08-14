package com.miguelrodriguez19.safecube.core.vault.domain.model.initialize

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError

sealed interface VaultInitializeError {
    data class Remote(
        val error: VaultKeyMaterialRemoteError,
    ) : VaultInitializeError

    data class Crypto(
        val failure: NetworkFailure,
    ) : VaultInitializeError {
        constructor(throwable: Throwable) : this(NetworkFailureClassifier.unknown())
    }
}
