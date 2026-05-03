package com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push

sealed interface PushLocalVaultChangesResult {
    data class Success(
        val processedCount: Int,
        val syncedCount: Int,
        val conflictCount: Int,
        val keptPendingCount: Int,
        val locallyResolvedDeleteCount: Int,
    ) : PushLocalVaultChangesResult

    data class Error(
        val reason: PushLocalVaultChangesError,
    ) : PushLocalVaultChangesResult
}
