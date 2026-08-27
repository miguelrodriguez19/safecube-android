package com.miguelrodriguez19.safecube.app.testsupport

import android.app.Service
import android.app.PendingIntent
import android.content.Intent
import android.os.IBinder
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Debug-only remote-process probe. It gives instrumentation a deterministic cold process without
 * killing the test runner or exposing a release component.
 */
@AndroidEntryPoint
class QuickUnlockColdStartProbeService : Service() {
    @Inject
    lateinit var vaultSessionManager: VaultSessionManager

    override fun onBind(intent: Intent?): IBinder? = null

    @Suppress("DEPRECATION")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getParcelableExtra<PendingIntent>(EXTRA_RESULT_PENDING_INTENT)?.send(
            this,
            RESULT_OK,
            Intent()
                .putExtra(EXTRA_IS_UNLOCKED, vaultSessionManager.isUnlocked())
                .putExtra(EXTRA_VAULT_STATE, vaultSessionManager.vaultState.value.javaClass.name),
        )
        stopSelf(startId)
        return START_NOT_STICKY
    }

    companion object {
        const val EXTRA_RESULT_PENDING_INTENT = "quick_unlock_cold_start_result_pending_intent"
        const val EXTRA_IS_UNLOCKED = "quick_unlock_cold_start_is_unlocked"
        const val EXTRA_VAULT_STATE = "quick_unlock_cold_start_vault_state"
        const val RESULT_OK = 1
    }
}
