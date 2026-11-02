package com.miguelrodriguez19.safecube.app.appearance

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.mutablePreferencesOf
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceUpdateResult
import java.io.IOException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppAppearanceRepositoryTest {
    @Test
    fun `repository emits defaults and confirmed updates`() = runTest {
        val store = InMemoryDataStore()
        val repository = AppAppearanceRepository(store)

        assertEquals(AppearanceSettings(), repository.settings.first())

        assertEquals(AppearanceUpdateResult.Updated, repository.setMode(AppearanceMode.Dark))
        assertEquals(
            AppearanceSettings(mode = AppearanceMode.Dark),
            repository.settings.first(),
        )

        assertEquals(AppearanceUpdateResult.Updated, repository.setDynamicColorEnabled(true))
        assertEquals(
            AppearanceSettings(mode = AppearanceMode.Dark, dynamicColorEnabled = true),
            repository.settings.first(),
        )
    }

    @Test
    fun `read io failure emits defaults`() = runTest {
        val repository = AppAppearanceRepository(
            object : DataStore<Preferences> {
                override val data: Flow<Preferences> = flow {
                    throw IOException("unavailable")
                }

                override suspend fun updateData(
                    transform: suspend (Preferences) -> Preferences,
                ): Preferences = emptyPreferences()
            },
        )

        assertEquals(AppearanceSettings(), repository.settings.first())
    }

    @Test
    fun `write failure keeps the last confirmed settings`() = runTest {
        val store = InMemoryDataStore()
        val repository = AppAppearanceRepository(store)
        assertEquals(AppearanceUpdateResult.Updated, repository.setMode(AppearanceMode.Light))
        store.failure = IOException("unavailable")

        assertEquals(AppearanceUpdateResult.Failed, repository.setDynamicColorEnabled(true))
        assertEquals(
            AppearanceSettings(mode = AppearanceMode.Light),
            repository.settings.first(),
        )
    }

    @Test
    fun `successful write recovers observation after a read io failure`() = runTest {
        val store = RecoveringDataStore()
        val repository = AppAppearanceRepository(store)
        val observed = mutableListOf<AppearanceSettings>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            repository.settings.take(2).toList(observed)
        }

        advanceUntilIdle()
        assertEquals(listOf(AppearanceSettings()), observed)

        assertEquals(AppearanceUpdateResult.Updated, repository.setMode(AppearanceMode.Dark))
        advanceUntilIdle()

        assertEquals(
            listOf(
                AppearanceSettings(),
                AppearanceSettings(mode = AppearanceMode.Dark),
            ),
            observed,
        )
    }

    @Test
    fun `cancellation is not converted into a failed update`() = runTest {
        val cancellation = CancellationException("cancelled")
        val repository = AppAppearanceRepository(
            object : DataStore<Preferences> {
                override val data: Flow<Preferences> = MutableStateFlow(emptyPreferences())

                override suspend fun updateData(
                    transform: suspend (Preferences) -> Preferences,
                ): Preferences = throw cancellation
            },
        )

        try {
            repository.setMode(AppearanceMode.Dark)
        } catch (actual: CancellationException) {
            assertSame(cancellation, actual)
            return@runTest
        }
        throw AssertionError("CancellationException should be rethrown")
    }

    private class InMemoryDataStore : DataStore<Preferences> {
        private val state = MutableStateFlow<Preferences>(emptyPreferences())
        var failure: Exception? = null

        override val data: Flow<Preferences> = state

        override suspend fun updateData(
            transform: suspend (Preferences) -> Preferences,
        ): Preferences {
            failure?.let { throw it }
            val updated = transform(state.value)
            state.value = updated
            return updated
        }
    }

    private class RecoveringDataStore : DataStore<Preferences> {
        private val state = MutableStateFlow<Preferences>(emptyPreferences())
        private var readFails = true

        override val data: Flow<Preferences> = flow {
            if (readFails) {
                throw IOException("temporarily unavailable")
            }
            emitAll(state)
        }

        override suspend fun updateData(
            transform: suspend (Preferences) -> Preferences,
        ): Preferences {
            val updated = transform(state.value)
            state.value = updated
            readFails = false
            return updated
        }
    }
}
