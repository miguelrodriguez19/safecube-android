package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import java.util.UUID

sealed interface PublishSecureItemDraftResult {
    data class Success(
        val logicalItemId: UUID,
        val draftType: SecureItemDraftType,
    ) : PublishSecureItemDraftResult

    data class Error(
        val reason: PublishSecureItemDraftError,
    ) : PublishSecureItemDraftResult
}

sealed interface PublishSecureItemDraftError {
    data class DraftNotFound(
        val logicalItemId: UUID,
    ) : PublishSecureItemDraftError

    data class MissingRemoteItemId(
        val logicalItemId: UUID,
        val draftType: SecureItemDraftType,
    ) : PublishSecureItemDraftError

    data class RemoteOperationFailed(
        val logicalItemId: UUID,
        val error: SecureItemRemoteError,
    ) : PublishSecureItemDraftError

    data class LocalStateUpdateFailed(
        val logicalItemId: UUID,
        val operation: String,
    ) : PublishSecureItemDraftError
}
