package com.miguelrodriguez19.safecube.app.appearance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceRepository
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

sealed interface AppearanceBootstrapState {
    data object Loading : AppearanceBootstrapState

    data class Ready(
        val settings: AppearanceSettings,
    ) : AppearanceBootstrapState
}

@HiltViewModel
class AppearanceBootstrapViewModel @Inject constructor(
    appearanceRepository: AppearanceRepository,
) : ViewModel() {
    private val _state = MutableStateFlow<AppearanceBootstrapState>(AppearanceBootstrapState.Loading)
    val state: StateFlow<AppearanceBootstrapState> = _state.asStateFlow()

    init {
        appearanceRepository.settings
            .onEach { settings ->
                _state.value = AppearanceBootstrapState.Ready(settings)
            }
            .catch { throwable ->
                if (throwable is CancellationException) {
                    throw throwable
                }
                _state.value = AppearanceBootstrapState.Ready(AppearanceSettings())
            }
            .launchIn(viewModelScope)
    }
}
