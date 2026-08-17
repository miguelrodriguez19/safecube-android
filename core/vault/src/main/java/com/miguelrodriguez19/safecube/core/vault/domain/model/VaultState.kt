package com.miguelrodriguez19.safecube.core.vault.domain.model

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure

sealed interface VaultState {
    data object InitialLoading : VaultState
    data object NotInitialized : VaultState
    data object Locked : VaultState
    data object Unlocked : VaultState
    data class RetryableRemoteFailure(
        val failure: NetworkFailure,
        val hasValidLocalKeyMaterial: Boolean,
    ) : VaultState
    data object CorruptedLocalKeyMaterial : VaultState
    data class TerminalRemoteFailure(
        val failure: NetworkFailure,
    ) : VaultState
    data object AuthenticationRequired : VaultState
}
