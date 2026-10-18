package com.miguelrodriguez19.safecube.app.appearance

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppearanceModule {
    @Provides
    @Singleton
    fun provideAppearanceDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
        produceFile = { context.preferencesDataStoreFile(APPEARANCE_DATA_STORE_NAME) },
    )

    @Provides
    @Singleton
    fun provideAppearanceRepository(
        repository: AppAppearanceRepository,
    ): AppearanceRepository = repository

    private const val APPEARANCE_DATA_STORE_NAME = "appearance"
}
