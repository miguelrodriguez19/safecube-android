package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import com.miguelrodriguez19.safecube.core.vault.data.session.QuickUnlockKeyMaterialAccess
import com.miguelrodriguez19.safecube.core.vault.domain.model.quickunlock.VaultUnlockProvenance
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCompletionResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockStoreResult
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.security.SecureRandom
import java.util.UUID
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class QuickUnlockManagerImplTest {
    private val store = mockk<QuickUnlockStore>()
    private val keyStore = mockk<QuickUnlockKeyStore>()
    private val keyMaterialAccess = mockk<QuickUnlockKeyMaterialAccess>()
    private val envelopeCodec = QuickUnlockEnvelopeCodec()

    private lateinit var target: QuickUnlockManagerImpl

    private val secureRandom = SecureRandom()
    private val accountId = UUID.randomUUID()
    private val envelope = validEnvelope()
    private val defaultKek = randomBytes(32)
    private var replacedKek: ByteArray? = null

    @Before
    fun setUp() {
        replacedKek = null
        target = QuickUnlockManagerImpl(store, keyStore, keyMaterialAccess, envelopeCodec)

        every { store.readEnvelope(any()) } returns QuickUnlockStoredEnvelope.Present(envelope)
        every { store.hasSeenOffer(any()) } returns true
        every { store.markOfferSeen(any()) } returns true
        every { store.saveEnvelope(any(), any()) } returns true
        every { store.clearEnrollmentArtifact(any()) } returns true
        every { store.clearAll() } returns true

        every { keyStore.isSupported() } returns true
        every { keyStore.hasAlias(any()) } returns true
        every { keyStore.prepareWrap(any(), any()) } returns QuickUnlockKeyStorePrepareResult.Ready
        every { keyStore.finishWrap(any(), any()) } returns QuickUnlockKeyStoreWrapResult.Success(envelope)
        every { keyStore.prepareUnwrap(any(), any(), any()) } returns QuickUnlockKeyStorePrepareResult.Ready
        every { keyStore.finishUnwrap(any()) } returns QuickUnlockKeyStoreFinishResult.AuthenticationFailed
        every { keyStore.cancel(any()) } just Runs
        every { keyStore.delete(any()) } returns true
        every { keyStore.deleteAll() } returns true

        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.None
        every { keyMaterialAccess.currentForEnrollment() } answers { defaultKek.copyOf() }
        every { keyMaterialAccess.replaceAfterQuickUnlock(any()) } answers {
            replacedKek = firstArg<ByteArray>().copyOf()
        }
    }

    @Test
    fun `finishUnlock_whenEnrollmentIsUnchanged_thenStoresKekThroughInternalSink`() {
        val expectedKek = randomBytes(32)
        val expectedKekSnapshot = expectedKek.copyOf()
        every { keyStore.finishUnwrap(any()) } returns QuickUnlockKeyStoreFinishResult.Success(expectedKek)

        val prepared = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready
        val result = target.finishUnlock(accountId, prepared.operationId)

        assertEquals(QuickUnlockCompletionResult.Unlocked, result)
        assertArrayEquals(expectedKekSnapshot, requireNotNull(replacedKek))
    }

    @Test
    fun `finishUnlock_whenEnrollmentChangesBeforeCallback_thenReturnsStaleOperation`() {
        every { store.readEnvelope(accountId) } returnsMany listOf(
            QuickUnlockStoredEnvelope.Present(envelope),
            QuickUnlockStoredEnvelope.Absent,
        )
        val prepared = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready

        val result = target.finishUnlock(accountId, prepared.operationId)

        assertEquals(QuickUnlockCompletionResult.StaleOperation, result)
        assertNull(replacedKek)
        verify(exactly = 1) { keyStore.cancel(prepared.operationId) }
    }

    @Test
    fun `finishUnlock_whenAuthenticationFails_thenPreservesEnrollment`() {
        val prepared = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready

        val result = target.finishUnlock(accountId, prepared.operationId)

        assertEquals(QuickUnlockCompletionResult.AuthenticationFailed, result)
        verify(exactly = 0) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `prepareUnlock_whenOperationForAccountIsPending_thenReturnsOperationInProgress`() {
        val first = target.prepareUnlock(accountId)

        val second = target.prepareUnlock(accountId)

        assertTrue(first is QuickUnlockPreparationResult.Ready)
        assertEquals(QuickUnlockPreparationResult.OperationInProgress, second)
        verify(exactly = 1) { keyStore.prepareUnwrap(any(), any(), any()) }
    }

    @Test
    fun `offerState_whenAliasIsAbsent_thenClearsInvalidEnrollment`() {
        every { keyStore.hasAlias(accountId) } returns false

        val result = target.offerState(accountId)

        assertEquals(QuickUnlockOfferState.InvalidEnrollment, result)
        verify(exactly = 1) { store.clearEnrollmentArtifact(accountId) }
        verify(exactly = 1) { keyStore.delete(accountId) }
    }

    @Test
    fun `prepareEnrollment_whenUnlockProvenanceIsNotPassphrase_thenRequiresPassphrase`() {
        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.RecoveryKey
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent

        val result = target.prepareEnrollment(accountId, consentGranted = true)

        assertEquals(QuickUnlockEnrollmentPreparationResult.RequiresPassphrase, result)
        verify(exactly = 0) { store.markOfferSeen(any()) }
    }

    @Test
    fun `finishEnrollment_whenPassphraseUnlockIsValid_thenSavesAuthenticatedEnvelope`() {
        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready

        val result = target.finishEnrollment(accountId, prepared.operationId)

        assertEquals(QuickUnlockEnrollmentResult.Enrolled, result)
        verify(exactly = 1) {
            store.saveEnvelope(accountId, match { it.contentEquals(envelope) })
        }
    }

    @Test
    fun `prepareEnrollment_whenOfferMarkerCannotPersist_thenReturnsStorageFailureWithoutCreatingKey`() {
        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.markOfferSeen(accountId) } returns false

        val result = target.prepareEnrollment(accountId, consentGranted = true)

        assertEquals(QuickUnlockEnrollmentPreparationResult.StorageFailure, result)
        verify(exactly = 0) { keyStore.prepareWrap(any(), any()) }
    }

    @Test
    fun `cancelUnlock_whenEnrollmentOperationIsPending_thenKeepsOfferMarkerAndDeletesAlias`() {
        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready

        target.cancelUnlock(prepared.operationId)

        verify(exactly = 1) { store.markOfferSeen(accountId) }
        verify(exactly = 1) { keyStore.delete(accountId) }
        verify(exactly = 1) { keyStore.cancel(prepared.operationId) }
    }

    @Test
    fun `clearEnrollment_whenArtifactCleanupFails_thenDeletesAliasAndReportsFailure`() {
        every { store.clearEnrollmentArtifact(accountId) } returns false

        val result = target.clearEnrollment(accountId)

        assertEquals(QuickUnlockCleanupResult.Failed, result)
        verify(exactly = 1) { keyStore.delete(accountId) }
    }

    @Test
    fun `finishEnrollment_whenKeystoreWrapFails_thenClearsPartialArtifact`() {
        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { keyStore.finishWrap(any(), any()) } returns QuickUnlockKeyStoreWrapResult.Failed
        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready

        val result = target.finishEnrollment(accountId, prepared.operationId)

        assertEquals(QuickUnlockEnrollmentResult.StorageFailure, result)
        verify(exactly = 1) { store.clearEnrollmentArtifact(accountId) }
        verify(exactly = 1) { keyStore.delete(accountId) }
    }

    @Test
    fun `offerState_whenUnsupportedSeenOrCorrupted_thenReturnsClosedStates`() {
        every { keyStore.isSupported() } returnsMany listOf(false, true, true)
        every { keyStore.hasAlias(accountId) } returns false
        every { store.readEnvelope(accountId) } returnsMany listOf(
            QuickUnlockStoredEnvelope.Absent,
            QuickUnlockStoredEnvelope.Corrupted,
        )
        every { store.hasSeenOffer(accountId) } returns true

        val unsupported = target.offerState(accountId)
        val seen = target.offerState(accountId)
        val corrupted = target.offerState(accountId)

        assertEquals(QuickUnlockOfferState.Unsupported, unsupported)
        assertEquals(QuickUnlockOfferState.Seen, seen)
        assertEquals(QuickUnlockOfferState.InvalidEnrollment, corrupted)
        verify(exactly = 1) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `offerState_whenEnrollmentIsAvailableEnrolledOrOrphaned_thenReturnsMatchingState`() {
        every { store.readEnvelope(accountId) } returnsMany listOf(
            QuickUnlockStoredEnvelope.Absent,
            QuickUnlockStoredEnvelope.Present(envelope),
            QuickUnlockStoredEnvelope.Absent,
        )
        every { store.hasSeenOffer(accountId) } returns false
        every { keyStore.hasAlias(accountId) } returnsMany listOf(false, true, true)

        val available = target.offerState(accountId)
        val enrolled = target.offerState(accountId)
        val orphaned = target.offerState(accountId)

        assertEquals(QuickUnlockOfferState.Available, available)
        assertEquals(QuickUnlockOfferState.Enrolled, enrolled)
        assertEquals(QuickUnlockOfferState.InvalidEnrollment, orphaned)
        verify(exactly = 1) { keyStore.delete(accountId) }
    }

    @Test
    fun `prepareUnlock_whenEnrollmentIsAbsentTemporaryOrInvalid_thenReturnsClosedResults`() {
        every { store.readEnvelope(accountId) } returnsMany listOf(
            QuickUnlockStoredEnvelope.Absent,
            QuickUnlockStoredEnvelope.Present(envelope),
            QuickUnlockStoredEnvelope.Present(envelope),
            QuickUnlockStoredEnvelope.Present(envelope),
        )
        every { keyStore.prepareUnwrap(any(), any(), any()) } returnsMany listOf(
            QuickUnlockKeyStorePrepareResult.Unsupported,
            QuickUnlockKeyStorePrepareResult.TemporarilyUnavailable,
            QuickUnlockKeyStorePrepareResult.InvalidEnrollment,
        )

        val absent = target.prepareUnlock(accountId)
        val unsupported = target.prepareUnlock(accountId)
        val temporary = target.prepareUnlock(accountId)
        val invalid = target.prepareUnlock(accountId)

        assertEquals(QuickUnlockPreparationResult.NotEnrolled, absent)
        assertEquals(QuickUnlockPreparationResult.Unsupported, unsupported)
        assertEquals(QuickUnlockPreparationResult.TemporarilyUnavailable, temporary)
        assertEquals(QuickUnlockPreparationResult.InvalidEnrollment, invalid)
        verify(exactly = 1) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `finishUnlock_whenKeystoreIsTemporaryOrInvalid_thenDoesNotInstallKek`() {
        every { keyStore.finishUnwrap(any()) } returnsMany listOf(
            QuickUnlockKeyStoreFinishResult.TemporarilyUnavailable,
            QuickUnlockKeyStoreFinishResult.InvalidEnrollment,
        )
        val temporaryOperation = (target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready).operationId
        val temporary = target.finishUnlock(accountId, temporaryOperation)
        val invalidOperation = (target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready).operationId
        val invalid = target.finishUnlock(accountId, invalidOperation)

        assertEquals(QuickUnlockCompletionResult.TemporarilyUnavailable, temporary)
        assertEquals(QuickUnlockCompletionResult.InvalidEnrollment, invalid)
        assertNull(replacedKek)
        verify(exactly = 1) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `prepareEnrollment_whenConsentUnsupportedExistingOrPending_thenReturnsGuardedResults`() {
        val consentRequired = target.prepareEnrollment(accountId, consentGranted = false)
        every { keyStore.isSupported() } returnsMany listOf(false, true, true)
        val unsupported = target.prepareEnrollment(accountId, consentGranted = true)
        val alreadyEnrolled = target.prepareEnrollment(accountId, consentGranted = true)

        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
        val inProgress = target.prepareEnrollment(accountId, consentGranted = true)

        assertEquals(QuickUnlockEnrollmentPreparationResult.ConsentRequired, consentRequired)
        assertEquals(QuickUnlockEnrollmentPreparationResult.Unsupported, unsupported)
        assertEquals(QuickUnlockEnrollmentPreparationResult.AlreadyEnrolled, alreadyEnrolled)
        assertTrue(prepared is QuickUnlockEnrollmentPreparationResult.Ready)
        assertEquals(QuickUnlockEnrollmentPreparationResult.OperationInProgress, inProgress)
    }

    @Test
    fun `clearAllEnrollments_whenPersistentCleanupFails_thenAttemptsKeystoreCleanup`() {
        every { store.clearAll() } returns false

        val result = target.clearAllEnrollments()

        assertEquals(QuickUnlockCleanupResult.Failed, result)
        verify(exactly = 1) { keyStore.deleteAll() }
    }

    @Test
    fun `markOfferSeen_whenStoreSucceedsOrFails_thenMapsStoreResult`() {
        every { store.markOfferSeen(accountId) } returnsMany listOf(true, false)

        val saved = target.markOfferSeen(accountId)
        val failed = target.markOfferSeen(accountId)

        assertEquals(QuickUnlockStoreResult.Saved, saved)
        assertEquals(QuickUnlockStoreResult.Failed, failed)
    }

    @Test
    fun `prepareEnrollment_whenPendingKekIsMissingOrKeystoreFails_thenReturnsClosedResults`() {
        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { keyMaterialAccess.currentForEnrollment() } returnsMany listOf(
            defaultKek.copyOf(),
            null,
            defaultKek.copyOf(),
            defaultKek.copyOf(),
        )
        every { keyStore.prepareWrap(any(), any()) } returnsMany listOf(
            QuickUnlockKeyStorePrepareResult.Ready,
            QuickUnlockKeyStorePrepareResult.Unsupported,
            QuickUnlockKeyStorePrepareResult.TemporarilyUnavailable,
        )

        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
        val inProgress = target.prepareEnrollment(accountId, consentGranted = true)
        target.cancelUnlock((prepared as QuickUnlockEnrollmentPreparationResult.Ready).operationId)
        val missingKek = target.prepareEnrollment(accountId, consentGranted = true)
        val unsupported = target.prepareEnrollment(accountId, consentGranted = true)
        val temporary = target.prepareEnrollment(accountId, consentGranted = true)

        assertTrue(prepared is QuickUnlockEnrollmentPreparationResult.Ready)
        assertEquals(QuickUnlockEnrollmentPreparationResult.OperationInProgress, inProgress)
        assertEquals(QuickUnlockEnrollmentPreparationResult.RequiresPassphrase, missingKek)
        assertEquals(QuickUnlockEnrollmentPreparationResult.Unsupported, unsupported)
        assertEquals(QuickUnlockEnrollmentPreparationResult.StorageFailure, temporary)
    }

    @Test
    fun `finishEnrollment_whenOperationIsMissingOrPersistenceFails_thenReturnsStorageFailure`() {
        val missingOperation = operationId()
        assertEquals(
            QuickUnlockEnrollmentResult.StorageFailure,
            target.finishEnrollment(accountId, missingOperation),
        )
        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.saveEnvelope(any(), any()) } returns false
        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready

        val result = target.finishEnrollment(accountId, prepared.operationId)

        assertEquals(QuickUnlockEnrollmentResult.StorageFailure, result)
        verify(exactly = 1) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `finishEnrollment_whenPassphraseMaterialChanges_thenRequiresPassphraseBeforeWriting`() {
        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready
        val changedKek = defaultKek.copyOf().also { it[0] = (it[0] + 1).toByte() }
        every { keyMaterialAccess.currentForEnrollment() } returns changedKek

        val result = target.finishEnrollment(accountId, prepared.operationId)

        assertEquals(QuickUnlockEnrollmentResult.RequiresPassphrase, result)
        verify(exactly = 0) { store.saveEnvelope(any(), any()) }
    }

    @Test
    fun `finishEnrollment_whenOperationTypeOrAccountChanges_thenReturnsGuardedResult`() {
        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returnsMany listOf(
            QuickUnlockStoredEnvelope.Present(envelope),
            QuickUnlockStoredEnvelope.Absent,
        )
        val unlock = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready
        val wrongType = target.finishEnrollment(accountId, unlock.operationId)
        val enrollment = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready
        val wrongAccount = target.finishEnrollment(UUID.randomUUID(), enrollment.operationId)

        assertEquals(QuickUnlockEnrollmentResult.StorageFailure, wrongType)
        assertEquals(QuickUnlockEnrollmentResult.RequiresPassphrase, wrongAccount)
    }

    @Test
    fun `finishEnrollment_whenKeystoreReportsUnsupported_thenClearsArtifacts`() {
        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { keyStore.finishWrap(any(), any()) } returns QuickUnlockKeyStoreWrapResult.Unsupported
        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready

        val result = target.finishEnrollment(accountId, prepared.operationId)

        assertEquals(QuickUnlockEnrollmentResult.Unsupported, result)
        verify(exactly = 1) { store.clearEnrollmentArtifact(accountId) }
        verify(exactly = 1) { keyStore.delete(accountId) }
    }

    @Test
    fun `finishUnlock_whenOperationTypeOrAccountChanges_thenReturnsStaleOperation`() {
        every { keyMaterialAccess.provenance() } returns VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returnsMany listOf(
            QuickUnlockStoredEnvelope.Absent,
            QuickUnlockStoredEnvelope.Present(envelope),
        )
        val enrollment = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready
        val wrongType = target.finishUnlock(accountId, enrollment.operationId)
        val unlock = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready
        val wrongAccount = target.finishUnlock(UUID.randomUUID(), unlock.operationId)

        assertEquals(QuickUnlockCompletionResult.StaleOperation, wrongType)
        assertEquals(QuickUnlockCompletionResult.StaleOperation, wrongAccount)
    }

    @Test
    fun `clearEnrollmentAndAllEnrollments_whenCleanupResultsVary_thenMapsEachResult`() {
        val cleared = target.clearEnrollment(accountId)
        every { keyStore.delete(accountId) } returns false
        val failedEnrollment = target.clearEnrollment(accountId)
        every { store.clearAll() } returns true
        every { keyStore.deleteAll() } returns false
        val failedAll = target.clearAllEnrollments()

        assertEquals(QuickUnlockCleanupResult.Cleared, cleared)
        assertEquals(QuickUnlockCleanupResult.Failed, failedEnrollment)
        assertEquals(QuickUnlockCleanupResult.Failed, failedAll)
    }

    private fun validEnvelope(): ByteArray = randomBytes(61).also { it[0] = 0x01 }

    private fun operationId(): String = UUID.randomUUID().toString()

    private fun randomBytes(size: Int): ByteArray = ByteArray(size).also(secureRandom::nextBytes)
}
