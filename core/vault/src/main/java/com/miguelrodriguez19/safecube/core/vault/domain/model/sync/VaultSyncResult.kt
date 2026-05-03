package com.miguelrodriguez19.safecube.core.vault.domain.model.sync

sealed interface VaultSyncResult {
    data class Success(
        val uploadedCount: Int,
        val downloadedCount: Int,
        val conflictCount: Int,
    ) : VaultSyncResult

    data class Error(
        val reason: VaultSyncError,
        val uploadedCount: Int = 0,
        val downloadedCount: Int = 0,
        val conflictCount: Int = 0,
    ) : VaultSyncResult
}
