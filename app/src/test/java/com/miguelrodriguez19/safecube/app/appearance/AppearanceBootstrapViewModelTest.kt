package com.miguelrodriguez19.safecube.app.appearance

import com.miguelrodriguez19.safecube.app.test.MainDispatcherRule
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceRepository
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceUpdateResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AppearanceBootstrapViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `bootstrap remains loading until first settings emission`() = runTest {
        val settings = MutableStateFlow<AppearanceSettings?>(null)
        val repository = FakeAppearanceRepository(
            settings = flow {
                settings.collect { value ->
                    if (value != null) emit(value)
                }
            },
        )
        val target = AppearanceBootstrapViewModel(repository)

        assertEquals(AppearanceBootstrapState.Loading, target.state.value)

        settings.value = AppearanceSettings(mode = AppearanceMode.Dark)
        advanceUntilIdle()

        assertEquals(
            AppearanceBootstrapState.Ready(AppearanceSettings(mode = AppearanceMode.Dark)),
            target.state.value,
        )
    }

    @Test
    fun `bootstrap follows confirmed settings changes`() = runTest {
        val settings = MutableStateFlow(AppearanceSettings())
        val target = AppearanceBootstrapViewModel(FakeAppearanceRepository(settings))

        advanceUntilIdle()
        settings.value = AppearanceSettings(mode = AppearanceMode.Light, dynamicColorEnabled = true)
        advanceUntilIdle()

        assertEquals(
            AppearanceBootstrapState.Ready(
                AppearanceSettings(mode = AppearanceMode.Light, dynamicColorEnabled = true),
            ),
            target.state.value,
        )
    }

    @Test
    fun `bootstrap falls back to defaults when repository fails`() = runTest {
        val target = AppearanceBootstrapViewModel(
            FakeAppearanceRepository(
                settings = flow { throw IllegalStateException("unavailable") },
            ),
        )

        advanceUntilIdle()

        assertEquals(AppearanceBootstrapState.Ready(AppearanceSettings()), target.state.value)
    }

    private class FakeAppearanceRepository(
        override val settings: Flow<AppearanceSettings>,
    ) : AppearanceRepository {
        override suspend fun setMode(mode: AppearanceMode): AppearanceUpdateResult =
            AppearanceUpdateResult.Updated

        override suspend fun setDynamicColorEnabled(enabled: Boolean): AppearanceUpdateResult =
            AppearanceUpdateResult.Updated
    }
}
