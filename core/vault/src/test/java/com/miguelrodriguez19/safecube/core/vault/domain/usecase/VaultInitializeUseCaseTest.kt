package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.crypto.domain.service.SaltGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeError
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationReadResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultInitializeUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultInitializeUseCaseTest {
    private val vaultKeyMaterialRemoteRepository = mockk<VaultKeyMaterialRemoteRepository>()
    private val vaultKeyMaterialLocalRepository =
        mockk<VaultKeyMaterialLocalRepository>(relaxed = true)
    private val pendingVaultInitializationRepository =
        mockk<PendingVaultInitializationRepository>()
    private val kdfEngine = mockk<KdfEngine>()
    private val keyWrapping = mockk<KeyWrapping>()
    private val saltGenerator = mockk<SaltGenerator>()

    init {
        every { pendingVaultInitializationRepository.read() } returns
            PendingVaultInitializationReadResult.Empty
        every { pendingVaultInitializationRepository.save(any()) } returns true
        every { pendingVaultInitializationRepository.clear() } returns true
    }

    @Test
    fun `invoke when vault is not initialized then initializes vault and caches refreshed material`() =
        runBlocking {
            val derivedMasterKey = ByteArray(32) { index -> (index + 1).toByte() }
            val generatedKek = ByteArray(32) { index -> (index + 51).toByte() }
            val generatedRecoveryKey = ByteArray(32) { index -> (index + 101).toByte() }
            val kdfSalt = ByteArray(16) { index -> (index + 11).toByte() }
            val expectedDerivedMasterKey = derivedMasterKey.copyOf()
            val expectedGeneratedKek = generatedKek.copyOf()
            val expectedGeneratedRecoveryKey = generatedRecoveryKey.copyOf()
            val expectedKdfSalt = kdfSalt.copyOf()
            val kekEncMaster = byteArrayOf(1, 2, 3, 4)
            val kekEncRecovery = byteArrayOf(5, 6, 7, 8)
            val refreshedVaultKeyMaterial = existingVaultKeyMaterial().copy(
                kekEncMaster = kekEncMaster.copyOf(),
                kekEncRecovery = kekEncRecovery.copyOf(),
                kdfSalt = expectedKdfSalt.copyOf(),
            )

            val kdfRequestSlot = slot<KdfRequest>()
            val wrapRequests = mutableListOf<KeyWrapRequest>()
            val savedVaultKeyMaterial = slot<VaultKeyMaterial>()
            var initializedVaultKeyMaterial: VaultKeyMaterial? = null

            coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returnsMany listOf(
                VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
                VaultKeyMaterialRemoteResult.Success(refreshedVaultKeyMaterial),
            )
            every { saltGenerator.generate(16) } returns kdfSalt
            every { saltGenerator.generate(32) } returnsMany listOf(
                generatedKek,
                generatedRecoveryKey
            )
            every { kdfEngine.deriveKey(capture(kdfRequestSlot)) } returns derivedMasterKey
            every { keyWrapping.wrapKey(any()) } answers {
                wrapRequests += firstArg<KeyWrapRequest>().copy(
                    keyToWrap = firstArg<KeyWrapRequest>().keyToWrap.copyOf(),
                    wrappingKey = firstArg<KeyWrapRequest>().wrappingKey.copyOf(),
                    aad = firstArg<KeyWrapRequest>().aad?.copyOf(),
                )

                when (wrapRequests.size) {
                    1 -> kekEncMaster.copyOf()
                    2 -> kekEncRecovery.copyOf()
                    else -> error("Unexpected wrap request count")
                }
            }
            coEvery {
                vaultKeyMaterialRemoteRepository.initKeyMaterial(any())
            } answers {
                val value = firstArg<VaultKeyMaterial>()
                initializedVaultKeyMaterial = value.copy(
                    kekEncMaster = value.kekEncMaster.copyOf(),
                    kekEncRecovery = value.kekEncRecovery.copyOf(),
                    kdfSalt = value.kdfSalt.copyOf(),
                )
                VaultKeyMaterialRemoteResult.Success(Unit)
            }
            every {
                vaultKeyMaterialLocalRepository.save(capture(savedVaultKeyMaterial))
            } returns Unit

            val target = createVaultInitializeUseCase()

            val result = target(passphrase = "correct horse battery staple")

            assertTrue(result is VaultInitializeResult.Initialized)
            val recoveryKey = (result as VaultInitializeResult.Initialized).recoveryKey
            assertArrayEquals(expectedGeneratedRecoveryKey, recoveryKey)
            assertEquals(32, recoveryKey.size)

            val initializedMaterial = requireNotNull(initializedVaultKeyMaterial)
            assertNull(initializedMaterial.accountId)
            assertArrayEquals(kekEncMaster, initializedMaterial.kekEncMaster)
            assertArrayEquals(kekEncRecovery, initializedMaterial.kekEncRecovery)
            assertEquals("argon2id", initializedMaterial.kdfAlgorithm)
            assertArrayEquals(expectedKdfSalt, initializedMaterial.kdfSalt)
            assertEquals("v1", initializedMaterial.cryptoVersion)
            assertEquals(65536, initializedMaterial.kdfMemoryKib)
            assertEquals(3, initializedMaterial.kdfIterations)
            assertEquals(1, initializedMaterial.kdfParallelism)
            assertEquals(32, initializedMaterial.kdfOutputLen)

            assertEquals(
                refreshedVaultKeyMaterial.accountId,
                savedVaultKeyMaterial.captured.accountId
            )
            assertArrayEquals(
                refreshedVaultKeyMaterial.kekEncMaster,
                savedVaultKeyMaterial.captured.kekEncMaster
            )
            assertArrayEquals(
                refreshedVaultKeyMaterial.kekEncRecovery,
                savedVaultKeyMaterial.captured.kekEncRecovery
            )
            assertArrayEquals(
                refreshedVaultKeyMaterial.kdfSalt,
                savedVaultKeyMaterial.captured.kdfSalt
            )
            assertEquals(
                refreshedVaultKeyMaterial.kdfAlgorithm,
                savedVaultKeyMaterial.captured.kdfAlgorithm
            )
            assertEquals(
                refreshedVaultKeyMaterial.cryptoVersion,
                savedVaultKeyMaterial.captured.cryptoVersion
            )

            assertEquals(65536, kdfRequestSlot.captured.memoryKib)
            assertEquals(3, kdfRequestSlot.captured.iterations)
            assertEquals(1, kdfRequestSlot.captured.parallelism)
            assertEquals(32, kdfRequestSlot.captured.outputLengthBytes)

            assertEquals(2, wrapRequests.size)
            assertArrayEquals(expectedGeneratedKek, wrapRequests[0].keyToWrap)
            assertArrayEquals(expectedDerivedMasterKey, wrapRequests[0].wrappingKey)
            assertArrayEquals(expectedGeneratedKek, wrapRequests[1].keyToWrap)
            assertArrayEquals(expectedGeneratedRecoveryKey, wrapRequests[1].wrappingKey)
            assertFalse(recoveryKey.contentEquals(initializedMaterial.kekEncMaster))
            assertFalse(recoveryKey.contentEquals(initializedMaterial.kekEncRecovery))

            coVerify(exactly = 2) { vaultKeyMaterialRemoteRepository.getKeyMaterial() }
            verify(exactly = 1) { saltGenerator.generate(16) }
            verify(exactly = 2) { saltGenerator.generate(32) }
            verify(exactly = 1) { kdfEngine.deriveKey(any()) }
            verify(exactly = 2) { keyWrapping.wrapKey(any()) }
            coVerify(exactly = 1) { vaultKeyMaterialRemoteRepository.initKeyMaterial(any()) }
            verify(exactly = 1) { vaultKeyMaterialLocalRepository.save(any()) }
            confirmVerified(
                vaultKeyMaterialRemoteRepository,
                vaultKeyMaterialLocalRepository,
                kdfEngine,
                keyWrapping,
                saltGenerator,
            )
        }

    @Test
    fun `invoke when key material already exists then returns already initialized`() = runBlocking {
        coEvery {
            vaultKeyMaterialRemoteRepository.getKeyMaterial()
        } returns VaultKeyMaterialRemoteResult.Success(existingVaultKeyMaterial())

        val target = createVaultInitializeUseCase()

        val result = target(passphrase = "irrelevant")

        assertEquals(VaultInitializeResult.AlreadyInitialized, result)
        coVerify(exactly = 1) { vaultKeyMaterialRemoteRepository.getKeyMaterial() }
        verify(exactly = 0) { saltGenerator.generate(any()) }
        verify(exactly = 0) { kdfEngine.deriveKey(any()) }
        verify(exactly = 0) { keyWrapping.wrapKey(any()) }
        coVerify(exactly = 0) { vaultKeyMaterialRemoteRepository.initKeyMaterial(any()) }
        verify(exactly = 0) { vaultKeyMaterialLocalRepository.save(any()) }
        confirmVerified(
            vaultKeyMaterialRemoteRepository,
            vaultKeyMaterialLocalRepository,
            kdfEngine,
            keyWrapping,
            saltGenerator,
        )
    }

    @Test
    fun `invoke when initial get fails with non not initialized error then returns remote error`() =
        runBlocking {
            coEvery {
                vaultKeyMaterialRemoteRepository.getKeyMaterial()
            } returns VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.Unauthorized)

            val target = createVaultInitializeUseCase()

            val result = target(passphrase = "irrelevant")

            assertTrue(result is VaultInitializeResult.Error)
            assertEquals(
                VaultInitializeError.Remote(VaultKeyMaterialRemoteError.Unauthorized),
                (result as VaultInitializeResult.Error).reason,
            )
            coVerify(exactly = 1) { vaultKeyMaterialRemoteRepository.getKeyMaterial() }
            verify(exactly = 0) { saltGenerator.generate(any()) }
            verify(exactly = 0) { kdfEngine.deriveKey(any()) }
            verify(exactly = 0) { keyWrapping.wrapKey(any()) }
            coVerify(exactly = 0) { vaultKeyMaterialRemoteRepository.initKeyMaterial(any()) }
            verify(exactly = 0) { vaultKeyMaterialLocalRepository.save(any()) }
            confirmVerified(
                vaultKeyMaterialRemoteRepository,
                vaultKeyMaterialLocalRepository,
                kdfEngine,
                keyWrapping,
                saltGenerator,
            )
        }

    @Test
    fun `invoke when init collides with existing vault then returns already initialized`() =
        runBlocking {
            coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returnsMany listOf(
                VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
                VaultKeyMaterialRemoteResult.Success(existingVaultKeyMaterial()),
            )
            every { saltGenerator.generate(16) } returns ByteArray(16) { index -> (index + 1).toByte() }
            every { saltGenerator.generate(32) } returnsMany listOf(
                ByteArray(32) { index -> (index + 31).toByte() },
                ByteArray(32) { index -> (index + 61).toByte() },
            )
            every { kdfEngine.deriveKey(any()) } returns ByteArray(32) { index -> (index + 91).toByte() }
            every { keyWrapping.wrapKey(any()) } returnsMany listOf(
                byteArrayOf(1, 2, 3),
                byteArrayOf(4, 5, 6),
            )
            coEvery {
                vaultKeyMaterialRemoteRepository.initKeyMaterial(any())
            } returns VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultAlreadyInitialized)

            val target = createVaultInitializeUseCase()

            val result = target(passphrase = "irrelevant")

            assertEquals(VaultInitializeResult.AlreadyInitialized, result)
            coVerify(exactly = 2) { vaultKeyMaterialRemoteRepository.getKeyMaterial() }
            verify(exactly = 1) { saltGenerator.generate(16) }
            verify(exactly = 2) { saltGenerator.generate(32) }
            verify(exactly = 1) { kdfEngine.deriveKey(any()) }
            verify(exactly = 2) { keyWrapping.wrapKey(any()) }
            coVerify(exactly = 1) { vaultKeyMaterialRemoteRepository.initKeyMaterial(any()) }
            verify(exactly = 1) { vaultKeyMaterialLocalRepository.save(any()) }
            verify(exactly = 1) { pendingVaultInitializationRepository.clear() }
            confirmVerified(
                vaultKeyMaterialRemoteRepository,
                vaultKeyMaterialLocalRepository,
                kdfEngine,
                keyWrapping,
                saltGenerator,
            )
        }

    @Test
    fun `invoke when init fails then returns remote error`() = runBlocking {
        coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returns
                VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized)
        every { saltGenerator.generate(16) } returns ByteArray(16) { index -> (index + 1).toByte() }
        every { saltGenerator.generate(32) } returnsMany listOf(
            ByteArray(32) { index -> (index + 31).toByte() },
            ByteArray(32) { index -> (index + 61).toByte() },
        )
        every { kdfEngine.deriveKey(any()) } returns ByteArray(32) { index -> (index + 91).toByte() }
        every { keyWrapping.wrapKey(any()) } returnsMany listOf(
            byteArrayOf(1, 2, 3),
            byteArrayOf(4, 5, 6),
        )
        coEvery {
            vaultKeyMaterialRemoteRepository.initKeyMaterial(any())
        } returns VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.Unauthorized)

        val target = createVaultInitializeUseCase()

        val result = target(passphrase = "irrelevant")

        assertTrue(result is VaultInitializeResult.Error)
        assertEquals(
            VaultInitializeError.Remote(VaultKeyMaterialRemoteError.Unauthorized),
            (result as VaultInitializeResult.Error).reason,
        )
        coVerify(exactly = 1) { vaultKeyMaterialRemoteRepository.getKeyMaterial() }
        verify(exactly = 1) { saltGenerator.generate(16) }
        verify(exactly = 2) { saltGenerator.generate(32) }
        verify(exactly = 1) { kdfEngine.deriveKey(any()) }
        verify(exactly = 2) { keyWrapping.wrapKey(any()) }
        coVerify(exactly = 1) { vaultKeyMaterialRemoteRepository.initKeyMaterial(any()) }
        verify(exactly = 0) { vaultKeyMaterialLocalRepository.save(any()) }
        confirmVerified(
            vaultKeyMaterialRemoteRepository,
            vaultKeyMaterialLocalRepository,
            kdfEngine,
            keyWrapping,
            saltGenerator,
        )
    }

    @Test
    fun `invoke when kdf throws then returns crypto error`() = runBlocking {
        coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returns
                VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized)
        every { saltGenerator.generate(16) } returns ByteArray(16) { index -> (index + 1).toByte() }
        every { kdfEngine.deriveKey(any()) } throws IllegalStateException("boom")

        val target = createVaultInitializeUseCase()

        val result = target(passphrase = "irrelevant")

        assertTrue(result is VaultInitializeResult.Error)
        assertTrue((result as VaultInitializeResult.Error).reason is VaultInitializeError.Crypto)
        coVerify(exactly = 1) { vaultKeyMaterialRemoteRepository.getKeyMaterial() }
        verify(exactly = 1) { saltGenerator.generate(16) }
        verify(exactly = 1) { kdfEngine.deriveKey(any()) }
        verify(exactly = 0) { keyWrapping.wrapKey(any()) }
        coVerify(exactly = 0) { vaultKeyMaterialRemoteRepository.initKeyMaterial(any()) }
        verify(exactly = 0) { vaultKeyMaterialLocalRepository.save(any()) }
        confirmVerified(
            vaultKeyMaterialRemoteRepository,
            vaultKeyMaterialLocalRepository,
            kdfEngine,
            keyWrapping,
            saltGenerator,
        )
    }

    @Test
    fun `confirm recovery key saved returns false when pending cleanup fails`() {
        every { pendingVaultInitializationRepository.clear() } throws IllegalStateException("storage unavailable")

        assertFalse(createVaultInitializeUseCase().confirmRecoveryKeySaved())
    }

    private fun createVaultInitializeUseCase(): VaultInitializeUseCase = VaultInitializeUseCase(
        vaultKeyMaterialRemoteRepository = vaultKeyMaterialRemoteRepository,
        vaultKeyMaterialLocalRepository = vaultKeyMaterialLocalRepository,
        pendingVaultInitializationRepository = pendingVaultInitializationRepository,
        kdfEngine = kdfEngine,
        keyWrapping = keyWrapping,
        saltGenerator = saltGenerator,
    )

    private fun existingVaultKeyMaterial(): VaultKeyMaterial = VaultKeyMaterial(
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
}
