package com.miguelrodriguez19.safecube.core.auth

import kotlinx.coroutines.flow.Flow

interface VaultSessionManager {
    val vaultState: Flow<VaultState>

    fun markVaultUnlocked()

    fun markVaultLocked()
}
