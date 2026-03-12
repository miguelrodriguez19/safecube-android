package com.miguelrodriguez19.safecube.feature.vault.presentation.recovery

import androidx.lifecycle.ViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class RecoveryKeyViewModel @Inject constructor() : ViewModel() {
    private val mutableUiState = MutableStateFlow(RecoveryKeyUiState())
    val uiState: StateFlow<RecoveryKeyUiState> = mutableUiState.asStateFlow()

    fun setRecoveryKey(recoveryKey: String?) {
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

    fun continueToUnlock() {
        if (mutableUiState.value.recoveryKey.isBlank()) {
            mutableUiState.update { state ->
                state.copy(errorMessageRes = UiR.string.generic_error)
            }
            return
        }

        mutableUiState.update { state ->
            state.copy(continueToUnlock = true)
        }
    }

    fun consumeContinue() {
        if (!mutableUiState.value.continueToUnlock) return
        mutableUiState.update { state -> state.copy(continueToUnlock = false) }
    }
}
