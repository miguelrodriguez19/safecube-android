package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import com.miguelrodriguez19.safecube.core.vault.data.session.QuickUnlockKeyMaterialAccess
import com.miguelrodriguez19.safecube.core.vault.domain.model.quickunlock.VaultUnlockProvenance
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCompletionResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult
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

    private class FakeQuickUnlockKeyStore : QuickUnlockKeyStore {
        var wrapResult: QuickUnlockKeyStoreWrapResult = QuickUnlockKeyStoreWrapResult.Failed
        var finishResult: QuickUnlockKeyStoreFinishResult = QuickUnlockKeyStoreFinishResult.AuthenticationFailed
        var aliasPresent: Boolean = true
        var prepareWrapCalls = 0
        var deleteCalls = 0

        override fun isSupported(): Boolean = true

        override fun hasAlias(accountId: UUID): Boolean = aliasPresent

        override fun prepareWrap(accountId: UUID, operationId: String): QuickUnlockKeyStorePrepareResult {
            prepareWrapCalls += 1
            return QuickUnlockKeyStorePrepareResult.Ready
        }

        override fun finishWrap(operationId: String, kek: ByteArray) = wrapResult

        override fun prepareUnwrap(
            accountId: UUID,
            envelope: ByteArray,
            operationId: String,
        ) = QuickUnlockKeyStorePrepareResult.Ready

        override fun finishUnwrap(operationId: String) = finishResult

        override fun cipherFor(operationId: String): Cipher? = null

        override fun cancel(operationId: String) = Unit

        override fun delete(accountId: UUID): Boolean {
            deleteCalls += 1
            return true
        }

        override fun deleteAll(): Boolean = true
    }

    private class FakeQuickUnlockKeyMaterialAccess : QuickUnlockKeyMaterialAccess {
        var unlockProvenance = VaultUnlockProvenance.None
        var kek = ByteArray(32) { 3 }
        var replacedKek: ByteArray? = null

        override fun currentForEnrollment(): ByteArray = kek.copyOf()

        override fun provenance(): VaultUnlockProvenance = unlockProvenance

        override fun replaceAfterQuickUnlock(kek: ByteArray) {
            replacedKek = kek.copyOf()
        }
    }
}
