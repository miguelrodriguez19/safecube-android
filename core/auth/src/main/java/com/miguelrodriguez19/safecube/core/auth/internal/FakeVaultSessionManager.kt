package com.miguelrodriguez19.safecube.core.auth.internal

import com.miguelrodriguez19.safecube.core.auth.VaultSessionManager
import com.miguelrodriguez19.safecube.core.auth.VaultState
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
