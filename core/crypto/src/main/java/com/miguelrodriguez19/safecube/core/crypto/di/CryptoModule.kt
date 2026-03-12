package com.miguelrodriguez19.safecube.core.crypto.di

import com.miguelrodriguez19.safecube.core.crypto.domain.port.CryptoEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KdfEngine
import com.miguelrodriguez19.safecube.core.crypto.domain.port.KeyWrapping
import com.miguelrodriguez19.safecube.core.crypto.data.engine.AesGcmCryptoEngine
import com.miguelrodriguez19.safecube.core.crypto.data.engine.AesGcmKeyWrapping
import com.miguelrodriguez19.safecube.core.crypto.data.engine.Argon2KdfEngine
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dependency graph bindings for crypto abstractions.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class CryptoModule {

    /**
     * Binds [CryptoEngine] to the AES-GCM implementation.
     */
    @Binds
    @Singleton
    abstract fun bindCryptoEngine(
        aesGcmCryptoEngine: AesGcmCryptoEngine,
    ): CryptoEngine

    /**
     * Binds [KdfEngine] to the Argon2id implementation.
     */
    @Binds
    @Singleton
    abstract fun bindKdfEngine(
        argon2KdfEngine: Argon2KdfEngine,
    ): KdfEngine

    /**
     * Binds [KeyWrapping] to the AES-GCM implementation.
     */
    @Binds
    @Singleton
    abstract fun bindKeyWrapping(
        aesGcmKeyWrapping: AesGcmKeyWrapping,
    ): KeyWrapping
}
