package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.error

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError

internal fun SecureItemCrudError.asUiMessage(): String = when (this) {
    SecureItemCrudError.VaultLocked -> "Vault is locked."
    SecureItemCrudError.ItemNotFound -> "Item not found."
    is SecureItemCrudError.ValidationError -> message
    SecureItemCrudError.CorruptedPayload -> "Item payload is corrupted."
    SecureItemCrudError.LocalStorageFailure -> "Local vault data could not be processed."
}
