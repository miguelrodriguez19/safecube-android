package com.miguelrodriguez19.safecube.core.auth

sealed interface VaultState {
    data object Unknown : VaultState
    data object NotInitialized : VaultState
    data object Locked : VaultState
    data object Unlocked : VaultState
}
