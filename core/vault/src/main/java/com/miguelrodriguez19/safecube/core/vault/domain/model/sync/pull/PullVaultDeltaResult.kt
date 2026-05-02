package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull

import java.time.Instant

sealed interface PullVaultDeltaResult {
    data class Success(
        val processedSummaryCount: Int,
        val appliedUpsertCount: Int,
        val appliedDeleteCount: Int,
        val skippedDirtyOrConflictCount: Int,
        val checkpointUpdatedTo: Instant?,
    ) : PullVaultDeltaResult

    data class Error(
        val reason: PullVaultDeltaError,
    ) : PullVaultDeltaResult
}