package com.miguelrodriguez19.safecube.core.vault.domain.quickunlock

import java.util.UUID

/**
 * Coordinates the local Android Keystore wrapping flow. This contract deliberately never exposes
 * KEK bytes or Android framework prompt types.
 */
interface QuickUnlockManager {
    fun offerState(accountId: UUID): QuickUnlockOfferState

    fun markOfferSeen(accountId: UUID): QuickUnlockStoreResult

    fun prepareEnrollment(
        accountId: UUID,
        consentGranted: Boolean,
    ): QuickUnlockEnrollmentPreparationResult

    fun finishEnrollment(accountId: UUID, operationId: String): QuickUnlockEnrollmentResult

    fun prepareUnlock(accountId: UUID): QuickUnlockPreparationResult

    fun finishUnlock(accountId: UUID, operationId: String): QuickUnlockCompletionResult

    fun cancelUnlock(operationId: String)

    fun clearEnrollment(accountId: UUID): QuickUnlockCleanupResult

    fun clearAllEnrollments(): QuickUnlockCleanupResult
}

sealed interface QuickUnlockOfferState {
    data object Available : QuickUnlockOfferState

    data object Seen : QuickUnlockOfferState

    data object Enrolled : QuickUnlockOfferState

    data object Unsupported : QuickUnlockOfferState

    data object InvalidEnrollment : QuickUnlockOfferState

    data object AccountUnavailable : QuickUnlockOfferState
}

sealed interface QuickUnlockStoreResult {
    data object Saved : QuickUnlockStoreResult

    data object Failed : QuickUnlockStoreResult

    data object AccountUnavailable : QuickUnlockStoreResult
}

sealed interface QuickUnlockEnrollmentResult {
    data object Enrolled : QuickUnlockEnrollmentResult

    data object ConsentRequired : QuickUnlockEnrollmentResult

    data object RequiresPassphrase : QuickUnlockEnrollmentResult

    data object Unsupported : QuickUnlockEnrollmentResult

    data object StorageFailure : QuickUnlockEnrollmentResult

    data object AccountUnavailable : QuickUnlockEnrollmentResult

    data object AccountChanged : QuickUnlockEnrollmentResult

    data object SessionInvalid : QuickUnlockEnrollmentResult
}

sealed interface QuickUnlockEnrollmentPreparationResult {
    data class Ready(val operationId: String) : QuickUnlockEnrollmentPreparationResult

    data object AlreadyEnrolled : QuickUnlockEnrollmentPreparationResult

    data object ConsentRequired : QuickUnlockEnrollmentPreparationResult

    data object RequiresPassphrase : QuickUnlockEnrollmentPreparationResult

    data object Unsupported : QuickUnlockEnrollmentPreparationResult

    data object StorageFailure : QuickUnlockEnrollmentPreparationResult

    data object AccountUnavailable : QuickUnlockEnrollmentPreparationResult

    data object SessionInvalid : QuickUnlockEnrollmentPreparationResult

    data object OperationInProgress : QuickUnlockEnrollmentPreparationResult
}

sealed interface QuickUnlockPreparationResult {
    data class Ready(val operationId: String) : QuickUnlockPreparationResult

    data object NotEnrolled : QuickUnlockPreparationResult

    data object Unsupported : QuickUnlockPreparationResult

    data object TemporarilyUnavailable : QuickUnlockPreparationResult

    data object InvalidEnrollment : QuickUnlockPreparationResult

    data object AccountUnavailable : QuickUnlockPreparationResult

    data object SessionInvalid : QuickUnlockPreparationResult

    data object OperationInProgress : QuickUnlockPreparationResult
}

sealed interface QuickUnlockCompletionResult {
    data object Unlocked : QuickUnlockCompletionResult

    data object StaleOperation : QuickUnlockCompletionResult

    data object AuthenticationFailed : QuickUnlockCompletionResult

    data object TemporarilyUnavailable : QuickUnlockCompletionResult

    data object InvalidEnrollment : QuickUnlockCompletionResult

    data object AccountUnavailable : QuickUnlockCompletionResult

    data object AccountChanged : QuickUnlockCompletionResult

    data object SessionInvalid : QuickUnlockCompletionResult
}

sealed interface QuickUnlockCleanupResult {
    data object Cleared : QuickUnlockCleanupResult

    data object Failed : QuickUnlockCleanupResult

    data object AccountUnavailable : QuickUnlockCleanupResult

    data object SessionInvalid : QuickUnlockCleanupResult
}
