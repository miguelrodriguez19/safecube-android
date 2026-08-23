package com.miguelrodriguez19.safecube.app.session.autolock

import android.os.Handler
import android.os.Looper
import javax.inject.Inject
import javax.inject.Singleton

internal fun interface AutoLockHandle {
    fun cancel()
}

internal interface AutoLockScheduler {
    fun schedule(delayMillis: Long, action: () -> Unit): AutoLockHandle
}

@Singleton
internal class MainLooperAutoLockScheduler @Inject constructor() : AutoLockScheduler {
    private val handler = Handler(Looper.getMainLooper())

    override fun schedule(delayMillis: Long, action: () -> Unit): AutoLockHandle {
        val runnable = Runnable(action)
        handler.postDelayed(runnable, delayMillis)
        return AutoLockHandle { handler.removeCallbacks(runnable) }
    }
}
