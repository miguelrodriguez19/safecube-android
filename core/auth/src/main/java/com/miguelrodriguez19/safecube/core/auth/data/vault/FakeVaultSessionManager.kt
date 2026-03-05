package com.miguelrodriguez19.safecube.core.auth.data.vault

import com.miguelrodriguez19.safecube.core.auth.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.auth.domain.vault.VaultSessionManager
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class FakeVaultSessionManager @Inject constructor() : VaultSessionManager {
    private val state = MutableStateFlow<VaultState>(VaultState.Locked)

    override val vaultState: Flow<VaultState> = state.asStateFlow()

    override fun markVaultUnlocked() {
        state.value = VaultState.Unlocked
    }

    override fun markVaultLocked() {
        state.value = VaultState.Locked
    }
}
