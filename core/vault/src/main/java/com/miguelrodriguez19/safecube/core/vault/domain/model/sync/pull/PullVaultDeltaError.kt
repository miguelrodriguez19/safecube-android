package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import java.util.UUID

sealed interface PullVaultDeltaError {
    data object AccountIdUnavailable : PullVaultDeltaError

    data class RemoteListFailed(
        val error: SecureItemRemoteError,
    ) : PullVaultDeltaError

    data class RemoteDetailFailed(
        val itemId: UUID,
        val error: SecureItemRemoteError,
    ) : PullVaultDeltaError

    data class RemoteDetailMissing(
        val itemId: UUID,
    ) : PullVaultDeltaError

    data class UnsupportedRemoteItemType(
        val itemId: UUID,
        val wireType: String,
    ) : PullVaultDeltaError

    data class LocalApplyFailed(
        val itemId: UUID,
        val operation: String,
    ) : PullVaultDeltaError
}