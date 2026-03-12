package com.miguelrodriguez19.safecube.core.vault.domain.model.initialize

sealed interface VaultInitializeResult {
    data class Initialized(
        val recoveryKey: ByteArray,
    ) : VaultInitializeResult

    data object AlreadyInitialized : VaultInitializeResult

    data class Error(
        val reason: VaultInitializeError,
    ) : VaultInitializeResult
}
