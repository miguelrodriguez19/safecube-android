package com.miguelrodriguez19.safecube.core.vault.domain.model.initialize

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VaultKeyMaterialRemoteError

sealed interface VaultInitializeError {
    data class Remote(
        val error: VaultKeyMaterialRemoteError,
    ) : VaultInitializeError

    data class Crypto(
        val throwable: Throwable,
    ) : VaultInitializeError
}
