package com.miguelrodriguez19.safecube.core.network.di

import com.miguelrodriguez19.safecube.core.network.TokenRefreshHandler
import dagger.BindsOptionalOf
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class TokenRefreshOptionalBindingModule {
    @BindsOptionalOf
    abstract fun bindOptionalTokenRefreshHandler(): TokenRefreshHandler
}
