package com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.action.UnlockVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.event.UnlockVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.state.UnlockVaultUiState
import dagger.hilt.android.lifecycle.HiltViewModel
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
            UnlockVaultUiAction.Submit -> unlockVault()
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

    private fun unlockVault() {
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
            val unlockError = vaultSessionManager.unlockWithPassphrase(passphrase)
            if (unlockError == null) {
                mutableUiState.update { current -> current.copy(isLoading = false) }
                mutableEvents.emit(UnlockVaultUiEvent.NavigateToApp)
            } else {
                mutableUiState.update { current ->
                    current.copy(
                        isLoading = false,
                        errorMessageRes = mapUnlockError(unlockError),
                    )
                }
            }
        }
    }

    private fun mapUnlockError(error: VaultUnlockError): Int = when (error) {
        VaultUnlockError.InvalidCredential -> UiR.string.auth_error_invalid_credentials
        VaultUnlockError.InvalidCachedKeyMaterial -> UiR.string.generic_error
        VaultUnlockError.KeyMaterialUnavailable -> UiR.string.generic_error
    }
}
