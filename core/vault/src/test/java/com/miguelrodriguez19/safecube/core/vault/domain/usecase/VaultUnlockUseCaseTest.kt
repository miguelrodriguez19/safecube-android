package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultUnlockUseCase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.UUID
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultUnlockUseCaseTest {
    private val vaultKeyMaterialLocalRepository = mockk<VaultKeyMaterialLocalRepository>()
    private val kdfEngine = mockk<KdfEngine>()
    private val keyWrapping = mockk<KeyWrapping>()

    @Test
    fun `unlockWithPassphrase when passphrase is valid then returns unlocked keyring`() {
        val cachedVaultKeyMaterial = sampleVaultKeyMaterial()
        val derivedMasterKey = ByteArray(32) { index -> (index + 1).toByte() }
        val expectedDerivedMasterKey = derivedMasterKey.copyOf()
        val kek = ByteArray(32) { index -> (index + 41).toByte() }
        val kdfRequestSlot = slot<KdfRequest>()
        val unwrapRequests = mutableListOf<KeyUnwrapRequest>()

        every { vaultKeyMaterialLocalRepository.get() } returns cachedVaultKeyMaterial
        every { kdfEngine.deriveKey(capture(kdfRequestSlot)) } returns derivedMasterKey
        every { keyWrapping.unwrapKey(any()) } answers {
            unwrapRequests += firstArg<KeyUnwrapRequest>().copy(
                wrappedKey = firstArg<KeyUnwrapRequest>().wrappedKey.copyOf(),
                wrappingKey = firstArg<KeyUnwrapRequest>().wrappingKey.copyOf(),
            )
            kek
        }

        val target = createTarget()

        val result = target.unlockWithPassphrase(passphrase = "correct-passphrase")

        assertTrue(result is VaultUnlockResult.Unlocked)
        assertArrayEquals(kek, (result as VaultUnlockResult.Unlocked).keyring.kek)
        assertArrayEquals(cachedVaultKeyMaterial.kdfSalt, kdfRequestSlot.captured.salt)
        assertEquals(cachedVaultKeyMaterial.kdfIterations, kdfRequestSlot.captured.iterations)
        assertEquals(cachedVaultKeyMaterial.kdfMemoryKib, kdfRequestSlot.captured.memoryKib)
        assertEquals(cachedVaultKeyMaterial.kdfParallelism, kdfRequestSlot.captured.parallelism)
        assertEquals(cachedVaultKeyMaterial.kdfOutputLen, kdfRequestSlot.captured.outputLengthBytes)
        assertArrayEquals(cachedVaultKeyMaterial.kekEncMaster, unwrapRequests.single().wrappedKey)
        assertArrayEquals(expectedDerivedMasterKey, unwrapRequests.single().wrappingKey)

        verify(exactly = 1) { vaultKeyMaterialLocalRepository.get() }
        verify(exactly = 1) { kdfEngine.deriveKey(any()) }
        verify(exactly = 1) { keyWrapping.unwrapKey(any()) }
        confirmVerified(vaultKeyMaterialLocalRepository, kdfEngine, keyWrapping)
    }

    @Test
    fun `unlockWithRecoveryKey when recovery key is valid then returns unlocked keyring`() {
        val cachedVaultKeyMaterial = sampleVaultKeyMaterial()
        val recoveryKey = ByteArray(32) { index -> (index + 71).toByte() }
        val expectedRecoveryKey = recoveryKey.copyOf()
        val kek = ByteArray(32) { index -> (index + 81).toByte() }
        val unwrapRequests = mutableListOf<KeyUnwrapRequest>()

        every { vaultKeyMaterialLocalRepository.get() } returns cachedVaultKeyMaterial
        every { keyWrapping.unwrapKey(any()) } answers {
            unwrapRequests += firstArg<KeyUnwrapRequest>().copy(
                wrappedKey = firstArg<KeyUnwrapRequest>().wrappedKey.copyOf(),
                wrappingKey = firstArg<KeyUnwrapRequest>().wrappingKey.copyOf(),
            )
            kek
        }

        val target = createTarget()

        val result = target.unlockWithRecoveryKey(recoveryKey = recoveryKey)

        assertTrue(result is VaultUnlockResult.Unlocked)
        assertArrayEquals(kek, (result as VaultUnlockResult.Unlocked).keyring.kek)
        assertArrayEquals(cachedVaultKeyMaterial.kekEncRecovery, unwrapRequests.single().wrappedKey)
        assertArrayEquals(expectedRecoveryKey, unwrapRequests.single().wrappingKey)

        verify(exactly = 1) { vaultKeyMaterialLocalRepository.get() }
        verify(exactly = 0) { kdfEngine.deriveKey(any()) }
        verify(exactly = 1) { keyWrapping.unwrapKey(any()) }
        confirmVerified(vaultKeyMaterialLocalRepository, kdfEngine, keyWrapping)
    }

    @Test
    fun `unlockWithPassphrase when passphrase is wrong then returns invalid credential error`() {
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial()
        every { kdfEngine.deriveKey(any()) } returns ByteArray(32) { index -> (index + 1).toByte() }
        every { keyWrapping.unwrapKey(any()) } throws IllegalStateException("authentication failed")

        val target = createTarget()

        val result = target.unlockWithPassphrase(passphrase = "wrong-passphrase")

        assertEquals(VaultUnlockResult.Error(VaultUnlockError.InvalidCredential), result)
        verify(exactly = 1) { vaultKeyMaterialLocalRepository.get() }
        verify(exactly = 1) { kdfEngine.deriveKey(any()) }
        verify(exactly = 1) { keyWrapping.unwrapKey(any()) }
        confirmVerified(vaultKeyMaterialLocalRepository, kdfEngine, keyWrapping)
    }

    @Test
    fun `unlockWithRecoveryKey when recovery key is wrong then returns invalid credential error`() {
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial()
        every { keyWrapping.unwrapKey(any()) } throws IllegalStateException("authentication failed")

        val target = createTarget()

        val result = target.unlockWithRecoveryKey(
            recoveryKey = ByteArray(32) { 7 },
        )

        assertEquals(VaultUnlockResult.Error(VaultUnlockError.InvalidCredential), result)
        verify(exactly = 1) { vaultKeyMaterialLocalRepository.get() }
        verify(exactly = 0) { kdfEngine.deriveKey(any()) }
        verify(exactly = 1) { keyWrapping.unwrapKey(any()) }
        confirmVerified(vaultKeyMaterialLocalRepository, kdfEngine, keyWrapping)
    }

    @Test
    fun `unlock methods when cache is empty then return key material unavailable`() {
        every { vaultKeyMaterialLocalRepository.get() } returns null

        val target = createTarget()

        val passphraseResult = target.unlockWithPassphrase(passphrase = "passphrase")
        val recoveryResult = target.unlockWithRecoveryKey(recoveryKey = ByteArray(32) { 1 })

        assertEquals(
            VaultUnlockResult.Error(VaultUnlockError.KeyMaterialUnavailable),
            passphraseResult
        )
        assertEquals(
            VaultUnlockResult.Error(VaultUnlockError.KeyMaterialUnavailable),
            recoveryResult
        )
        verify(exactly = 2) { vaultKeyMaterialLocalRepository.get() }
        verify(exactly = 0) { kdfEngine.deriveKey(any()) }
        verify(exactly = 0) { keyWrapping.unwrapKey(any()) }
        confirmVerified(vaultKeyMaterialLocalRepository, kdfEngine, keyWrapping)
    }

    @Test
    fun `unlockWithPassphrase when cached envelope is malformed then returns invalid cached key material`() {
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial()
        every { kdfEngine.deriveKey(any()) } returns ByteArray(32) { index -> (index + 1).toByte() }
        every { keyWrapping.unwrapKey(any()) } throws IllegalArgumentException("malformed envelope")

        val target = createTarget()

        val result = target.unlockWithPassphrase(passphrase = "correct-passphrase")

        assertEquals(VaultUnlockResult.Error(VaultUnlockError.InvalidCachedKeyMaterial), result)
        verify(exactly = 1) { vaultKeyMaterialLocalRepository.get() }
        verify(exactly = 1) { kdfEngine.deriveKey(any()) }
        verify(exactly = 1) { keyWrapping.unwrapKey(any()) }
        confirmVerified(vaultKeyMaterialLocalRepository, kdfEngine, keyWrapping)
    }

    private fun createTarget(): VaultUnlockUseCase = VaultUnlockUseCase(
        vaultKeyMaterialLocalRepository = vaultKeyMaterialLocalRepository,
        kdfEngine = kdfEngine,
        keyWrapping = keyWrapping,
    )

    private fun sampleVaultKeyMaterial(): VaultKeyMaterial = VaultKeyMaterial(
        accountId = UUID.randomUUID(),
        kekEncMaster = byteArrayOf(1, 2, 3, 4),
        kekEncRecovery = byteArrayOf(5, 6, 7, 8),
        kdfAlgorithm = "argon2id",
        kdfSalt = byteArrayOf(9, 10, 11, 12),
        kdfMemoryKib = 65536,
        kdfIterations = 3,
        kdfParallelism = 1,
        kdfOutputLen = 32,
        cryptoVersion = "v1",
    )
}
