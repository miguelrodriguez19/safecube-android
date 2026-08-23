package com.miguelrodriguez19.safecube.core.vault.di

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.miguelrodriguez19.safecube.core.vault.data.local.EncryptedVaultKeyMaterialPrefs
import com.miguelrodriguez19.safecube.core.vault.data.local.EncryptedVaultInitializationPrefs
import com.miguelrodriguez19.safecube.core.vault.data.local.AutoLockPreferences
import com.miguelrodriguez19.safecube.core.vault.data.local.AutoLockTimeoutRepositoryImpl
import com.miguelrodriguez19.safecube.core.vault.data.local.AUTO_LOCK_PREFERENCES_NAME
import com.miguelrodriguez19.safecube.core.vault.data.codec.JsonSecureItemContentCodec
import com.miguelrodriguez19.safecube.core.vault.data.crypto.SecureItemPayloadEnvelopeIdentityReader
import com.miguelrodriguez19.safecube.core.vault.data.crypto.VaultItemCipher
import com.miguelrodriguez19.safecube.core.vault.data.local.VaultKeyMaterialCache
import com.miguelrodriguez19.safecube.core.vault.data.local.PendingVaultInitializationStore
import com.miguelrodriguez19.safecube.core.vault.data.remote.RemoteSecureItemDataSource
import com.miguelrodriguez19.safecube.core.vault.data.remote.RemoteVaultKeyMaterialDataSource
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.data.session.VaultSessionManagerImpl
import com.miguelrodriguez19.safecube.core.vault.data.session.LocalVaultDataCleanerImpl
import com.miguelrodriguez19.safecube.core.vault.data.session.VaultInMemoryKekStore
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentCodec
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.PendingVaultInitializationRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.AutoLockTimeoutRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultKekProvider
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultDataCleaner
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemPayloadIdentityReader
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.CurrentInstantProvider
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.RandomSecureItemIdGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.RandomSecureItemMutationIdGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemIdGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationIdGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SystemCurrentInstantProvider
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultUnlockUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultUnlocker
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
    internal abstract fun bindVaultKekProvider(
        vaultInMemoryKekStore: VaultInMemoryKekStore,
    ): VaultKekProvider

    @Binds
    @Singleton
    internal abstract fun bindLocalVaultDataCleaner(
        localVaultDataCleanerImpl: LocalVaultDataCleanerImpl,
    ): LocalVaultDataCleaner

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
    abstract fun bindPendingVaultInitializationRepository(
        pendingVaultInitializationStore: PendingVaultInitializationStore,
    ): PendingVaultInitializationRepository

    @Binds
    @Singleton
    internal abstract fun bindAutoLockTimeoutRepository(
        autoLockTimeoutRepositoryImpl: AutoLockTimeoutRepositoryImpl,
    ): AutoLockTimeoutRepository

    @Binds
    @Singleton
    abstract fun bindSecureItemRemoteRepository(
        remoteSecureItemDataSource: RemoteSecureItemDataSource,
    ): SecureItemRemoteRepository

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

    @Binds
    @Singleton
    internal abstract fun bindSecureItemPayloadIdentityReader(
        secureItemPayloadEnvelopeIdentityReader: SecureItemPayloadEnvelopeIdentityReader,
    ): SecureItemPayloadIdentityReader

    @Binds
    @Singleton
    internal abstract fun bindCurrentInstantProvider(
        systemCurrentInstantProvider: SystemCurrentInstantProvider,
    ): CurrentInstantProvider

    @Binds
    @Singleton
    internal abstract fun bindSecureItemIdGenerator(
        randomSecureItemIdGenerator: RandomSecureItemIdGenerator,
    ): SecureItemIdGenerator

    @Binds
    @Singleton
    internal abstract fun bindSecureItemMutationIdGenerator(
        randomSecureItemMutationIdGenerator: RandomSecureItemMutationIdGenerator,
    ): SecureItemMutationIdGenerator

    companion object {
        private const val PREFERENCES_NAME = "vault_key_material_encrypted_preferences"
        private const val PENDING_INITIALIZATION_PREFERENCES_NAME =
            "vault_initialization_pending_encrypted_preferences"

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

        @Provides
        @Singleton
        @EncryptedVaultInitializationPrefs
        fun provideEncryptedVaultInitializationPrefs(
            @ApplicationContext context: Context,
        ): SharedPreferences = EncryptedSharedPreferences.create(
            context,
            PENDING_INITIALIZATION_PREFERENCES_NAME,
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

        @Provides
        @Singleton
        @AutoLockPreferences
        fun provideAutoLockPreferences(
            @ApplicationContext context: Context,
        ): SharedPreferences = context.getSharedPreferences(
            AUTO_LOCK_PREFERENCES_NAME,
            Context.MODE_PRIVATE,
        )
    }
}
