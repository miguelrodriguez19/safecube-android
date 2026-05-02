package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull

internal sealed interface DeltaApplyResult {
    data class Success(val counters: ApplyDeltaCounters) : DeltaApplyResult
    data class Error(val error: PullVaultDeltaError) : DeltaApplyResult
}

internal data class ApplyDeltaCounters(
    var appliedUpserts: Int = 0,
    var appliedDeletes: Int = 0,
    var skippedDirtyOrConflict: Int = 0,
)
