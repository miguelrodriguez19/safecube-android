package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push

internal data class PushProgressCounters(
    var processedCount: Int = 0,
    var syncedCount: Int = 0,
    var conflictCount: Int = 0,
    var keptPendingCount: Int = 0,
    var locallyResolvedDeleteCount: Int = 0,
)

internal sealed interface PushItemResult {
    data object Synced : PushItemResult
    data object Conflict : PushItemResult
    data object KeptPending : PushItemResult
    data object LocallyResolvedDelete : PushItemResult

    data class Fatal(
        val error: PushLocalVaultChangesError,
    ) : PushItemResult
}
