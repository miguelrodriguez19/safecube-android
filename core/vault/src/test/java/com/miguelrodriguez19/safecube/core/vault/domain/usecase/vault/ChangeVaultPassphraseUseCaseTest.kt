package com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase.ChangeVaultPassphraseError
import com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase.ChangeVaultPassphraseResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.MasterWrapperUpdateConfirmation
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VersionedVaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalReadResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.QuickUnlockPromptMode
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultKekProvider
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import java.io.IOException
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ChangeVaultPassphraseUseCaseTest {
    private val vaultSessionManager = mockk<VaultSessionManager>()
    private val vaultKekProvider = mockk<VaultKekProvider>()
    private val localRepository = mockk<VaultKeyMaterialLocalRepository>()
    private val remoteRepository = mockk<VaultKeyMaterialRemoteRepository>()
    private val kdfEngine = mockk<KdfEngine>()
    private val keyWrapping = mockk<KeyWrapping>()

    private val activeKek = ByteArray(32) { index -> (index + 31).toByte() }
    private val oldMasterWrapper = byteArrayOf(1, 2, 3, 4)
    private val newMasterWrapper = byteArrayOf(5, 6, 7, 8)
    private val thirdMasterWrapper = byteArrayOf(9, 10, 11, 12)
    private val oldMasterKey = ByteArray(32) { index -> (index + 51).toByte() }
    private val newMasterKey = ByteArray(32) { index -> (index + 81).toByte() }
    private val baseEtag = "\"master-1\""
    private val newEtag = "\"master-2\""
    private val thirdEtag = "\"master-3\""
    private val baseMaterial = sampleVaultKeyMaterial(oldMasterWrapper)
    private val updatedWrappers = mutableListOf<ByteArray>()
    private val kdfRequests = mutableListOf<KdfRequest>()
    private val unwrapRequests = mutableListOf<KeyUnwrapRequest>()
    private val wrapRequests = mutableListOf<KeyWrapRequest>()

    @Before
    fun setUp() {
        every { vaultSessionManager.isUnlocked() } returns true
        every { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) } just Runs
        every { vaultSessionManager.clearQuickUnlockEnrollment() } returns QuickUnlockCleanupResult.Cleared
        every { vaultKekProvider.snapshot() } answers { activeKek.copyOf() }
        every { localRepository.read() } returns VaultKeyMaterialLocalReadResult.Present(baseMaterial)
        every { localRepository.save(any()) } just Runs
        every { localRepository.updateMasterWrappedKek(any()) } answers {
            updatedWrappers += firstArg<ByteArray>().copyOf()
        }
        every { localRepository.clearMasterWrappedKek() } just Runs
    }

    @Test
    fun `change passphrase when valid then gets fresh material and sends exact etag`() {
        stubSuccessfulCrypto()
        stubInitialVersion()
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns successConfirmation(newEtag)

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(ChangeVaultPassphraseResult.Success, result)
        assertEquals(2, kdfRequests.size)
        assertEquals("current-passphrase", kdfRequests[0].secret.decodeToString())
        assertEquals("new-passphrase", kdfRequests[1].secret.decodeToString())
        assertArrayEquals(baseMaterial.kdfSalt, kdfRequests[0].salt)
        assertEquals(baseMaterial.kdfIterations, kdfRequests[0].iterations)
        assertEquals(baseMaterial.kdfMemoryKib, kdfRequests[0].memoryKib)
        assertEquals(baseMaterial.kdfParallelism, kdfRequests[0].parallelism)
        assertEquals(baseMaterial.kdfOutputLen, kdfRequests[0].outputLengthBytes)
        assertArrayEquals(baseMaterial.kekEncMaster, unwrapRequests.single().wrappedKey)
        assertArrayEquals(oldMasterKey, unwrapRequests.single().wrappingKey)
        assertArrayEquals(activeKek, wrapRequests.single().keyToWrap)
        assertArrayEquals(newMasterKey, wrapRequests.single().wrappingKey)
        assertEquals(1, updatedWrappers.size)
        assertArrayEquals(newMasterWrapper, updatedWrappers.single())
        coVerify(exactly = 1) { remoteRepository.getVersionedKeyMaterial() }
        coVerify(exactly = 1) {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        }
        coVerifyOrder {
            remoteRepository.getVersionedKeyMaterial()
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        }
        verify(exactly = 0) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 0) { vaultSessionManager.clearQuickUnlockEnrollment() }
        verify(exactly = 0) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
    }

    @Test
    fun `change passphrase uses remote material even when local cache is stale`() {
        stubSuccessfulCrypto()
        stubInitialVersion()
        every { localRepository.read() } returns VaultKeyMaterialLocalReadResult.Present(
            baseMaterial.copy(kekEncMaster = thirdMasterWrapper),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns successConfirmation(newEtag)

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(ChangeVaultPassphraseResult.Success, result)
        assertArrayEquals(baseMaterial.kekEncMaster, unwrapRequests.single().wrappedKey)
        assertEquals(2, updatedWrappers.size)
        assertArrayEquals(baseMaterial.kekEncMaster, updatedWrappers[0])
        assertArrayEquals(newMasterWrapper, updatedWrappers[1])
        verify(exactly = 0) { localRepository.save(any()) }
        verify(exactly = 1) { localRepository.read() }
    }

    @Test
    fun `change passphrase when validated remote base cannot be cached then fails closed before put`() {
        stubSuccessfulCrypto()
        every { localRepository.read() } returns VaultKeyMaterialLocalReadResult.Present(
            baseMaterial.copy(kekEncMaster = thirdMasterWrapper),
        )
        stubInitialVersion()
        every { localRepository.updateMasterWrappedKek(any()) } throws IllegalStateException("storage failed")

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ReconciliationRequired),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any(), any()) }
        verify(exactly = 1) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 1) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
    }

    @Test
    fun `change passphrase when remote supersedes cache and old credential is submitted then reconciles and locks`() {
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returns versioned(baseMaterial.copy(kekEncMaster = thirdMasterWrapper), thirdEtag)
        every { kdfEngine.deriveKey(any()) } returns oldMasterKey.copyOf()
        every { keyWrapping.unwrapKey(any()) } throws IllegalStateException("authentication failed")

        val result = invoke("stale-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ConcurrentRemoteChange),
            result,
        )
        assertEquals(1, updatedWrappers.size)
        assertArrayEquals(thirdMasterWrapper, updatedWrappers.single())
        verify(exactly = 0) { localRepository.save(any()) }
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any(), any()) }
        verify(exactly = 1) { vaultSessionManager.clearQuickUnlockEnrollment() }
        verify(exactly = 1) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
    }

    @Test
    fun `change passphrase when current credential is invalid then does not put`() {
        stubInitialVersion()
        every { kdfEngine.deriveKey(any()) } returns oldMasterKey.copyOf()
        every { keyWrapping.unwrapKey(any()) } throws IllegalStateException("authentication failed")

        val result = invoke("wrong-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidCurrentPassphrase),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any(), any()) }
        verify(exactly = 0) { localRepository.updateMasterWrappedKek(any()) }
        verify(exactly = 0) { localRepository.save(any()) }
        verify(exactly = 0) { vaultSessionManager.clearQuickUnlockEnrollment() }
    }

    @Test
    fun `change passphrase when etag is not strong and opaque then rejects before put`() {
        stubSuccessfulCrypto()
        val invalidEtags = listOf(
            "",
            "master-1",
            "W/\"master-1\"",
            "*",
            "\"master-1",
            "\"master-1\", \"master-2\"",
            "\"master-1\r\"",
            "\"master-1\n\"",
        )

        invalidEtags.forEach { invalidEtag ->
            coEvery {
                remoteRepository.getVersionedKeyMaterial()
            } returns versioned(baseMaterial, invalidEtag)

            val result = invoke("current-passphrase", "new-passphrase")

            assertEquals(
                ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidLocalKeyMaterial),
                result,
            )
        }

        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any(), any()) }
        verify(exactly = 0) { localRepository.save(any()) }
        verify(exactly = 0) { vaultSessionManager.clearQuickUnlockEnrollment() }
    }

    @Test
    fun `change passphrase when revision conflict reconciles candidate without retrying put`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returnsMany listOf(
            versioned(baseMaterial, baseEtag),
            versioned(baseMaterial.copy(kekEncMaster = newMasterWrapper), newEtag),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.MasterKeyRevisionConflict,
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(ChangeVaultPassphraseResult.Success, result)
        coVerify(exactly = 2) { remoteRepository.getVersionedKeyMaterial() }
        coVerify(exactly = 1) {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        }
        coVerifyOrder {
            remoteRepository.getVersionedKeyMaterial()
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
            remoteRepository.getVersionedKeyMaterial()
        }
        assertEquals(1, updatedWrappers.size)
        assertArrayEquals(newMasterWrapper, updatedWrappers.single())
        verify(exactly = 0) { vaultSessionManager.clearQuickUnlockEnrollment() }
        verify(exactly = 0) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
    }

    @Test
    fun `change passphrase when revision conflict reconciles base as not applied`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returnsMany listOf(
            versioned(baseMaterial, baseEtag),
            versioned(baseMaterial, baseEtag),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.MasterKeyRevisionConflict,
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.RemoteChangeNotApplied),
            result,
        )
        assertTrue(updatedWrappers.isEmpty())
        verify(exactly = 0) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 0) { vaultSessionManager.clearQuickUnlockEnrollment() }
        verify(exactly = 0) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
    }

    @Test
    fun `change passphrase when revision conflict reconciles third wrapper and locks manually`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returnsMany listOf(
            versioned(baseMaterial, baseEtag),
            versioned(baseMaterial.copy(kekEncMaster = thirdMasterWrapper), thirdEtag),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.MasterKeyRevisionConflict,
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ConcurrentRemoteChange),
            result,
        )
        assertEquals(1, updatedWrappers.size)
        assertArrayEquals(thirdMasterWrapper, updatedWrappers.single())
        verify(exactly = 0) { localRepository.save(any()) }
        verify(exactly = 1) { vaultSessionManager.clearQuickUnlockEnrollment() }
        verify(exactly = 1) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
        verifyOrder {
            vaultSessionManager.clearQuickUnlockEnrollment()
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
        }
        verify(exactly = 0) { localRepository.clearMasterWrappedKek() }
    }

    @Test
    fun `change passphrase when quick unlock cleanup fails still locks manually`() {
        every { vaultSessionManager.clearQuickUnlockEnrollment() } returns QuickUnlockCleanupResult.Failed
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returnsMany listOf(
            versioned(baseMaterial, baseEtag),
            versioned(baseMaterial.copy(kekEncMaster = thirdMasterWrapper), thirdEtag),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.MasterKeyRevisionConflict,
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ConcurrentRemoteChange),
            result,
        )
        verify(exactly = 1) { vaultSessionManager.clearQuickUnlockEnrollment() }
        verify(exactly = 1) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
        verifyOrder {
            vaultSessionManager.clearQuickUnlockEnrollment()
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
        }
    }

    @Test
    fun `change passphrase when put response is lost and candidate is remote then succeeds`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returnsMany listOf(
            versioned(baseMaterial, baseEtag),
            versioned(baseMaterial.copy(kekEncMaster = newMasterWrapper), newEtag),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IOException()),
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(ChangeVaultPassphraseResult.Success, result)
        coVerify(exactly = 2) { remoteRepository.getVersionedKeyMaterial() }
        coVerify(exactly = 1) {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        }
        verify(exactly = 0) { vaultSessionManager.clearQuickUnlockEnrollment() }
    }

    @Test
    fun `change passphrase when put response is lost and base is remote then reports not applied`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returnsMany listOf(
            versioned(baseMaterial, baseEtag),
            versioned(baseMaterial, baseEtag),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IOException()),
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.RemoteChangeNotApplied),
            result,
        )
        verify(exactly = 0) { vaultSessionManager.clearQuickUnlockEnrollment() }
        verify(exactly = 0) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
    }

    @Test
    fun `change passphrase when put response is lost and third wrapper is remote then returns conflict`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returnsMany listOf(
            versioned(baseMaterial, baseEtag),
            versioned(baseMaterial.copy(kekEncMaster = thirdMasterWrapper), thirdEtag),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IOException()),
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ConcurrentRemoteChange),
            result,
        )
        verify(exactly = 1) { vaultSessionManager.clearQuickUnlockEnrollment() }
        verify(exactly = 1) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
        verifyOrder {
            vaultSessionManager.clearQuickUnlockEnrollment()
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
        }
    }

    @Test
    fun `change passphrase when reconciliation fails clears master cache and locks manually`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returnsMany listOf(
            versioned(baseMaterial, baseEtag),
            VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.NetworkError(IOException())),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IOException()),
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ReconciliationRequired),
            result,
        )
        verify(exactly = 1) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 1) { vaultSessionManager.clearQuickUnlockEnrollment() }
        verify(exactly = 1) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
        verifyOrder {
            vaultSessionManager.clearQuickUnlockEnrollment()
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
        }
    }

    @Test
    fun `change passphrase when update response violates contract reconciles without a second put`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returnsMany listOf(
            versioned(baseMaterial, baseEtag),
            versioned(baseMaterial.copy(kekEncMaster = newMasterWrapper), newEtag),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.ContractViolation,
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(ChangeVaultPassphraseResult.Success, result)
        coVerify(exactly = 1) {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        }
        coVerify(exactly = 2) { remoteRepository.getVersionedKeyMaterial() }
    }

    @Test
    fun `change passphrase when successful update has malformed etag reconciles without a second put`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returnsMany listOf(
            versioned(baseMaterial, baseEtag),
            versioned(baseMaterial.copy(kekEncMaster = newMasterWrapper), newEtag),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns successConfirmation("W/\"master-2\"")

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(ChangeVaultPassphraseResult.Success, result)
        coVerify(exactly = 1) {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        }
        coVerify(exactly = 2) { remoteRepository.getVersionedKeyMaterial() }
    }

    @Test
    fun `change passphrase when confirmed wrapper cannot be cached fails closed`() {
        stubSuccessfulCrypto()
        stubInitialVersion()
        every { localRepository.updateMasterWrappedKek(any()) } throws IllegalStateException("cache failure")
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns successConfirmation(newEtag)

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ReconciliationRequired),
            result,
        )
        verify(exactly = 1) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 1) { vaultSessionManager.clearQuickUnlockEnrollment() }
        verify(exactly = 1) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
        verifyOrder {
            vaultSessionManager.clearQuickUnlockEnrollment()
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
        }
    }

    @Test
    fun `change passphrase when new passphrase is empty then stops before reading secrets or remote`() {
        val result = invoke("current-passphrase", "")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidNewPassphrase),
            result,
        )
        verify(exactly = 0) { vaultKekProvider.snapshot() }
        verify(exactly = 0) { localRepository.read() }
        coVerify(exactly = 0) { remoteRepository.getVersionedKeyMaterial() }
    }

    @Test
    fun `change passphrase when active kek is unavailable or empty then stops before remote`() {
        every { vaultKekProvider.snapshot() } returns null

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ActiveKekUnavailable),
            invoke("current-passphrase", "new-passphrase"),
        )

        every { vaultKekProvider.snapshot() } returns byteArrayOf()

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ActiveKekUnavailable),
            invoke("current-passphrase", "new-passphrase"),
        )
        verify(exactly = 0) { localRepository.read() }
        coVerify(exactly = 0) { remoteRepository.getVersionedKeyMaterial() }
    }

    @Test
    fun `change passphrase when local material is absent corrupted unreadable or invalid then stops before remote`() {
        val invalidMaterials = listOf(
            baseMaterial.copy(accountId = null),
            baseMaterial.copy(kekEncMaster = byteArrayOf()),
            baseMaterial.copy(kekEncRecovery = byteArrayOf()),
            baseMaterial.copy(kdfAlgorithm = ""),
            baseMaterial.copy(kdfSalt = byteArrayOf()),
            baseMaterial.copy(kdfMemoryKib = 0),
            baseMaterial.copy(kdfIterations = 0),
            baseMaterial.copy(kdfParallelism = 0),
            baseMaterial.copy(kdfOutputLen = 0),
            baseMaterial.copy(cryptoVersion = ""),
        )
        val invalidReadResults = listOf<VaultKeyMaterialLocalReadResult>(
            VaultKeyMaterialLocalReadResult.Absent,
            VaultKeyMaterialLocalReadResult.Corrupted,
        ) + invalidMaterials.map(VaultKeyMaterialLocalReadResult::Present)

        invalidReadResults.forEach { readResult ->
            every { localRepository.read() } returns readResult

            assertEquals(
                ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidLocalKeyMaterial),
                invoke("current-passphrase", "new-passphrase"),
            )
        }

        every { localRepository.read() } throws IllegalStateException("storage unavailable")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidLocalKeyMaterial),
            invoke("current-passphrase", "new-passphrase"),
        )
        coVerify(exactly = 0) { remoteRepository.getVersionedKeyMaterial() }
    }

    @Test
    fun `change passphrase when initial remote read fails then preserves local state`() {
        coEvery { remoteRepository.getVersionedKeyMaterial() } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.Unauthorized,
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.RemoteFailure(VaultKeyMaterialRemoteError.Unauthorized),
            ),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any(), any()) }
        verify(exactly = 0) { localRepository.save(any()) }
        verify(exactly = 0) { localRepository.clearMasterWrappedKek() }
    }

    @Test
    fun `change passphrase when crypto preparation fails then never sends put`() {
        stubInitialVersion()
        every { kdfEngine.deriveKey(any()) } throws IllegalStateException("kdf failed")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidLocalKeyMaterial),
            invoke("current-passphrase", "new-passphrase"),
        )

        every { kdfEngine.deriveKey(any()) } returns oldMasterKey.copyOf()
        every { keyWrapping.unwrapKey(any()) } throws IllegalArgumentException("malformed wrapper")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidLocalKeyMaterial),
            invoke("current-passphrase", "new-passphrase"),
        )

        every { keyWrapping.unwrapKey(any()) } returns ByteArray(activeKek.size) { 1 }

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidCurrentPassphrase),
            invoke("current-passphrase", "new-passphrase"),
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any(), any()) }
    }

    @Test
    fun `change passphrase when new key derivation or wrapping fails then never sends put`() {
        stubInitialVersion()
        var deriveCalls = 0
        every { kdfEngine.deriveKey(any()) } answers {
            deriveCalls += 1
            if (deriveCalls % 2 == 1) oldMasterKey.copyOf() else throw IllegalStateException("kdf failed")
        }
        every { keyWrapping.unwrapKey(any()) } answers { activeKek.copyOf() }

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.CryptoFailure),
            invoke("current-passphrase", "new-passphrase"),
        )

        every { kdfEngine.deriveKey(any()) } returnsMany listOf(
            oldMasterKey.copyOf(),
            newMasterKey.copyOf(),
            oldMasterKey.copyOf(),
            newMasterKey.copyOf(),
            oldMasterKey.copyOf(),
            newMasterKey.copyOf(),
        )
        every { keyWrapping.wrapKey(any()) } throws IllegalStateException("wrap failed")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.CryptoFailure),
            invoke("current-passphrase", "new-passphrase"),
        )

        listOf(byteArrayOf(), oldMasterWrapper.copyOf()).forEach { invalidWrapper ->
            every { keyWrapping.wrapKey(any()) } returns invalidWrapper

            assertEquals(
                ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.CryptoFailure),
                invoke("current-passphrase", "new-passphrase"),
            )
        }
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any(), any()) }
    }

    @Test
    fun `change passphrase when put is definitively rejected then does not reconcile`() {
        stubSuccessfulCrypto()
        val terminalErrors = listOf<VaultKeyMaterialRemoteError>(
            VaultKeyMaterialRemoteError.Unauthorized,
            VaultKeyMaterialRemoteError.Forbidden,
            VaultKeyMaterialRemoteError.VaultNotInitialized,
            VaultKeyMaterialRemoteError.PreconditionRequired,
            VaultKeyMaterialRemoteError.HttpError(NetworkFailureClassifier.fromHttpStatus(400)),
        )

        terminalErrors.forEach { terminalError ->
            stubInitialVersion()
            coEvery {
                remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
            } returns VaultKeyMaterialRemoteResult.Error(terminalError)

            assertEquals(
                ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.RemoteFailure(terminalError)),
                invoke("current-passphrase", "new-passphrase"),
            )
        }
        coVerify(exactly = terminalErrors.size) { remoteRepository.getVersionedKeyMaterial() }
        coVerify(exactly = terminalErrors.size) {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        }
        verify(exactly = 0) { localRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when reconciliation material or version is incompatible then fails closed`() {
        stubSuccessfulCrypto()
        val incompatibleVersions = listOf(
            versioned(baseMaterial.copy(accountId = null), newEtag),
            versioned(baseMaterial.copy(kekEncMaster = byteArrayOf()), newEtag),
            versioned(baseMaterial.copy(kekEncRecovery = byteArrayOf()), newEtag),
            versioned(baseMaterial.copy(kdfAlgorithm = ""), newEtag),
            versioned(baseMaterial.copy(kdfSalt = byteArrayOf()), newEtag),
            versioned(baseMaterial.copy(kdfMemoryKib = 0), newEtag),
            versioned(baseMaterial.copy(kdfIterations = 0), newEtag),
            versioned(baseMaterial.copy(kdfParallelism = 0), newEtag),
            versioned(baseMaterial.copy(kdfOutputLen = 0), newEtag),
            versioned(baseMaterial.copy(cryptoVersion = ""), newEtag),
            versioned(baseMaterial.copy(accountId = UUID.randomUUID()), newEtag),
            versioned(baseMaterial.copy(kekEncRecovery = byteArrayOf(91)), newEtag),
            versioned(baseMaterial.copy(kdfAlgorithm = "other"), newEtag),
            versioned(baseMaterial.copy(kdfSalt = byteArrayOf(92)), newEtag),
            versioned(baseMaterial.copy(kdfMemoryKib = baseMaterial.kdfMemoryKib + 1), newEtag),
            versioned(baseMaterial.copy(kdfIterations = baseMaterial.kdfIterations + 1), newEtag),
            versioned(baseMaterial.copy(kdfParallelism = baseMaterial.kdfParallelism + 1), newEtag),
            versioned(baseMaterial.copy(kdfOutputLen = baseMaterial.kdfOutputLen + 1), newEtag),
            versioned(baseMaterial.copy(cryptoVersion = "v2"), newEtag),
            versioned(baseMaterial.copy(kekEncMaster = thirdMasterWrapper), "W/\"master-2\""),
        )

        incompatibleVersions.forEachIndexed { index, incompatibleVersion ->
            coEvery {
                remoteRepository.getVersionedKeyMaterial()
            } returnsMany listOf(versioned(baseMaterial, baseEtag), incompatibleVersion)
            coEvery {
                remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
            } returns VaultKeyMaterialRemoteResult.Error(
                VaultKeyMaterialRemoteError.MasterKeyRevisionConflict,
            )

            assertEquals(
                "incompatible version index $index",
                ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ReconciliationRequired),
                invoke("current-passphrase", "new-passphrase"),
            )
        }
        verify(exactly = incompatibleVersions.size) { localRepository.clearMasterWrappedKek() }
        verify(exactly = incompatibleVersions.size) {
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
        }
    }

    @Test
    fun `change passphrase when concurrent remote wrapper cannot be cached then fails closed`() {
        every { localRepository.updateMasterWrappedKek(any()) } throws IllegalStateException("storage failed")
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returnsMany listOf(
            versioned(baseMaterial, baseEtag),
            versioned(baseMaterial.copy(kekEncMaster = thirdMasterWrapper), thirdEtag),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(newMasterWrapper, baseEtag)
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.MasterKeyRevisionConflict,
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ReconciliationRequired),
            result,
        )
        verify(exactly = 1) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 1) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
    }

    @Test
    fun `change passphrase when vault is locked then does not access remote`() {
        every { vaultSessionManager.isUnlocked() } returns false
        every { vaultSessionManager.vaultState } returns MutableStateFlow(VaultState.Locked)

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.InvalidVaultState(VaultState.Locked),
            ),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.getVersionedKeyMaterial() }
        verify(exactly = 0) { vaultKekProvider.snapshot() }
    }

    @Test
    fun `two clients sharing cas backend have exactly one winner`() = runBlocking {
        val backend = FakeCasBackend(
            VersionedVaultKeyMaterial(
                material = snapshot(baseMaterial),
                etag = baseEtag,
            ),
        )
        val clientA = createClientFixture(newMasterWrapper, backend)
        val clientB = createClientFixture(thirdMasterWrapper, backend)

        val resultA = async { clientA.target("current-passphrase", "new-passphrase-a") }
        val resultB = async { clientB.target("current-passphrase", "new-passphrase-b") }
        val valueA = resultA.await()
        val valueB = resultB.await()
        val results = listOf(valueA, valueB)

        assertEquals(
            results.toString(),
            1,
            results.count { it == ChangeVaultPassphraseResult.Success },
        )
        assertEquals(
            results.toString(),
            1,
            results.count {
                it == ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ConcurrentRemoteChange)
            },
        )
        val current = backend.currentMaterial()
        assertTrue(
            current.material.kekEncMaster.contentEquals(newMasterWrapper) ||
                current.material.kekEncMaster.contentEquals(thirdMasterWrapper),
        )
        coVerify(exactly = 1) { clientA.remote.updateMasterWrappedKek(any(), baseEtag) }
        coVerify(exactly = 1) { clientB.remote.updateMasterWrappedKek(any(), baseEtag) }
        if (valueA == ChangeVaultPassphraseResult.Success) {
            verify(exactly = 0) { clientA.session.clearQuickUnlockEnrollment() }
            verify(exactly = 1) { clientB.session.clearQuickUnlockEnrollment() }
            verify(exactly = 0) { clientA.session.lock(QuickUnlockPromptMode.ManualOnly) }
            verify(exactly = 1) { clientB.session.lock(QuickUnlockPromptMode.ManualOnly) }
        } else {
            verify(exactly = 1) { clientA.session.clearQuickUnlockEnrollment() }
            verify(exactly = 0) { clientB.session.clearQuickUnlockEnrollment() }
            verify(exactly = 1) { clientA.session.lock(QuickUnlockPromptMode.ManualOnly) }
            verify(exactly = 0) { clientB.session.lock(QuickUnlockPromptMode.ManualOnly) }
        }
    }

    private fun stubInitialVersion() {
        coEvery {
            remoteRepository.getVersionedKeyMaterial()
        } returns versioned(baseMaterial, baseEtag)
    }

    private fun stubSuccessfulCrypto() {
        every { kdfEngine.deriveKey(any()) } answers {
            val request = firstArg<KdfRequest>()
            kdfRequests += request.copy(
                secret = request.secret.copyOf(),
                salt = request.salt.copyOf(),
            )
            if (kdfRequests.size % 2 == 1) oldMasterKey.copyOf() else newMasterKey.copyOf()
        }
        every { keyWrapping.unwrapKey(any()) } answers {
            val request = firstArg<KeyUnwrapRequest>()
            unwrapRequests += request.copy(
                wrappedKey = request.wrappedKey.copyOf(),
                wrappingKey = request.wrappingKey.copyOf(),
            )
            activeKek.copyOf()
        }
        every { keyWrapping.wrapKey(any()) } answers {
            val request = firstArg<KeyWrapRequest>()
            wrapRequests += request.copy(
                keyToWrap = request.keyToWrap.copyOf(),
                wrappingKey = request.wrappingKey.copyOf(),
            )
            newMasterWrapper.copyOf()
        }
    }

    private fun createTarget(): ChangeVaultPassphraseUseCase = ChangeVaultPassphraseUseCase(
        vaultSessionManager = vaultSessionManager,
        vaultKekProvider = vaultKekProvider,
        vaultKeyMaterialLocalRepository = localRepository,
        vaultKeyMaterialRemoteRepository = remoteRepository,
        kdfEngine = kdfEngine,
        keyWrapping = keyWrapping,
    )

    private fun invoke(
        currentPassphrase: String,
        newPassphrase: String,
    ): ChangeVaultPassphraseResult = runBlocking {
        createTarget()(currentPassphrase, newPassphrase)
    }

    private fun versioned(
        material: VaultKeyMaterial,
        etag: String,
    ): VaultKeyMaterialRemoteResult.Success<VersionedVaultKeyMaterial> =
        VaultKeyMaterialRemoteResult.Success(
            VersionedVaultKeyMaterial(
                material = snapshot(material),
                etag = etag,
            ),
        )

    private fun successConfirmation(
        etag: String,
    ): VaultKeyMaterialRemoteResult.Success<MasterWrapperUpdateConfirmation> =
        VaultKeyMaterialRemoteResult.Success(MasterWrapperUpdateConfirmation(etag = etag))

    private fun snapshot(material: VaultKeyMaterial): VaultKeyMaterial = material.copy(
        kekEncMaster = material.kekEncMaster.copyOf(),
        kekEncRecovery = material.kekEncRecovery.copyOf(),
        kdfSalt = material.kdfSalt.copyOf(),
    )

    private fun sampleVaultKeyMaterial(masterWrapper: ByteArray): VaultKeyMaterial = VaultKeyMaterial(
        accountId = UUID.fromString("11111111-2222-3333-4444-555555555555"),
        kekEncMaster = masterWrapper.copyOf(),
        kekEncRecovery = byteArrayOf(11, 12, 13, 14),
        kdfAlgorithm = "argon2id",
        kdfSalt = byteArrayOf(21, 22, 23, 24),
        kdfMemoryKib = 65536,
        kdfIterations = 3,
        kdfParallelism = 1,
        kdfOutputLen = 32,
        cryptoVersion = "v1",
    )

    private fun createClientFixture(
        candidateWrapper: ByteArray,
        backend: FakeCasBackend,
    ): ClientFixture {
        val session = mockk<VaultSessionManager>()
        val kekProvider = mockk<VaultKekProvider>()
        val local = mockk<VaultKeyMaterialLocalRepository>()
        val remote = mockk<VaultKeyMaterialRemoteRepository>()
        val kdf = mockk<KdfEngine>()
        val wrapping = mockk<KeyWrapping>()
        val deriveCalls = AtomicInteger(0)

        every { session.isUnlocked() } returns true
        every { session.clearQuickUnlockEnrollment() } returns QuickUnlockCleanupResult.Cleared
        every { session.lock(QuickUnlockPromptMode.ManualOnly) } just Runs
        every { kekProvider.snapshot() } answers { activeKek.copyOf() }
        every { local.read() } returns VaultKeyMaterialLocalReadResult.Present(baseMaterial)
        every { local.save(any()) } just Runs
        every { local.updateMasterWrappedKek(any()) } just Runs
        every { local.clearMasterWrappedKek() } just Runs
        every { kdf.deriveKey(any()) } answers {
            if (deriveCalls.incrementAndGet() % 2 == 1) oldMasterKey.copyOf() else newMasterKey.copyOf()
        }
        every { wrapping.unwrapKey(any()) } returns activeKek.copyOf()
        every { wrapping.wrapKey(any()) } returns candidateWrapper.copyOf()
        coEvery { remote.getVersionedKeyMaterial() } coAnswers { backend.getVersioned() }
        coEvery {
            remote.updateMasterWrappedKek(any(), any())
        } coAnswers {
            backend.update(firstArg(), secondArg())
        }

        return ClientFixture(
            session = session,
            remote = remote,
            target = ChangeVaultPassphraseUseCase(
                vaultSessionManager = session,
                vaultKekProvider = kekProvider,
                vaultKeyMaterialLocalRepository = local,
                vaultKeyMaterialRemoteRepository = remote,
                kdfEngine = kdf,
                keyWrapping = wrapping,
            ),
        )
    }

    private data class ClientFixture(
        val session: VaultSessionManager,
        val remote: VaultKeyMaterialRemoteRepository,
        val target: ChangeVaultPassphraseUseCase,
    )

    private inner class FakeCasBackend(initial: VersionedVaultKeyMaterial) {
        private val stateMutex = Mutex()
        private val initialGetCount = AtomicInteger(0)
        private val bothInitialGets = CompletableDeferred<Unit>()
        private var current = initial

        suspend fun getVersioned(): VaultKeyMaterialRemoteResult<VersionedVaultKeyMaterial> {
            val snapshot = stateMutex.withLock { copyVersion(current) }
            if (initialGetCount.incrementAndGet() <= 2) {
                if (initialGetCount.get() == 2) {
                    bothInitialGets.complete(Unit)
                }
                bothInitialGets.await()
            }
            return VaultKeyMaterialRemoteResult.Success(snapshot)
        }

        suspend fun update(
            newKekEncMaster: ByteArray,
            ifMatch: String,
        ): VaultKeyMaterialRemoteResult<MasterWrapperUpdateConfirmation> = stateMutex.withLock {
            if (ifMatch != current.etag) {
                return@withLock VaultKeyMaterialRemoteResult.Error(
                    VaultKeyMaterialRemoteError.MasterKeyRevisionConflict,
                )
            }
            current = VersionedVaultKeyMaterial(
                material = current.material.copy(kekEncMaster = newKekEncMaster.copyOf()),
                etag = if (current.etag == baseEtag) newEtag else thirdEtag,
            )
            VaultKeyMaterialRemoteResult.Success(
                MasterWrapperUpdateConfirmation(etag = current.etag),
            )
        }

        suspend fun currentMaterial(): VersionedVaultKeyMaterial = stateMutex.withLock { copyVersion(current) }

        private fun copyVersion(value: VersionedVaultKeyMaterial): VersionedVaultKeyMaterial =
            VersionedVaultKeyMaterial(
                material = snapshot(value.material),
                etag = value.etag,
            )
    }
}
