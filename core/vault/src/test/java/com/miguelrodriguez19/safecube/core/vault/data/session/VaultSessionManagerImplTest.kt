package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.model.UnlockedKeyring
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.quickunlock.VaultUnlockProvenance
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCompletionResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockManager
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
import java.util.Optional
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
    private val quickUnlockManager = mockk<QuickUnlockManager>()
    private val accountSessionValidator = mockk<com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockAccountSessionValidator>()

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
            vaultInMemoryKekStore.replace(expectedKek, VaultUnlockProvenance.Passphrase)
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
            vaultInMemoryKekStore.replace(expectedKek, VaultUnlockProvenance.RecoveryKey)
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

    @Test
    fun `finishQuickUnlock_whenOperationWasNotPrepared_thenReturnsAccountUnavailable`() {
        val operationId = java.util.UUID.randomUUID().toString()
        target = createTarget()

        val result = target.finishQuickUnlock(operationId)

        assertEquals(QuickUnlockCompletionResult.AccountUnavailable, result)
        verify(exactly = 0) { quickUnlockManager.cancelUnlock(operationId) }
        verify(exactly = 0) { quickUnlockManager.finishUnlock(any(), any()) }
    }

    @Test
    fun `finishQuickUnlock_whenLocalAccountChanged_rejectsCallbackBeforeInstallingKek`() {
        val firstAccount = java.util.UUID.randomUUID()
        val secondAccount = java.util.UUID.randomUUID()
        val operationId = java.util.UUID.randomUUID().toString()
        every { vaultKeyMaterialLocalRepository.read() } returnsMany listOf(
            VaultKeyMaterialLocalReadResult.Present(createVaultKeyMaterial().copy(accountId = firstAccount)),
            VaultKeyMaterialLocalReadResult.Present(createVaultKeyMaterial().copy(accountId = secondAccount)),
        )
        every { accountSessionValidator.isValid(firstAccount) } returns true
        every { quickUnlockManager.cancelUnlock(any()) } returns Unit
        every { quickUnlockManager.prepareUnlock(firstAccount) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult.Ready(operationId)
        target = createTarget()
        target.lock()

        target.prepareQuickUnlock()
        val result = target.finishQuickUnlock(operationId)

        assertEquals(QuickUnlockCompletionResult.AccountChanged, result)
        verify(exactly = 1) { quickUnlockManager.cancelUnlock(operationId) }
        verify(exactly = 0) { quickUnlockManager.finishUnlock(any(), any()) }
    }

    @Test
    fun `prepareQuickUnlock_whenAccountSessionIsInvalid_returnsSessionInvalid`() {
        val accountId = java.util.UUID.randomUUID()
        every {
            vaultKeyMaterialLocalRepository.read()
        } returns VaultKeyMaterialLocalReadResult.Present(createVaultKeyMaterial().copy(accountId = accountId))
        every { accountSessionValidator.isValid(accountId) } returns false
        target = createTarget()
        target.lock()

        val result = target.prepareQuickUnlock()

        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult.SessionInvalid,
            result,
        )
        verify(exactly = 0) { quickUnlockManager.prepareUnlock(any()) }
    }

    @Test
    fun `lock when quick unlock callback is pending cancels it before callback can install kek`() {
        val accountId = java.util.UUID.randomUUID()
        val operationId = java.util.UUID.randomUUID().toString()
        every {
            vaultKeyMaterialLocalRepository.read()
        } returns VaultKeyMaterialLocalReadResult.Present(createVaultKeyMaterial().copy(accountId = accountId))
        every { accountSessionValidator.isValid(accountId) } returns true
        every { quickUnlockManager.prepareUnlock(accountId) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult.Ready(operationId)
        every { quickUnlockManager.cancelUnlock(any()) } returns Unit
        target = createTarget()
        target.lock()

        target.prepareQuickUnlock()
        target.lock()
        val result = target.finishQuickUnlock(operationId)

        assertEquals(QuickUnlockCompletionResult.AccountUnavailable, result)
        verify(exactly = 1) { quickUnlockManager.cancelUnlock(operationId) }
        verify(exactly = 0) { quickUnlockManager.finishUnlock(any(), any()) }
    }

    @Test
    fun `quick unlock enrollment when vault is locked requires passphrase without manager call`() {
        target = createTarget()
        target.lock()

        val result = target.prepareQuickUnlockEnrollment(consentGranted = true)

        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult.RequiresPassphrase,
            result,
        )
        verify(exactly = 0) { quickUnlockManager.prepareEnrollment(any(), any()) }
    }

    @Test
    fun `clear quick unlock enrollment fails closed without local account or valid session`() {
        target = createTarget()
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult.AccountUnavailable,
            target.clearQuickUnlockEnrollment(),
        )
        val accountId = java.util.UUID.randomUUID()
        every {
            vaultKeyMaterialLocalRepository.read()
        } returns VaultKeyMaterialLocalReadResult.Present(createVaultKeyMaterial().copy(accountId = accountId))
        every { accountSessionValidator.isValid(accountId) } returns false

        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult.SessionInvalid,
            target.clearQuickUnlockEnrollment(),
        )
        verify(exactly = 0) { quickUnlockManager.clearEnrollment(any()) }
    }

    @Test
    fun `quick unlock offer and marker require local account`() {
        target = createTarget()

        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState.AccountUnavailable,
            target.quickUnlockOfferState(),
        )
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockStoreResult.AccountUnavailable,
            target.markQuickUnlockOfferSeen(),
        )
    }

    @Test
    fun `quick unlock offer and marker delegate for the locally derived account`() {
        val accountId = java.util.UUID.randomUUID()
        every { vaultKeyMaterialLocalRepository.read() } returns localMaterialFor(accountId)
        every { quickUnlockManager.offerState(accountId) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState.Available
        every { quickUnlockManager.markOfferSeen(accountId) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockStoreResult.Saved
        target = createTarget()

        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState.Available,
            target.quickUnlockOfferState(),
        )
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockStoreResult.Saved,
            target.markQuickUnlockOfferSeen(),
        )
    }

    @Test
    fun `prepare enrollment requires account and session before delegating ready operation`() {
        target = createTarget()
        unlockTarget()
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult.AccountUnavailable,
            target.prepareQuickUnlockEnrollment(true),
        )

        val accountId = java.util.UUID.randomUUID()
        every { vaultKeyMaterialLocalRepository.read() } returns localMaterialFor(accountId)
        every { accountSessionValidator.isValid(accountId) } returns false
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult.SessionInvalid,
            target.prepareQuickUnlockEnrollment(true),
        )

        val operationId = java.util.UUID.randomUUID().toString()
        every { accountSessionValidator.isValid(accountId) } returns true
        every { quickUnlockManager.prepareEnrollment(accountId, true) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult.Ready(operationId)
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult.Ready(operationId),
            target.prepareQuickUnlockEnrollment(true),
        )
    }

    @Test
    fun `finish enrollment handles successful state account and session changes`() {
        val accountId = java.util.UUID.randomUUID()
        val operationId = java.util.UUID.randomUUID().toString()
        every { vaultKeyMaterialLocalRepository.read() } returns localMaterialFor(accountId)
        every { accountSessionValidator.isValid(accountId) } returns true
        every { quickUnlockManager.prepareEnrollment(accountId, true) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult.Ready(operationId)
        every { quickUnlockManager.finishEnrollment(accountId, operationId) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult.Enrolled
        target = createTarget()
        unlockTarget()

        target.prepareQuickUnlockEnrollment(true)
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult.Enrolled,
            target.finishQuickUnlockEnrollment(operationId),
        )

        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult.AccountUnavailable,
            target.finishQuickUnlockEnrollment("missing"),
        )
    }

    @Test
    fun `finish enrollment cancels operation when vault becomes locked`() {
        val accountId = java.util.UUID.randomUUID()
        val operationId = java.util.UUID.randomUUID().toString()
        every { vaultKeyMaterialLocalRepository.read() } returns localMaterialFor(accountId)
        every { accountSessionValidator.isValid(accountId) } returns true
        every { quickUnlockManager.prepareEnrollment(accountId, true) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult.Ready(operationId)
        every { quickUnlockManager.cancelUnlock(operationId) } returns Unit
        target = createTarget()
        unlockTarget()
        target.prepareQuickUnlockEnrollment(true)
        target.lock()

        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult.AccountUnavailable,
            target.finishQuickUnlockEnrollment(operationId),
        )
        verify(exactly = 1) { quickUnlockManager.cancelUnlock(operationId) }
    }

    @Test
    fun `prepare quick unlock covers wrong state absent account session and ready operation`() {
        target = createTarget()
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult.TemporarilyUnavailable,
            target.prepareQuickUnlock(),
        )
        target.lock()
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult.AccountUnavailable,
            target.prepareQuickUnlock(),
        )

        val accountId = java.util.UUID.randomUUID()
        every { vaultKeyMaterialLocalRepository.read() } returns localMaterialFor(accountId)
        every { accountSessionValidator.isValid(accountId) } returns false
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult.SessionInvalid,
            target.prepareQuickUnlock(),
        )

        val operationId = java.util.UUID.randomUUID().toString()
        every { accountSessionValidator.isValid(accountId) } returns true
        every { quickUnlockManager.prepareUnlock(accountId) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult.Ready(operationId)
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult.Ready(operationId),
            target.prepareQuickUnlock(),
        )
    }

    @Test
    fun `finish quick unlock installs unlocked state only after a validated manager success`() {
        val accountId = java.util.UUID.randomUUID()
        val operationId = java.util.UUID.randomUUID().toString()
        every { vaultKeyMaterialLocalRepository.read() } returns localMaterialFor(accountId)
        every { accountSessionValidator.isValid(accountId) } returns true
        every { quickUnlockManager.prepareUnlock(accountId) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult.Ready(operationId)
        every { quickUnlockManager.finishUnlock(accountId, operationId) } returns QuickUnlockCompletionResult.Unlocked
        target = createTarget()
        target.lock()

        target.prepareQuickUnlock()
        assertEquals(QuickUnlockCompletionResult.Unlocked, target.finishQuickUnlock(operationId))
        assertEquals(VaultState.Unlocked, target.vaultState.value)
    }

    @Test
    fun `finish quick unlock cancels operation after account or session changes`() {
        val firstAccount = java.util.UUID.randomUUID()
        val secondAccount = java.util.UUID.randomUUID()
        val operationId = java.util.UUID.randomUUID().toString()
        every { vaultKeyMaterialLocalRepository.read() } returnsMany listOf(
            localMaterialFor(firstAccount),
            localMaterialFor(secondAccount),
        )
        every { accountSessionValidator.isValid(firstAccount) } returns true
        every { quickUnlockManager.prepareUnlock(firstAccount) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult.Ready(operationId)
        every { quickUnlockManager.cancelUnlock(operationId) } returns Unit
        target = createTarget()
        target.lock()
        target.prepareQuickUnlock()

        assertEquals(QuickUnlockCompletionResult.AccountChanged, target.finishQuickUnlock(operationId))
        verify(exactly = 1) { quickUnlockManager.cancelUnlock(operationId) }
    }

    @Test
    fun `finish quick unlock cancels operation when account session becomes invalid`() {
        val accountId = java.util.UUID.randomUUID()
        val operationId = java.util.UUID.randomUUID().toString()
        every { vaultKeyMaterialLocalRepository.read() } returns localMaterialFor(accountId)
        every { accountSessionValidator.isValid(accountId) } returnsMany listOf(true, false)
        every { quickUnlockManager.prepareUnlock(accountId) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult.Ready(operationId)
        every { quickUnlockManager.cancelUnlock(operationId) } returns Unit
        target = createTarget()
        target.lock()
        target.prepareQuickUnlock()

        assertEquals(QuickUnlockCompletionResult.SessionInvalid, target.finishQuickUnlock(operationId))
        verify(exactly = 1) { quickUnlockManager.cancelUnlock(operationId) }
    }

    @Test
    fun `cancel and clear quick unlock enrollment delegate only after account validation`() {
        val accountId = java.util.UUID.randomUUID()
        every { vaultKeyMaterialLocalRepository.read() } returns localMaterialFor(accountId)
        every { accountSessionValidator.isValid(accountId) } returns true
        every { quickUnlockManager.clearEnrollment(accountId) } returns
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult.Cleared
        every { quickUnlockManager.cancelUnlock("operation") } returns Unit
        target = createTarget()

        target.cancelQuickUnlock("operation")
        assertEquals(
            com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult.Cleared,
            target.clearQuickUnlockEnrollment(),
        )
        verify(exactly = 1) { quickUnlockManager.cancelUnlock("operation") }
        verify(exactly = 1) { quickUnlockManager.clearEnrollment(accountId) }
    }

    private fun createTarget() = VaultSessionManagerImpl(
        vaultUnlocker = vaultUnlocker,
        vaultKeyMaterialLocalRepository = vaultKeyMaterialLocalRepository,
        vaultKeyMaterialRemoteRepository = vaultKeyMaterialRemoteRepository,
        vaultInMemoryKekStore = vaultInMemoryKekStore,
        quickUnlockManager = quickUnlockManager,
        accountSessionValidator = Optional.of(accountSessionValidator),
    )

    private fun localMaterialFor(accountId: java.util.UUID): VaultKeyMaterialLocalReadResult.Present =
        VaultKeyMaterialLocalReadResult.Present(createVaultKeyMaterial().copy(accountId = accountId))

    private fun unlockTarget() {
        every { vaultUnlocker.unlockWithPassphrase("passphrase") } returns VaultUnlockResult.Unlocked(
            UnlockedKeyring(kek = ByteArray(32) { 1 }),
        )
        target.unlockWithPassphrase("passphrase")
    }

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
