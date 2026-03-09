package com.miguelrodriguez19.safecube.core.vault.di

import com.miguelrodriguez19.safecube.core.vault.data.session.FakeVaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class VaultModule {
    @Binds
    @Singleton
    abstract fun bindVaultSessionManager(
        fakeVaultSessionManager: FakeVaultSessionManager,
    ): VaultSessionManager
}
