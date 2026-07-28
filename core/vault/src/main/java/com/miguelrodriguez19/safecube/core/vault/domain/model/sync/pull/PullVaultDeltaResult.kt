package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull

sealed interface PullVaultDeltaResult {
    data class Success(
        val processedSummaryCount: Int,
        val appliedUpsertCount: Int,
        val appliedDeleteCount: Int,
        val skippedDirtyOrConflictCount: Int,
        val checkpointUpdatedTo: Long?,
    ) : PullVaultDeltaResult

    data class Error(
        val reason: PullVaultDeltaError,
    ) : PullVaultDeltaResult
}
