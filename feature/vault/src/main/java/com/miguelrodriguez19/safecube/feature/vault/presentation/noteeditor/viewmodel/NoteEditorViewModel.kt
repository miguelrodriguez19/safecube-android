package com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureNoteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.CreateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.UpdateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.action.NoteEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.event.NoteEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.state.NoteEditorUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.error.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val observeSecureItemDetailUseCase: ObserveSecureItemDetailUseCase,
    private val createSecureNoteUseCase: CreateSecureNoteUseCase,
    private val updateSecureNoteUseCase: UpdateSecureNoteUseCase,
    private val softDeleteSecureItemUseCase: SoftDeleteSecureItemUseCase,
    observeVaultSyncingUseCase: ObserveVaultSyncingUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<NoteEditorUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<NoteEditorUiEvent> = mutableEvents.asSharedFlow()
    private var observeItemJob: Job? = null

    init {
        viewModelScope.launch {
            observeVaultSyncingUseCase().collect { isSyncing ->
                mutableUiState.update { state ->
                    state.copy(isSyncing = isSyncing)
                }
            }
        }
    }

    fun load(logicalItemId: String?) {
        stopObservingItem()
        if (logicalItemId == null) {
            mutableUiState.value = NoteEditorUiState(
                isSyncing = mutableUiState.value.isSyncing,
            )
            return
        }

        val parsedLogicalItemId = logicalItemId.toUuidOrNull()
        if (parsedLogicalItemId == null) {
            showError(SecureItemCrudError.ItemNotFound)
            return
        }

        mutableUiState.update { state ->
            state.copy(
                logicalItemId = parsedLogicalItemId,
                isLoading = true,
                errorMessage = null,
                hasUnsavedLocalChanges = false,
            )
        }

        observeItemJob = viewModelScope.launch {
            observeSecureItemDetailUseCase(parsedLogicalItemId).collect { result ->
                when (result) {
                    is ObserveSecureItemDetailResult.Success -> showDetail(result)
                    is ObserveSecureItemDetailResult.Error -> showError(result.reason)
                }
            }
        }
    }

    fun onAction(action: NoteEditorUiAction) {
        when (action) {
            is NoteEditorUiAction.DisplayHintChanged -> updateState { copy(displayHint = action.value) }
            is NoteEditorUiAction.BodyChanged -> updateState { copy(body = action.value) }
            NoteEditorUiAction.SaveClicked -> save()
            NoteEditorUiAction.DeleteClicked -> delete()
        }
    }

    private fun showDetail(result: ObserveSecureItemDetailResult.Success) {
        val content = result.detail.content as? NoteSecureItemContent
        if (content == null) {
            showError(SecureItemCrudError.ItemNotFound)
            return
        }

        val shouldPreserveDraft = mutableUiState.value.hasUnsavedLocalChanges &&
            mutableUiState.value.logicalItemId == result.detail.logicalItemId

        mutableUiState.update { state ->
            if (shouldPreserveDraft) {
                state.copy(
                    logicalItemId = result.detail.logicalItemId,
                    itemSyncState = result.detail.syncState,
                    itemSyncError = result.detail.lastSyncError,
                    isLoading = false,
                    isSaving = false,
                    errorMessage = null,
                )
            } else {
                state.copy(
                    logicalItemId = result.detail.logicalItemId,
                    displayHint = result.detail.displayHint,
                    body = content.body,
                    itemSyncState = result.detail.syncState,
                    itemSyncError = result.detail.lastSyncError,
                    isLoading = false,
                    isSaving = false,
                    hasUnsavedLocalChanges = false,
                    errorMessage = null,
                )
            }
        }
    }

    private fun updateState(transform: NoteEditorUiState.() -> NoteEditorUiState) {
        mutableUiState.update { state ->
            state.transform().copy(
                errorMessage = null,
                isLoading = false,
                hasUnsavedLocalChanges = true,
            )
        }
    }

    private fun save() {
        val state = mutableUiState.value
        if (state.isLoading || state.isSaving) return

        mutableUiState.update { current ->
            current.copy(
                isSaving = true,
                errorMessage = null,
            )
        }

        viewModelScope.launch {
            val draft = SecureNoteDraft(
                displayHint = state.displayHint,
                body = state.body,
            )
            val result = if (state.logicalItemId == null) {
                createSecureNoteUseCase(draft)
            } else {
                updateSecureNoteUseCase(state.logicalItemId, draft)
            }
            handleMutationResult(result)
        }
    }

    private fun delete() {
        val state = mutableUiState.value
        val logicalItemId = state.logicalItemId ?: return
        if (state.isLoading || state.isSaving) return

        mutableUiState.update { current ->
            current.copy(
                isSaving = true,
                errorMessage = null,
            )
        }

        viewModelScope.launch {
            handleMutationResult(softDeleteSecureItemUseCase(logicalItemId))
        }
    }

    private suspend fun handleMutationResult(result: SecureItemMutationResult) {
        when (result) {
            is SecureItemMutationResult.Success -> {
                stopObservingItem()
                mutableUiState.value = NoteEditorUiState(
                    isSyncing = mutableUiState.value.isSyncing,
                )
                mutableEvents.emit(NoteEditorUiEvent.NavigateBack)
            }

            is SecureItemMutationResult.Error -> {
                showError(result.reason)
            }
        }
    }

    private fun showError(error: SecureItemCrudError) {
        mutableUiState.update { state ->
            state.copy(
                isLoading = false,
                isSaving = false,
                errorMessage = error.asUiMessage(),
            )
        }
    }

    override fun onCleared() {
        stopObservingItem()
        super.onCleared()
    }

    private fun stopObservingItem() {
        observeItemJob?.cancel()
        observeItemJob = null
    }
}

private fun String.toUuidOrNull(): UUID? = runCatching { UUID.fromString(this) }.getOrNull()
