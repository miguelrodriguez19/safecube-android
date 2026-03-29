package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem

sealed interface SecureItemMutationResult {
    data class Success(
        val item: SecureItem,
    ) : SecureItemMutationResult

    data class Error(
        val reason: SecureItemCrudError,
    ) : SecureItemMutationResult
}
