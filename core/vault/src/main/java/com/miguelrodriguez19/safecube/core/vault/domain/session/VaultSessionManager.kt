package com.miguelrodriguez19.safecube.core.vault.domain.session

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import kotlinx.coroutines.flow.Flow

interface VaultSessionManager {
    val vaultState: Flow<VaultState>

    fun markVaultUnlocked()

    fun markVaultLocked()
}
