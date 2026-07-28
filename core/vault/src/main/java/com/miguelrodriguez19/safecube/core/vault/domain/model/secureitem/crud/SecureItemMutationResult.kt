package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud

import java.util.UUID

sealed interface SecureItemMutationResult {
    data class Success(
        val logicalItemId: UUID,
    ) : SecureItemMutationResult

    data class Error(
        val reason: SecureItemCrudError,
    ) : SecureItemMutationResult
}
