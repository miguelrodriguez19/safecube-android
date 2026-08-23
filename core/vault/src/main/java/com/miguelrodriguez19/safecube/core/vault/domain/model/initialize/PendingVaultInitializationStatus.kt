package com.miguelrodriguez19.safecube.core.vault.domain.model.initialize

/**
 * Non-sensitive view of the encrypted initialization record used by navigation gates.
 */
sealed interface PendingVaultInitializationStatus {
    data object None : PendingVaultInitializationStatus

    data object AwaitingRemoteConfirmation : PendingVaultInitializationStatus

    data object RemoteConfirmed : PendingVaultInitializationStatus

    data object Corrupted : PendingVaultInitializationStatus
}
