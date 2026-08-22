package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.note.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncResult
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.note.action.NoteEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.note.event.NoteEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.note.state.NoteEditorUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.lifecycle.SecureItemEditorLifecycleCoordinator
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.contract.SecureItemEditorMutationOperations
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.mutation.model.SecureItemEditorMutationRequest
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.observation.SecureItemEditorObservationCoordinator
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.observation.model.SecureItemEditorObservationResult
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.state.SecureItemEditorState
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.error.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NoteEditorViewModel @Inject internal constructor(
    private val observationCoordinator: SecureItemEditorObservationCoordinator,
    private val mutationOperations: SecureItemEditorMutationOperations,
    private val lifecycleCoordinator: SecureItemEditorLifecycleCoordinator,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<NoteEditorUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<NoteEditorUiEvent> = mutableEvents.asSharedFlow()

    private var observationJob: Job? = null
    private var mutationJob: Job? = null
    private var latestObservation: SecureItemEditorObservationResult.Content? = null
    private var loadGeneration = 0L

    init {
        viewModelScope.launch {
            lifecycleCoordinator.observeSyncing().collect { isSyncing ->
                mutableUiState.update { state -> state.copy(isSyncing = isSyncing) }
            }
        }
        viewModelScope.launch {
            lifecycleCoordinator.observeVaultLocked().collect {
                handleVaultLocked()
            }
        }
    }

    fun load(logicalItemId: String?) {
        val generation = ++loadGeneration
        stopObserving()
        mutationJob?.cancel()
        latestObservation = null
        resetForLoad()

        if (lifecycleCoordinator.isVaultLocked()) {
            handleVaultLocked()
            return
        }
        if (logicalItemId == null) {
            mutableUiState.value = NoteEditorUiState(isSyncing = mutableUiState.value.isSyncing)
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
                editorState = SecureItemEditorState.Loading
            )
        }
        observationJob = viewModelScope.launch {
            try {
                observationCoordinator.observe(parsedLogicalItemId).collect { result ->
                    if (generation != loadGeneration) return@collect
                    when (result) {
                        is SecureItemEditorObservationResult.Content -> showObservedContent(result)
                        SecureItemEditorObservationResult.NotFound -> showNotFound()
                        is SecureItemEditorObservationResult.Error -> showError(result.reason)
                        SecureItemEditorObservationResult.InconsistentOfficialDraft -> {
                            handleInconsistentOfficialDraft()
                        }
                    }
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                if (generation == loadGeneration) handleLocalStorageFailure()
            }
        }
    }

    fun onAction(action: NoteEditorUiAction) {
        when (action) {
            is NoteEditorUiAction.DisplayHintChanged -> updateState { copy(displayHint = action.value) }
            is NoteEditorUiAction.BodyChanged -> updateState { copy(body = action.value) }
            NoteEditorUiAction.SaveClicked -> save()
            NoteEditorUiAction.DeleteClicked -> delete()
            NoteEditorUiAction.PublishDraftClicked -> publishDraft()
            NoteEditorUiAction.DiscardDraftClicked -> discardDraft()
            NoteEditorUiAction.RetryReadClicked -> retryRead()
        }
    }

    private fun showObservedContent(result: SecureItemEditorObservationResult.Content) {
        val officialContent = result.officialDetail?.content as? NoteSecureItemContent
        val draftContent = result.draftDetail?.content as? NoteSecureItemContent
        if ((result.officialDetail != null && officialContent == null) ||
            (result.draftDetail != null && draftContent == null)
        ) {
            handleInconsistentOfficialDraft()
            return
        }
        latestObservation = result
        renderObservedState(result, officialContent, draftContent)
    }

    private fun renderObservedState(
        result: SecureItemEditorObservationResult.Content,
        officialContent: NoteSecureItemContent?,
        draftContent: NoteSecureItemContent?,
    ) {
        val officialDetail = result.officialDetail
        val draftDetail = result.draftDetail
        val logicalItemId = draftDetail?.logicalItemId ?: officialDetail?.logicalItemId ?: return
        mutableUiState.update { state ->
            val preserveLocalChanges =
                state.hasUnsavedLocalChanges && state.logicalItemId == logicalItemId
            state.copy(
                logicalItemId = logicalItemId,
                displayHint = when {
                    preserveLocalChanges -> state.displayHint
                    draftDetail != null -> draftDetail.displayHint
                    else -> officialDetail?.displayHint.orEmpty()
                },
                body = when {
                    preserveLocalChanges -> state.body
                    draftContent != null -> draftContent.body
                    else -> officialContent?.body.orEmpty()
                },
                hasDraft = draftDetail != null,
                draftType = draftDetail?.draftType,
                draftSyncStatus = draftDetail?.draftSyncStatus,
                lastDraftError = draftDetail?.lastSyncError ?: state.lastDraftError,
                requiresSaveAsNew = draftDetail?.requiresSaveAsNew == true,
                editorState = if (state.isSaving) SecureItemEditorState.Saving
                else SecureItemEditorState.EditableContent,
                hasUnsavedLocalChanges = preserveLocalChanges,
                errorMessage = null,
            )
        }
    }

    private fun updateState(transform: NoteEditorUiState.() -> NoteEditorUiState) {
        mutableUiState.update { state ->
            if (!state.canEdit) return@update state
            state.transform().copy(
                errorMessage = null,
                editorState = SecureItemEditorState.EditableContent,
                hasUnsavedLocalChanges = true,
            )
        }
    }

    private fun publishDraft() {
        val state = mutableUiState.value
        val logicalItemId = state.logicalItemId ?: return
        if (!state.canEdit || !state.hasConflict || state.isDraftActionInProgress) return
        mutableUiState.update {
            it.copy(
                isDraftActionInProgress = true,
                errorMessage = null,
                lastDraftError = null
            )
        }
        mutationJob = viewModelScope.launch {
            try {
                when (val result = mutationOperations.publish(logicalItemId)) {
                    is PrepareSecureItemDraftForSyncResult.Success -> finishWithNavigateBack()
                    is PrepareSecureItemDraftForSyncResult.Error -> mutableUiState.update {
                        it.copy(
                            isDraftActionInProgress = false,
                            lastDraftError = result.reason.asUiMessage()
                        )
                    }
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                handleLocalStorageFailure()
            }
        }
    }

    private fun discardDraft() {
        val state = mutableUiState.value
        val logicalItemId = state.logicalItemId ?: return
        if (!state.canEdit || !state.hasDraft || state.isDraftActionInProgress) return
        mutableUiState.update {
            it.copy(
                isDraftActionInProgress = true,
                errorMessage = null,
                lastDraftError = null
            )
        }
        mutationJob = viewModelScope.launch {
            try {
                when (val result = mutationOperations.discard(logicalItemId)) {
                    is DiscardSecureItemDraftResult.Success -> {
                        val observation = latestObservation
                        if (observation?.officialDetail == null || observation.draftDetail?.draftType?.isCreateDraft() == true) {
                            finishWithNavigateBack()
                        } else {
                            mutableUiState.update {
                                it.copy(
                                    isDraftActionInProgress = false,
                                    lastDraftError = null
                                )
                            }
                        }
                    }

                    is DiscardSecureItemDraftResult.Error -> mutableUiState.update {
                        it.copy(
                            isDraftActionInProgress = false,
                            lastDraftError = result.reason.asUiMessage()
                        )
                    }
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                handleLocalStorageFailure()
            }
        }
    }

    private fun save() {
        val state = mutableUiState.value
        if (!state.canEdit || state.isDraftActionInProgress) return
        mutableUiState.update {
            it.copy(
                editorState = SecureItemEditorState.Saving,
                errorMessage = null
            )
        }
        mutationJob = viewModelScope.launch {
            try {
                val content = runCatching { NoteSecureItemContent(state.body) }.getOrElse {
                    showError(SecureItemCrudError.ValidationError("Invalid note item."))
                    return@launch
                }
                val result = mutationOperations.save(
                    logicalItemId = state.logicalItemId,
                    request = SecureItemEditorMutationRequest(
                        displayHint = state.displayHint,
                        content = content,
                    ),
                )
                handleMutationResult(result)
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                handleLocalStorageFailure()
            }
        }
    }

    private fun delete() {
        val logicalItemId = mutableUiState.value.logicalItemId ?: return
        if (!mutableUiState.value.canEdit || mutableUiState.value.isDraftActionInProgress) return
        mutableUiState.update {
            it.copy(
                editorState = SecureItemEditorState.Saving,
                errorMessage = null
            )
        }
        mutationJob = viewModelScope.launch {
            try {
                handleMutationResult(mutationOperations.delete(logicalItemId))
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                handleLocalStorageFailure()
            }
        }
    }

    private suspend fun handleMutationResult(result: SecureItemMutationResult) {
        when (result) {
            is SecureItemMutationResult.Success -> finishWithNavigateBack()
            is SecureItemMutationResult.Error -> showError(result.reason)
        }
    }

    private fun showError(error: SecureItemCrudError) {
        when (error) {
            SecureItemCrudError.VaultLocked -> handleVaultLocked()
            SecureItemCrudError.CorruptedPayload -> handleCorruptedPayload()
            SecureItemCrudError.LocalStorageFailure -> handleLocalStorageFailure()
            SecureItemCrudError.ItemNotFound -> showNotFound()
            is SecureItemCrudError.ValidationError -> mutableUiState.update {
                it.copy(
                    editorState = SecureItemEditorState.EditableContent,
                    isDraftActionInProgress = false,
                    errorMessage = error.asUiMessage(),
                )
            }
        }
    }

    private fun showNotFound() {
        stopObserving()
        latestObservation = null
        clearSensitiveState(
            SecureItemEditorState.NotFound,
            SecureItemCrudError.ItemNotFound.asUiMessage()
        )
    }

    private fun handleCorruptedPayload() {
        abortOperations()
        clearSensitiveState(
            SecureItemEditorState.CorruptedPayload,
            SecureItemCrudError.CorruptedPayload.asUiMessage()
        )
    }

    private fun handleInconsistentOfficialDraft() {
        abortOperations()
        clearSensitiveState(
            SecureItemEditorState.InconsistentOfficialDraft,
            "Official item and draft are inconsistent."
        )
    }

    private fun handleLocalStorageFailure() {
        abortOperations()
        clearSensitiveState(
            SecureItemEditorState.LocalStorageFailure,
            SecureItemCrudError.LocalStorageFailure.asUiMessage()
        )
    }

    private fun handleVaultLocked() {
        if (mutableUiState.value.editorState == SecureItemEditorState.VaultLocked) return
        abortOperations()
        clearSensitiveState(
            SecureItemEditorState.VaultLocked,
            SecureItemCrudError.VaultLocked.asUiMessage()
        )
        mutableEvents.tryEmit(NoteEditorUiEvent.NavigateToUnlock)
    }

    private fun clearSensitiveState(editorState: SecureItemEditorState, errorMessage: String) {
        mutableUiState.update {
            it.copy(
                displayHint = "",
                body = "",
                editorState = editorState,
                hasDraft = false,
                draftType = null,
                draftSyncStatus = null,
                requiresSaveAsNew = false,
                isDraftActionInProgress = false,
                hasUnsavedLocalChanges = false,
                errorMessage = errorMessage,
            )
        }
    }

    private fun resetForLoad() {
        mutableUiState.update {
            it.copy(
                logicalItemId = null,
                displayHint = "",
                body = "",
                editorState = SecureItemEditorState.Loading,
                hasDraft = false,
                draftType = null,
                draftSyncStatus = null,
                requiresSaveAsNew = false,
                isDraftActionInProgress = false,
                hasUnsavedLocalChanges = false,
                errorMessage = null,
                lastDraftError = null,
            )
        }
    }

    private fun retryRead() {
        val logicalItemId = mutableUiState.value.logicalItemId ?: return
        if (mutableUiState.value.canRetryRead) load(logicalItemId.toString())
    }

    private suspend fun finishWithNavigateBack() {
        stopObserving()
        mutableUiState.value = NoteEditorUiState(isSyncing = mutableUiState.value.isSyncing)
        mutableEvents.emit(NoteEditorUiEvent.NavigateBack)
    }

    private fun stopObserving() {
        observationJob?.cancel()
        observationJob = null
    }

    override fun onCleared() {
        stopObserving()
        mutationJob?.cancel()
        super.onCleared()
    }

    private fun abortOperations() {
        loadGeneration++
        stopObserving()
        mutationJob?.cancel()
        latestObservation = null
    }
}

private fun String.toUuidOrNull(): UUID? = runCatching { UUID.fromString(this) }.getOrNull()
