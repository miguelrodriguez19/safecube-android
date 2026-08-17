package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.model.UnlockedKeyring
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalReadResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultUnlocker
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import java.io.IOException
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.random.Random

class VaultSessionManagerImplTest {

    private val vaultUnlocker = mockk<VaultUnlocker>()
    private val vaultKeyMaterialLocalRepository =
        mockk<VaultKeyMaterialLocalRepository>(relaxed = true)
    private val vaultKeyMaterialRemoteRepository = mockk<VaultKeyMaterialRemoteRepository>()
    private val vaultInMemoryKekStore = mockk<VaultInMemoryKekStore>(relaxed = true)

    private lateinit var target: VaultSessionManagerImpl

    @Before
    fun setup() {
        every { vaultKeyMaterialLocalRepository.read() } returns VaultKeyMaterialLocalReadResult.Absent
    }

    @Test
    fun `init_whenCreated_thenStateIsInitialLoading`() {
        target = createTarget()

        assertEquals(VaultState.InitialLoading, target.vaultState.value)
    }

    @Test
    fun `refreshVaultState_whenRemoteSuccess_thenSavesLocalAndClearsKekAndStateIsLocked`() =
        runBlocking {
            val remoteKeyMaterial = createVaultKeyMaterial()
            coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returns VaultKeyMaterialRemoteResult.Success(
                remoteKeyMaterial
            )
            target = createTarget()

            target.refreshVaultState()

            assertEquals(VaultState.Locked, target.vaultState.value)
            verify(exactly = 1) { vaultKeyMaterialLocalRepository.save(remoteKeyMaterial) }
            verify(exactly = 1) { vaultInMemoryKekStore.clear() }
        }

    @Test
    fun `refreshVaultState_whenRemoteVaultNotInitialized_thenClearsLocalAndKekAndStateIsNotInitialized`() =
        runBlocking {
            coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returns VaultKeyMaterialRemoteResult.Error(
                VaultKeyMaterialRemoteError.VaultNotInitialized
            )
            target = createTarget()

            target.refreshVaultState()

            assertEquals(VaultState.NotInitialized, target.vaultState.value)
            verify(exactly = 1) { vaultKeyMaterialLocalRepository.clear() }
            verify(exactly = 1) { vaultInMemoryKekStore.clear() }
        }

    @Test
    fun `refreshVaultState_whenRemoteUnauthorized_thenRequiresAuthentication`() = runBlocking {
        coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returns VaultKeyMaterialRemoteResult.Error(
            VaultKeyMaterialRemoteError.Unauthorized
        )
        target = createTarget()

        target.refreshVaultState()

        assertEquals(VaultState.AuthenticationRequired, target.vaultState.value)
        verify(exactly = 1) { vaultInMemoryKekStore.clear() }
    }

    @Test
    fun `refreshVaultState_whenRemoteNetworkErrorAndNoCache_thenStateIsRetryableWithoutLocalMaterial`() = runBlocking {
        val remoteError = VaultKeyMaterialRemoteError.NetworkError(IOException())
        coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returns VaultKeyMaterialRemoteResult.Error(
            remoteError,
        )
        target = createTarget()

        target.refreshVaultState()

        assertEquals(
            VaultState.RetryableRemoteFailure(
                failure = remoteError.failure,
                hasValidLocalKeyMaterial = false,
            ),
            target.vaultState.value,
        )
    }

    @Test
    fun `refreshVaultState_whenRemoteNetworkErrorWithCache_thenStateIsRetryableWithLocalMaterial`() = runBlocking {
        val localMaterial = createVaultKeyMaterial()
        val remoteError = VaultKeyMaterialRemoteError.NetworkError(IOException())
        every {
            vaultKeyMaterialLocalRepository.read()
        } returns VaultKeyMaterialLocalReadResult.Present(localMaterial)
        coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returns VaultKeyMaterialRemoteResult.Error(
            remoteError,
        )
        target = createTarget()

        target.refreshVaultState()

        assertEquals(
            VaultState.RetryableRemoteFailure(
                failure = remoteError.failure,
                hasValidLocalKeyMaterial = true,
            ),
            target.vaultState.value,
        )
    }

    @Test
    fun `refreshVaultState_whenLocalMaterialIsCorruptedAndRemoteUnavailable_thenStateIsCorrupted`() =
        runBlocking {
            every {
                vaultKeyMaterialLocalRepository.read()
            } returns VaultKeyMaterialLocalReadResult.Corrupted
            coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returns
                VaultKeyMaterialRemoteResult.Error(
                    VaultKeyMaterialRemoteError.NetworkError(IOException()),
                )
            target = createTarget()

            target.refreshVaultState()

            assertEquals(VaultState.CorruptedLocalKeyMaterial, target.vaultState.value)
        }

    @Test
    fun `refreshVaultState_whenLocalMaterialIsCorruptedAndRemoteSucceeds_thenRefreshesCacheOnly`() =
        runBlocking {
            val remoteKeyMaterial = createVaultKeyMaterial()
            every {
                vaultKeyMaterialLocalRepository.read()
            } returns VaultKeyMaterialLocalReadResult.Corrupted
            coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returns
                VaultKeyMaterialRemoteResult.Success(remoteKeyMaterial)
            target = createTarget()

            target.refreshVaultState()

            assertEquals(VaultState.Locked, target.vaultState.value)
            verify(exactly = 1) { vaultKeyMaterialLocalRepository.save(remoteKeyMaterial) }
            verify(exactly = 0) { vaultKeyMaterialLocalRepository.clear() }
        }

    @Test
    fun `refreshVaultState_whenRemoteForbidden_thenStaysInExplicitTerminalState`() = runBlocking {
        val remoteError = VaultKeyMaterialRemoteError.Forbidden
        coEvery { vaultKeyMaterialRemoteRepository.getKeyMaterial() } returns
            VaultKeyMaterialRemoteResult.Error(remoteError)
        target = createTarget()

        target.refreshVaultState()

        assertEquals(
            VaultState.TerminalRemoteFailure(remoteError.failure),
            target.vaultState.value,
        )
    }

    @Test
    fun `unlockWithPassphrase_whenSuccess_thenStoresKekAndStateIsUnlocked`() {
        val passphrase = Random.nextLong().toString()
        val expectedKek = Random.nextBytes(32)
        every { vaultUnlocker.unlockWithPassphrase(passphrase) } returns VaultUnlockResult.Unlocked(
            UnlockedKeyring(kek = expectedKek)
        )
        target = createTarget()

        val result = target.unlockWithPassphrase(passphrase)

        assertNull(result)
        assertEquals(VaultState.Unlocked, target.vaultState.value)
        verifyOrder {
            vaultInMemoryKekStore.clear()
            vaultInMemoryKekStore.replace(expectedKek)
        }
    }

    @Test
    fun `unlockWithRecoveryKey_whenSuccess_thenStoresKekAndStateIsUnlocked`() {
        val recoveryKey = Random.nextBytes(32)
        val expectedKek = Random.nextBytes(32)
        every { vaultUnlocker.unlockWithRecoveryKey(recoveryKey) } returns VaultUnlockResult.Unlocked(
            UnlockedKeyring(kek = expectedKek)
        )
        target = createTarget()

        val result = target.unlockWithRecoveryKey(recoveryKey)

        assertNull(result)
        assertEquals(VaultState.Unlocked, target.vaultState.value)
        verifyOrder {
            vaultInMemoryKekStore.clear()
            vaultInMemoryKekStore.replace(expectedKek)
        }
    }

    @Test
    fun `unlockWithPassphrase_whenInvalidCredential_thenReturnsErrorAndStateIsLocked`() {
        val passphrase = Random.nextLong().toString()
        every { vaultUnlocker.unlockWithPassphrase(passphrase) } returns VaultUnlockResult.Error(
            VaultUnlockError.InvalidCredential
        )
        target = createTarget()

        val result = target.unlockWithPassphrase(passphrase)

        assertEquals(VaultUnlockError.InvalidCredential, result)
        assertEquals(VaultState.Locked, target.vaultState.value)
    }

    @Test
    fun `unlockWithPassphrase_whenKeyMaterialUnavailableWithoutRemoteConfirmation_thenKeepsStateLocked`() {
        val passphrase = Random.nextLong().toString()
        every { vaultUnlocker.unlockWithPassphrase(passphrase) } returns VaultUnlockResult.Error(
            VaultUnlockError.KeyMaterialUnavailable
        )
        target = createTarget()

        val result = target.unlockWithPassphrase(passphrase)

        assertEquals(VaultUnlockError.KeyMaterialUnavailable, result)
        assertEquals(VaultState.Locked, target.vaultState.value)
    }

    @Test
    fun `lock_whenCalled_thenClearsKekAndStateIsLocked`() {
        target = createTarget()

        target.lock()

        assertEquals(VaultState.Locked, target.vaultState.value)
        verify(exactly = 1) { vaultInMemoryKekStore.clear() }
    }

    @Test
    fun `isUnlocked_whenStateIsUnlocked_thenReturnsTrue`() {
        val passphrase = Random.nextLong().toString()
        every { vaultUnlocker.unlockWithPassphrase(passphrase) } returns VaultUnlockResult.Unlocked(
            UnlockedKeyring(kek = Random.nextBytes(32))
        )
        target = createTarget()
        target.unlockWithPassphrase(passphrase)

        val result = target.isUnlocked()

        assertTrue(result)
    }

    @Test
    fun `isUnlocked_whenStateIsLocked_thenReturnsFalse`() {
        target = createTarget()

        val result = target.isUnlocked()

        assertFalse(result)
    }

    private fun createTarget() = VaultSessionManagerImpl(
        vaultUnlocker = vaultUnlocker,
        vaultKeyMaterialLocalRepository = vaultKeyMaterialLocalRepository,
        vaultKeyMaterialRemoteRepository = vaultKeyMaterialRemoteRepository,
        vaultInMemoryKekStore = vaultInMemoryKekStore,
    )

    private fun createVaultKeyMaterial() = VaultKeyMaterial(
        kekEncMaster = Random.nextBytes(32),
        kekEncRecovery = Random.nextBytes(32),
        kdfAlgorithm = Random.nextLong().toString(),
        kdfSalt = Random.nextBytes(16),
        kdfMemoryKib = Random.nextInt(),
        kdfIterations = Random.nextInt(),
        kdfParallelism = Random.nextInt(),
        kdfOutputLen = Random.nextInt(),
        cryptoVersion = Random.nextLong().toString(),
    )
}
