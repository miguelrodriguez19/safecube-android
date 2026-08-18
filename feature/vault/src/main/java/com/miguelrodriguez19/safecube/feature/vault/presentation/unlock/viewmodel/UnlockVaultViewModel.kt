package com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.action.UnlockVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.event.UnlockVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.state.UnlockVaultUiState
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
class UnlockVaultViewModel @Inject constructor(
    private val vaultSessionManager: VaultSessionManager,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(UnlockVaultUiState())
    val uiState: StateFlow<UnlockVaultUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<UnlockVaultUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<UnlockVaultUiEvent> = mutableEvents.asSharedFlow()

    fun onAction(action: UnlockVaultUiAction) {
        when (action) {
            is UnlockVaultUiAction.PassphraseChanged -> onPassphraseChanged(action.value)
            UnlockVaultUiAction.Submit -> submit()
            UnlockVaultUiAction.Retry -> retry()
        }
    }

    private fun onPassphraseChanged(value: String) {
        mutableUiState.update { state ->
            state.copy(
                passphrase = value,
                operationState = if (state.operationState == VaultUiOperationState.Loading) {
                    VaultUiOperationState.Loading
                } else {
                    VaultUiOperationState.Idle
                },
                passphraseErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    private fun submit() {
        if (mutableUiState.value.isRetryable) {
            retry()
        } else {
            unlockVault()
        }
    }

    private fun retry() {
        if (!mutableUiState.value.isRetryable) return
        unlockVault()
    }

    private fun unlockVault() {
        val state = mutableUiState.value
        if (state.isLoading) return

        val passphrase = state.passphrase.trim()
        if (passphrase.isBlank()) {
            mutableUiState.update { current ->
                current.copy(
                    operationState = VaultUiOperationState.TerminalError,
                    passphraseErrorRes = UiR.string.password_is_required,
                    errorMessageRes = null,
                )
            }
            return
        }

        mutableUiState.update { current ->
            current.copy(
                operationState = VaultUiOperationState.Loading,
                passphraseErrorRes = null,
                errorMessageRes = null,
            )
        }

        viewModelScope.launch {
            try {
                val unlockError = vaultSessionManager.unlockWithPassphrase(passphrase)
                when {
                    unlockError == null && vaultSessionManager.isUnlocked() -> {
                        mutableUiState.update { current ->
                            current.copy(
                                passphrase = "",
                                operationState = VaultUiOperationState.Success,
                            )
                        }
                        mutableEvents.emit(UnlockVaultUiEvent.NavigateToApp)
                    }

                    unlockError == null -> {
                        mutableUiState.update { current ->
                            current.copy(
                                operationState = VaultUiOperationState.RetryableError,
                                errorMessageRes = UiR.string.vault_error_locked_during_operation,
                            )
                        }
                    }

                    else -> {
                        mutableUiState.update { current ->
                            current.copy(
                                passphrase = "",
                                operationState = VaultUiOperationState.TerminalError,
                                errorMessageRes = mapUnlockError(unlockError),
                            )
                        }
                    }
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                mutableUiState.update { current ->
                    current.copy(
                        passphrase = "",
                        operationState = VaultUiOperationState.TerminalError,
                        errorMessageRes = UiR.string.vault_error_terminal,
                    )
                }
            }
        }
    }

    private fun mapUnlockError(error: VaultUnlockError): Int = when (error) {
        VaultUnlockError.InvalidCredential -> UiR.string.vault_error_invalid_passphrase
        VaultUnlockError.InvalidCachedKeyMaterial -> UiR.string.vault_error_material_corrupted
        VaultUnlockError.KeyMaterialUnavailable -> UiR.string.vault_error_material_unavailable
    }
}
