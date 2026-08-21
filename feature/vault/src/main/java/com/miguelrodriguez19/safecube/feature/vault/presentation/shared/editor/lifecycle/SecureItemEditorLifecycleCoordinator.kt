package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.lifecycle

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

@Singleton
internal class SecureItemEditorLifecycleCoordinator @Inject constructor(
    private val observeVaultSyncingUseCase: ObserveVaultSyncingUseCase,
    private val vaultSessionManager: VaultSessionManager,
) {
    fun observeSyncing(): StateFlow<Boolean> = observeVaultSyncingUseCase()

    fun observeVaultLocked(): Flow<Unit> = vaultSessionManager.vaultState
        .filter { state -> state == VaultState.Locked }
        .map { }

    fun isVaultLocked(): Boolean = vaultSessionManager.vaultState.value == VaultState.Locked
}
