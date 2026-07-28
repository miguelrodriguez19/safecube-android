package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import java.util.UUID

sealed interface PrepareSecureItemDraftForSyncResult {
    data class Success(
        val logicalItemId: UUID,
        val draftType: SecureItemDraftType,
    ) : PrepareSecureItemDraftForSyncResult

    data class Error(
        val reason: PrepareSecureItemDraftForSyncError,
    ) : PrepareSecureItemDraftForSyncResult
}

sealed interface PrepareSecureItemDraftForSyncError {
    data class DraftNotFound(
        val logicalItemId: UUID,
    ) : PrepareSecureItemDraftForSyncError

    data class DraftNotPublishable(
        val logicalItemId: UUID,
        val draftType: SecureItemDraftType,
    ) : PrepareSecureItemDraftForSyncError

    data class LocalStateUpdateFailed(
        val logicalItemId: UUID,
        val operation: String,
    ) : PrepareSecureItemDraftForSyncError
}
