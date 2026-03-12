package com.miguelrodriguez19.safecube.core.network.di

import com.miguelrodriguez19.safecube.core.network.domain.port.TokenRefreshHandler
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Optional binding point for modules that implement [TokenRefreshHandler].
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class TokenRefreshOptionalBindingModule {
    /**
     * Exposes [TokenRefreshHandler] as an optional dependency.
     */
    @BindsOptionalOf
    abstract fun bindOptionalTokenRefreshHandler(): TokenRefreshHandler
}
