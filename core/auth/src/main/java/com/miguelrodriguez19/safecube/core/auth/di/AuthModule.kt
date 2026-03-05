package com.miguelrodriguez19.safecube.core.auth.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.miguelrodriguez19.safecube.core.auth.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.AuthRepositoryImpl
import com.miguelrodriguez19.safecube.core.auth.SessionManager
import com.miguelrodriguez19.safecube.core.auth.TokenStorage
import com.miguelrodriguez19.safecube.core.auth.VaultSessionManager
import com.miguelrodriguez19.safecube.core.auth.internal.EncryptedTokenStorage
import com.miguelrodriguez19.safecube.core.auth.internal.EncryptedTokenPrefs
import com.miguelrodriguez19.safecube.core.auth.internal.FakeVaultSessionManager
import com.miguelrodriguez19.safecube.core.network.TokenProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl,
    ): AuthRepository

    @Binds
    @Singleton
    abstract fun bindTokenStorage(
        encryptedTokenStorage: EncryptedTokenStorage,
    ): TokenStorage

    @Binds
    @Singleton
    abstract fun bindTokenProvider(
        sessionManager: SessionManager,
    ): TokenProvider

    @Binds
    @Singleton
    abstract fun bindVaultSessionManager(
        fakeVaultSessionManager: FakeVaultSessionManager,
    ): VaultSessionManager

    companion object {
        private const val PREFERENCES_NAME = "auth_encrypted_preferences"

        @Provides
        @Singleton
        @EncryptedTokenPrefs
        fun provideEncryptedTokenPrefs(
            @ApplicationContext context: Context,
        ): SharedPreferences = EncryptedSharedPreferences.create(
            context,
            PREFERENCES_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }
}
