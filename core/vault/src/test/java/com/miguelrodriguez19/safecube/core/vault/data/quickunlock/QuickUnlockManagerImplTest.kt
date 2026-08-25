package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import com.miguelrodriguez19.safecube.core.vault.data.session.QuickUnlockKeyMaterialAccess
import com.miguelrodriguez19.safecube.core.vault.domain.model.quickunlock.VaultUnlockProvenance
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCompletionResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import javax.crypto.Cipher
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QuickUnlockManagerImplTest {
    private val store = mockk<QuickUnlockStore>(relaxed = true)
    private val keyStore = FakeQuickUnlockKeyStore()
    private val keyMaterialAccess = FakeQuickUnlockKeyMaterialAccess()
    private val target = QuickUnlockManagerImpl(store, keyStore, keyMaterialAccess, QuickUnlockEnvelopeCodec())
    private val accountId = UUID.fromString("aa3e81ea-9253-4d75-ae46-5640f2738b4a")
    private val envelope = ByteArray(61) { it.toByte() }.also { it[0] = 0x01 }

    @Test
    fun `prepare and finish unlock when enrollment is unchanged stores kek through internal sink`() {
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Present(envelope)
        keyStore.finishResult = QuickUnlockKeyStoreFinishResult.Success(ByteArray(32) { 7 })

        val prepared = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready
        val result = target.finishUnlock(accountId, prepared.operationId)

        assertEquals(QuickUnlockCompletionResult.Unlocked, result)
        assertTrue(keyMaterialAccess.replacedKek!!.all { it == 7.toByte() })
    }

    @Test
    fun `finish unlock when envelope changed rejects stale callback without accepting kek`() {
        every { store.readEnvelope(accountId) } returnsMany listOf(
            QuickUnlockStoredEnvelope.Present(envelope),
            QuickUnlockStoredEnvelope.Absent,
        )
        val prepared = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready

        val result = target.finishUnlock(accountId, prepared.operationId)

        assertEquals(QuickUnlockCompletionResult.StaleOperation, result)
        assertEquals(null, keyMaterialAccess.replacedKek)
    }

    @Test
    fun `finish unlock when authentication fails preserves enrollment`() {
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Present(envelope)
        keyStore.finishResult = QuickUnlockKeyStoreFinishResult.AuthenticationFailed
        val prepared = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready

        val result = target.finishUnlock(accountId, prepared.operationId)

        assertEquals(QuickUnlockCompletionResult.AuthenticationFailed, result)
        verify(exactly = 0) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `prepare unlock when operation for account is pending is single flight`() {
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Present(envelope)

        val first = target.prepareUnlock(accountId)
        val second = target.prepareUnlock(accountId)

        assertTrue(first is QuickUnlockPreparationResult.Ready)
        assertEquals(QuickUnlockPreparationResult.OperationInProgress, second)
    }

    @Test
    fun `offer state when alias is absent clears invalid enrollment`() {
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Present(envelope)
        keyStore.aliasPresent = false

        val result = target.offerState(accountId)

        assertEquals(com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState.InvalidEnrollment, result)
        verify(exactly = 1) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `prepare enrollment without passphrase provenance requires passphrase`() {
        keyMaterialAccess.unlockProvenance = VaultUnlockProvenance.RecoveryKey

        val result = target.prepareEnrollment(accountId, consentGranted = true)

        assertEquals(QuickUnlockEnrollmentPreparationResult.RequiresPassphrase, result)
    }

    @Test
    fun `prepare and finish enrollment only saves authenticated envelope`() {
        keyMaterialAccess.unlockProvenance = VaultUnlockProvenance.Passphrase
        keyStore.wrapResult = QuickUnlockKeyStoreWrapResult.Success(envelope)
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.markOfferSeen(accountId) } returns true
        every { store.saveEnvelope(accountId, envelope) } returns true

        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready
        val result = target.finishEnrollment(accountId, prepared.operationId)

        assertEquals(QuickUnlockEnrollmentResult.Enrolled, result)
        verify(exactly = 1) { store.saveEnvelope(accountId, envelope) }
    }

    @Test
    fun `prepare enrollment whenofferSeenCannotPersist doesNotCreateKey`() {
        keyMaterialAccess.unlockProvenance = VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.markOfferSeen(accountId) } returns false

        val result = target.prepareEnrollment(accountId, consentGranted = true)

        assertEquals(QuickUnlockEnrollmentPreparationResult.StorageFailure, result)
        assertEquals(0, keyStore.prepareWrapCalls)
    }

    @Test
    fun `cancel enrollment keeps offerSeen and removes newly created alias`() {
        keyMaterialAccess.unlockProvenance = VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.markOfferSeen(accountId) } returns true

        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready
        target.cancelUnlock(prepared.operationId)

        verify(exactly = 1) { store.markOfferSeen(accountId) }
        assertEquals(1, keyStore.deleteCalls)
    }

    @Test
    fun `clear enrollment when artifact cleanup fails still deletes alias and reports failure`() {
        every { store.clearEnrollmentArtifact(accountId) } returns false

        val result = target.clearEnrollment(accountId)

        assertEquals(QuickUnlockCleanupResult.Failed, result)
        assertEquals(1, keyStore.deleteCalls)
    }

    @Test
    fun `finish enrollment when keystore reports authentication failure clears partial artifact`() {
        keyMaterialAccess.unlockProvenance = VaultUnlockProvenance.Passphrase
        keyStore.wrapResult = QuickUnlockKeyStoreWrapResult.Failed
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.markOfferSeen(accountId) } returns true

        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready
        val result = target.finishEnrollment(accountId, prepared.operationId)

        assertEquals(QuickUnlockEnrollmentResult.StorageFailure, result)
        verify(exactly = 1) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `offer state distinguishes unsupported seen and corrupt enrollment`() {
        keyStore.supported = false
        assertEquals(QuickUnlockOfferState.Unsupported, target.offerState(accountId))
        keyStore.supported = true
        keyStore.aliasPresent = false
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.hasSeenOffer(accountId) } returns true
        assertEquals(QuickUnlockOfferState.Seen, target.offerState(accountId))
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Corrupted

        assertEquals(QuickUnlockOfferState.InvalidEnrollment, target.offerState(accountId))
        verify(exactly = 1) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `offer state distinguishes available enrolled and orphaned aliases`() {
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.hasSeenOffer(accountId) } returns false
        keyStore.aliasPresent = false

        assertEquals(QuickUnlockOfferState.Available, target.offerState(accountId))

        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Present(envelope)
        keyStore.aliasPresent = true
        assertEquals(QuickUnlockOfferState.Enrolled, target.offerState(accountId))

        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        assertEquals(QuickUnlockOfferState.InvalidEnrollment, target.offerState(accountId))
        assertEquals(1, keyStore.deleteCalls)
    }

    @Test
    fun `prepare unlock reports absent temporary and invalid enrollments without accepting kek`() {
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        assertEquals(QuickUnlockPreparationResult.NotEnrolled, target.prepareUnlock(accountId))
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Present(envelope)
        keyStore.prepareUnwrapResult = QuickUnlockKeyStorePrepareResult.Unsupported
        assertEquals(QuickUnlockPreparationResult.Unsupported, target.prepareUnlock(accountId))
        keyStore.prepareUnwrapResult = QuickUnlockKeyStorePrepareResult.TemporarilyUnavailable
        assertEquals(QuickUnlockPreparationResult.TemporarilyUnavailable, target.prepareUnlock(accountId))
        keyStore.prepareUnwrapResult = QuickUnlockKeyStorePrepareResult.InvalidEnrollment

        assertEquals(QuickUnlockPreparationResult.InvalidEnrollment, target.prepareUnlock(accountId))
        verify(atLeast = 1) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `finish unlock reports temporary and invalid keystore outcomes without installing kek`() {
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Present(envelope)
        keyStore.finishResult = QuickUnlockKeyStoreFinishResult.TemporarilyUnavailable
        val temporary = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready

        assertEquals(QuickUnlockCompletionResult.TemporarilyUnavailable, target.finishUnlock(accountId, temporary.operationId))
        keyStore.finishResult = QuickUnlockKeyStoreFinishResult.InvalidEnrollment
        val invalid = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready

        assertEquals(QuickUnlockCompletionResult.InvalidEnrollment, target.finishUnlock(accountId, invalid.operationId))
        assertEquals(null, keyMaterialAccess.replacedKek)
        verify(atLeast = 1) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `prepare enrollment rejects consent unsupported existing and pending account operations`() {
        assertEquals(
            QuickUnlockEnrollmentPreparationResult.ConsentRequired,
            target.prepareEnrollment(accountId, consentGranted = false),
        )
        keyStore.supported = false
        assertEquals(
            QuickUnlockEnrollmentPreparationResult.Unsupported,
            target.prepareEnrollment(accountId, consentGranted = true),
        )
        keyStore.supported = true
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Present(envelope)
        assertEquals(
            QuickUnlockEnrollmentPreparationResult.AlreadyEnrolled,
            target.prepareEnrollment(accountId, consentGranted = true),
        )
    }

    @Test
    fun `clear all reports failure but attempts both persistent and keystore cleanup`() {
        every { store.clearAll() } returns false

        val result = target.clearAllEnrollments()

        assertEquals(QuickUnlockCleanupResult.Failed, result)
        assertEquals(1, keyStore.deleteAllCalls)
    }

    @Test
    fun `mark offer returns saved and failed according to durable store result`() {
        every { store.markOfferSeen(accountId) } returns true
        assertEquals(com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockStoreResult.Saved, target.markOfferSeen(accountId))
        every { store.markOfferSeen(accountId) } returns false
        assertEquals(com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockStoreResult.Failed, target.markOfferSeen(accountId))
    }

    @Test
    fun `prepare enrollment handles pending null kek and all keystore prepare failures`() {
        keyMaterialAccess.unlockProvenance = VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.markOfferSeen(accountId) } returns true
        val first = target.prepareEnrollment(accountId, true) as QuickUnlockEnrollmentPreparationResult.Ready
        assertEquals(QuickUnlockEnrollmentPreparationResult.OperationInProgress, target.prepareEnrollment(accountId, true))
        target.cancelUnlock(first.operationId)
        keyMaterialAccess.kek = ByteArray(0)
        assertEquals(QuickUnlockEnrollmentPreparationResult.RequiresPassphrase, target.prepareEnrollment(accountId, true))
        keyMaterialAccess.kek = ByteArray(32) { 3 }
        keyStore.prepareWrapResult = QuickUnlockKeyStorePrepareResult.Unsupported
        assertEquals(QuickUnlockEnrollmentPreparationResult.Unsupported, target.prepareEnrollment(accountId, true))
        keyStore.prepareWrapResult = QuickUnlockKeyStorePrepareResult.TemporarilyUnavailable
        assertEquals(QuickUnlockEnrollmentPreparationResult.StorageFailure, target.prepareEnrollment(accountId, true))
    }

    @Test
    fun `finish enrollment reports missing operation and persistence failure`() {
        assertEquals(QuickUnlockEnrollmentResult.StorageFailure, target.finishEnrollment(accountId, "missing"))
        keyMaterialAccess.unlockProvenance = VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.markOfferSeen(accountId) } returns true
        every { store.saveEnvelope(accountId, any()) } returns false
        keyStore.wrapResult = QuickUnlockKeyStoreWrapResult.Success(envelope)
        val prepared = target.prepareEnrollment(accountId, true) as QuickUnlockEnrollmentPreparationResult.Ready

        assertEquals(QuickUnlockEnrollmentResult.StorageFailure, target.finishEnrollment(accountId, prepared.operationId))
        verify(atLeast = 1) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `finish enrollment rejects changed passphrase material before writing envelope`() {
        keyMaterialAccess.unlockProvenance = VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.markOfferSeen(accountId) } returns true
        val prepared = target.prepareEnrollment(accountId, consentGranted = true)
            as QuickUnlockEnrollmentPreparationResult.Ready
        keyMaterialAccess.kek = ByteArray(32) { 9 }

        val result = target.finishEnrollment(accountId, prepared.operationId)

        assertEquals(QuickUnlockEnrollmentResult.RequiresPassphrase, result)
        verify(exactly = 0) { store.saveEnvelope(any(), any()) }
    }

    @Test
    fun `finish enrollment rejects missing wrong type and changed account callbacks`() {
        assertEquals(QuickUnlockEnrollmentResult.StorageFailure, target.finishEnrollment(accountId, "missing"))
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Present(envelope)
        val unlock = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready

        assertEquals(QuickUnlockEnrollmentResult.StorageFailure, target.finishEnrollment(accountId, unlock.operationId))

        keyMaterialAccess.unlockProvenance = VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.markOfferSeen(accountId) } returns true
        val enrollment = target.prepareEnrollment(accountId, true) as QuickUnlockEnrollmentPreparationResult.Ready

        assertEquals(
            QuickUnlockEnrollmentResult.RequiresPassphrase,
            target.finishEnrollment(UUID.randomUUID(), enrollment.operationId),
        )
    }

    @Test
    fun `finish enrollment maps unsupported keystore result and clears artifacts`() {
        keyMaterialAccess.unlockProvenance = VaultUnlockProvenance.Passphrase
        keyStore.wrapResult = QuickUnlockKeyStoreWrapResult.Unsupported
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.markOfferSeen(accountId) } returns true
        val prepared = target.prepareEnrollment(accountId, true) as QuickUnlockEnrollmentPreparationResult.Ready

        assertEquals(QuickUnlockEnrollmentResult.Unsupported, target.finishEnrollment(accountId, prepared.operationId))
        verify(exactly = 1) { store.clearEnrollmentArtifact(accountId) }
    }

    @Test
    fun `finish unlock rejects wrong type and account callbacks`() {
        keyMaterialAccess.unlockProvenance = VaultUnlockProvenance.Passphrase
        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Absent
        every { store.markOfferSeen(accountId) } returns true
        val enrollment = target.prepareEnrollment(accountId, true) as QuickUnlockEnrollmentPreparationResult.Ready

        assertEquals(QuickUnlockCompletionResult.StaleOperation, target.finishUnlock(accountId, enrollment.operationId))

        every { store.readEnvelope(accountId) } returns QuickUnlockStoredEnvelope.Present(envelope)
        val unlock = target.prepareUnlock(accountId) as QuickUnlockPreparationResult.Ready
        assertEquals(QuickUnlockCompletionResult.StaleOperation, target.finishUnlock(UUID.randomUUID(), unlock.operationId))
    }

    @Test
    fun `clear enrollment and all report both successful and failed cleanup combinations`() {
        every { store.clearEnrollmentArtifact(accountId) } returns true
        keyStore.deleteResult = true
        assertEquals(QuickUnlockCleanupResult.Cleared, target.clearEnrollment(accountId))

        keyStore.deleteResult = false
        assertEquals(QuickUnlockCleanupResult.Failed, target.clearEnrollment(accountId))

        every { store.clearAll() } returns true
        keyStore.deleteAllResult = false
        assertEquals(QuickUnlockCleanupResult.Failed, target.clearAllEnrollments())
    }

    private class FakeQuickUnlockKeyStore : QuickUnlockKeyStore {
        var supported = true
        var wrapResult: QuickUnlockKeyStoreWrapResult = QuickUnlockKeyStoreWrapResult.Failed
        var finishResult: QuickUnlockKeyStoreFinishResult = QuickUnlockKeyStoreFinishResult.AuthenticationFailed
        var prepareUnwrapResult: QuickUnlockKeyStorePrepareResult = QuickUnlockKeyStorePrepareResult.Ready
        var aliasPresent: Boolean = true
        var prepareWrapCalls = 0
        var prepareWrapResult: QuickUnlockKeyStorePrepareResult = QuickUnlockKeyStorePrepareResult.Ready
        var deleteCalls = 0
        var deleteAllCalls = 0
        var deleteResult = true
        var deleteAllResult = true

        override fun isSupported(): Boolean = supported

        override fun hasAlias(accountId: UUID): Boolean = aliasPresent

        override fun prepareWrap(accountId: UUID, operationId: String): QuickUnlockKeyStorePrepareResult {
            prepareWrapCalls += 1
            return prepareWrapResult
        }

        override fun finishWrap(operationId: String, kek: ByteArray) = wrapResult

        override fun prepareUnwrap(
            accountId: UUID,
            envelope: ByteArray,
            operationId: String,
        ) = prepareUnwrapResult

        override fun finishUnwrap(operationId: String) = finishResult

        override fun cipherFor(operationId: String): Cipher? = null

        override fun acceptAuthenticatedCipher(operationId: String, cipher: Cipher?): Boolean = true

        override fun cancel(operationId: String) = Unit

        override fun delete(accountId: UUID): Boolean {
            deleteCalls += 1
            return deleteResult
        }

        override fun deleteAll(): Boolean {
            deleteAllCalls += 1
            return deleteAllResult
        }
    }

    private class FakeQuickUnlockKeyMaterialAccess : QuickUnlockKeyMaterialAccess {
        var unlockProvenance = VaultUnlockProvenance.None
        var kek = ByteArray(32) { 3 }
        var replacedKek: ByteArray? = null

        override fun currentForEnrollment(): ByteArray? = kek.takeIf { it.isNotEmpty() }?.copyOf()

        override fun provenance(): VaultUnlockProvenance = unlockProvenance

        override fun replaceAfterQuickUnlock(kek: ByteArray) {
            replacedKek = kek.copyOf()
        }
    }
}
