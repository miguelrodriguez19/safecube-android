package com.miguelrodriguez19.safecube.core.storage.di

import com.miguelrodriguez19.safecube.core.storage.local.SecureItemLocalStorage
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class StorageBindingsModule {
    @Binds
    @Singleton
    abstract fun bindSecureItemRepository(
        secureItemLocalStorage: SecureItemLocalStorage,
    ): SecureItemRepository
}
