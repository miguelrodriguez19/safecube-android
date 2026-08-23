package com.miguelrodriguez19.safecube.app.session.autolock

import androidx.lifecycle.LifecycleOwner
import com.miguelrodriguez19.safecube.core.vault.domain.model.AutoLockTimeout
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.repository.AutoLockTimeoutRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultAutoLockCoordinatorTest {
    @Test
    fun `default immediately locks when process enters background`() {
        val session = FakeVaultSessionManager()
        val scheduler = FakeAutoLockScheduler()
        val target = createTarget(session = session, scheduler = scheduler)

        target.onProcessBackground()

        assertEquals(1, session.lockCalls)
        assertTrue(session.isLocked())
        assertTrue(scheduler.tasks.isEmpty())
    }

    @Test
    fun `each approved timeout schedules its exact monotonic duration`() {
        AutoLockTimeout.entries
            .filterNot { timeout -> timeout == AutoLockTimeout.Immediately }
            .forEach { timeout ->
                val session = FakeVaultSessionManager()
                val scheduler = FakeAutoLockScheduler()
                val target = createTarget(
                    session = session,
                    scheduler = scheduler,
                    timeout = timeout,
                )

                target.onProcessBackground()

                assertEquals(timeout.durationMillis, scheduler.tasks.single().delayMillis)
            }
    }

    @Test
    fun `deadline callback locks after monotonic timeout`() {
        val clock = FakeAutoLockClock()
        val session = FakeVaultSessionManager()
        val scheduler = FakeAutoLockScheduler()
        val target = createTarget(
            session = session,
            scheduler = scheduler,
            clock = clock,
            timeout = AutoLockTimeout.ThirtySeconds,
        )

        target.onProcessBackground()
        clock.nowMillis = AutoLockTimeout.ThirtySeconds.durationMillis
        scheduler.runNext()

        assertEquals(1, session.lockCalls)
    }

    @Test
    fun `foreground before deadline cancels timeout and keeps vault unlocked`() {
        val clock = FakeAutoLockClock()
        val session = FakeVaultSessionManager()
        val scheduler = FakeAutoLockScheduler()
        val target = createTarget(
            session = session,
            scheduler = scheduler,
            clock = clock,
            timeout = AutoLockTimeout.FiveMinutes,
        )

        target.onProcessBackground()
        clock.nowMillis = 299_999L
        target.onProcessForeground()
        scheduler.runAll()

        assertEquals(0, session.lockCalls)
        assertFalse(scheduler.tasks.single().wasRun)
    }

    @Test
    fun `foreground after missed callback locks before content can be used`() {
        val clock = FakeAutoLockClock()
        val session = FakeVaultSessionManager()
        val scheduler = FakeAutoLockScheduler()
        val target = createTarget(
            session = session,
            scheduler = scheduler,
            clock = clock,
            timeout = AutoLockTimeout.OneMinute,
        )

        target.onProcessBackground()
        clock.nowMillis = AutoLockTimeout.OneMinute.durationMillis + 1L
        target.onProcessForeground()

        assertEquals(1, session.lockCalls)
    }

    @Test
    fun `changing configuration while foreground does not start inactivity tracking`() {
        val session = FakeVaultSessionManager()
        val scheduler = FakeAutoLockScheduler()
        val repository = FakeAutoLockTimeoutRepository(AutoLockTimeout.ThirtySeconds)
        val target = VaultAutoLockCoordinator(
            vaultSessionManager = session,
            autoLockTimeoutRepository = repository,
            clock = FakeAutoLockClock(),
            scheduler = scheduler,
        )

        repository.timeout.value = AutoLockTimeout.Immediately

        assertTrue(scheduler.tasks.isEmpty())
        assertEquals(0, session.lockCalls)
    }

    @Test
    fun `changing configuration while background keeps the current deadline`() {
        val clock = FakeAutoLockClock()
        val session = FakeVaultSessionManager()
        val scheduler = FakeAutoLockScheduler()
        val repository = FakeAutoLockTimeoutRepository(AutoLockTimeout.ThirtySeconds)
        val target = VaultAutoLockCoordinator(
            vaultSessionManager = session,
            autoLockTimeoutRepository = repository,
            clock = clock,
            scheduler = scheduler,
        )

        target.onProcessBackground()
        repository.timeout.value = AutoLockTimeout.Immediately

        assertEquals(0, session.lockCalls)
        assertEquals(AutoLockTimeout.ThirtySeconds.durationMillis, scheduler.tasks.single().delayMillis)
    }

    @Test
    fun `lock now cancels pending timeout and locks vault`() {
        val session = FakeVaultSessionManager()
        val scheduler = FakeAutoLockScheduler()
        val target = createTarget(
            session = session,
            scheduler = scheduler,
            timeout = AutoLockTimeout.FifteenMinutes,
        )

        target.onProcessBackground()
        target.lockNow()
        scheduler.runAll()

        assertEquals(1, session.lockCalls)
        assertTrue(scheduler.tasks.single().cancelled)
    }

    @Test
    fun `activity recreation without process lifecycle transition does not lock vault`() {
        val session = FakeVaultSessionManager()
        val scheduler = FakeAutoLockScheduler()
        val target = createTarget(session = session, scheduler = scheduler)

        target.onProcessForeground()

        assertEquals(0, session.lockCalls)
        assertTrue(session.isUnlocked())
    }

    private fun createTarget(
        session: FakeVaultSessionManager,
        scheduler: FakeAutoLockScheduler,
        clock: FakeAutoLockClock = FakeAutoLockClock(),
        timeout: AutoLockTimeout = AutoLockTimeout.Immediately,
    ) = VaultAutoLockCoordinator(
        vaultSessionManager = session,
        autoLockTimeoutRepository = FakeAutoLockTimeoutRepository(timeout),
        clock = clock,
        scheduler = scheduler,
    )
}

private class FakeAutoLockClock(
    var nowMillis: Long = 0L,
) : AutoLockClock {
    override fun elapsedRealtimeMillis(): Long = nowMillis
}

private class FakeAutoLockScheduler : AutoLockScheduler {
    val tasks = mutableListOf<FakeTask>()

    override fun schedule(delayMillis: Long, action: () -> Unit): AutoLockHandle =
        FakeTask(delayMillis, action).also(tasks::add)

    fun runNext() {
        tasks.first { !it.cancelled && !it.wasRun }.run()
    }

    fun runAll() {
        tasks.filter { !it.cancelled && !it.wasRun }.forEach(FakeTask::run)
    }

    class FakeTask(
        val delayMillis: Long,
        private val action: () -> Unit,
    ) : AutoLockHandle {
        var cancelled = false
        var wasRun = false

        override fun cancel() {
            cancelled = true
        }

        fun run() {
            if (cancelled || wasRun) return
            wasRun = true
            action()
        }
    }
}

private class FakeAutoLockTimeoutRepository(
    initialTimeout: AutoLockTimeout,
) : AutoLockTimeoutRepository {
    override val timeout = MutableStateFlow(initialTimeout)

    override fun setTimeout(timeout: AutoLockTimeout) {
        this.timeout.value = timeout
    }
}

private class FakeVaultSessionManager : VaultSessionManager {
    private val mutableVaultState = MutableStateFlow<VaultState>(VaultState.Unlocked)
    var lockCalls = 0
        private set

    override val vaultState: StateFlow<VaultState> = mutableVaultState

    override suspend fun refreshVaultState() = Unit

    override fun isUnlocked(): Boolean = mutableVaultState.value == VaultState.Unlocked

    override fun unlockWithPassphrase(passphrase: String): VaultUnlockError? {
        mutableVaultState.value = VaultState.Unlocked
        return null
    }

    override fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockError? {
        mutableVaultState.value = VaultState.Unlocked
        return null
    }

    override fun lock() {
        lockCalls++
        mutableVaultState.value = VaultState.Locked
    }

    fun isLocked(): Boolean = mutableVaultState.value == VaultState.Locked
}
