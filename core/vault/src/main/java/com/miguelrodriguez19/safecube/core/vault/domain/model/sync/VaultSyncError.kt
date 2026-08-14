package com.miguelrodriguez19.safecube.core.vault.domain.model.sync

import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError

sealed interface VaultSyncError {
    val retryDecision: RetryDecision

    data class InvalidVaultState(
        val currentState: VaultState,
    ) : VaultSyncError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
    }

    data class PushFailed(
        val error: PushLocalVaultChangesError,
    ) : VaultSyncError {
        override val retryDecision: RetryDecision
            get() = error.retryDecision
    }

    data class PullFailed(
        val error: PullVaultDeltaError,
    ) : VaultSyncError {
        override val retryDecision: RetryDecision
            get() = error.retryDecision
    }
}
