package com.miguelrodriguez19.safecube.core.vault.data.local

import android.content.SharedPreferences
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitialization
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitializationState
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationReadResult
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PendingVaultInitializationStoreTest {
    private val encryptedPreferences = mockk<SharedPreferences>()
    private val editor = mockk<SharedPreferences.Editor>()
    private val codec = PendingVaultInitializationCodec()
    private val target = PendingVaultInitializationStore(encryptedPreferences, codec)
    private var storedValue: String? = null

    @Before
    fun setUp() {
        every { encryptedPreferences.getString(any(), null) } answers { storedValue }
        every { encryptedPreferences.edit() } returns editor
        every { editor.putString(any(), any()) } answers {
            storedValue = secondArg()
            editor
        }
        every { editor.remove(any()) } answers {
            storedValue = null
            editor
        }
        every { editor.commit() } returns true
    }

    @Test
    fun `read returns empty when encrypted record is absent`() {
        assertEquals(PendingVaultInitializationReadResult.Empty, target.read())
    }

    @Test
    fun `read returns present when encrypted record is valid`() {
        val value = samplePendingInitialization()
        storedValue = codec.encode(value)

        val result = target.read()

        assertTrue(result is PendingVaultInitializationReadResult.Present)
        val decoded = (result as PendingVaultInitializationReadResult.Present).value
        assertEquals(value.state, decoded.state)
        assertEquals(value.candidate.accountId, decoded.candidate.accountId)
        assertArrayEquals(value.candidate.kekEncMaster, decoded.candidate.kekEncMaster)
        assertArrayEquals(value.candidate.kekEncRecovery, decoded.candidate.kekEncRecovery)
        assertArrayEquals(value.candidate.kdfSalt, decoded.candidate.kdfSalt)
        assertArrayEquals(value.recoveryKey, decoded.recoveryKey)
    }

    @Test
    fun `read returns corrupted when encrypted record cannot be decoded`() {
        storedValue = "not-a-pending-record"

        assertEquals(PendingVaultInitializationReadResult.Corrupted, target.read())
    }

    @Test
    fun `save commits and verifies the encrypted record`() {
        assertTrue(target.save(samplePendingInitialization()))
    }

    @Test
    fun `save returns false when commit fails`() {
        every { editor.commit() } returns false

        assertFalse(target.save(samplePendingInitialization()))
    }

    @Test
    fun `save returns false when readback is absent`() {
        every { editor.putString(any(), any()) } returns editor

        assertFalse(target.save(samplePendingInitialization()))
    }

    @Test
    fun `save returns false when readback is corrupted`() {
        every { editor.putString(any(), any()) } returns editor
        storedValue = "corrupted"

        assertFalse(target.save(samplePendingInitialization()))
    }

    @Test
    fun `save rejects every changed field during readback verification`() {
        val value = samplePendingInitialization()
        every { editor.putString(any(), any()) } returns editor

        val variants = listOf(
            value.copy(state = PendingVaultInitializationState.RemoteConfirmed),
            value.copy(candidate = value.candidate.copy(accountId = UUID.randomUUID())),
            value.copy(candidate = value.candidate.copy(kekEncMaster = byteArrayOf(21, 22, 23))),
            value.copy(candidate = value.candidate.copy(kekEncRecovery = byteArrayOf(24, 25, 26))),
            value.copy(candidate = value.candidate.copy(kdfAlgorithm = "other")),
            value.copy(candidate = value.candidate.copy(kdfSalt = byteArrayOf(27, 28, 29))),
            value.copy(candidate = value.candidate.copy(kdfMemoryKib = 2)),
            value.copy(candidate = value.candidate.copy(kdfIterations = 4)),
            value.copy(candidate = value.candidate.copy(kdfParallelism = 2)),
            value.copy(candidate = value.candidate.copy(kdfOutputLen = 16)),
            value.copy(candidate = value.candidate.copy(cryptoVersion = "other")),
            value.copy(recoveryKey = byteArrayOf(30, 31, 32)),
        )

        variants.forEach { variant ->
            storedValue = codec.encode(variant)
            assertFalse(target.save(value))
        }
    }

    @Test
    fun `clear returns true only after committed removal is observed`() {
        storedValue = codec.encode(samplePendingInitialization())

        assertTrue(target.clear())
    }

    @Test
    fun `clear returns false when commit fails`() {
        every { editor.commit() } returns false

        assertFalse(target.clear())
    }

    @Test
    fun `clear returns false when record remains after commit`() {
        every { editor.remove(any()) } returns editor
        storedValue = codec.encode(samplePendingInitialization())

        assertFalse(target.clear())
    }

    private fun samplePendingInitialization() = PendingVaultInitialization(
        candidate = VaultKeyMaterial(
            accountId = UUID.randomUUID(),
            kekEncMaster = byteArrayOf(1, 2, 3),
            kekEncRecovery = byteArrayOf(4, 5, 6),
            kdfAlgorithm = "argon2id",
            kdfSalt = byteArrayOf(7, 8, 9),
            kdfMemoryKib = 65536,
            kdfIterations = 3,
            kdfParallelism = 1,
            kdfOutputLen = 32,
            cryptoVersion = "v1",
        ),
        recoveryKey = byteArrayOf(10, 11, 12),
        state = PendingVaultInitializationState.AwaitingRemoteConfirmation,
    )
}
