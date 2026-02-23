package com.miguelrodriguez19.safecube.core.auth.di

import com.miguelrodriguez19.safecube.core.auth.TokenStorage
import com.miguelrodriguez19.safecube.core.auth.internal.EncryptedTokenStorage
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindTokenStorage(
        encryptedTokenStorage: EncryptedTokenStorage,
    ): TokenStorage
}
