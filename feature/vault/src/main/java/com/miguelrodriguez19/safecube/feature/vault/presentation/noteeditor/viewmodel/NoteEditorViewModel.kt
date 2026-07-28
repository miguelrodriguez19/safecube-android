package com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.action.NoteEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.event.NoteEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.state.NoteEditorUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.error.asUiMessage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
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
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<NoteEditorUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<NoteEditorUiEvent> = mutableEvents.asSharedFlow()
    private var observeItemJob: Job? = null
    private var observeDraftJob: Job? = null
    private var latestOfficialDetail: SecureItemDetail? = null
    private var latestDraftDetail: SecureItemDraftDetail? = null

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
        latestOfficialDetail = null
        latestDraftDetail = null
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
                lastDraftError = null,
            )
        }

        observeItemJob = viewModelScope.launch {
            observeSecureItemDetailUseCase(parsedLogicalItemId).collect { result ->
                when (result) {
                    is ObserveSecureItemDetailResult.Success -> showOfficialDetail(result.detail)
                    is ObserveSecureItemDetailResult.Error -> showError(result.reason)
                }
            }
        }
        observeDraftJob = viewModelScope.launch {
            observeSecureItemDraftDetailUseCase(parsedLogicalItemId).collect { result ->
                when (result) {
                    is ObserveSecureItemDraftDetailResult.Success -> showDraftDetail(result)
                    ObserveSecureItemDraftDetailResult.NotFound -> clearDraft()
                    is ObserveSecureItemDraftDetailResult.Error -> showDraftError(result.reason)
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
            NoteEditorUiAction.PublishDraftClicked -> publishDraft()
            NoteEditorUiAction.DiscardDraftClicked -> discardDraft()
        }
    }

    private fun showOfficialDetail(detail: SecureItemDetail) {
        val content = detail.content as? NoteSecureItemContent
        if (content == null) {
            showError(SecureItemCrudError.ItemNotFound)
            return
        }
        latestOfficialDetail = detail
        renderObservedState(officialContent = content)
    }

    private fun showDraftDetail(result: ObserveSecureItemDraftDetailResult.Success) {
        val content = result.detail.content as? NoteSecureItemContent
        if (content == null) {
            showDraftError(SecureItemCrudError.CorruptedPayload)
            return
        }
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
        renderObservedState(officialContent = null)
    }

    private fun showDraftError(error: SecureItemCrudError) {
        latestDraftDetail = null
        mutableUiState.update { state ->
            state.copy(
                hasDraft = false,
                draftType = null,
                draftSyncStatus = null,
                lastDraftError = error.asUiMessage(),
                requiresSaveAsNew = false,
            )
        }
        renderObservedState(officialContent = null)
    }

    private fun renderObservedState(officialContent: NoteSecureItemContent?) {
        val detail = latestOfficialDetail
        val resolvedOfficialContent = officialContent ?: (detail?.content as? NoteSecureItemContent)
        val draftDetail = latestDraftDetail
        val draftContent = draftDetail?.content as? NoteSecureItemContent
        val logicalItemId = draftDetail?.logicalItemId ?: detail?.logicalItemId ?: return

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
                isLoading = false,
                isSaving = false,
                isDraftActionInProgress = false,
                hasUnsavedLocalChanges = preserveDraft,
                errorMessage = null,
            )
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

    private fun publishDraft() {
        val state = mutableUiState.value
        val logicalItemId = state.logicalItemId ?: return
        if (!state.hasConflict || state.isLoading || state.isSaving || state.isDraftActionInProgress) return

        mutableUiState.update { current ->
            current.copy(
                isDraftActionInProgress = true,
                errorMessage = null,
                lastDraftError = null,
            )
        }

        viewModelScope.launch {
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
        }
    }

    private fun discardDraft() {
        val state = mutableUiState.value
        val logicalItemId = state.logicalItemId ?: return
        if (!state.hasDraft || state.isLoading || state.isSaving || state.isDraftActionInProgress) return

        mutableUiState.update { current ->
            current.copy(
                isDraftActionInProgress = true,
                errorMessage = null,
                lastDraftError = null,
            )
        }

        viewModelScope.launch {
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
        }
    }

    private fun save() {
        val state = mutableUiState.value
        if (state.isLoading || state.isSaving || state.isDraftActionInProgress) return

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
        if (state.isLoading || state.isSaving || state.isDraftActionInProgress) return

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
        if (error == SecureItemCrudError.ItemNotFound && latestDraftDetail != null) {
            mutableUiState.update { state ->
                state.copy(
                    isLoading = false,
                    isSaving = false,
                    isDraftActionInProgress = false,
                    errorMessage = null,
                )
            }
            return
        }
        mutableUiState.update { state ->
            state.copy(
                isLoading = false,
                isSaving = false,
                isDraftActionInProgress = false,
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
        observeDraftJob?.cancel()
        observeDraftJob = null
    }
}

private fun String.toUuidOrNull(): UUID? = runCatching { UUID.fromString(this) }.getOrNull()
