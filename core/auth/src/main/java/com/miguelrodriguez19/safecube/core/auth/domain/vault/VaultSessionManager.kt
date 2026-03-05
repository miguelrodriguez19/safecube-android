package com.miguelrodriguez19.safecube.core.auth.domain.vault

import com.miguelrodriguez19.safecube.core.auth.domain.model.VaultState
import kotlinx.coroutines.flow.Flow

interface VaultSessionManager {
    val vaultState: Flow<VaultState>

    fun markVaultUnlocked()

    fun markVaultLocked()
}
