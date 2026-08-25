package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import com.miguelrodriguez19.safecube.core.vault.data.session.QuickUnlockKeyMaterialAccess
import com.miguelrodriguez19.safecube.core.vault.domain.model.quickunlock.VaultUnlockProvenance
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCompletionResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockManager
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockStoreResult
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class QuickUnlockManagerImpl @Inject constructor(
    private val store: QuickUnlockStore,
    private val keyStore: QuickUnlockKeyStore,
    private val keyMaterialAccess: QuickUnlockKeyMaterialAccess,
    private val envelopeCodec: QuickUnlockEnvelopeCodec,
) : QuickUnlockManager {
    private val pendingOperations = mutableMapOf<String, PendingQuickUnlockOperation>()

    @Synchronized
    override fun offerState(accountId: UUID): QuickUnlockOfferState {
        if (!keyStore.isSupported()) return QuickUnlockOfferState.Unsupported
        return when (val envelope = store.readEnvelope(accountId)) {
            is QuickUnlockStoredEnvelope.Present -> when {
                envelopeCodec.decode(envelope.value) !is QuickUnlockEnvelopeDecodeResult.Valid ||
                    !keyStore.hasAlias(accountId) -> {
                    clearInvalidEnrollment(accountId)
                    QuickUnlockOfferState.InvalidEnrollment
                }

                else -> QuickUnlockOfferState.Enrolled
            }

            QuickUnlockStoredEnvelope.Corrupted -> {
                clearInvalidEnrollment(accountId)
                QuickUnlockOfferState.InvalidEnrollment
            }

            QuickUnlockStoredEnvelope.Absent -> {
                if (keyStore.hasAlias(accountId)) {
                    keyStore.delete(accountId)
                    QuickUnlockOfferState.InvalidEnrollment
                } else if (store.hasSeenOffer(accountId)) {
                    QuickUnlockOfferState.Seen
                } else {
                    QuickUnlockOfferState.Available
                }
            }
        }
    }

    @Synchronized
    override fun markOfferSeen(accountId: UUID): QuickUnlockStoreResult = if (
        store.markOfferSeen(accountId)
    ) {
        QuickUnlockStoreResult.Saved
    } else {
        QuickUnlockStoreResult.Failed
    }

    @Synchronized
    override fun prepareEnrollment(
        accountId: UUID,
        consentGranted: Boolean,
    ): QuickUnlockEnrollmentPreparationResult {
        if (!consentGranted) return QuickUnlockEnrollmentPreparationResult.ConsentRequired
        if (!keyStore.isSupported()) return QuickUnlockEnrollmentPreparationResult.Unsupported
        if (hasPendingOperation(accountId)) {
            return QuickUnlockEnrollmentPreparationResult.OperationInProgress
        }
        if (store.readEnvelope(accountId) is QuickUnlockStoredEnvelope.Present) {
            return QuickUnlockEnrollmentPreparationResult.AlreadyEnrolled
        }
        if (keyMaterialAccess.provenance() != VaultUnlockProvenance.Passphrase) {
            return QuickUnlockEnrollmentPreparationResult.RequiresPassphrase
        }
        val kek = keyMaterialAccess.currentForEnrollment()
            ?: return QuickUnlockEnrollmentPreparationResult.RequiresPassphrase
        if (!store.markOfferSeen(accountId)) {
            kek.fill(0)
            return QuickUnlockEnrollmentPreparationResult.StorageFailure
        }
        val operationId = UUID.randomUUID().toString()
        return when (keyStore.prepareWrap(accountId, operationId)) {
            QuickUnlockKeyStorePrepareResult.Ready -> {
                pendingOperations[operationId] = PendingQuickUnlockOperation.Enrollment(accountId, kek)
                QuickUnlockEnrollmentPreparationResult.Ready(operationId)
            }

            QuickUnlockKeyStorePrepareResult.Unsupported -> {
                kek.fill(0)
                QuickUnlockEnrollmentPreparationResult.Unsupported
            }

            QuickUnlockKeyStorePrepareResult.TemporarilyUnavailable,
            QuickUnlockKeyStorePrepareResult.InvalidEnrollment,
                -> {
                kek.fill(0)
                keyStore.delete(accountId)
                QuickUnlockEnrollmentPreparationResult.StorageFailure
            }
        }
    }

    @Synchronized
    override fun finishEnrollment(
        accountId: UUID,
        operationId: String,
    ): QuickUnlockEnrollmentResult {
        val operation = pendingOperations.remove(operationId)
            ?: return QuickUnlockEnrollmentResult.StorageFailure
        if (operation !is PendingQuickUnlockOperation.Enrollment) {
            operation.clear()
            keyStore.cancel(operationId)
            return QuickUnlockEnrollmentResult.StorageFailure
        }
        if (operation.accountId != accountId || !isSamePassphraseUnlock(operation.kek)) {
            operation.clear()
            keyStore.cancel(operationId)
            keyStore.delete(accountId)
            return QuickUnlockEnrollmentResult.RequiresPassphrase
        }
        return try {
            when (val result = keyStore.finishWrap(operationId, operation.kek)) {
                is QuickUnlockKeyStoreWrapResult.Success -> {
                    if (store.saveEnvelope(accountId, result.envelope)) {
                        QuickUnlockEnrollmentResult.Enrolled
                    } else {
                        clearInvalidEnrollment(accountId)
                        QuickUnlockEnrollmentResult.StorageFailure
                    }
                }

                QuickUnlockKeyStoreWrapResult.Unsupported -> {
                    clearInvalidEnrollment(accountId)
                    QuickUnlockEnrollmentResult.Unsupported
                }
                QuickUnlockKeyStoreWrapResult.Failed -> {
                    clearInvalidEnrollment(accountId)
                    QuickUnlockEnrollmentResult.StorageFailure
                }
            }
        } finally {
            operation.clear()
        }
    }

    @Synchronized
    override fun prepareUnlock(accountId: UUID): QuickUnlockPreparationResult {
        if (!keyStore.isSupported()) return QuickUnlockPreparationResult.Unsupported
        if (hasPendingOperation(accountId)) return QuickUnlockPreparationResult.OperationInProgress
        val envelope = when (val stored = store.readEnvelope(accountId)) {
            is QuickUnlockStoredEnvelope.Present -> stored.value
            QuickUnlockStoredEnvelope.Absent -> {
                keyStore.delete(accountId)
                return QuickUnlockPreparationResult.NotEnrolled
            }
            QuickUnlockStoredEnvelope.Corrupted -> {
                clearInvalidEnrollment(accountId)
                return QuickUnlockPreparationResult.InvalidEnrollment
            }
        }
        if (envelopeCodec.decode(envelope) !is QuickUnlockEnvelopeDecodeResult.Valid ||
            !keyStore.hasAlias(accountId)
        ) {
            clearInvalidEnrollment(accountId)
            return QuickUnlockPreparationResult.InvalidEnrollment
        }
        val operationId = UUID.randomUUID().toString()
        return when (keyStore.prepareUnwrap(accountId, envelope, operationId)) {
            QuickUnlockKeyStorePrepareResult.Ready -> {
                pendingOperations[operationId] = PendingQuickUnlockOperation.Unlock(accountId, envelope.copyOf())
                QuickUnlockPreparationResult.Ready(operationId)
            }

            QuickUnlockKeyStorePrepareResult.Unsupported -> QuickUnlockPreparationResult.Unsupported
            QuickUnlockKeyStorePrepareResult.TemporarilyUnavailable ->
                QuickUnlockPreparationResult.TemporarilyUnavailable

            QuickUnlockKeyStorePrepareResult.InvalidEnrollment -> {
                clearInvalidEnrollment(accountId)
                QuickUnlockPreparationResult.InvalidEnrollment
            }
        }
    }

    @Synchronized
    override fun finishUnlock(
        accountId: UUID,
        operationId: String,
    ): QuickUnlockCompletionResult {
        val operation = pendingOperations.remove(operationId)
            ?: return QuickUnlockCompletionResult.StaleOperation
        if (operation !is PendingQuickUnlockOperation.Unlock ||
            operation.accountId != accountId ||
            !matchesCurrentEnrollment(operation)
        ) {
            keyStore.cancel(operationId)
            operation.clear()
            return QuickUnlockCompletionResult.StaleOperation
        }
        return try {
            when (val result = keyStore.finishUnwrap(operationId)) {
                is QuickUnlockKeyStoreFinishResult.Success -> {
                    try {
                        keyMaterialAccess.replaceAfterQuickUnlock(result.kek)
                        QuickUnlockCompletionResult.Unlocked
                    } finally {
                        result.kek.fill(0)
                    }
                }

                QuickUnlockKeyStoreFinishResult.AuthenticationFailed ->
                    QuickUnlockCompletionResult.AuthenticationFailed

                QuickUnlockKeyStoreFinishResult.TemporarilyUnavailable ->
                    QuickUnlockCompletionResult.TemporarilyUnavailable

                QuickUnlockKeyStoreFinishResult.InvalidEnrollment -> {
                    clearInvalidEnrollment(accountId)
                    QuickUnlockCompletionResult.InvalidEnrollment
                }
            }
        } finally {
            operation.clear()
        }
    }

    @Synchronized
    override fun cancelUnlock(operationId: String) {
        val operation = pendingOperations.remove(operationId)
        operation?.clear()
        keyStore.cancel(operationId)
        if (operation is PendingQuickUnlockOperation.Enrollment) {
            keyStore.delete(operation.accountId)
        }
    }

    @Synchronized
    override fun clearEnrollment(accountId: UUID): QuickUnlockCleanupResult {
        pendingOperations.entries
            .filter { it.value.accountId == accountId }
            .map { it.key }
            .forEach(::cancelUnlock)
        val storeCleared = store.clearEnrollmentArtifact(accountId)
        val aliasDeleted = keyStore.delete(accountId)
        return if (storeCleared && aliasDeleted) {
            QuickUnlockCleanupResult.Cleared
        } else {
            QuickUnlockCleanupResult.Failed
        }
    }

    @Synchronized
    override fun clearAllEnrollments(): QuickUnlockCleanupResult {
        pendingOperations.keys.toList().forEach(::cancelUnlock)
        val storeCleared = store.clearAll()
        val aliasesDeleted = keyStore.deleteAll()
        return if (storeCleared && aliasesDeleted) {
            QuickUnlockCleanupResult.Cleared
        } else {
            QuickUnlockCleanupResult.Failed
        }
    }

    private fun matchesCurrentEnrollment(operation: PendingQuickUnlockOperation.Unlock): Boolean = when (
        val current = store.readEnvelope(operation.accountId)
    ) {
        is QuickUnlockStoredEnvelope.Present -> current.value.contentEquals(operation.envelope)
        QuickUnlockStoredEnvelope.Absent,
        QuickUnlockStoredEnvelope.Corrupted,
            -> false
    }

    private fun clearInvalidEnrollment(accountId: UUID) {
        store.clearEnrollmentArtifact(accountId)
        keyStore.delete(accountId)
    }

    private fun isSamePassphraseUnlock(expectedKek: ByteArray): Boolean {
        if (keyMaterialAccess.provenance() != VaultUnlockProvenance.Passphrase) return false
        val currentKek = keyMaterialAccess.currentForEnrollment() ?: return false
        return try {
            currentKek.contentEquals(expectedKek)
        } finally {
            currentKek.fill(0)
        }
    }

    private fun hasPendingOperation(accountId: UUID): Boolean =
        pendingOperations.values.any { it.accountId == accountId }

    private sealed interface PendingQuickUnlockOperation {
        val accountId: UUID

        fun clear()

        data class Enrollment(
            override val accountId: UUID,
            val kek: ByteArray,
        ) : PendingQuickUnlockOperation {
            override fun clear() = kek.fill(0)
        }

        data class Unlock(
            override val accountId: UUID,
            val envelope: ByteArray,
        ) : PendingQuickUnlockOperation {
            override fun clear() = envelope.fill(0)
        }
    }
}
