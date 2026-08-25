package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import java.util.UUID
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class AndroidKeystoreQuickUnlockAdapterTest {
    private val platform = FakePlatform()
    private val target = AndroidKeystoreQuickUnlockAdapter(platform, QuickUnlockEnvelopeCodec())
    private val accountId = UUID.fromString("b7c29b86-c5e0-4a53-a29a-b7fc97ec3f1b")

    @Test
    fun `wrap then unwrap round trips kek using the authenticated cipher lifecycle`() {
        val kek = ByteArray(32) { it.toByte() }
        val wrapOperation = "wrap"

        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareWrap(accountId, wrapOperation))
        assertNotNull(target.cipherFor(wrapOperation))
        assertTrue(target.acceptAuthenticatedCipher(wrapOperation, target.cipherFor(wrapOperation)))
        val envelope = (target.finishWrap(wrapOperation, kek) as QuickUnlockKeyStoreWrapResult.Success).envelope
        val unwrapOperation = "unwrap"

        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareUnwrap(accountId, envelope, unwrapOperation))
        assertTrue(target.acceptAuthenticatedCipher(unwrapOperation, target.cipherFor(unwrapOperation)))
        val result = target.finishUnwrap(unwrapOperation) as QuickUnlockKeyStoreFinishResult.Success

        assertArrayEquals(kek, result.kek)
        result.kek.fill(0)
    }

    @Test
    fun `prepare wrap when unsupported or alias exists returns closed result`() {
        platform.supported = false
        assertEquals(QuickUnlockKeyStorePrepareResult.Unsupported, target.prepareWrap(accountId, "first"))
        platform.supported = true
        platform.keys[accountId] = generateKey()

        assertEquals(QuickUnlockKeyStorePrepareResult.InvalidEnrollment, target.prepareWrap(accountId, "second"))
    }

    @Test
    fun `prepare unwrap when envelope corrupted or key missing fails closed`() {
        assertEquals(
            QuickUnlockKeyStorePrepareResult.InvalidEnrollment,
            target.prepareUnwrap(accountId, ByteArray(61), "bad"),
        )
        val envelope = ByteArray(61).also { it[0] = 0x01 }

        assertEquals(
            QuickUnlockKeyStorePrepareResult.InvalidEnrollment,
            target.prepareUnwrap(accountId, envelope, "missing"),
        )
    }

    @Test
    fun `accept authenticated cipher rejects missing operation or cipher`() {
        assertFalse(target.acceptAuthenticatedCipher("missing", null))
        assertFalse(target.acceptAuthenticatedCipher("missing", javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")))

        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareWrap(accountId, "wrap"))
        assertFalse(target.acceptAuthenticatedCipher("wrap", null))
        target.cancel("wrap")
        assertFalse(target.acceptAuthenticatedCipher("wrap", javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")))
    }

    @Test
    fun `finish operations with no pending cipher fail closed`() {
        assertNull(target.cipherFor("missing"))
        assertEquals(QuickUnlockKeyStoreWrapResult.Failed, target.finishWrap("missing", ByteArray(32)))
        assertEquals(QuickUnlockKeyStoreFinishResult.AuthenticationFailed, target.finishUnwrap("missing"))
    }

    @Test
    fun `has alias fails closed when the platform cannot inspect the keystore`() {
        platform.failHasAlias = true

        assertFalse(target.hasAlias(accountId))
    }

    @Test
    fun `finish wrap rejects a kek with an invalid envelope length`() {
        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareWrap(accountId, "bad-kek"))

        assertEquals(QuickUnlockKeyStoreWrapResult.Failed, target.finishWrap("bad-kek", ByteArray(31)))
    }

    @Test
    fun `finish unwrap rejects an authenticated envelope with corrupted ciphertext`() {
        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareWrap(accountId, "seed"))
        val key = requireNotNull(platform.keys[accountId])
        target.cancel("seed")
        val nonce = ByteArray(12) { it.toByte() }
        val cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            javax.crypto.Cipher.ENCRYPT_MODE,
            key,
            javax.crypto.spec.GCMParameterSpec(128, nonce),
        )
        cipher.updateAAD(QuickUnlockAliasFactory.aadFor(accountId))
        val ciphertextAndTag = cipher.doFinal(ByteArray(32)).also { it[it.lastIndex] = (it.last() + 1).toByte() }
        val envelope = requireNotNull(QuickUnlockEnvelopeCodec().encode(nonce, ciphertextAndTag))

        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareUnwrap(accountId, envelope, "short-kek"))
        assertEquals(QuickUnlockKeyStoreFinishResult.InvalidEnrollment, target.finishUnwrap("short-kek"))
    }

    @Test
    fun `prepare wrap cleans up key after platform failure and delegated deletion results are retained`() {
        platform.failCreate = true

        assertEquals(QuickUnlockKeyStorePrepareResult.TemporarilyUnavailable, target.prepareWrap(accountId, "wrap"))
        assertEquals(1, platform.deleteCalls)
        platform.deleteResult = false
        assertFalse(target.delete(accountId))
        platform.deleteAllResult = false
        assertFalse(target.deleteAll())
    }

    @Test
    fun `prepare unwrap maps unsupported and missing key to closed results`() {
        platform.supported = false
        assertEquals(QuickUnlockKeyStorePrepareResult.Unsupported, target.prepareUnwrap(accountId, validEnvelope(), "unsupported"))
        platform.supported = true

        assertEquals(QuickUnlockKeyStorePrepareResult.InvalidEnrollment, target.prepareUnwrap(accountId, validEnvelope(), "missing"))
    }

    private class FakePlatform : QuickUnlockAndroidKeyStorePlatform {
        var supported = true
        val keys = mutableMapOf<UUID, SecretKey>()
        var failHasAlias = false
        var failCreate = false
        var deleteResult = true
        var deleteAllResult = true
        var deleteCalls = 0

        override fun isSupported(): Boolean = supported

        override fun createKey(accountId: UUID): SecretKey {
            check(!failCreate)
            return generateKey().also { keys[accountId] = it }
        }

        override fun loadKey(accountId: UUID): SecretKey? = keys[accountId]

        override fun hasAlias(accountId: UUID): Boolean {
            check(!failHasAlias)
            return keys.containsKey(accountId)
        }

        override fun delete(accountId: UUID): Boolean {
            deleteCalls += 1
            keys.remove(accountId)
            return deleteResult
        }

        override fun deleteAll(): Boolean {
            keys.clear()
            return deleteAllResult
        }
    }

    private companion object {
        fun generateKey(): SecretKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()

        fun validEnvelope(): ByteArray = ByteArray(61).also { it[0] = 0x01 }
    }
}
