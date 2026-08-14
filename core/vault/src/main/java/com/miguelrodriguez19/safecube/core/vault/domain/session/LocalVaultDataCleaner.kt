package com.miguelrodriguez19.safecube.core.vault.domain.session

sealed interface LocalVaultCleanupResult {
    data object Success : LocalVaultCleanupResult

    data object Failure : LocalVaultCleanupResult
}

interface LocalVaultDataCleaner {
    suspend fun clear(): LocalVaultCleanupResult
}
