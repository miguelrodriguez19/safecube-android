package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push

import java.util.UUID

sealed interface PushLocalVaultChangesError {
    data class LocalStateUpdateFailed(
        val logicalItemId: UUID,
        val operation: String,
    ) : PushLocalVaultChangesError

    data class ProtocolIntegrityFailed(
        val logicalItemId: UUID,
        val operation: String,
    ) : PushLocalVaultChangesError
}
