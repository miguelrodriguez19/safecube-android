package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft

import java.util.UUID

sealed interface DiscardSecureItemDraftResult {
    data class Success(
        val logicalItemId: UUID,
    ) : DiscardSecureItemDraftResult

    data class Error(
        val reason: DiscardSecureItemDraftError,
    ) : DiscardSecureItemDraftResult
}

sealed interface DiscardSecureItemDraftError {
    data class DraftNotFound(
        val logicalItemId: UUID,
    ) : DiscardSecureItemDraftError

    data class LocalStateUpdateFailed(
        val logicalItemId: UUID,
        val operation: String,
    ) : DiscardSecureItemDraftError
}
