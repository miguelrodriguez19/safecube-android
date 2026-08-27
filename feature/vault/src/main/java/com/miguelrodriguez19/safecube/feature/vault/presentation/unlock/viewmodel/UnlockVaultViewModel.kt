package com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCompletionResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.QuickUnlockPromptMode
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptOperation
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptRequest
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.action.UnlockVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.event.UnlockVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.state.UnlockVaultUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class UnlockVaultViewModel @Inject constructor(
    private val vaultSessionManager: VaultSessionManager,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(UnlockVaultUiState())
    val uiState: StateFlow<UnlockVaultUiState> = mutableUiState.asStateFlow()

    private val eventsChannel = Channel<UnlockVaultUiEvent>(Channel.BUFFERED)
    val events: Flow<UnlockVaultUiEvent> = eventsChannel.receiveAsFlow()

    private var quickUnlockAvailabilityChecked = false
    private var pendingPrompt: QuickUnlockPromptRequest? = null
    private var navigationEmitted = false

    fun onAction(action: UnlockVaultUiAction) {
        when (action) {
            is UnlockVaultUiAction.PassphraseChanged -> onPassphraseChanged(action.value)
            UnlockVaultUiAction.ScreenEntered -> checkQuickUnlockAvailability()
            UnlockVaultUiAction.Submit -> submit()
            UnlockVaultUiAction.Retry -> retry()
            UnlockVaultUiAction.RetryQuickUnlock -> prepareQuickUnlock()
            UnlockVaultUiAction.EnableQuickUnlock -> prepareQuickUnlockEnrollment()
            UnlockVaultUiAction.DeclineQuickUnlock -> declineQuickUnlockOffer()
            is UnlockVaultUiAction.QuickUnlockPromptSucceeded -> finishPrompt(action.operationId)
            is UnlockVaultUiAction.QuickUnlockPromptCancelled -> cancelPrompt(action.operationId)
        }
    }

    fun onQuickUnlockPromptPresented(operationId: String) {
        if (pendingPrompt?.operationId != operationId) return
        mutableUiState.update { current -> current.copy(quickUnlockPromptPresented = true) }
    }

    override fun onCleared() {
        cancelPendingPrompt()
        eventsChannel.close()
        super.onCleared()
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

    private fun checkQuickUnlockAvailability() {
        if (quickUnlockAvailabilityChecked) return
        quickUnlockAvailabilityChecked = true
        if (vaultSessionManager.quickUnlockOfferState() == QuickUnlockOfferState.Enrolled) {
            mutableUiState.update { current ->
                current.copy(hasQuickUnlockEnrollment = true)
            }
            if (vaultSessionManager.quickUnlockPromptMode() ==
                QuickUnlockPromptMode.AutomaticOnUnlockEntry
            ) {
                prepareQuickUnlock()
            }
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
        if (mutableUiState.value.isRetryable) unlockVault()
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
        cancelPendingPrompt()
        mutableUiState.update { current ->
            current.copy(
                operationState = VaultUiOperationState.Loading,
                passphraseErrorRes = null,
                errorMessageRes = null,
                canRetryQuickUnlock = false,
            )
        }

        viewModelScope.launch {
            try {
                val unlockError = vaultSessionManager.unlockWithPassphrase(passphrase)
                when {
                    unlockError == null && vaultSessionManager.isUnlocked() -> onPassphraseUnlocked()
                    unlockError == null -> mutableUiState.update { current ->
                        current.copy(
                            operationState = VaultUiOperationState.RetryableError,
                            errorMessageRes = UiR.string.vault_error_locked_during_operation,
                        )
                    }

                    else -> mutableUiState.update { current ->
                        current.copy(
                            passphrase = "",
                            operationState = VaultUiOperationState.TerminalError,
                            errorMessageRes = mapUnlockError(unlockError),
                        )
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

    private fun onPassphraseUnlocked() {
        mutableUiState.update { current ->
            current.copy(
                passphrase = "",
                operationState = VaultUiOperationState.Success,
                errorMessageRes = null,
            )
        }
        if (vaultSessionManager.consumeQuickUnlockEnrollmentAfterPassphrase()) {
            prepareQuickUnlockEnrollment()
            return
        }
        when (vaultSessionManager.quickUnlockOfferState()) {
            QuickUnlockOfferState.Available -> mutableUiState.update { current ->
                current.copy(showQuickUnlockOffer = true)
            }

            else -> navigateToApp()
        }
    }

    private fun declineQuickUnlockOffer() {
        vaultSessionManager.markQuickUnlockOfferSeen()
        mutableUiState.update { current -> current.copy(showQuickUnlockOffer = false) }
        navigateToApp()
    }

    private fun prepareQuickUnlockEnrollment() {
        if (pendingPrompt != null) return
        mutableUiState.update { current ->
            current.copy(showQuickUnlockOffer = false, errorMessageRes = null)
        }
        when (val result = vaultSessionManager.prepareQuickUnlockEnrollment(consentGranted = true)) {
            is QuickUnlockEnrollmentPreparationResult.Ready -> emitPrompt(
                operationId = result.operationId,
                operation = QuickUnlockPromptOperation.Enrollment,
            )

            else -> {
                mutableUiState.update { current ->
                    current.copy(errorMessageRes = UiR.string.quick_unlock_error)
                }
                navigateToApp()
            }
        }
    }

    private fun prepareQuickUnlock() {
        if (pendingPrompt != null) return
        when (val result = vaultSessionManager.prepareQuickUnlock()) {
            is QuickUnlockPreparationResult.Ready -> emitPrompt(
                operationId = result.operationId,
                operation = QuickUnlockPromptOperation.Unlock,
            )

            else -> quickUnlockFailed()
        }
    }

    private fun emitPrompt(operationId: String, operation: QuickUnlockPromptOperation) {
        val request = QuickUnlockPromptRequest(operationId = operationId, operation = operation)
        pendingPrompt = request
        mutableUiState.update { current ->
            current.copy(
                canRetryQuickUnlock = false,
                errorMessageRes = null,
                pendingQuickUnlockPrompt = request,
                quickUnlockPromptPresented = false,
            )
        }
        eventsChannel.trySend(UnlockVaultUiEvent.LaunchQuickUnlockPrompt(request))
    }

    private fun finishPrompt(operationId: String) {
        val request = takePendingPrompt(operationId) ?: return
        when (request.operation) {
            QuickUnlockPromptOperation.Unlock -> when (
                vaultSessionManager.finishQuickUnlock(operationId)
            ) {
                QuickUnlockCompletionResult.Unlocked -> navigateToApp()
                else -> quickUnlockFailed()
            }

            QuickUnlockPromptOperation.Enrollment -> {
                when (vaultSessionManager.finishQuickUnlockEnrollment(operationId)) {
                    QuickUnlockEnrollmentResult.Enrolled -> Unit
                    else -> mutableUiState.update { current ->
                        current.copy(errorMessageRes = UiR.string.quick_unlock_error)
                    }
                }
                navigateToApp()
            }
        }
    }

    private fun cancelPrompt(operationId: String) {
        val request = takePendingPrompt(operationId) ?: return
        vaultSessionManager.cancelQuickUnlock(operationId)
        when (request.operation) {
            QuickUnlockPromptOperation.Unlock -> quickUnlockFailed()
            QuickUnlockPromptOperation.Enrollment -> navigateToApp()
        }
    }

    private fun takePendingPrompt(operationId: String): QuickUnlockPromptRequest? = pendingPrompt
        ?.takeIf { it.operationId == operationId }
        ?.also {
            pendingPrompt = null
            mutableUiState.update { current ->
                current.copy(
                    pendingQuickUnlockPrompt = null,
                    quickUnlockPromptPresented = false,
                )
            }
        }

    private fun cancelPendingPrompt() {
        pendingPrompt?.let { vaultSessionManager.cancelQuickUnlock(it.operationId) }
        pendingPrompt = null
        mutableUiState.update { current ->
            current.copy(
                pendingQuickUnlockPrompt = null,
                quickUnlockPromptPresented = false,
            )
        }
    }

    private fun quickUnlockFailed() {
        mutableUiState.update { current ->
            current.copy(
                operationState = VaultUiOperationState.Idle,
                errorMessageRes = UiR.string.quick_unlock_error,
                canRetryQuickUnlock = true,
            )
        }
    }

    private fun navigateToApp() {
        if (navigationEmitted) return
        navigationEmitted = true
        eventsChannel.trySend(UnlockVaultUiEvent.NavigateToApp)
    }

    private fun mapUnlockError(error: VaultUnlockError): Int = when (error) {
        VaultUnlockError.InvalidCredential -> UiR.string.vault_error_invalid_passphrase
        VaultUnlockError.InvalidCachedKeyMaterial -> UiR.string.vault_error_material_corrupted
        VaultUnlockError.KeyMaterialUnavailable -> UiR.string.vault_error_material_unavailable
    }
}
