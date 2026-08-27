package com.miguelrodriguez19.safecube.core.vault.domain.session

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCompletionResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockStoreResult
import kotlinx.coroutines.flow.StateFlow

enum class QuickUnlockPromptMode {
    AutomaticOnUnlockEntry,
    ManualOnly,
}

interface VaultSessionManager {
    val vaultState: StateFlow<VaultState>

    /** Process-local presentation policy for the next visit to Unlock. */
    fun quickUnlockPromptMode(): QuickUnlockPromptMode

    suspend fun refreshVaultState()

    fun isUnlocked(): Boolean

    fun unlockWithPassphrase(passphrase: String): VaultUnlockError?

    fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockError?

    fun quickUnlockOfferState(): QuickUnlockOfferState = QuickUnlockOfferState.AccountUnavailable

    fun markQuickUnlockOfferSeen(): QuickUnlockStoreResult = QuickUnlockStoreResult.AccountUnavailable

    fun prepareQuickUnlockEnrollment(
        consentGranted: Boolean,
    ): QuickUnlockEnrollmentPreparationResult = QuickUnlockEnrollmentPreparationResult.AccountUnavailable

    fun finishQuickUnlockEnrollment(
        operationId: String,
    ): QuickUnlockEnrollmentResult = QuickUnlockEnrollmentResult.AccountUnavailable

    fun prepareQuickUnlock(): QuickUnlockPreparationResult = QuickUnlockPreparationResult.AccountUnavailable

    fun finishQuickUnlock(operationId: String): QuickUnlockCompletionResult =
        QuickUnlockCompletionResult.AccountUnavailable

    fun cancelQuickUnlock(operationId: String) = Unit

    fun clearQuickUnlockEnrollment(): QuickUnlockCleanupResult =
        QuickUnlockCleanupResult.AccountUnavailable

    fun requestQuickUnlockEnrollmentAfterPassphrase(): Boolean

    fun consumeQuickUnlockEnrollmentAfterPassphrase(): Boolean

    fun clearPendingQuickUnlockEnrollment()

    /** Compatibility entry point; production callers should provide the prompt mode explicitly. */
    fun lock()

    fun lock(promptMode: QuickUnlockPromptMode)
}
