package com.miguelrodriguez19.safecube.feature.vault.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemDraftSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultDraftSummariesUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultItemSummariesUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultDirtyStateUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.SyncVaultNowUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultHomeUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultItemSummaryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class VaultHomeViewModel @Inject constructor(
    observeVaultItemSummariesUseCase: ObserveVaultItemSummariesUseCase,
    observeVaultDraftSummariesUseCase: ObserveVaultDraftSummariesUseCase,
    observeVaultDirtyStateUseCase: ObserveVaultDirtyStateUseCase,
    observeVaultSyncingUseCase: ObserveVaultSyncingUseCase,
    private val syncVaultNowUseCase: SyncVaultNowUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(VaultHomeUiState())
    val uiState = mutableUiState.asStateFlow()
    private var isVaultScreenVisible = false
    private var hasTriggeredInitialSync = false
    private var dirtyState = false
    private var syncJob: Job? = null

    init {
        viewModelScope.launch {
            combine(
                observeVaultItemSummariesUseCase(),
                observeVaultDraftSummariesUseCase(),
            ) { summaries, draftSummaries ->
                mapToUiItems(
                    summaries = summaries,
                    draftSummaries = draftSummaries,
                )
            }.collect { items ->
                mutableUiState.update { state ->
                    state.copy(
                        items = items,
                    )
                }
            }
        }
        viewModelScope.launch {
            observeVaultDirtyStateUseCase().collect { isDirty ->
                val wasDirty = dirtyState
                dirtyState = isDirty
                mutableUiState.update { state ->
                    state.copy(isDirty = isDirty)
                }
                if (isVaultScreenVisible && isDirty && !wasDirty) {
                    requestSync()
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

    fun onVaultScreenShown() {
        isVaultScreenVisible = true
        if (!hasTriggeredInitialSync) {
            hasTriggeredInitialSync = true
            requestSync()
            return
        }
        if (dirtyState) {
            requestSync()
        }
    }

    fun onVaultScreenHidden() {
        isVaultScreenVisible = false
    }

    private fun mapToUiItems(
        summaries: List<VaultItemSummary>,
        draftSummaries: List<VaultItemDraftSummary>,
    ): List<VaultItemSummaryUiModel> {
        val draftByLogicalItemId = draftSummaries.associateBy(VaultItemDraftSummary::logicalItemId)
        val officialItems = summaries.map { summary ->
            val draft = draftByLogicalItemId[summary.logicalItemId]
            VaultItemSummaryUiModel(
                logicalItemId = summary.logicalItemId,
                displayHint = draft?.displayHint ?: summary.displayHint,
                itemType = draft?.itemType ?: summary.itemType,
                updatedAt = draft?.updatedAt ?: summary.updatedAt,
                hasDraft = draft != null,
                draftType = draft?.draftType,
                draftSyncStatus = draft?.draftSyncStatus,
                lastDraftError = draft?.lastSyncError,
            )
        }
        val createDrafts = draftSummaries
            .filter { it.draftType == SecureItemDraftType.CREATE }
            .filterNot { draft -> summaries.any { summary -> summary.logicalItemId == draft.logicalItemId } }
            .map { draft ->
                VaultItemSummaryUiModel(
                    logicalItemId = draft.logicalItemId,
                    displayHint = draft.displayHint,
                    itemType = draft.itemType,
                    updatedAt = draft.updatedAt,
                    hasDraft = true,
                    draftType = draft.draftType,
                    draftSyncStatus = draft.draftSyncStatus,
                    lastDraftError = draft.lastSyncError,
                )
            }
        return (officialItems + createDrafts).sortedByDescending(VaultItemSummaryUiModel::updatedAt)
    }

    fun syncNow() {
        requestSync()
    }

    private fun requestSync() {
        if (mutableUiState.value.isSyncing || syncJob?.isActive == true) {
            return
        }
        syncJob = viewModelScope.launch {
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
