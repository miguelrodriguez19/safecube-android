package com.miguelrodriguez19.safecube.feature.vault.presentation.create.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeError
import com.miguelrodriguez19.safecube.core.vault.domain.model.initialize.VaultInitializeResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.VaultInitializeUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.action.CreateVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.event.CreateVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.state.CreateVaultUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Base64
import javax.inject.Inject
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
            CreateVaultUiAction.Submit -> createVault()
        }
    }

    private fun onPassphraseChanged(value: String) {
        mutableUiState.update { state ->
            state.copy(
                passphrase = value,
                passphraseErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    private fun createVault() {
        val state = mutableUiState.value
        if (state.isLoading) return

        val passphrase = state.passphrase.trim()
        if (passphrase.isBlank()) {
            mutableUiState.update { current ->
                current.copy(
                    passphraseErrorRes = UiR.string.password_is_required,
                    errorMessageRes = null,
                )
            }
            return
        }

        mutableUiState.update { current ->
            current.copy(
                isLoading = true,
                passphraseErrorRes = null,
                errorMessageRes = null,
            )
        }

        viewModelScope.launch {
            when (val result = vaultInitializeUseCase(passphrase)) {
                is VaultInitializeResult.Initialized -> {
                    vaultSessionManager.lock()
                    mutableUiState.update { current -> current.copy(isLoading = false) }
                    mutableEvents.emit(
                        CreateVaultUiEvent.NavigateToRecoveryKey(
                            recoveryKeyBase64 = Base64.getEncoder().encodeToString(result.recoveryKey),
                        ),
                    )
                }

                is VaultInitializeResult.AlreadyInitialized -> {
                    vaultSessionManager.lock()
                    mutableUiState.update { current -> current.copy(isLoading = false) }
                    mutableEvents.emit(CreateVaultUiEvent.NavigateToUnlock)
                }

                is VaultInitializeResult.Error -> {
                    mutableUiState.update { current ->
                        current.copy(
                            isLoading = false,
                            errorMessageRes = mapError(result.reason),
                        )
                    }
                }
            }
        }
    }

    private fun mapError(error: VaultInitializeError): Int = when (error) {
        is VaultInitializeError.Remote -> when (error.error) {
            VaultKeyMaterialRemoteError.Unauthorized -> UiR.string.auth_error_forbidden
            else -> UiR.string.generic_error
        }

        is VaultInitializeError.Crypto -> UiR.string.generic_error
    }
}
