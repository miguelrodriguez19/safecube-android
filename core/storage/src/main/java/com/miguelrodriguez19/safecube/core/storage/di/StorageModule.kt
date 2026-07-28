package com.miguelrodriguez19.safecube.core.storage.di

import android.content.Context
import androidx.room.Room
import com.miguelrodriguez19.safecube.core.storage.AppDatabase
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointDao
import com.miguelrodriguez19.safecube.core.storage.StorageMigrations
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object StorageModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
    ): AppDatabase = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DATABASE_NAME,
    ).addMigrations(
        StorageMigrations.MIGRATION_1_2,
        StorageMigrations.MIGRATION_2_3,
        StorageMigrations.MIGRATION_3_4,
        StorageMigrations.MIGRATION_4_5,
        StorageMigrations.MIGRATION_5_6,
        StorageMigrations.MIGRATION_6_7,
    ).build()

    @Provides
    @Singleton
    fun provideSecureItemDao(
        appDatabase: AppDatabase,
    ): SecureItemDao = appDatabase.secureItemDao()

    @Provides
    @Singleton
    fun provideSecureItemDraftDao(
        appDatabase: AppDatabase,
    ): SecureItemDraftDao = appDatabase.secureItemDraftDao()

    @Provides
    @Singleton
    fun provideSecureItemSyncCheckpointDao(
        appDatabase: AppDatabase,
    ): SecureItemSyncCheckpointDao = appDatabase.secureItemSyncCheckpointDao()

    private const val DATABASE_NAME = "safecube.db"
}
