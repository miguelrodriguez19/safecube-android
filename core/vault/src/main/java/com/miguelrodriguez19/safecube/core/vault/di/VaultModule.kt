package com.miguelrodriguez19.safecube.core.vault.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.miguelrodriguez19.safecube.core.vault.data.local.EncryptedVaultKeyMaterialPrefs
import com.miguelrodriguez19.safecube.core.vault.data.local.VaultKeyMaterialCache
import com.miguelrodriguez19.safecube.core.vault.data.remote.RemoteVaultKeyMaterialDataSource
import com.miguelrodriguez19.safecube.core.vault.data.session.FakeVaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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

    @Binds
    @Singleton
    abstract fun bindVaultKeyMaterialRemoteRepository(
        remoteVaultKeyMaterialDataSource: RemoteVaultKeyMaterialDataSource,
    ): VaultKeyMaterialRemoteRepository

    @Binds
    @Singleton
    abstract fun bindVaultKeyMaterialLocalRepository(
        vaultKeyMaterialCache: VaultKeyMaterialCache,
    ): VaultKeyMaterialLocalRepository

    companion object {
        private const val PREFERENCES_NAME = "vault_key_material_encrypted_preferences"

        @Provides
        @Singleton
        @EncryptedVaultKeyMaterialPrefs
        fun provideEncryptedVaultKeyMaterialPrefs(
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
