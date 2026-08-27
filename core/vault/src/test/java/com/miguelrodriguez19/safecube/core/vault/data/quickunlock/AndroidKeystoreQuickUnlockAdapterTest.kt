package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class AndroidKeystoreQuickUnlockAdapterTest {
    private val platform = mockk<QuickUnlockAndroidKeyStorePlatform>()
    private val envelopeCodec = QuickUnlockEnvelopeCodec()
    private lateinit var target: AndroidKeystoreQuickUnlockAdapter
    private val accountId = UUID.randomUUID()
    private val key = generateKey()
    private val secureRandom = SecureRandom()

    @Before
    fun setUp() {
        target = AndroidKeystoreQuickUnlockAdapter(platform, envelopeCodec)

        every { platform.isSupported() } returns true
        every { platform.hasAlias(any()) } returns false
        every { platform.createKey(any()) } returns key
        every { platform.loadKey(any()) } returns key
        every { platform.delete(any()) } returns true
        every { platform.deleteAll() } returns true
    }

    @Test
    fun `prepareWrap_whenPlatformUnsupported_thenReturnsUnsupported`() {
        every { platform.isSupported() } returns false
        val operationId = operationId()

        val result = target.prepareWrap(accountId, operationId)

        assertEquals(QuickUnlockKeyStorePrepareResult.Unsupported, result)
        verify(exactly = 0) { platform.hasAlias(any()) }
    }

    @Test
    fun `prepareWrap_whenAliasAlreadyExists_thenReturnsInvalidEnrollment`() {
        every { platform.hasAlias(accountId) } returns true
        val operationId = operationId()

        val result = target.prepareWrap(accountId, operationId)

        assertEquals(QuickUnlockKeyStorePrepareResult.InvalidEnrollment, result)
        verify(exactly = 0) { platform.createKey(any()) }
    }

    @Test
    fun `prepareWrap_whenPlatformFailsToCreateKey_thenDeletesAliasAndReturnsTemporaryUnavailable`() {
        every { platform.createKey(accountId) } throws IllegalStateException()
        val operationId = operationId()

        val result = target.prepareWrap(accountId, operationId)

        assertEquals(QuickUnlockKeyStorePrepareResult.TemporarilyUnavailable, result)
        verify(exactly = 1) { platform.delete(accountId) }
    }

    @Test
    fun `prepareWrap_whenPlatformIsReady_thenStoresCipherForOperation`() {
        val operationId = operationId()

        val result = target.prepareWrap(accountId, operationId)

        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, result)
        assertTrue(target.cipherFor(operationId) != null)
    }

    @Test
    fun `finishWrap_whenAuthenticatedCipherIsAccepted_thenReturnsEnvelope`() {
        val kek = randomBytes(32)
        val operationId = operationId()
        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareWrap(accountId, operationId))
        val cipher = requireNotNull(target.cipherFor(operationId))
        assertTrue(target.acceptAuthenticatedCipher(operationId, cipher))

        val result = target.finishWrap(operationId, kek)

        assertTrue(result is QuickUnlockKeyStoreWrapResult.Success)
        val envelope = (result as QuickUnlockKeyStoreWrapResult.Success).envelope
        assertTrue(envelopeCodec.decode(envelope) is QuickUnlockEnvelopeDecodeResult.Valid)
    }

    @Test
    fun `finishWrap_whenKekLengthIsInvalid_thenReturnsFailed`() {
        val operationId = operationId()
        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareWrap(accountId, operationId))
        val cipher = requireNotNull(target.cipherFor(operationId))
        assertTrue(target.acceptAuthenticatedCipher(operationId, cipher))

        val result = target.finishWrap(operationId, randomBytes(31))

        assertEquals(QuickUnlockKeyStoreWrapResult.Failed, result)
    }

    @Test
    fun `prepareUnwrap_whenEnvelopeIsMalformed_thenReturnsInvalidEnrollment`() {
        val operationId = operationId()

        val result = target.prepareUnwrap(accountId, randomBytes(60), operationId)

        assertEquals(QuickUnlockKeyStorePrepareResult.InvalidEnrollment, result)
        verify(exactly = 0) { platform.loadKey(any()) }
    }

    @Test
    fun `prepareUnwrap_whenKeyIsMissing_thenReturnsInvalidEnrollment`() {
        every { platform.loadKey(accountId) } returns null
        val operationId = operationId()

        val result = target.prepareUnwrap(accountId, validEnvelope(), operationId)

        assertEquals(QuickUnlockKeyStorePrepareResult.InvalidEnrollment, result)
    }

    @Test
    fun `prepareUnwrap_whenPlatformUnsupported_thenReturnsUnsupported`() {
        every { platform.isSupported() } returns false
        val operationId = operationId()

        val result = target.prepareUnwrap(accountId, validEnvelope(), operationId)

        assertEquals(QuickUnlockKeyStorePrepareResult.Unsupported, result)
        verify(exactly = 0) { platform.loadKey(any()) }
    }

    @Test
    fun `finishUnwrap_whenAuthenticatedCipherIsAccepted_thenReturnsKek`() {
        val expectedKek = randomBytes(32)
        val operationId = operationId()
        val envelope = envelopeFor(expectedKek)
        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareUnwrap(accountId, envelope, operationId))
        val cipher = requireNotNull(target.cipherFor(operationId))
        assertTrue(target.acceptAuthenticatedCipher(operationId, cipher))

        val result = target.finishUnwrap(operationId) as QuickUnlockKeyStoreFinishResult.Success

        assertArrayEquals(expectedKek, result.kek)
        result.kek.fill(0)
    }

    @Test
    fun `finishUnwrap_whenCiphertextIsTampered_thenReturnsInvalidEnrollment`() {
        val operationId = operationId()
        val envelope = envelopeFor(randomBytes(32)).also {
            it[it.lastIndex] = (it.last() + 1).toByte()
        }
        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareUnwrap(accountId, envelope, operationId))
        val cipher = requireNotNull(target.cipherFor(operationId))
        assertTrue(target.acceptAuthenticatedCipher(operationId, cipher))

        val result = target.finishUnwrap(operationId)

        assertEquals(QuickUnlockKeyStoreFinishResult.InvalidEnrollment, result)
    }

    @Test
    fun `cipherFor_whenOperationIsUnknown_thenReturnsNull`() {
        val result = target.cipherFor(operationId())

        assertNull(result)
    }

    @Test
    fun `acceptAuthenticatedCipher_whenOperationIsUnknown_thenReturnsFalse`() {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        val result = target.acceptAuthenticatedCipher(operationId(), cipher)

        assertFalse(result)
    }

    @Test
    fun `acceptAuthenticatedCipher_whenCipherIsNull_thenReturnsFalse`() {
        val operationId = operationId()
        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareWrap(accountId, operationId))

        val result = target.acceptAuthenticatedCipher(operationId, null)

        assertFalse(result)
    }

    @Test
    fun `acceptAuthenticatedCipher_whenOperationIsCancelled_thenReturnsFalse`() {
        val operationId = operationId()
        assertEquals(QuickUnlockKeyStorePrepareResult.Ready, target.prepareWrap(accountId, operationId))
        val cipher = requireNotNull(target.cipherFor(operationId))
        target.cancel(operationId)

        val result = target.acceptAuthenticatedCipher(operationId, cipher)

        assertFalse(result)
    }

    @Test
    fun `finishWrap_whenOperationIsUnknown_thenReturnsFailed`() {
        val result = target.finishWrap(operationId(), randomBytes(32))

        assertEquals(QuickUnlockKeyStoreWrapResult.Failed, result)
    }

    @Test
    fun `finishUnwrap_whenOperationIsUnknown_thenReturnsAuthenticationFailed`() {
        val result = target.finishUnwrap(operationId())

        assertEquals(QuickUnlockKeyStoreFinishResult.AuthenticationFailed, result)
    }

    @Test
    fun `hasAlias_whenPlatformInspectionFails_thenReturnsFalse`() {
        every { platform.hasAlias(accountId) } throws IllegalStateException()

        val result = target.hasAlias(accountId)

        assertFalse(result)
    }

    @Test
    fun `delete_whenPlatformReturnsFalse_thenReturnsFalse`() {
        every { platform.delete(accountId) } returns false

        val result = target.delete(accountId)

        assertFalse(result)
        verify(exactly = 1) { platform.delete(accountId) }
    }

    @Test
    fun `deleteAll_whenPlatformReturnsFalse_thenReturnsFalse`() {
        every { platform.deleteAll() } returns false

        val result = target.deleteAll()

        assertFalse(result)
        verify(exactly = 1) { platform.deleteAll() }
    }

    private fun envelopeFor(kek: ByteArray): ByteArray {
        val nonce = randomBytes(12)
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(
            Cipher.ENCRYPT_MODE,
            key,
            GCMParameterSpec(128, nonce),
        )
        cipher.updateAAD(QuickUnlockAliasFactory.aadFor(accountId))
        return requireNotNull(envelopeCodec.encode(nonce, cipher.doFinal(kek)))
    }

    private fun validEnvelope(): ByteArray = requireNotNull(
        envelopeCodec.encode(randomBytes(12), randomBytes(48)),
    )

    private fun operationId(): String = UUID.randomUUID().toString()

    private fun randomBytes(size: Int): ByteArray = ByteArray(size).also(secureRandom::nextBytes)

    private companion object {
        fun generateKey(): SecretKey = KeyGenerator.getInstance("AES").apply { init(256) }.generateKey()
    }
}
