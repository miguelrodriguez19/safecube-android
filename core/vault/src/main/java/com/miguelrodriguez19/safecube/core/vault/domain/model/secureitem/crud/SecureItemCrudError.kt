package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud

sealed interface SecureItemCrudError {
    data object VaultLocked : SecureItemCrudError

    data object ItemNotFound : SecureItemCrudError

    data class ValidationError(
        val message: String,
    ) : SecureItemCrudError

    data object CorruptedPayload : SecureItemCrudError
}
