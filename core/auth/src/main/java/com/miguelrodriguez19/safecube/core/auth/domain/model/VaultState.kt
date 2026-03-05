package com.miguelrodriguez19.safecube.core.auth.domain.model

sealed interface VaultState {
    data object Unknown : VaultState
    data object NotInitialized : VaultState
    data object Locked : VaultState
    data object Unlocked : VaultState
}
