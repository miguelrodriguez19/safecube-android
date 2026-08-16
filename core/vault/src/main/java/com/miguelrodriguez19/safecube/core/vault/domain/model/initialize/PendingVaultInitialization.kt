package com.miguelrodriguez19.safecube.core.vault.domain.model.initialize

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial

enum class PendingVaultInitializationState {
    AwaitingRemoteConfirmation,
    RemoteConfirmed,
}

data class PendingVaultInitialization(
    val candidate: VaultKeyMaterial,
    val recoveryKey: ByteArray,
    val state: PendingVaultInitializationState,
)
