package com.miguelrodriguez19.safecube.core.storage.di

import android.content.Context
import androidx.room.Room
import com.miguelrodriguez19.safecube.core.storage.AppDatabase
import com.miguelrodriguez19.safecube.core.storage.SecureItemDao
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
    ).build()

    @Provides
    @Singleton
    fun provideSecureItemDao(
        appDatabase: AppDatabase,
    ): SecureItemDao = appDatabase.secureItemDao()

    private const val DATABASE_NAME = "safecube.db"
}
