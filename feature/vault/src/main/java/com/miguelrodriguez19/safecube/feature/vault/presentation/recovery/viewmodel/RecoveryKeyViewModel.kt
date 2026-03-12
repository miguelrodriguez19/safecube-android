package com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.viewmodel

import androidx.lifecycle.ViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action.RecoveryKeyUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.event.RecoveryKeyUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state.RecoveryKeyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class RecoveryKeyViewModel @Inject constructor() : ViewModel() {
    private val mutableUiState = MutableStateFlow(RecoveryKeyUiState())
    val uiState: StateFlow<RecoveryKeyUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<RecoveryKeyUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<RecoveryKeyUiEvent> = mutableEvents.asSharedFlow()

    fun onAction(action: RecoveryKeyUiAction) {
        when (action) {
            is RecoveryKeyUiAction.SetRecoveryKey -> setRecoveryKey(action.value)
            RecoveryKeyUiAction.Continue -> continueToUnlock()
        }
    }

    private fun setRecoveryKey(recoveryKey: String?) {
        if (mutableUiState.value.recoveryKey.isNotBlank()) return

        val normalized = recoveryKey?.trim().orEmpty()
        if (normalized.isBlank()) {
            mutableUiState.update { state ->
                state.copy(errorMessageRes = UiR.string.generic_error)
            }
            return
        }

        mutableUiState.update { state ->
            state.copy(
                recoveryKey = normalized,
                errorMessageRes = null,
            )
        }
    }

    private fun continueToUnlock() {
        if (mutableUiState.value.recoveryKey.isBlank()) {
            mutableUiState.update { state ->
                state.copy(errorMessageRes = UiR.string.generic_error)
            }
            return
        }

        mutableEvents.tryEmit(RecoveryKeyUiEvent.ContinueToUnlock)
    }
}
