package com.miguelrodriguez19.safecube.feature.vault.presentation.create.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeError
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.session.QuickUnlockPromptMode
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultInitializeUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.action.CreateVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.event.CreateVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.state.CreateVaultUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
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
class CreateVaultViewModel @Inject constructor(
    private val vaultInitializeUseCase: VaultInitializeUseCase,
    private val vaultSessionManager: VaultSessionManager,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(CreateVaultUiState())
    val uiState: StateFlow<CreateVaultUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<CreateVaultUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<CreateVaultUiEvent> = mutableEvents.asSharedFlow()

    fun onAction(action: CreateVaultUiAction) {
        when (action) {
            is CreateVaultUiAction.PassphraseChanged -> onPassphraseChanged(action.value)
            CreateVaultUiAction.Submit -> submit()
            CreateVaultUiAction.Retry -> retry()
        }
    }

    private fun onPassphraseChanged(value: String) {
        mutableUiState.update { state ->
            state.copy(
                passphrase = value,
                operationState = state.operationState.takeIf {
                    it == VaultUiOperationState.Loading
                } ?: VaultUiOperationState.Idle,
                passphraseErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    private fun submit() {
        if (mutableUiState.value.isRetryable) {
            retry()
        } else {
            createVault()
        }
    }

    private fun retry() {
        if (!mutableUiState.value.isRetryable) return
        createVault()
    }

    private fun createVault() {
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
                when (val result = vaultInitializeUseCase(passphrase)) {
                    is VaultInitializeResult.Initialized -> {
                        result.recoveryKey.fill(0)
                        vaultSessionManager.lock(QuickUnlockPromptMode.AutomaticOnUnlockEntry)
                        mutableUiState.update { current ->
                            current.copy(
                                passphrase = "",
                                operationState = VaultUiOperationState.Success,
                            )
                        }
                        mutableEvents.emit(CreateVaultUiEvent.NavigateToRecoveryKey)
                    }

                    is VaultInitializeResult.AlreadyInitialized -> {
                        vaultSessionManager.lock(QuickUnlockPromptMode.AutomaticOnUnlockEntry)
                        mutableUiState.update { current ->
                            current.copy(
                                passphrase = "",
                                operationState = VaultUiOperationState.Success,
                            )
                        }
                        mutableEvents.emit(CreateVaultUiEvent.NavigateToUnlock)
                    }

                    is VaultInitializeResult.Error -> {
                        mutableUiState.update { current ->
                            current.copy(
                                passphrase = if (isRetryable(result.reason)) current.passphrase else "",
                                operationState = if (isRetryable(result.reason)) {
                                    VaultUiOperationState.RetryableError
                                } else {
                                    VaultUiOperationState.TerminalError
                                },
                                errorMessageRes = mapError(result.reason),
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

    private fun mapError(error: VaultInitializeError): Int = when (error) {
        is VaultInitializeError.Remote -> when (error.error) {
            VaultKeyMaterialRemoteError.Unauthorized -> UiR.string.vault_error_unauthorized
            VaultKeyMaterialRemoteError.Forbidden -> UiR.string.vault_error_forbidden
            else -> if (isRetryable(error)) {
                UiR.string.vault_error_retryable
            } else {
                UiR.string.vault_error_terminal
            }
        }

        is VaultInitializeError.Crypto -> UiR.string.vault_error_crypto
        is VaultInitializeError.LocalStorage -> UiR.string.vault_error_local_storage
    }

    private fun isRetryable(initializeError: VaultInitializeError): Boolean =
        initializeError is VaultInitializeError.Remote &&
            initializeError.error.failure.decision == RetryDecision.Retryable
}
