package com.miguelrodriguez19.safecube.app.session.autolock

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AutoLockModule {
    @Binds
    @Singleton
    internal abstract fun bindAutoLockClock(
        clock: AndroidAutoLockClock,
    ): AutoLockClock

    @Binds
    @Singleton
    internal abstract fun bindAutoLockScheduler(
        scheduler: MainLooperAutoLockScheduler,
    ): AutoLockScheduler

    @Binds
    @Singleton
    internal abstract fun bindVaultAutoLockController(
        coordinator: VaultAutoLockCoordinator,
    ): VaultAutoLockController
}
