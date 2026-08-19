package com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDraftDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDraftDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureNoteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDraftDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.CreateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.UpdateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.DiscardSecureItemDraftUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.PrepareSecureItemDraftForSyncUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.action.NoteEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.event.NoteEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.state.NoteEditorUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.state.SecureItemEditorState
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val observeSecureItemDetailUseCase: ObserveSecureItemDetailUseCase,
    private val observeSecureItemDraftDetailUseCase: ObserveSecureItemDraftDetailUseCase,
    private val createSecureNoteUseCase: CreateSecureNoteUseCase,
    private val updateSecureNoteUseCase: UpdateSecureNoteUseCase,
    private val softDeleteSecureItemUseCase: SoftDeleteSecureItemUseCase,
    private val prepareSecureItemDraftForSyncUseCase: PrepareSecureItemDraftForSyncUseCase,
    private val discardSecureItemDraftUseCase: DiscardSecureItemDraftUseCase,
    observeVaultSyncingUseCase: ObserveVaultSyncingUseCase,
    private val vaultSessionManager: VaultSessionManager,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<NoteEditorUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<NoteEditorUiEvent> = mutableEvents.asSharedFlow()
    private var observeItemJob: Job? = null
    private var observeDraftJob: Job? = null
    private var latestOfficialDetail: SecureItemDetail? = null
    private var latestDraftDetail: SecureItemDraftDetail? = null
    private var mutationJob: Job? = null
    private var loadGeneration = 0L
    private var officialObservationResolved = false
    private var draftObservationResolved = false
    private var officialNotFound = false
    private var draftNotFound = false

    init {
        viewModelScope.launch {
            observeVaultSyncingUseCase().collect { isSyncing ->
                mutableUiState.update { state ->
                    state.copy(isSyncing = isSyncing)
                }
            }
        }
        viewModelScope.launch {
            vaultSessionManager.vaultState.collect { vaultState ->
                if (vaultState == VaultState.Locked) {
                    handleVaultLocked()
                }
            }
        }
    }

    fun load(logicalItemId: String?) {
        val generation = ++loadGeneration
        stopObservingItem()
        mutationJob?.cancel()
        latestOfficialDetail = null
        latestDraftDetail = null
        officialObservationResolved = false
        draftObservationResolved = false
        officialNotFound = false
        draftNotFound = false
        mutableUiState.update { state ->
            state.copy(
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
        if (vaultSessionManager.vaultState.value == VaultState.Locked) {
            handleVaultLocked()
            return
        }
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
                editorState = SecureItemEditorState.Loading,
                displayHint = "",
                body = "",
                hasDraft = false,
                draftType = null,
                draftSyncStatus = null,
                requiresSaveAsNew = false,
                isDraftActionInProgress = false,
                errorMessage = null,
                hasUnsavedLocalChanges = false,
                lastDraftError = null,
            )
        }

        observeItemJob = viewModelScope.launch {
            try {
                observeSecureItemDetailUseCase(parsedLogicalItemId).collect { result ->
                    if (generation != loadGeneration) return@collect
                    when (result) {
                        is ObserveSecureItemDetailResult.Success -> showOfficialDetail(result.detail)
                        is ObserveSecureItemDetailResult.Error -> showOfficialError(result.reason)
                    }
                }
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                if (generation == loadGeneration) handleLocalStorageFailure()
            }
        }
        observeDraftJob = viewModelScope.launch {
            try {
                observeSecureItemDraftDetailUseCase(parsedLogicalItemId).collect { result ->
                    if (generation != loadGeneration) return@collect
                    when (result) {
                        is ObserveSecureItemDraftDetailResult.Success -> showDraftDetail(result)
                        ObserveSecureItemDraftDetailResult.NotFound -> clearDraft()
                        is ObserveSecureItemDraftDetailResult.Error -> showDraftError(result.reason)
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

    private fun showOfficialDetail(detail: SecureItemDetail) {
        val content = detail.content as? NoteSecureItemContent
        if (content == null) {
            handleInconsistentOfficialDraft()
            return
        }
        officialObservationResolved = true
        officialNotFound = false
        latestOfficialDetail = detail
        renderObservedState(officialContent = content)
    }

    private fun showOfficialError(error: SecureItemCrudError) {
        officialObservationResolved = true
        when (error) {
            SecureItemCrudError.ItemNotFound -> {
                officialNotFound = true
                resolveNotFoundOrRenderDraft()
            }

            else -> showError(error)
        }
    }

    private fun showDraftDetail(result: ObserveSecureItemDraftDetailResult.Success) {
        val content = result.detail.content as? NoteSecureItemContent
        if (content == null) {
            handleInconsistentOfficialDraft()
            return
        }
        draftObservationResolved = true
        draftNotFound = false
        latestDraftDetail = result.detail
        mutableUiState.update { state ->
            state.copy(
                hasDraft = true,
                draftType = result.detail.draftType,
                draftSyncStatus = result.detail.draftSyncStatus,
                lastDraftError = result.detail.lastSyncError,
                requiresSaveAsNew = result.detail.requiresSaveAsNew,
            )
        }
        renderObservedState(officialContent = null)
    }

    private fun clearDraft() {
        draftObservationResolved = true
        draftNotFound = true
        latestDraftDetail = null
        mutableUiState.update { state ->
            state.copy(
                hasDraft = false,
                draftType = null,
                draftSyncStatus = null,
                lastDraftError = null,
                requiresSaveAsNew = false,
            )
        }
        if (latestOfficialDetail != null) {
            renderObservedState(officialContent = null)
        } else {
            resolveNotFoundOrRenderDraft()
        }
    }

    private fun showDraftError(error: SecureItemCrudError) {
        draftObservationResolved = true
        if (error == SecureItemCrudError.ItemNotFound) {
            clearDraft()
            return
        }
        latestDraftDetail = null
        showError(error)
    }

    private fun renderObservedState(officialContent: NoteSecureItemContent?) {
        val detail = latestOfficialDetail
        val resolvedOfficialContent = officialContent ?: (detail?.content as? NoteSecureItemContent)
        val draftDetail = latestDraftDetail
        val draftContent = draftDetail?.content as? NoteSecureItemContent
        val logicalItemId = draftDetail?.logicalItemId ?: detail?.logicalItemId ?: return

        if (
            detail != null && draftDetail != null &&
            (detail.logicalItemId != draftDetail.logicalItemId || detail.itemType != draftDetail.itemType)
        ) {
            handleInconsistentOfficialDraft()
            return
        }
        if (
            detail == null && draftDetail != null &&
            draftDetail.draftType != com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType.CREATE &&
            !draftDetail.requiresSaveAsNew
        ) {
            handleInconsistentOfficialDraft()
            return
        }

        mutableUiState.update { state ->
            val preserveDraft = state.hasUnsavedLocalChanges && state.logicalItemId == logicalItemId
            val displayHint = if (draftDetail != null) {
                if (preserveDraft) state.displayHint else draftDetail.displayHint
            } else {
                state.displayHint.takeIf { preserveDraft } ?: detail?.displayHint.orEmpty()
            }
            val body = when {
                preserveDraft -> state.body
                draftContent != null -> draftContent.body
                else -> resolvedOfficialContent?.body.orEmpty()
            }
            state.copy(
                logicalItemId = logicalItemId,
                displayHint = displayHint,
                body = body,
                hasDraft = draftDetail != null,
                draftType = draftDetail?.draftType,
                draftSyncStatus = draftDetail?.draftSyncStatus,
                lastDraftError = draftDetail?.lastSyncError ?: state.lastDraftError,
                requiresSaveAsNew = draftDetail?.requiresSaveAsNew == true,
                editorState = if (state.isSaving) {
                    SecureItemEditorState.Saving
                } else {
                    SecureItemEditorState.EditableContent
                },
                hasUnsavedLocalChanges = preserveDraft,
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

        mutableUiState.update { current ->
            current.copy(
                isDraftActionInProgress = true,
                errorMessage = null,
                lastDraftError = null,
            )
        }

        mutationJob = viewModelScope.launch {
            try {
                when (val result = prepareSecureItemDraftForSyncUseCase(logicalItemId)) {
                    is PrepareSecureItemDraftForSyncResult.Success -> {
                        stopObservingItem()
                        mutableUiState.value = NoteEditorUiState(
                            isSyncing = mutableUiState.value.isSyncing,
                        )
                        mutableEvents.emit(NoteEditorUiEvent.NavigateBack)
                    }

                    is PrepareSecureItemDraftForSyncResult.Error -> {
                        mutableUiState.update { current ->
                            current.copy(
                                isDraftActionInProgress = false,
                                lastDraftError = result.reason.asUiMessage(),
                            )
                        }
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

        mutableUiState.update { current ->
            current.copy(
                isDraftActionInProgress = true,
                errorMessage = null,
                lastDraftError = null,
            )
        }

        mutationJob = viewModelScope.launch {
            try {
                when (val result = discardSecureItemDraftUseCase(logicalItemId)) {
                    is DiscardSecureItemDraftResult.Success -> {
                        if (latestOfficialDetail == null || latestDraftDetail?.draftType == com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType.CREATE) {
                            stopObservingItem()
                            mutableUiState.value = NoteEditorUiState(
                                isSyncing = mutableUiState.value.isSyncing,
                            )
                            mutableEvents.emit(NoteEditorUiEvent.NavigateBack)
                        } else {
                            mutableUiState.update { current ->
                                current.copy(
                                    isDraftActionInProgress = false,
                                    lastDraftError = null,
                                )
                            }
                        }
                    }

                    is DiscardSecureItemDraftResult.Error -> {
                        mutableUiState.update { current ->
                            current.copy(
                                isDraftActionInProgress = false,
                                lastDraftError = result.reason.asUiMessage(),
                            )
                        }
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

        mutableUiState.update { current ->
            current.copy(
                editorState = SecureItemEditorState.Saving,
                errorMessage = null,
            )
        }

        mutationJob = viewModelScope.launch {
            try {
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
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                handleLocalStorageFailure()
            }
        }
    }

    private fun delete() {
        val state = mutableUiState.value
        val logicalItemId = state.logicalItemId ?: return
        if (!state.canEdit || state.isDraftActionInProgress) return

        mutableUiState.update { current ->
            current.copy(
                editorState = SecureItemEditorState.Saving,
                errorMessage = null,
            )
        }

        mutationJob = viewModelScope.launch {
            try {
                handleMutationResult(softDeleteSecureItemUseCase(logicalItemId))
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (_: Throwable) {
                handleLocalStorageFailure()
            }
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
        when (error) {
            SecureItemCrudError.VaultLocked -> handleVaultLocked()
            SecureItemCrudError.CorruptedPayload -> handleCorruptedPayload()
            SecureItemCrudError.LocalStorageFailure -> handleLocalStorageFailure()
            SecureItemCrudError.ItemNotFound -> showNotFound()
            is SecureItemCrudError.ValidationError -> mutableUiState.update { state ->
                state.copy(
                    editorState = SecureItemEditorState.EditableContent,
                    isDraftActionInProgress = false,
                    errorMessage = error.asUiMessage(),
                )
            }
        }
    }

    private fun resolveNotFoundOrRenderDraft() {
        if (latestDraftDetail != null) {
            renderObservedState(officialContent = null)
        } else if (
            officialObservationResolved && draftObservationResolved && officialNotFound && draftNotFound
        ) {
            showNotFound()
        }
    }

    private fun showNotFound() {
        stopObservingItem()
        latestOfficialDetail = null
        latestDraftDetail = null
        mutableUiState.update { state ->
            state.copy(
                displayHint = "",
                body = "",
                editorState = SecureItemEditorState.NotFound,
                hasDraft = false,
                draftType = null,
                draftSyncStatus = null,
                requiresSaveAsNew = false,
                isDraftActionInProgress = false,
                hasUnsavedLocalChanges = false,
                errorMessage = SecureItemCrudError.ItemNotFound.asUiMessage(),
            )
        }
    }

    private fun handleCorruptedPayload() {
        loadGeneration++
        stopObservingItem()
        mutationJob?.cancel()
        latestOfficialDetail = null
        latestDraftDetail = null
        mutableUiState.update { state ->
            state.copy(
                displayHint = "",
                body = "",
                editorState = SecureItemEditorState.CorruptedPayload,
                hasDraft = false,
                draftType = null,
                draftSyncStatus = null,
                requiresSaveAsNew = false,
                isDraftActionInProgress = false,
                hasUnsavedLocalChanges = false,
                errorMessage = SecureItemCrudError.CorruptedPayload.asUiMessage(),
            )
        }
    }

    private fun handleInconsistentOfficialDraft() {
        loadGeneration++
        stopObservingItem()
        latestOfficialDetail = null
        latestDraftDetail = null
        mutableUiState.update { state ->
            state.copy(
                displayHint = "",
                body = "",
                editorState = SecureItemEditorState.InconsistentOfficialDraft,
                hasDraft = false,
                draftType = null,
                draftSyncStatus = null,
                requiresSaveAsNew = false,
                isDraftActionInProgress = false,
                hasUnsavedLocalChanges = false,
                errorMessage = "Official item and draft are inconsistent.",
            )
        }
    }

    private fun handleLocalStorageFailure() {
        loadGeneration++
        stopObservingItem()
        mutationJob?.cancel()
        latestOfficialDetail = null
        latestDraftDetail = null
        mutableUiState.update { state ->
            state.copy(
                displayHint = "",
                body = "",
                editorState = SecureItemEditorState.LocalStorageFailure,
                hasDraft = false,
                draftType = null,
                draftSyncStatus = null,
                requiresSaveAsNew = false,
                isDraftActionInProgress = false,
                hasUnsavedLocalChanges = false,
                errorMessage = SecureItemCrudError.LocalStorageFailure.asUiMessage(),
            )
        }
    }

    private fun handleVaultLocked() {
        if (mutableUiState.value.editorState == SecureItemEditorState.VaultLocked) return
        loadGeneration++
        stopObservingItem()
        mutationJob?.cancel()
        latestOfficialDetail = null
        latestDraftDetail = null
        mutableUiState.update { state ->
            state.copy(
                displayHint = "",
                body = "",
                editorState = SecureItemEditorState.VaultLocked,
                hasDraft = false,
                draftType = null,
                draftSyncStatus = null,
                requiresSaveAsNew = false,
                isDraftActionInProgress = false,
                hasUnsavedLocalChanges = false,
                errorMessage = SecureItemCrudError.VaultLocked.asUiMessage(),
            )
        }
        mutableEvents.tryEmit(NoteEditorUiEvent.NavigateToUnlock)
    }

    private fun retryRead() {
        val logicalItemId = mutableUiState.value.logicalItemId ?: return
        if (!mutableUiState.value.canRetryRead) return
        load(logicalItemId.toString())
    }

    override fun onCleared() {
        stopObservingItem()
        mutationJob?.cancel()
        super.onCleared()
    }

    private fun stopObservingItem() {
        observeItemJob?.cancel()
        observeItemJob = null
        observeDraftJob?.cancel()
        observeDraftJob = null
    }
}

private fun String.toUuidOrNull(): UUID? = runCatching { UUID.fromString(this) }.getOrNull()
