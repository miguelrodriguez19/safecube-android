package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.VaultSyncExecutionLock
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.VaultSyncTrigger
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.PushLocalVaultChangesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultSyncTriggerTest {
    private val vaultState = MutableStateFlow<VaultState>(VaultState.Unlocked)
    private val vaultSessionManager = object : VaultSessionManager {
        override val vaultState: StateFlow<VaultState> = this@VaultSyncTriggerTest.vaultState
        override fun isUnlocked(): Boolean = vaultState.value is VaultState.Unlocked
        override suspend fun refreshVaultState() = error("Not required in test")
        override fun unlockWithPassphrase(passphrase: String): VaultUnlockError? = error("Not required in test")
        override fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockError? = error("Not required in test")
        override fun lock() = error("Not required in test")
        override fun onLogout() = error("Not required in test")
    }
    private val pushLocalVaultChangesUseCase = mockk<PushLocalVaultChangesUseCase>()
    private val vaultSyncExecutionLock = VaultSyncExecutionLock()

    private val target = VaultSyncTrigger(
        vaultSessionManager = vaultSessionManager,
        pushLocalVaultChangesUseCase = pushLocalVaultChangesUseCase,
        vaultSyncExecutionLock = vaultSyncExecutionLock,
    )

    @Test
    fun `onLocalMutationStored when vault is locked then skips opportunistic push`() = runBlocking {
        vaultState.value = VaultState.Locked

        target.onLocalMutationStored()
        delay(50)

        coVerify(exactly = 0) { pushLocalVaultChangesUseCase.invoke() }
    }

    @Test
    fun `onLocalMutationStored when vault is unlocked then runs opportunistic push asynchronously`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        coEvery { pushLocalVaultChangesUseCase.invoke() } returns successResult()

        target.onLocalMutationStored()

        coVerify(timeout = 2_000, exactly = 1) { pushLocalVaultChangesUseCase.invoke() }
    }

    @Test
    fun `onLocalMutationStored when sync is in progress then coalesces without second push`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        val lockAcquired = vaultSyncExecutionLock.tryLock()
        assertTrue(lockAcquired)

        target.onLocalMutationStored()
        delay(50)

        coVerify(exactly = 0) { pushLocalVaultChangesUseCase.invoke() }
        vaultSyncExecutionLock.unlock()
    }

    @Test
    fun `onLocalMutationStored when push fails then releases lock for next trigger`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        val pushCalls = AtomicInteger(0)
        coEvery { pushLocalVaultChangesUseCase.invoke() } coAnswers {
            if (pushCalls.incrementAndGet() == 1) {
                throw RuntimeException("network down")
            }
            successResult()
        }

        target.onLocalMutationStored()
        coVerify(timeout = 2_000, exactly = 1) { pushLocalVaultChangesUseCase.invoke() }

        target.onLocalMutationStored()
        coVerify(timeout = 2_000, exactly = 2) { pushLocalVaultChangesUseCase.invoke() }
    }

    @Test
    fun `onLocalMutationStored when called while push is running then keeps single in flight execution`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        val pushCalls = AtomicInteger(0)
        val enteredFirstPush = CompletableDeferred<Unit>()
        val releaseFirstPush = CompletableDeferred<Unit>()
        coEvery { pushLocalVaultChangesUseCase.invoke() } coAnswers {
            val callNumber = pushCalls.incrementAndGet()
            if (callNumber == 1) {
                enteredFirstPush.complete(Unit)
                releaseFirstPush.await()
            }
            successResult()
        }

        target.onLocalMutationStored()
        enteredFirstPush.await()
        target.onLocalMutationStored()
        delay(50)

        coVerify(exactly = 1) { pushLocalVaultChangesUseCase.invoke() }

        releaseFirstPush.complete(Unit)
        coVerify(timeout = 2_000, exactly = 1) { pushLocalVaultChangesUseCase.invoke() }
    }
}

private fun successResult(): PushLocalVaultChangesResult.Success = PushLocalVaultChangesResult.Success(
    processedCount = 1,
    syncedCount = 1,
    conflictCount = 0,
    keptPendingCount = 0,
    locallyResolvedDeleteCount = 0,
)
