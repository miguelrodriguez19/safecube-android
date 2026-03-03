package com.miguelrodriguez19.safecube.core.crypto.di

import com.miguelrodriguez19.safecube.core.crypto.CryptoEngine
import com.miguelrodriguez19.safecube.core.crypto.internal.FakeCryptoEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CryptoModule {

    @Binds
    @Singleton
    abstract fun bindCryptoEngine(
        fakeCryptoEngine: FakeCryptoEngine,
    ): CryptoEngine
}
