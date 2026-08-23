package com.miguelrodriguez19.safecube.app.entrypoint

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.miguelrodriguez19.safecube.app.session.autolock.VaultAutoLockCoordinator
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class SafeCubeApp : Application() {
    @Inject
    internal lateinit var vaultAutoLockCoordinator: VaultAutoLockCoordinator

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(vaultAutoLockCoordinator)
    }
}
