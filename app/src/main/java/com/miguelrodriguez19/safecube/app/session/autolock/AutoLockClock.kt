package com.miguelrodriguez19.safecube.app.session.autolock

import android.os.SystemClock
import javax.inject.Inject
import javax.inject.Singleton

internal fun interface AutoLockClock {
    fun elapsedRealtimeMillis(): Long
}

@Singleton
internal class AndroidAutoLockClock @Inject constructor() : AutoLockClock {
    override fun elapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()
}
