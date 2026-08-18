package com.miguelrodriguez19.safecube.core.vault.domain.model.initialize

sealed interface PendingVaultRecoveryKeyResult {
    data class Available(
        val recoveryKey: ByteArray,
    ) : PendingVaultRecoveryKeyResult

    data object Unavailable : PendingVaultRecoveryKeyResult

    data object Corrupted : PendingVaultRecoveryKeyResult
}
