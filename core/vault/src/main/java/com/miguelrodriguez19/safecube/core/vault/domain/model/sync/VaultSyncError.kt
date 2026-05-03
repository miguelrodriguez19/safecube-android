package com.miguelrodriguez19.safecube.core.vault.domain.model.sync

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError

sealed interface VaultSyncError {
    data class InvalidVaultState(
        val currentState: VaultState,
    ) : VaultSyncError

    data class PushFailed(
        val error: PushLocalVaultChangesError,
    ) : VaultSyncError

    data class PullFailed(
        val error: PullVaultDeltaError,
    ) : VaultSyncError
}
