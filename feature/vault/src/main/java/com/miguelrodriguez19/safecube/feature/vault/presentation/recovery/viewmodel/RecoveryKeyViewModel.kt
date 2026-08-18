package com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.PendingVaultRecoveryKeyResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultInitializeUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action.RecoveryKeyUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.event.RecoveryKeyUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state.RecoveryKeyUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Base64
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class RecoveryKeyViewModel @Inject constructor(
    private val vaultInitializeUseCase: VaultInitializeUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(RecoveryKeyUiState())
    val uiState: StateFlow<RecoveryKeyUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<RecoveryKeyUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<RecoveryKeyUiEvent> = mutableEvents.asSharedFlow()

    init {
        loadRecoveryKey()
    }

    fun onAction(action: RecoveryKeyUiAction) {
        when (action) {
            RecoveryKeyUiAction.Retry -> retry()
            is RecoveryKeyUiAction.ConfirmationChanged -> setConfirmation(action.isConfirmed)
            RecoveryKeyUiAction.Continue -> continueToUnlock()
        }
    }

    private fun retry() {
        if (!mutableUiState.value.isRetryable) return
        loadRecoveryKey()
    }

    private fun loadRecoveryKey() {
        mutableUiState.update { current ->
            current.copy(
                operationState = VaultUiOperationState.Loading,
                isConfirmed = false,
                errorMessageRes = null,
            )
        }

        viewModelScope.launch {
            try {
                when (val result = vaultInitializeUseCase.readPendingRecoveryKey()) {
                    is PendingVaultRecoveryKeyResult.Available -> {
                        val encodedRecoveryKey = try {
                            Base64.getEncoder().encodeToString(result.recoveryKey)
                        } finally {
                            result.recoveryKey.fill(0)
                        }
                        mutableUiState.update { current ->
                            current.copy(
                                recoveryKey = encodedRecoveryKey,
                                operationState = VaultUiOperationState.Success,
                                isConfirmed = false,
                                errorMessageRes = null,
                            )
                        }
                    }

                    PendingVaultRecoveryKeyResult.Unavailable -> showTerminalError(
                        UiR.string.vault_recovery_unavailable,
                    )

                    PendingVaultRecoveryKeyResult.Corrupted -> showTerminalError(
                        UiR.string.vault_error_material_corrupted,
                    )
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                showTerminalError(UiR.string.vault_error_terminal)
            }
        }
    }

    private fun setConfirmation(isConfirmed: Boolean) {
        mutableUiState.update { current ->
            if (current.operationState != VaultUiOperationState.Success ||
                current.recoveryKey.isBlank()
            ) {
                current
            } else {
                current.copy(
                    isConfirmed = isConfirmed,
                    errorMessageRes = null,
                )
            }
        }
    }

    private fun continueToUnlock() {
        val state = mutableUiState.value
        if (state.isLoading) return

        if (state.recoveryKey.isBlank()) {
            showTerminalError(UiR.string.vault_recovery_unavailable)
            return
        }

        if (!state.isConfirmed) {
            mutableUiState.update { current ->
                current.copy(errorMessageRes = UiR.string.vault_recovery_confirmation_required)
            }
            return
        }

        mutableUiState.update { current ->
            current.copy(
                operationState = VaultUiOperationState.Loading,
                errorMessageRes = null,
            )
        }

        viewModelScope.launch {
            if (vaultInitializeUseCase.confirmRecoveryKeySaved()) {
                mutableUiState.update { current ->
                    current.copy(operationState = VaultUiOperationState.Success)
                }
                mutableEvents.emit(RecoveryKeyUiEvent.ContinueToUnlock)
            } else {
                mutableUiState.update { current ->
                    current.copy(
                        operationState = VaultUiOperationState.RetryableError,
                        errorMessageRes = UiR.string.vault_recovery_confirmation_failed,
                    )
                }
            }
        }
    }

    private fun showTerminalError(messageRes: Int) {
        mutableUiState.update { current ->
            current.copy(
                recoveryKey = "",
                operationState = VaultUiOperationState.TerminalError,
                isConfirmed = false,
                errorMessageRes = messageRes,
            )
        }
    }
}
