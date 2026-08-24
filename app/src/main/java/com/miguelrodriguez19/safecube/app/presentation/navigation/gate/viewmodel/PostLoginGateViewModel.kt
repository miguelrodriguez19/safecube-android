package com.miguelrodriguez19.safecube.app.presentation.navigation.gate.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.GateDestination
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.event.PostLoginGateUiEvent
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.resolveGateDestination
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.resolveGateMessage
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.shouldShowGateLoading
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.state.PostLoginGateUiState
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault.VaultInitializeUseCase
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
class PostLoginGateViewModel @Inject constructor(
    private val vaultSessionManager: VaultSessionManager,
    private val vaultInitializeUseCase: VaultInitializeUseCase,
    private val accountSessionLifecycle: AccountSessionLifecycle,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(PostLoginGateUiState())
    val uiState: StateFlow<PostLoginGateUiState> = mutableUiState.asStateFlow()

    private val eventsChannel = Channel<PostLoginGateUiEvent>(Channel.BUFFERED)
    val events: Flow<PostLoginGateUiEvent> = eventsChannel.receiveAsFlow()

    init {
        refresh()
    }

    fun retry() {
        if (!mutableUiState.value.isLoading) {
            refresh()
        }
    }

    private fun refresh() {
        mutableUiState.update { current ->
            current.copy(
                isLoading = true,
                messageRes = UiR.string.vault_bootstrap_loading,
            )
        }

        viewModelScope.launch {
            try {
                val pendingInitializationStatus =
                    vaultInitializeUseCase.readPendingInitializationStatus()
                vaultSessionManager.refreshVaultState()
                val vaultState = vaultSessionManager.vaultState.value
                val destination = resolveGateDestination(
                    vaultState = vaultState,
                    pendingInitializationStatus = pendingInitializationStatus,
                )

                mutableUiState.update { current ->
                    current.copy(
                        isLoading = shouldShowGateLoading(
                            vaultState = vaultState,
                            pendingInitializationStatus = pendingInitializationStatus,
                        ),
                        messageRes = resolveGateMessage(
                            vaultState = vaultState,
                            pendingInitializationStatus = pendingInitializationStatus,
                        ),
                    )
                }
                handleDestination(destination)
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                mutableUiState.update { current ->
                    current.copy(
                        isLoading = false,
                        messageRes = UiR.string.vault_bootstrap_terminal_error,
                    )
                }
            }
        }
    }

    private suspend fun handleDestination(destination: GateDestination) {
        when (destination) {
            GateDestination.Stay,
            GateDestination.PendingInitializationError,
                -> Unit

            GateDestination.CreateVault -> eventsChannel.send(PostLoginGateUiEvent.CreateVault)
            GateDestination.RecoveryKey -> eventsChannel.send(PostLoginGateUiEvent.RecoveryKey)
            GateDestination.UnlockVault -> eventsChannel.send(PostLoginGateUiEvent.UnlockVault)
            GateDestination.Home -> eventsChannel.send(PostLoginGateUiEvent.Home)
            GateDestination.AuthenticationRequired -> accountSessionLifecycle.terminateSession(
                reason = SessionTerminationReason.SessionExpired,
            )
        }
    }
}
