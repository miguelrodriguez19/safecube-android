package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.crypto.domain.service.SaltGenerator
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitialization
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultInitializationState
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultRecoveryKeyResult
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
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.io.IOException
import java.util.ArrayDeque
import java.util.UUID
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class VaultInitializeRecoveryUseCaseTest {
    private val remoteRepository = mockk<VaultKeyMaterialRemoteRepository>()
    private val localRepository = mockk<VaultKeyMaterialLocalRepository>()
    private val pendingRepository = mockk<PendingVaultInitializationRepository>()
    private val kdfEngine = mockk<KdfEngine>()
    private val keyWrapping = mockk<KeyWrapping>()
    private val saltGenerator = mockk<SaltGenerator>()
    private val target = VaultInitializeUseCase(
        vaultKeyMaterialRemoteRepository = remoteRepository,
        vaultKeyMaterialLocalRepository = localRepository,
        pendingVaultInitializationRepository = pendingRepository,
        kdfEngine = kdfEngine,
        keyWrapping = keyWrapping,
        saltGenerator = saltGenerator,
    )
    private val postedCandidates = mutableListOf<VaultKeyMaterial>()
    private val pendingSaveResults = ArrayDeque<Boolean>()
    private var pendingValue: PendingVaultInitialization? = null
    private var pendingReadResultOverride: PendingVaultInitializationReadResult? = null
    private var pendingClearResult = true
    private var pendingSaveCount = 0
    private var pendingClearCount = 0
    private var savedMaterial: VaultKeyMaterial? = null

    @Before
    fun setUp() {
        every { pendingRepository.read() } answers {
            pendingReadResultOverride
                ?: pendingValue?.let { PendingVaultInitializationReadResult.Present(it.deepCopy()) }
                ?: PendingVaultInitializationReadResult.Empty
        }
        every { pendingRepository.save(any()) } answers {
            pendingSaveCount += 1
            val result = if (pendingSaveResults.isEmpty()) true else pendingSaveResults.removeFirst()
            if (result) pendingValue = firstArg<PendingVaultInitialization>().deepCopy()
            result
        }
        every { pendingRepository.clear() } answers {
            pendingClearCount += 1
            if (pendingClearResult) pendingValue = null
            pendingClearResult
        }
        every { localRepository.save(any()) } answers {
            savedMaterial = firstArg<VaultKeyMaterial>().deepCopy()
        }
    }

    @Test
    fun `invoke_whenPostResponseIsUncertain_thenPersistsCandidateForReconciliation`() = runBlocking {
        configureCrypto()
        coEvery { remoteRepository.getKeyMaterial() } returns notInitialized()
        coEvery { remoteRepository.initKeyMaterial(any()) } answers {
            postedCandidates += firstArg<VaultKeyMaterial>().deepCopy()
            networkError()
        }

        val result = target("passphrase")

        assertRetryable(result)
        val persisted = requireNotNull(pendingValue)
        assertEquals(1, pendingSaveCount)
        assertEquals(1, postedCandidates.size)
        assertVaultMaterialEquals(persisted.candidate, postedCandidates.single())
        assertEquals(PendingVaultInitializationState.AwaitingRemoteConfirmation, persisted.state)
        assertNull(savedMaterial)
        assertEquals(0, pendingClearCount)
        coVerify(exactly = 1) { remoteRepository.getKeyMaterial() }
        coVerify(exactly = 1) { remoteRepository.initKeyMaterial(any()) }
    }

    @Test
    fun `invoke_whenPendingCandidateMatchesRemote_thenReturnsOriginalRecoveryKey`() = runBlocking {
        val pending = samplePendingInitialization()
        val remoteMaterial = pending.candidate.deepCopy(accountId = UUID.randomUUID())
        pendingValue = pending.deepCopy()
        coEvery {
            remoteRepository.getKeyMaterial()
        } returns VaultKeyMaterialRemoteResult.Success(remoteMaterial)

        val result = target("different passphrase is ignored")

        assertInitialized(result, pending.recoveryKey)
        assertVaultMaterialEquals(remoteMaterial, savedMaterial)
        assertEquals(1, pendingSaveCount)
        assertEquals(PendingVaultInitializationState.RemoteConfirmed, pendingValue?.state)
        assertEquals(remoteMaterial.accountId, pendingValue?.candidate?.accountId)
        assertEquals(0, pendingClearCount)
        coVerify(exactly = 1) { remoteRepository.getKeyMaterial() }
        coVerify(exactly = 0) { remoteRepository.initKeyMaterial(any()) }
    }

    @Test
    fun `invoke_whenPendingRemoteReportsNotInitialized_thenRepostsSameCandidate`() = runBlocking {
        val pending = samplePendingInitialization()
        val remoteMaterial = pending.candidate.deepCopy(accountId = UUID.randomUUID())
        pendingValue = pending.deepCopy()
        coEvery { remoteRepository.getKeyMaterial() } returnsMany listOf(
            notInitialized(),
            VaultKeyMaterialRemoteResult.Success(remoteMaterial),
        )
        coEvery {
            remoteRepository.initKeyMaterial(any())
        } answers {
            postedCandidates += firstArg<VaultKeyMaterial>().deepCopy()
            VaultKeyMaterialRemoteResult.Success(Unit)
        }

        val result = target("passphrase")

        assertInitialized(result, pending.recoveryKey)
        assertEquals(1, postedCandidates.size)
        assertVaultMaterialEquals(pending.candidate, postedCandidates.single())
        assertEquals(1, pendingSaveCount)
        coVerify(exactly = 2) { remoteRepository.getKeyMaterial() }
        coVerify(exactly = 1) { remoteRepository.initKeyMaterial(any()) }
    }

    @Test
    fun `invoke_whenPostCollidesAndRemoteMaterialDiffers_thenKeepsRemoteMaterial`() = runBlocking {
        val pending = samplePendingInitialization()
        val remoteMaterial = differentVaultKeyMaterial()
        pendingValue = pending.deepCopy()
        coEvery { remoteRepository.getKeyMaterial() } returnsMany listOf(
            notInitialized(),
            VaultKeyMaterialRemoteResult.Success(remoteMaterial),
        )
        coEvery {
            remoteRepository.initKeyMaterial(any())
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.VaultAlreadyInitialized,
        )

        val result = target("passphrase")

        assertEquals(VaultInitializeResult.AlreadyInitialized, result)
        assertVaultMaterialEquals(remoteMaterial, savedMaterial)
        assertNull(pendingValue)
        assertEquals(1, pendingClearCount)
        coVerify(exactly = 2) { remoteRepository.getKeyMaterial() }
        coVerify(exactly = 1) { remoteRepository.initKeyMaterial(any()) }
    }

    @Test
    fun `invoke_whenPostIsCancelled_thenRethrowsCancellationAndKeepsPendingCandidate`() {
        val pending = samplePendingInitialization()
        pendingValue = pending.deepCopy()
        coEvery { remoteRepository.getKeyMaterial() } returns notInitialized()
        coEvery {
            remoteRepository.initKeyMaterial(any())
        } throws CancellationException("interrupted")

        assertThrows(CancellationException::class.java) {
            runBlocking { target("passphrase") }
        }

        assertVaultMaterialEquals(pending.candidate, requireNotNull(pendingValue).candidate)
        assertArrayEquals(pending.recoveryKey, pendingValue?.recoveryKey)
        assertEquals(0, pendingClearCount)
        coVerify(exactly = 1) { remoteRepository.getKeyMaterial() }
        coVerify(exactly = 1) { remoteRepository.initKeyMaterial(any()) }
    }

    @Test
    fun `invoke_whenPendingRecordIsCorrupted_thenReturnsLocalReadErrorWithoutRemoteCall`() = runBlocking {
        pendingReadResultOverride = PendingVaultInitializationReadResult.Corrupted

        val result = target("passphrase")

        assertLocalStorageError(result, VaultInitializeError.LocalStorageOperation.Read)
        coVerify(exactly = 0) { remoteRepository.getKeyMaterial() }
        coVerify(exactly = 0) { remoteRepository.initKeyMaterial(any()) }
    }

    @Test
    fun `invoke_whenPendingRemoteReadFails_thenKeepsPendingCandidate`() = runBlocking {
        val pending = samplePendingInitialization()
        pendingValue = pending.deepCopy()
        coEvery {
            remoteRepository.getKeyMaterial()
        } returns VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.Unauthorized)

        val result = target("passphrase")

        assertRemoteError(result, VaultKeyMaterialRemoteError.Unauthorized)
        assertVaultMaterialEquals(pending.candidate, requireNotNull(pendingValue).candidate)
        assertEquals(0, pendingClearCount)
        assertNull(savedMaterial)
        coVerify(exactly = 1) { remoteRepository.getKeyMaterial() }
        coVerify(exactly = 0) { remoteRepository.initKeyMaterial(any()) }
    }

    @Test
    fun `invoke_whenPendingRecordCannotBeSaved_thenReturnsLocalPersistErrorWithoutPost`() = runBlocking {
        configureCrypto()
        pendingSaveResults.addLast(false)
        coEvery { remoteRepository.getKeyMaterial() } returns notInitialized()

        val result = target("passphrase")

        assertLocalStorageError(result, VaultInitializeError.LocalStorageOperation.Persist)
        assertEquals(1, pendingSaveCount)
        coVerify(exactly = 1) { remoteRepository.getKeyMaterial() }
        coVerify(exactly = 0) { remoteRepository.initKeyMaterial(any()) }
        verifyNoLocalSave()
    }

    @Test
    fun `invoke_whenMatchingRemoteMaterialCannotBeCached_thenReturnsLocalPersistError`() = runBlocking {
        val pending = samplePendingInitialization()
        pendingValue = pending.deepCopy()
        every { localRepository.save(any()) } throws IllegalStateException("cache unavailable")
        coEvery {
            remoteRepository.getKeyMaterial()
        } returns VaultKeyMaterialRemoteResult.Success(
            pending.candidate.deepCopy(accountId = UUID.randomUUID()),
        )

        val result = target("passphrase")

        assertLocalStorageError(result, VaultInitializeError.LocalStorageOperation.Persist)
        assertVaultMaterialEquals(pending.candidate, requireNotNull(pendingValue).candidate)
        assertEquals(0, pendingSaveCount)
        assertEquals(0, pendingClearCount)
        coVerify(exactly = 1) { remoteRepository.getKeyMaterial() }
    }

    @Test
    fun `invoke_whenConfirmedPendingRecordCannotBeUpdated_thenReturnsLocalPersistError`() = runBlocking {
        val pending = samplePendingInitialization()
        pendingValue = pending.deepCopy()
        pendingSaveResults.addLast(false)
        val remoteMaterial = pending.candidate.deepCopy(accountId = UUID.randomUUID())
        coEvery {
            remoteRepository.getKeyMaterial()
        } returns VaultKeyMaterialRemoteResult.Success(remoteMaterial)

        val result = target("passphrase")

        assertLocalStorageError(result, VaultInitializeError.LocalStorageOperation.Persist)
        assertVaultMaterialEquals(remoteMaterial, savedMaterial)
        assertEquals(1, pendingSaveCount)
        assertVaultMaterialEquals(pending.candidate, requireNotNull(pendingValue).candidate)
        coVerify(exactly = 1) { remoteRepository.getKeyMaterial() }
    }

    @Test
    fun `invoke_whenDifferentRemoteMaterialCannotBeCached_thenReturnsLocalPersistError`() = runBlocking {
        val pending = samplePendingInitialization()
        pendingValue = pending.deepCopy()
        every { localRepository.save(any()) } throws IllegalStateException("cache unavailable")
        coEvery {
            remoteRepository.getKeyMaterial()
        } returns VaultKeyMaterialRemoteResult.Success(differentVaultKeyMaterial())

        val result = target("passphrase")

        assertLocalStorageError(result, VaultInitializeError.LocalStorageOperation.Persist)
        assertVaultMaterialEquals(pending.candidate, requireNotNull(pendingValue).candidate)
        assertEquals(0, pendingClearCount)
        coVerify(exactly = 1) { remoteRepository.getKeyMaterial() }
    }

    @Test
    fun `invoke_whenDifferentRemoteMaterialCannotClearCandidate_thenReturnsLocalCleanupError`() = runBlocking {
        val pending = samplePendingInitialization()
        pendingValue = pending.deepCopy()
        pendingClearResult = false
        coEvery {
            remoteRepository.getKeyMaterial()
        } returns VaultKeyMaterialRemoteResult.Success(differentVaultKeyMaterial())

        val result = target("passphrase")

        assertLocalStorageError(result, VaultInitializeError.LocalStorageOperation.Cleanup)
        assertEquals(1, pendingClearCount)
        assertVaultMaterialEquals(pending.candidate, requireNotNull(pendingValue).candidate)
        coVerify(exactly = 1) { remoteRepository.getKeyMaterial() }
    }

    @Test
    fun `confirmRecoveryKeySaved_whenPendingRecordClears_thenReturnsTrue`() {
        pendingValue = samplePendingInitialization()

        val result = target.confirmRecoveryKeySaved()

        assertTrue(result)
        assertNull(pendingValue)
        assertEquals(1, pendingClearCount)
    }

    @Test
    fun `confirmRecoveryKeySaved_whenPendingRecordClearFails_thenReturnsFalse`() {
        every { pendingRepository.clear() } throws IllegalStateException("storage unavailable")

        val result = target.confirmRecoveryKeySaved()

        assertFalse(result)
        assertEquals(0, pendingClearCount)
    }

    @Test
    fun `readPendingRecoveryKey_whenPendingRecordExists_thenReturnsRecoveryKeyCopy`() {
        pendingValue = samplePendingInitialization()
        val expectedRecoveryKey = pendingValue!!.recoveryKey.copyOf()

        val result = target.readPendingRecoveryKey()

        assertTrue(result is PendingVaultRecoveryKeyResult.Available)
        assertArrayEquals(
            expectedRecoveryKey,
            (result as PendingVaultRecoveryKeyResult.Available).recoveryKey,
        )
        assertArrayEquals(expectedRecoveryKey, pendingValue!!.recoveryKey)
    }

    @Test
    fun `readPendingRecoveryKey_whenPendingRecordIsCorrupted_thenReturnsCorrupted`() {
        pendingReadResultOverride = PendingVaultInitializationReadResult.Corrupted

        val result = target.readPendingRecoveryKey()

        assertEquals(PendingVaultRecoveryKeyResult.Corrupted, result)
    }

    private fun configureCrypto() {
        every { saltGenerator.generate(16) } returns ByteArray(16) { 1 }
        every { saltGenerator.generate(32) } returnsMany listOf(
            ByteArray(32) { 2 },
            ByteArray(32) { 3 },
        )
        every { kdfEngine.deriveKey(any()) } returns ByteArray(32) { 4 }
        every { keyWrapping.wrapKey(any()) } answers {
            firstArg<KeyWrapRequest>().keyToWrap.copyOf()
        }
    }

    private fun samplePendingInitialization() = PendingVaultInitialization(
        candidate = VaultKeyMaterial(
            accountId = null,
            kekEncMaster = ByteArray(4) { 10 },
            kekEncRecovery = ByteArray(4) { 11 },
            kdfAlgorithm = "argon2id",
            kdfSalt = ByteArray(16) { 12 },
            kdfMemoryKib = 65536,
            kdfIterations = 3,
            kdfParallelism = 1,
            kdfOutputLen = 32,
            cryptoVersion = "v1",
        ),
        recoveryKey = ByteArray(32) { 13 },
        state = PendingVaultInitializationState.AwaitingRemoteConfirmation,
    )

    private fun notInitialized() = VaultKeyMaterialRemoteResult.Error(
        VaultKeyMaterialRemoteError.VaultNotInitialized,
    )

    private fun networkError() = VaultKeyMaterialRemoteResult.Error(
        VaultKeyMaterialRemoteError.NetworkError(
            NetworkFailureClassifier.fromThrowable(IOException("connection lost")),
        ),
    )

    private fun differentVaultKeyMaterial() = VaultKeyMaterial(
        accountId = UUID.randomUUID(),
        kekEncMaster = ByteArray(4) { 40 },
        kekEncRecovery = ByteArray(4) { 41 },
        kdfAlgorithm = "argon2id",
        kdfSalt = ByteArray(16) { 42 },
        kdfMemoryKib = 65536,
        kdfIterations = 3,
        kdfParallelism = 1,
        kdfOutputLen = 32,
        cryptoVersion = "v1",
    )

    private fun assertInitialized(
        result: VaultInitializeResult,
        expectedRecoveryKey: ByteArray,
    ) {
        assertTrue(result is VaultInitializeResult.Initialized)
        assertArrayEquals(
            expectedRecoveryKey,
            (result as VaultInitializeResult.Initialized).recoveryKey,
        )
    }

    private fun assertRetryable(result: VaultInitializeResult) {
        assertTrue(result is VaultInitializeResult.Error)
        val error = (result as VaultInitializeResult.Error).reason
        assertTrue(error is VaultInitializeError.Remote)
        assertEquals(
            RetryDecision.Retryable,
            (error as VaultInitializeError.Remote).error.failure.decision,
        )
    }

    private fun assertRemoteError(
        result: VaultInitializeResult,
        expected: VaultKeyMaterialRemoteError,
    ) {
        assertTrue(result is VaultInitializeResult.Error)
        assertEquals(
            VaultInitializeError.Remote(expected),
            (result as VaultInitializeResult.Error).reason,
        )
    }

    private fun assertLocalStorageError(
        result: VaultInitializeResult,
        operation: VaultInitializeError.LocalStorageOperation,
    ) {
        assertTrue(result is VaultInitializeResult.Error)
        assertEquals(
            VaultInitializeError.LocalStorage(operation),
            (result as VaultInitializeResult.Error).reason,
        )
    }

    private fun verifyNoLocalSave() {
        verify(exactly = 0) { localRepository.save(any()) }
    }

    private fun assertVaultMaterialEquals(
        expected: VaultKeyMaterial?,
        actual: VaultKeyMaterial?,
    ) {
        requireNotNull(expected)
        requireNotNull(actual)
        assertEquals(expected.accountId, actual.accountId)
        assertArrayEquals(expected.kekEncMaster, actual.kekEncMaster)
        assertArrayEquals(expected.kekEncRecovery, actual.kekEncRecovery)
        assertEquals(expected.kdfAlgorithm, actual.kdfAlgorithm)
        assertArrayEquals(expected.kdfSalt, actual.kdfSalt)
        assertEquals(expected.kdfMemoryKib, actual.kdfMemoryKib)
        assertEquals(expected.kdfIterations, actual.kdfIterations)
        assertEquals(expected.kdfParallelism, actual.kdfParallelism)
        assertEquals(expected.kdfOutputLen, actual.kdfOutputLen)
        assertEquals(expected.cryptoVersion, actual.cryptoVersion)
    }
}

private fun VaultKeyMaterial.deepCopy(accountId: UUID? = this.accountId): VaultKeyMaterial = copy(
    accountId = accountId,
    kekEncMaster = kekEncMaster.copyOf(),
    kekEncRecovery = kekEncRecovery.copyOf(),
    kdfSalt = kdfSalt.copyOf(),
)

private fun PendingVaultInitialization.deepCopy(): PendingVaultInitialization = copy(
    candidate = candidate.deepCopy(),
    recoveryKey = recoveryKey.copyOf(),
)
