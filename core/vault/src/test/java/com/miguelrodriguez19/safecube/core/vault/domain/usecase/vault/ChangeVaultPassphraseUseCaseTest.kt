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
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalReadResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultKekProvider
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.Runs
import io.mockk.verify
import java.io.IOException
import java.util.UUID
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.flow.MutableStateFlow

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
    private val oldMasterKey = ByteArray(32) { index -> (index + 51).toByte() }
    private val newMasterKey = ByteArray(32) { index -> (index + 81).toByte() }
    private val cachedMaterial = sampleVaultKeyMaterial(oldMasterWrapper)
    private val kdfRequests = mutableListOf<KdfRequest>()
    private val unwrapRequests = mutableListOf<KeyUnwrapRequest>()
    private val wrapRequests = mutableListOf<KeyWrapRequest>()

    @Before
    fun setUp() {
        every { vaultSessionManager.isUnlocked() } returns true
        every { vaultSessionManager.lock() } just Runs
        every { vaultKekProvider.snapshot() } answers { activeKek.copyOf() }
        every { localRepository.read() } returns VaultKeyMaterialLocalReadResult.Present(
            cachedMaterial,
        )
        every { localRepository.updateMasterWrappedKek(any()) } just Runs
        every { localRepository.clearMasterWrappedKek() } just Runs
    }

    @Test
    fun `change passphrase when valid then rewraps same kek and updates only master wrapper`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.updateMasterWrappedKek(any())
        } returns VaultKeyMaterialRemoteResult.Success(Unit)

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(ChangeVaultPassphraseResult.Success, result)
        assertEquals(2, kdfRequests.size)
        assertEquals("current-passphrase", kdfRequests[0].secret.decodeToString())
        assertEquals("new-passphrase", kdfRequests[1].secret.decodeToString())
        assertArrayEquals(cachedMaterial.kdfSalt, kdfRequests[0].salt)
        assertEquals(cachedMaterial.kdfIterations, kdfRequests[0].iterations)
        assertEquals(cachedMaterial.kdfMemoryKib, kdfRequests[0].memoryKib)
        assertEquals(cachedMaterial.kdfParallelism, kdfRequests[0].parallelism)
        assertEquals(cachedMaterial.kdfOutputLen, kdfRequests[0].outputLengthBytes)
        assertArrayEquals(cachedMaterial.kekEncMaster, unwrapRequests.single().wrappedKey)
        assertArrayEquals(oldMasterKey, unwrapRequests.single().wrappingKey)
        assertArrayEquals(activeKek, wrapRequests.single().keyToWrap)
        assertArrayEquals(newMasterKey, wrapRequests.single().wrappingKey)
        coVerify(exactly = 1) { remoteRepository.updateMasterWrappedKek(newMasterWrapper) }
        verify(exactly = 1) { localRepository.updateMasterWrappedKek(newMasterWrapper) }
        verify(exactly = 0) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 0) { vaultSessionManager.lock() }
    }

    @Test
    fun `change passphrase when current credential is invalid then does not call remote`() {
        every { kdfEngine.deriveKey(any()) } returns oldMasterKey.copyOf()
        every { keyWrapping.unwrapKey(any()) } throws IllegalStateException("authentication failed")

        val result = invoke("wrong-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidCurrentPassphrase),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
        verify(exactly = 0) { localRepository.updateMasterWrappedKek(any()) }
        verify(exactly = 0) { vaultSessionManager.lock() }
    }

    @Test
    fun `change passphrase when unwrapped kek does not match active kek then does not call remote`() {
        every { kdfEngine.deriveKey(any()) } returns oldMasterKey.copyOf()
        every { keyWrapping.unwrapKey(any()) } returns ByteArray(32) { 0 }

        val result = invoke("wrong-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidCurrentPassphrase),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
        verify(exactly = 0) { localRepository.updateMasterWrappedKek(any()) }
        verify(exactly = 0) { vaultSessionManager.lock() }
    }

    @Test
    fun `change passphrase when remote returns unauthorized then preserves local state`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.updateMasterWrappedKek(any())
        } returns VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.Unauthorized)

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.RemoteFailure(VaultKeyMaterialRemoteError.Unauthorized),
            ),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.getKeyMaterial() }
        verify(exactly = 0) { localRepository.updateMasterWrappedKek(any()) }
        verify(exactly = 0) { vaultSessionManager.lock() }
    }

    @Test
    fun `change passphrase when remote returns server error then returns retryable remote error`() {
        stubSuccessfulCrypto()
        val remoteError = VaultKeyMaterialRemoteError.HttpError(
            failure = NetworkFailureClassifier.fromHttpStatus(503),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(any())
        } returns VaultKeyMaterialRemoteResult.Error(remoteError)

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.RemoteFailure(remoteError)), result)
        assertEquals(
            com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision.Retryable,
            (result as ChangeVaultPassphraseResult.Error).reason.retryDecision,
        )
        coVerify(exactly = 0) { remoteRepository.getKeyMaterial() }
        verify(exactly = 0) { localRepository.updateMasterWrappedKek(any()) }
        verify(exactly = 0) { vaultSessionManager.lock() }
    }

    @Test
    fun `change passphrase when put response is lost and get confirms new wrapper then succeeds`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.updateMasterWrappedKek(any())
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IOException()),
        )
        coEvery { remoteRepository.getKeyMaterial() } returns VaultKeyMaterialRemoteResult.Success(
            cachedMaterial.copy(kekEncMaster = newMasterWrapper.copyOf()),
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(ChangeVaultPassphraseResult.Success, result)
        coVerify(exactly = 1) { remoteRepository.getKeyMaterial() }
        verify(exactly = 1) { localRepository.updateMasterWrappedKek(newMasterWrapper) }
        verify(exactly = 0) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 0) { vaultSessionManager.lock() }
    }

    @Test
    fun `change passphrase when put response is lost and get confirms old wrapper then reports not applied`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.updateMasterWrappedKek(any())
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IOException()),
        )
        coEvery { remoteRepository.getKeyMaterial() } returns VaultKeyMaterialRemoteResult.Success(
            cachedMaterial,
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.RemoteChangeNotApplied),
            result,
        )
        verify(exactly = 0) { localRepository.updateMasterWrappedKek(any()) }
        verify(exactly = 0) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 0) { vaultSessionManager.lock() }
    }

    @Test
    fun `change passphrase when reconciliation is uncertain then clears master cache and locks vault`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.updateMasterWrappedKek(any())
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IOException()),
        )
        coEvery {
            remoteRepository.getKeyMaterial()
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IOException()),
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ReconciliationRequired),
            result,
        )
        val error = (result as ChangeVaultPassphraseResult.Error).reason
        assertTrue(error.requiresConnection)
        assertEquals(
            com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision.Retryable,
            error.retryDecision,
        )
        verify(exactly = 1) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 1) { vaultSessionManager.lock() }
        verify(exactly = 0) { localRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when reconciliation returns divergent wrapper then clears master cache and locks vault`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.updateMasterWrappedKek(any())
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IOException()),
        )
        coEvery { remoteRepository.getKeyMaterial() } returns VaultKeyMaterialRemoteResult.Success(
            cachedMaterial.copy(kekEncMaster = byteArrayOf(41, 42, 43, 44)),
        )

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ReconciliationRequired),
            result,
        )
        verify(exactly = 1) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 1) { vaultSessionManager.lock() }
    }

    @Test
    fun `change passphrase when confirmed wrapper cannot be cached then clears master cache and locks vault`() {
        stubSuccessfulCrypto()
        every {
            localRepository.updateMasterWrappedKek(any())
        } throws IllegalStateException("cache failure")
        coEvery {
            remoteRepository.updateMasterWrappedKek(any())
        } returns VaultKeyMaterialRemoteResult.Success(Unit)

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ReconciliationRequired),
            result,
        )
        verify(exactly = 1) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 1) { vaultSessionManager.lock() }
    }

    @Test
    fun `change passphrase when vault is locked then does not access key material or remote`() {
        every { vaultSessionManager.isUnlocked() } returns false
        every { vaultSessionManager.vaultState } returns MutableStateFlow(VaultState.Locked)

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(
                ChangeVaultPassphraseError.InvalidVaultState(VaultState.Locked),
            ),
            result,
        )
        verify(exactly = 0) { localRepository.read() }
        verify(exactly = 0) { vaultKekProvider.snapshot() }
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when new passphrase is empty then does not access key material or remote`() {
        val result = invoke("current-passphrase", "")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidNewPassphrase),
            result,
        )
        verify(exactly = 0) { localRepository.read() }
        verify(exactly = 0) { vaultKekProvider.snapshot() }
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when active kek is unavailable then does not call remote`() {
        every { vaultKekProvider.snapshot() } returns null

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ActiveKekUnavailable),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when active kek is empty then does not call remote`() {
        every { vaultKekProvider.snapshot() } returns byteArrayOf()

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ActiveKekUnavailable),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when local material is absent then does not call remote`() {
        every { localRepository.read() } returns VaultKeyMaterialLocalReadResult.Absent

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidLocalKeyMaterial),
            result,
        )
        verify(exactly = 0) { vaultKekProvider.snapshot() }
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when local material is corrupted then does not call remote`() {
        every { localRepository.read() } returns VaultKeyMaterialLocalReadResult.Corrupted

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidLocalKeyMaterial),
            result,
        )
        verify(exactly = 0) { vaultKekProvider.snapshot() }
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when current key derivation fails then returns invalid local material`() {
        every { kdfEngine.deriveKey(any()) } throws IllegalStateException("kdf failure")

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidLocalKeyMaterial),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when cached wrapper is malformed then returns invalid local material`() {
        every { kdfEngine.deriveKey(any()) } returns oldMasterKey.copyOf()
        every { keyWrapping.unwrapKey(any()) } throws IllegalArgumentException("malformed wrapper")

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.InvalidLocalKeyMaterial),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when new key derivation fails then returns crypto failure`() {
        var deriveCalls = 0
        every { kdfEngine.deriveKey(any()) } answers {
            deriveCalls += 1
            if (deriveCalls == 1) oldMasterKey.copyOf()
            else throw IllegalStateException("kdf failure")
        }
        every { keyWrapping.unwrapKey(any()) } returns activeKek.copyOf()

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.CryptoFailure),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when wrapping fails then returns crypto failure`() {
        every { kdfEngine.deriveKey(any()) } returnsMany listOf(
            oldMasterKey.copyOf(),
            newMasterKey.copyOf(),
        )
        every { keyWrapping.unwrapKey(any()) } returns activeKek.copyOf()
        every { keyWrapping.wrapKey(any()) } throws IllegalStateException("wrap failure")

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.CryptoFailure),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when wrapper is empty then returns crypto failure`() {
        stubSuccessfulCrypto()
        every { keyWrapping.wrapKey(any()) } returns byteArrayOf()

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.CryptoFailure),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when wrapper is reused then returns crypto failure`() {
        stubSuccessfulCrypto()
        every { keyWrapping.wrapKey(any()) } returns oldMasterWrapper.copyOf()

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.CryptoFailure),
            result,
        )
        coVerify(exactly = 0) { remoteRepository.updateMasterWrappedKek(any()) }
    }

    @Test
    fun `change passphrase when reconciliation get throws then fails closed`() {
        stubSuccessfulCrypto()
        coEvery {
            remoteRepository.updateMasterWrappedKek(any())
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IOException()),
        )
        coEvery { remoteRepository.getKeyMaterial() } throws IOException("connection lost")

        val result = invoke("current-passphrase", "new-passphrase")

        assertEquals(
            ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ReconciliationRequired),
            result,
        )
        verify(exactly = 1) { localRepository.clearMasterWrappedKek() }
        verify(exactly = 1) { vaultSessionManager.lock() }
    }

    @Test
    fun `change passphrase when remote material has invalid fields then fails closed for every invalid shape`() {
        stubSuccessfulCrypto()
        val invalidRemoteMaterials = listOf(
            cachedMaterial.copy(accountId = null),
            cachedMaterial.copy(kekEncMaster = byteArrayOf()),
            cachedMaterial.copy(kekEncRecovery = byteArrayOf()),
            cachedMaterial.copy(kdfAlgorithm = ""),
            cachedMaterial.copy(kdfSalt = byteArrayOf()),
            cachedMaterial.copy(kdfMemoryKib = 0),
            cachedMaterial.copy(kdfIterations = 0),
            cachedMaterial.copy(kdfParallelism = 0),
            cachedMaterial.copy(kdfOutputLen = 0),
            cachedMaterial.copy(cryptoVersion = ""),
        )
        coEvery {
            remoteRepository.updateMasterWrappedKek(any())
        } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.NetworkError(IOException()),
        )
        invalidRemoteMaterials.forEachIndexed { index, invalidMaterial ->
            coEvery {
                remoteRepository.getKeyMaterial()
            } returns VaultKeyMaterialRemoteResult.Success(invalidMaterial)
            assertEquals(
                "invalid material index $index",
                ChangeVaultPassphraseResult.Error(ChangeVaultPassphraseError.ReconciliationRequired),
                invoke("current-passphrase", "new-passphrase"),
            )
        }

        verify(exactly = invalidRemoteMaterials.size) { localRepository.clearMasterWrappedKek() }
        verify(exactly = invalidRemoteMaterials.size) { vaultSessionManager.lock() }
    }

    private fun stubSuccessfulCrypto() {
        every { kdfEngine.deriveKey(any()) } answers {
            val request = firstArg<KdfRequest>()
            kdfRequests += request.copy(
                secret = request.secret.copyOf(),
                salt = request.salt.copyOf(),
            )
            if (kdfRequests.size == 1) oldMasterKey.copyOf() else newMasterKey.copyOf()
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
    ): ChangeVaultPassphraseResult = kotlinx.coroutines.runBlocking {
        createTarget()(currentPassphrase, newPassphrase)
    }

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
}
