package com.miguelrodriguez19.safecube.core.vault.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.miguelrodriguez19.safecube.core.crypto.domain.service.SaltGenerator
import com.miguelrodriguez19.safecube.core.vault.data.local.EncryptedVaultKeyMaterialPrefs
import com.miguelrodriguez19.safecube.core.vault.data.codec.JsonSecureItemContentCodec
import com.miguelrodriguez19.safecube.core.vault.data.crypto.VaultItemCipher
import com.miguelrodriguez19.safecube.core.vault.data.local.VaultKeyMaterialCache
import com.miguelrodriguez19.safecube.core.vault.data.remote.RemoteVaultKeyMaterialDataSource
import com.miguelrodriguez19.safecube.core.vault.data.session.VaultSessionManagerImpl
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentCodec
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.VaultUnlockUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.VaultUnlocker
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
    internal abstract fun bindVaultSessionManager(
        vaultSessionManagerImpl: VaultSessionManagerImpl,
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

    @Binds
    @Singleton
    abstract fun bindVaultUnlocker(
        vaultUnlockUseCase: VaultUnlockUseCase,
    ): VaultUnlocker

    @Binds
    @Singleton
    abstract fun bindSecureItemContentCodec(
        jsonSecureItemContentCodec: JsonSecureItemContentCodec,
    ): SecureItemContentCodec

    @Binds
    @Singleton
    internal abstract fun bindSecureItemCryptoService(
        vaultItemCipher: VaultItemCipher,
    ): SecureItemCryptoService

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
