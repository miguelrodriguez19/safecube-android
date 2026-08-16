package com.miguelrodriguez19.safecube.core.vault.data.local

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitialization
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitializationState
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.util.Base64
import java.util.UUID
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Test

class PendingVaultInitializationCodecTest {
    private val target = PendingVaultInitializationCodec()

    @Test
    fun `encode and decode preserves candidate recovery key and operation state`() {
        val value = PendingVaultInitialization(
            candidate = sampleVaultKeyMaterial(),
            recoveryKey = "recovery-key-never-logged".encodeToByteArray(),
            state = PendingVaultInitializationState.RemoteConfirmed,
        )

        val encoded = target.encode(value)
        val decoded = target.decode(encoded)

        requireNotNull(decoded)
        assertEquals(value.candidate.accountId, decoded.candidate.accountId)
        assertArrayEquals(value.candidate.kekEncMaster, decoded.candidate.kekEncMaster)
        assertArrayEquals(value.candidate.kekEncRecovery, decoded.candidate.kekEncRecovery)
        assertEquals(value.candidate.kdfAlgorithm, decoded.candidate.kdfAlgorithm)
        assertArrayEquals(value.candidate.kdfSalt, decoded.candidate.kdfSalt)
        assertEquals(value.candidate.kdfMemoryKib, decoded.candidate.kdfMemoryKib)
        assertEquals(value.candidate.kdfIterations, decoded.candidate.kdfIterations)
        assertEquals(value.candidate.kdfParallelism, decoded.candidate.kdfParallelism)
        assertEquals(value.candidate.kdfOutputLen, decoded.candidate.kdfOutputLen)
        assertEquals(value.candidate.cryptoVersion, decoded.candidate.cryptoVersion)
        assertArrayEquals(value.recoveryKey, decoded.recoveryKey)
        assertEquals(value.state, decoded.state)
        assertFalse(encoded.contains("recovery-key-never-logged"))
    }

    @Test
    fun `decode when record version is unsupported then returns null`() {
        val encoded = Base64.getEncoder().encodeToString(byteArrayOf(0x7F, 0x00))

        assertNull(target.decode(encoded))
    }

    @Test
    fun `decode when record has trailing bytes then returns null`() {
        val encoded = target.encode(
            PendingVaultInitialization(
                candidate = sampleVaultKeyMaterial(),
                recoveryKey = ByteArray(32) { 8 },
                state = PendingVaultInitializationState.AwaitingRemoteConfirmation,
            ),
        )
        val withTrailingBytes = Base64.getEncoder().encodeToString(
            Base64.getDecoder().decode(encoded) + byteArrayOf(1),
        )

        assertNull(target.decode(withTrailingBytes))
    }

    @Test
    fun `encode and decode supports an initialization candidate without account id`() {
        val value = PendingVaultInitialization(
            candidate = sampleVaultKeyMaterial().copy(accountId = null),
            recoveryKey = ByteArray(32) { 9 },
            state = PendingVaultInitializationState.AwaitingRemoteConfirmation,
        )

        val decoded = target.decode(target.encode(value))

        assertNull(requireNotNull(decoded).candidate.accountId)
    }

    @Test
    fun `decode rejects unsupported state and malformed fields`() {
        val unsupportedState = rawRecord(state = 99)
        val invalidByteLength = rawRecord(masterLengthOverride = 0)
        val invalidStringLength = rawRecord(algorithmLengthOverride = 0)
        val invalidNumericValue = rawRecord(memoryKib = 0)

        assertNull(target.decode(unsupportedState))
        assertNull(target.decode(invalidByteLength))
        assertNull(target.decode(invalidStringLength))
        assertNull(target.decode(invalidNumericValue))
        assertNull(target.decode("%"))
        assertNull(target.decode(Base64.getEncoder().encodeToString(ByteArray(32 * 1024 + 1))))
    }

    @Test
    fun `encode rejects empty and oversized sensitive fields`() {
        assertThrows(IllegalArgumentException::class.java) {
            target.encode(
                PendingVaultInitialization(
                    candidate = sampleVaultKeyMaterial().copy(kekEncMaster = byteArrayOf()),
                    recoveryKey = ByteArray(32) { 1 },
                    state = PendingVaultInitializationState.AwaitingRemoteConfirmation,
                ),
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            target.encode(
                PendingVaultInitialization(
                    candidate = sampleVaultKeyMaterial(),
                    recoveryKey = ByteArray(4097),
                    state = PendingVaultInitializationState.AwaitingRemoteConfirmation,
                ),
            )
        }
        assertThrows(IllegalArgumentException::class.java) {
            target.encode(
                PendingVaultInitialization(
                    candidate = sampleVaultKeyMaterial().copy(kdfAlgorithm = ""),
                    recoveryKey = ByteArray(32) { 1 },
                    state = PendingVaultInitializationState.AwaitingRemoteConfirmation,
                ),
            )
        }
    }

    private fun sampleVaultKeyMaterial() = VaultKeyMaterial(
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
    )

    private fun rawRecord(
        state: Int = PendingVaultInitializationState.AwaitingRemoteConfirmation.ordinal,
        accountId: UUID? = null,
        masterLengthOverride: Int? = null,
        algorithmLengthOverride: Int? = null,
        memoryKib: Int = 1,
    ): String = ByteArrayOutputStream().use { output ->
        DataOutputStream(output).use { data ->
            data.writeByte(1)
            data.writeByte(state)
            if (accountId == null) {
                data.writeBoolean(false)
            } else {
                data.writeBoolean(true)
                data.writeLong(accountId.mostSignificantBits)
                data.writeLong(accountId.leastSignificantBits)
            }
            writeBytes(data, byteArrayOf(1, 2, 3), masterLengthOverride)
            writeBytes(data, byteArrayOf(4, 5, 6), null)
            writeString(data, "argon2id", algorithmLengthOverride)
            writeBytes(data, byteArrayOf(7, 8, 9), null)
            data.writeInt(memoryKib)
            data.writeInt(1)
            data.writeInt(1)
            data.writeInt(1)
            writeString(data, "v1", null)
            writeBytes(data, byteArrayOf(10, 11, 12), null)
        }
        Base64.getEncoder().encodeToString(output.toByteArray())
    }

    private fun writeBytes(data: DataOutputStream, value: ByteArray, lengthOverride: Int?) {
        data.writeInt(lengthOverride ?: value.size)
        if (lengthOverride == null || lengthOverride > 0) data.write(value)
    }

    private fun writeString(data: DataOutputStream, value: String, lengthOverride: Int?) {
        val bytes = value.encodeToByteArray()
        data.writeInt(lengthOverride ?: bytes.size)
        if (lengthOverride == null || lengthOverride > 0) data.write(bytes)
    }
}
