package com.miguelrodriguez19.safecube.feature.vault.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultItemSummariesUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.SyncVaultNowUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultHomeUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultItemSummaryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class VaultHomeViewModel @Inject constructor(
    observeVaultItemSummariesUseCase: ObserveVaultItemSummariesUseCase,
    observeVaultSyncingUseCase: ObserveVaultSyncingUseCase,
    private val syncVaultNowUseCase: SyncVaultNowUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(VaultHomeUiState())
    val uiState = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            observeVaultItemSummariesUseCase().collect { summaries ->
                mutableUiState.update { state ->
                    state.copy(
                        items = summaries.map { summary ->
                            VaultItemSummaryUiModel(
                                logicalItemId = summary.logicalItemId,
                                displayHint = summary.displayHint,
                                itemType = summary.itemType,
                                updatedAt = summary.updatedAt,
                                syncState = summary.syncState,
                                lastSyncError = summary.lastSyncError,
                            )
                        },
                    )
                }
            }
        }
        viewModelScope.launch {
            observeVaultSyncingUseCase().collect { isSyncing ->
                mutableUiState.update { state ->
                    state.copy(isSyncing = isSyncing)
                }
            }
        }
    }

    fun syncNow() {
        if (mutableUiState.value.isSyncing) {
            return
        }
        viewModelScope.launch {
            val result = syncVaultNowUseCase()
            mutableUiState.update { state ->
                state.copy(
                    lastSyncResult = result,
                    lastSyncError = (result as? VaultSyncResult.Error)?.reason,
                )
            }
        }
    }
}
