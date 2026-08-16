package com.miguelrodriguez19.safecube.core.vault.domain.repository

import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitialization

sealed interface PendingVaultInitializationReadResult {
    data object Empty : PendingVaultInitializationReadResult

    data class Present(
        val value: PendingVaultInitialization,
    ) : PendingVaultInitializationReadResult

    data object Corrupted : PendingVaultInitializationReadResult
}

interface PendingVaultInitializationRepository {
    fun read(): PendingVaultInitializationReadResult

    fun save(value: PendingVaultInitialization): Boolean

    fun clear(): Boolean
}
