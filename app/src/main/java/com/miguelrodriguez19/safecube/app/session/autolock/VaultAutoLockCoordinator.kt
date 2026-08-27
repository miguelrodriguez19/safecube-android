package com.miguelrodriguez19.safecube.app.session.autolock

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.miguelrodriguez19.safecube.core.vault.domain.model.AutoLockTimeout
import com.miguelrodriguez19.safecube.core.vault.domain.repository.AutoLockTimeoutRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.QuickUnlockPromptMode
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VaultAutoLockCoordinator @Inject constructor(
    private val vaultSessionManager: VaultSessionManager,
    private val autoLockTimeoutRepository: AutoLockTimeoutRepository,
    private val clock: AutoLockClock,
    private val scheduler: AutoLockScheduler,
) : DefaultLifecycleObserver, VaultAutoLockController {

    private var backgroundSinceMillis: Long? = null
    private var backgroundTimeout: AutoLockTimeout? = null
    private var scheduledHandle: AutoLockHandle? = null
    private var generation = 0L

    override fun onStart(owner: LifecycleOwner) {
        onProcessForeground()
    }

    override fun onStop(owner: LifecycleOwner) {
        onProcessBackground()
    }

    fun onProcessBackground() {
        if (backgroundSinceMillis != null || !vaultSessionManager.isUnlocked()) return

        val startedAt = clock.elapsedRealtimeMillis()
        val timeout = autoLockTimeoutRepository.timeout.value
        backgroundSinceMillis = startedAt
        backgroundTimeout = timeout
        val currentGeneration = ++generation

        if (timeout.durationMillis == 0L) {
            lockIfCurrent(startedAt, currentGeneration)
        } else {
            scheduleRemaining(startedAt, timeout, currentGeneration)
        }
    }

    fun onProcessForeground() {
        val startedAt = backgroundSinceMillis ?: return
        val timeout = backgroundTimeout ?: return
        if (elapsedSince(startedAt) >= timeout.durationMillis) {
            lockIfCurrent(startedAt, generation)
        } else {
            clearBackgroundTracking()
        }
    }

    override fun lockNow() {
        clearBackgroundTracking()
        if (vaultSessionManager.isUnlocked()) {
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
        }
    }

    private fun scheduleRemaining(
        startedAt: Long,
        timeout: AutoLockTimeout,
        currentGeneration: Long,
    ) {
        val remainingMillis = (timeout.durationMillis - elapsedSince(startedAt)).coerceAtLeast(0L)
        scheduledHandle = scheduler.schedule(remainingMillis) {
            if (currentGeneration != generation || backgroundSinceMillis != startedAt) return@schedule

            if (elapsedSince(startedAt) >= timeout.durationMillis) {
                lockIfCurrent(startedAt, currentGeneration)
            } else {
                scheduleRemaining(startedAt, timeout, currentGeneration)
            }
        }
    }

    private fun lockIfCurrent(startedAt: Long, currentGeneration: Long) {
        if (currentGeneration != generation || backgroundSinceMillis != startedAt) return

        clearBackgroundTracking()
        if (vaultSessionManager.isUnlocked()) {
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
        }
    }

    private fun clearBackgroundTracking() {
        scheduledHandle?.cancel()
        scheduledHandle = null
        backgroundSinceMillis = null
        backgroundTimeout = null
        generation++
    }

    private fun elapsedSince(startedAt: Long): Long =
        (clock.elapsedRealtimeMillis() - startedAt).coerceAtLeast(0L)
}
