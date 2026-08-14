package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull

import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import java.util.UUID

sealed interface PullVaultDeltaError {
    val retryDecision: RetryDecision

    data object AccountIdUnavailable : PullVaultDeltaError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
    }

    data class RemoteListFailed(
        val error: SecureItemRemoteError,
    ) : PullVaultDeltaError {
        override val retryDecision: RetryDecision
            get() = error.failure.decision
    }

    data class RemoteDetailFailed(
        val itemId: UUID,
        val error: SecureItemRemoteError,
    ) : PullVaultDeltaError {
        override val retryDecision: RetryDecision
            get() = error.failure.decision
    }

    data class RemoteDetailMissing(
        val itemId: UUID,
    ) : PullVaultDeltaError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
    }

    data class UnsupportedRemoteItemType(
        val itemId: UUID,
        val wireType: String,
    ) : PullVaultDeltaError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
    }

    data class LocalApplyFailed(
        val itemId: UUID,
        val operation: String,
    ) : PullVaultDeltaError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
    }
}
