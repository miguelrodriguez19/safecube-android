package com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase.ChangeVaultPassphraseError
import com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase.ChangeVaultPassphraseResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.ChangeVaultPassphraseUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.action.ChangePassphraseUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.event.ChangePassphraseUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.state.ChangePassphraseUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.state.ChangePassphraseUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
class ChangePassphraseViewModel @Inject constructor(
    private val changeVaultPassphraseUseCase: ChangeVaultPassphraseUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(ChangePassphraseUiState())
    val uiState: StateFlow<ChangePassphraseUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<ChangePassphraseUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<ChangePassphraseUiEvent> = mutableEvents.asSharedFlow()

    fun onAction(action: ChangePassphraseUiAction) {
        when (action) {
            ChangePassphraseUiAction.FieldsChanged -> clearInputFeedback()
            is ChangePassphraseUiAction.Submit -> submit(action)
        }
    }

    private fun clearInputFeedback() {
        if (mutableUiState.value.isLoading) return
        mutableUiState.update { ChangePassphraseUiState() }
    }

    private fun submit(action: ChangePassphraseUiAction.Submit) {
        if (mutableUiState.value.isLoading) return

        val currentMissing = action.currentPassphrase.isBlank()
        val newMissing = action.newPassphrase.isBlank()
        val confirmationMissing = action.confirmation.isBlank()
        val newIsUnchanged = !currentMissing && !newMissing &&
            action.currentPassphrase == action.newPassphrase
        val confirmationDoesNotMatch = !newMissing && !confirmationMissing &&
            action.newPassphrase != action.confirmation

        if (currentMissing || newMissing || confirmationMissing ||
            newIsUnchanged || confirmationDoesNotMatch
        ) {
            mutableUiState.update {
                ChangePassphraseUiState(
                    operationState = ChangePassphraseUiOperationState.ValidationError,
                    currentPassphraseErrorRes = UiR.string.change_passphrase_current_required
                        .takeIf { currentMissing },
                    newPassphraseErrorRes = when {
                        newMissing -> UiR.string.change_passphrase_new_required
                        newIsUnchanged -> UiR.string.change_passphrase_new_must_differ
                        else -> null
                    },
                    confirmationErrorRes = when {
                        confirmationMissing -> UiR.string.change_passphrase_confirmation_required
                        confirmationDoesNotMatch -> UiR.string.change_passphrase_confirmation_mismatch
                        else -> null
                    },
                )
            }
            return
        }

        mutableUiState.update { state ->
            state.copy(
                operationState = ChangePassphraseUiOperationState.Loading,
                currentPassphraseErrorRes = null,
                newPassphraseErrorRes = null,
                confirmationErrorRes = null,
                errorMessageRes = null,
                successMessageRes = null,
            )
        }

        viewModelScope.launch {
            try {
                when (val result = changeVaultPassphraseUseCase(
                    currentPassphrase = action.currentPassphrase,
                    newPassphrase = action.newPassphrase,
                )) {
                    ChangeVaultPassphraseResult.Success -> {
                        mutableUiState.update {
                            ChangePassphraseUiState(
                                operationState = ChangePassphraseUiOperationState.Success,
                                successMessageRes = UiR.string.change_passphrase_success,
                            )
                        }
                        mutableEvents.emit(ChangePassphraseUiEvent.ClearFields)
                    }

                    is ChangeVaultPassphraseResult.Error -> handleError(result.reason)
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                setTerminalError(UiR.string.vault_error_terminal)
            }
        }
    }

    private suspend fun handleError(error: ChangeVaultPassphraseError) {
        when (error) {
            ChangeVaultPassphraseError.InvalidCurrentPassphrase -> {
                mutableUiState.update {
                    ChangePassphraseUiState(
                        operationState = ChangePassphraseUiOperationState.InvalidCurrentPassphrase,
                        errorMessageRes = UiR.string.change_passphrase_invalid_current,
                    )
                }
                mutableEvents.emit(ChangePassphraseUiEvent.ClearFields)
            }

            is ChangeVaultPassphraseError.InvalidVaultState -> {
                mutableUiState.update {
                    ChangePassphraseUiState(
                        operationState = ChangePassphraseUiOperationState.SessionRequired,
                        errorMessageRes = UiR.string.change_passphrase_session_required,
                    )
                }
                mutableEvents.emit(ChangePassphraseUiEvent.ClearFields)
                mutableEvents.emit(ChangePassphraseUiEvent.NavigateToUnlock)
            }

            ChangeVaultPassphraseError.ConcurrentRemoteChange -> {
                mutableUiState.update {
                    ChangePassphraseUiState(
                        operationState = ChangePassphraseUiOperationState.SessionRequired,
                        errorMessageRes = UiR.string.change_passphrase_concurrent_remote_change,
                    )
                }
                mutableEvents.emit(ChangePassphraseUiEvent.ClearFields)
                mutableEvents.emit(ChangePassphraseUiEvent.NavigateToUnlock)
            }

            is ChangeVaultPassphraseError.RemoteFailure -> handleRemoteFailure(error)

            ChangeVaultPassphraseError.RemoteChangeNotApplied -> {
                mutableUiState.update {
                    ChangePassphraseUiState(
                        operationState = ChangePassphraseUiOperationState.RetryableError,
                        errorMessageRes = UiR.string.change_passphrase_not_applied,
                    )
                }
            }

            ChangeVaultPassphraseError.ReconciliationRequired -> {
                mutableUiState.update {
                    ChangePassphraseUiState(
                        operationState = ChangePassphraseUiOperationState.UncertainError,
                        errorMessageRes = UiR.string.change_passphrase_uncertain,
                    )
                }
                mutableEvents.emit(ChangePassphraseUiEvent.ClearFields)
            }

            ChangeVaultPassphraseError.InvalidNewPassphrase -> setTerminalError(
                UiR.string.change_passphrase_invalid_new,
            )

            ChangeVaultPassphraseError.LocalKeyMaterialUnavailable,
            ChangeVaultPassphraseError.InvalidLocalKeyMaterial,
                -> setTerminalError(UiR.string.vault_error_material_corrupted)

            ChangeVaultPassphraseError.ActiveKekUnavailable,
            ChangeVaultPassphraseError.CryptoFailure,
                -> setTerminalError(UiR.string.vault_error_crypto)
        }
    }

    private suspend fun handleRemoteFailure(
        error: ChangeVaultPassphraseError.RemoteFailure,
    ) {
        when {
            error.error == VaultKeyMaterialRemoteError.Unauthorized -> {
                mutableUiState.update {
                    ChangePassphraseUiState(
                        operationState = ChangePassphraseUiOperationState.SessionRequired,
                        errorMessageRes = UiR.string.change_passphrase_session_required,
                    )
                }
                mutableEvents.emit(ChangePassphraseUiEvent.ClearFields)
            }

            error.retryDecision == RetryDecision.Retryable -> {
                mutableUiState.update {
                    ChangePassphraseUiState(
                        operationState = ChangePassphraseUiOperationState.RetryableError,
                        errorMessageRes = UiR.string.change_passphrase_retryable,
                    )
                }
            }

            else -> setTerminalError(
                when (error.error) {
                    VaultKeyMaterialRemoteError.Forbidden -> UiR.string.vault_error_forbidden
                    else -> UiR.string.vault_error_terminal
                },
            )
        }
    }

    private suspend fun setTerminalError(messageRes: Int) {
        mutableUiState.update {
            ChangePassphraseUiState(
                operationState = ChangePassphraseUiOperationState.TerminalError,
                errorMessageRes = messageRes,
            )
        }
        mutableEvents.emit(ChangePassphraseUiEvent.ClearFields)
    }
}
