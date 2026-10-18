package com.miguelrodriguez19.safecube.app.appearance

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceRepository
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceUpdateResult
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

@OptIn(ExperimentalCoroutinesApi::class)
class AppAppearanceRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>,
) : AppearanceRepository {
    private val refreshes = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override val settings: Flow<AppearanceSettings> = refreshes
        .onStart { emit(Unit) }
        .flatMapLatest { readSettings() }
        .distinctUntilChanged()

    override suspend fun setMode(mode: AppearanceMode): AppearanceUpdateResult {
        return updateDataStore {
            this[AppearanceSettingsCodec.modeKey] = AppearanceSettingsCodec.encodeMode(mode)
        }
    }

    override suspend fun setDynamicColorEnabled(enabled: Boolean): AppearanceUpdateResult {
        return updateDataStore {
            this[AppearanceSettingsCodec.dynamicColorEnabledKey] = enabled
        }
    }

    private suspend fun updateDataStore(
        update: suspend MutablePreferences.() -> Unit,
    ): AppearanceUpdateResult {
        return try {
            dataStore.edit { preferences ->
                preferences.update()
            }
            refreshes.emit(Unit)
            AppearanceUpdateResult.Updated
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            AppearanceUpdateResult.Failed
        }
    }

    private fun readSettings(): Flow<AppearanceSettings> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) {
                emit(emptyPreferences())
            } else {
                throw throwable
            }
        }
        .map(AppearanceSettingsCodec::decode)
}
