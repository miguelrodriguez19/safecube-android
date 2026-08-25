package com.miguelrodriguez19.safecube.feature.vault.presentation.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.AutoLockTimeout
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentPreparationResult
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockEnrollmentResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.AutoLockTimeoutRepository
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultDraftSummariesUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.PendingQuickUnlockEnrollment
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptOperation
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptRequest
import com.miguelrodriguez19.safecube.feature.vault.presentation.settings.event.SettingsUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.settings.state.SettingsQuickUnlockUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeVaultDraftSummariesUseCase: ObserveVaultDraftSummariesUseCase,
    private val autoLockTimeoutRepository: AutoLockTimeoutRepository,
    private val vaultSessionManager: VaultSessionManager,
) : ViewModel() {
    val hasActiveDrafts: StateFlow<Boolean?> = observeVaultDraftSummariesUseCase()
        .map { drafts -> drafts.isNotEmpty() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = null,
        )

    val autoLockTimeout: StateFlow<AutoLockTimeout> = autoLockTimeoutRepository.timeout

    private val mutableQuickUnlockUiState = MutableStateFlow(
        SettingsQuickUnlockUiState(offerState = vaultSessionManager.quickUnlockOfferState()),
    )
    val quickUnlockUiState: StateFlow<SettingsQuickUnlockUiState> =
        mutableQuickUnlockUiState.asStateFlow()

    private val eventsChannel = Channel<SettingsUiEvent>(Channel.BUFFERED)
    val events: Flow<SettingsUiEvent> = eventsChannel.receiveAsFlow()

    private var pendingEnrollmentOperationId: String? = null

    fun setAutoLockTimeout(timeout: AutoLockTimeout) {
        autoLockTimeoutRepository.setTimeout(timeout)
    }

    fun enableQuickUnlock() {
        if (pendingEnrollmentOperationId != null) return
        when (val result = vaultSessionManager.prepareQuickUnlockEnrollment(consentGranted = true)) {
            is QuickUnlockEnrollmentPreparationResult.Ready -> {
                pendingEnrollmentOperationId = result.operationId
                mutableQuickUnlockUiState.update { it.copy(errorMessageRes = null) }
                eventsChannel.trySend(
                    SettingsUiEvent.LaunchQuickUnlockPrompt(
                        QuickUnlockPromptRequest(
                            operationId = result.operationId,
                            operation = QuickUnlockPromptOperation.Enrollment,
                        ),
                    ),
                )
            }

            QuickUnlockEnrollmentPreparationResult.RequiresPassphrase -> {
                PendingQuickUnlockEnrollment.request()
                vaultSessionManager.lock()
            }

            else -> setQuickUnlockError()
        }
    }

    fun disableQuickUnlock() {
        when (vaultSessionManager.clearQuickUnlockEnrollment()) {
            QuickUnlockCleanupResult.Cleared -> refreshQuickUnlockState()
            else -> setQuickUnlockError()
        }
    }

    fun onQuickUnlockPromptSucceeded(operationId: String) {
        if (pendingEnrollmentOperationId != operationId) return
        pendingEnrollmentOperationId = null
        when (vaultSessionManager.finishQuickUnlockEnrollment(operationId)) {
            QuickUnlockEnrollmentResult.Enrolled -> refreshQuickUnlockState()
            else -> setQuickUnlockError()
        }
    }

    fun onQuickUnlockPromptCancelled(operationId: String) {
        if (pendingEnrollmentOperationId != operationId) return
        pendingEnrollmentOperationId = null
        vaultSessionManager.cancelQuickUnlock(operationId)
        refreshQuickUnlockState()
    }

    private fun refreshQuickUnlockState() {
        mutableQuickUnlockUiState.update {
            SettingsQuickUnlockUiState(offerState = vaultSessionManager.quickUnlockOfferState())
        }
    }

    private fun setQuickUnlockError() {
        mutableQuickUnlockUiState.update {
            it.copy(errorMessageRes = UiR.string.quick_unlock_error)
        }
    }
}
