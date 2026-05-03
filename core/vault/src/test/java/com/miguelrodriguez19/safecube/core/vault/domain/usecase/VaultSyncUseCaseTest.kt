package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.VaultSyncExecutionLock
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.VaultSyncUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull.PullVaultDeltaUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.PushLocalVaultChangesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultSyncUseCaseTest {
    private val vaultState = MutableStateFlow<VaultState>(VaultState.Unlocked)
    private val vaultSessionManager = object : VaultSessionManager {
        override val vaultState: StateFlow<VaultState> = this@VaultSyncUseCaseTest.vaultState
        override fun isUnlocked(): Boolean = vaultState.value is VaultState.Unlocked
        override suspend fun refreshVaultState() = error("Not required in test")
        override fun unlockWithPassphrase(passphrase: String): VaultUnlockError? = error("Not required in test")
        override fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockError? = error("Not required in test")
        override fun lock() = error("Not required in test")
        override fun onLogout() = error("Not required in test")
    }

    private val pushLocalVaultChangesUseCase = mockk<PushLocalVaultChangesUseCase>()
    private val pullVaultDeltaUseCase = mockk<PullVaultDeltaUseCase>()
    private val vaultSyncExecutionLock = VaultSyncExecutionLock()

    private val target = VaultSyncUseCase(
        vaultSessionManager = vaultSessionManager,
        pushLocalVaultChangesUseCase = pushLocalVaultChangesUseCase,
        pullVaultDeltaUseCase = pullVaultDeltaUseCase,
        vaultSyncExecutionLock = vaultSyncExecutionLock,
    )

    @Test
    fun `invoke when vault is not unlocked then fails closed and skips push pull`() = runBlocking {
        vaultState.value = VaultState.Locked

        val result = target()

        assertEquals(
            VaultSyncResult.Error(
                reason = VaultSyncError.InvalidVaultState(VaultState.Locked),
                uploadedCount = 0,
                downloadedCount = 0,
                conflictCount = 0,
            ),
            result,
        )
        coVerify(exactly = 0) { pushLocalVaultChangesUseCase.invoke() }
        coVerify(exactly = 0) { pullVaultDeltaUseCase.invoke(any()) }
    }

    @Test
    fun `invoke when push succeeds and pull succeeds then returns merged summary`() = runBlocking {
        coEvery { pushLocalVaultChangesUseCase.invoke() } returns PushLocalVaultChangesResult.Success(
            processedCount = 4,
            syncedCount = 3,
            conflictCount = 1,
            keptPendingCount = 0,
            locallyResolvedDeleteCount = 0,
        )
        coEvery { pullVaultDeltaUseCase.invoke(50) } returns PullVaultDeltaResult.Success(
            processedSummaryCount = 5,
            appliedUpsertCount = 2,
            appliedDeleteCount = 1,
            skippedDirtyOrConflictCount = 2,
            checkpointUpdatedTo = null,
        )

        val result = target(pullLimit = 50)

        assertEquals(
            VaultSyncResult.Success(
                uploadedCount = 3,
                downloadedCount = 3,
                conflictCount = 3,
            ),
            result,
        )
    }

    @Test
    fun `invoke when push fails then returns global error and does not call pull`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val pushError = PushLocalVaultChangesError.LocalStateUpdateFailed(
            logicalItemId = logicalItemId,
            operation = "CREATE",
        )
        coEvery { pushLocalVaultChangesUseCase.invoke() } returns PushLocalVaultChangesResult.Error(
            pushError,
        )

        val result = target()

        assertTrue(result is VaultSyncResult.Error)
        assertEquals(
            VaultSyncError.PushFailed(pushError),
            (result as VaultSyncResult.Error).reason,
        )
        coVerify(exactly = 1) { pushLocalVaultChangesUseCase.invoke() }
        coVerify(exactly = 0) { pullVaultDeltaUseCase.invoke(any()) }
    }

    @Test
    fun `invoke when pull fails then returns global error preserving push counters`() = runBlocking {
        coEvery { pushLocalVaultChangesUseCase.invoke() } returns PushLocalVaultChangesResult.Success(
            processedCount = 2,
            syncedCount = 2,
            conflictCount = 1,
            keptPendingCount = 0,
            locallyResolvedDeleteCount = 0,
        )
        coEvery { pullVaultDeltaUseCase.invoke(null) } returns PullVaultDeltaResult.Error(
            PullVaultDeltaError.AccountIdUnavailable,
        )

        val result = target()

        assertEquals(
            VaultSyncResult.Error(
                reason = VaultSyncError.PullFailed(PullVaultDeltaError.AccountIdUnavailable),
                uploadedCount = 2,
                downloadedCount = 0,
                conflictCount = 1,
            ),
            result,
        )
        coVerify(exactly = 1) { pushLocalVaultChangesUseCase.invoke() }
        coVerify(exactly = 1) { pullVaultDeltaUseCase.invoke(null) }
    }

    @Test
    fun `invoke when two syncs run concurrently then serializes executions with mutex`() = runBlocking {
        val enteredPush = CompletableDeferred<Unit>()
        val releasePush = CompletableDeferred<Unit>()
        val pushCalls = AtomicInteger(0)
        coEvery { pushLocalVaultChangesUseCase.invoke() } coAnswers {
            val callNumber = pushCalls.incrementAndGet()
            if (callNumber == 1) {
                enteredPush.complete(Unit)
                releasePush.await()
            }
            PushLocalVaultChangesResult.Success(
                processedCount = 0,
                syncedCount = 0,
                conflictCount = 0,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            )
        }
        coEvery { pullVaultDeltaUseCase.invoke(null) } returns PullVaultDeltaResult.Success(
            processedSummaryCount = 0,
            appliedUpsertCount = 0,
            appliedDeleteCount = 0,
            skippedDirtyOrConflictCount = 0,
            checkpointUpdatedTo = null,
        )

        val first = async { target() }
        enteredPush.await()
        val second = async { target() }

        delay(50)
        coVerify(exactly = 1) { pushLocalVaultChangesUseCase.invoke() }

        releasePush.complete(Unit)

        withTimeout(5_000) {
            first.await()
            second.await()
        }
        coVerify(exactly = 2) { pushLocalVaultChangesUseCase.invoke() }
        coVerify(exactly = 2) { pullVaultDeltaUseCase.invoke(null) }
    }
}
