package com.miguelrodriguez19.safecube.app.session

import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AccountSessionModule {
    @Binds
    @Singleton
    abstract fun bindAccountSessionLifecycle(
        accountSessionLifecycleImpl: AccountSessionLifecycleImpl,
    ): AccountSessionLifecycle
}
