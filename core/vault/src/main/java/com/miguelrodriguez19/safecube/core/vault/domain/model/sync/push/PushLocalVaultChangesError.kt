package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure
import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import java.util.UUID

sealed interface PushLocalVaultChangesError {
    val retryDecision: RetryDecision

    data class LocalStateUpdateFailed(
        val logicalItemId: UUID,
        val operation: String,
    ) : PushLocalVaultChangesError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
    }

    data class ProtocolIntegrityFailed(
        val logicalItemId: UUID,
        val operation: String,
    ) : PushLocalVaultChangesError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
    }

    data class RemoteFailure(
        val logicalItemId: UUID,
        val operation: String,
        val failure: NetworkFailure,
    ) : PushLocalVaultChangesError {
        override val retryDecision: RetryDecision
            get() = failure.decision
    }
}
