package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDraftDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDraftDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordWebsiteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDraftDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.CreateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.UpdateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.DiscardSecureItemDraftUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.PrepareSecureItemDraftForSyncUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.action.PasswordEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.event.PasswordEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.state.PasswordEditorUiState
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
class PasswordEditorViewModel @Inject constructor(
    private val observeSecureItemDetailUseCase: ObserveSecureItemDetailUseCase,
    private val observeSecureItemDraftDetailUseCase: ObserveSecureItemDraftDetailUseCase,
    private val createSecurePasswordUseCase: CreateSecurePasswordUseCase,
    private val updateSecurePasswordUseCase: UpdateSecurePasswordUseCase,
    private val softDeleteSecureItemUseCase: SoftDeleteSecureItemUseCase,
    private val prepareSecureItemDraftForSyncUseCase: PrepareSecureItemDraftForSyncUseCase,
    private val discardSecureItemDraftUseCase: DiscardSecureItemDraftUseCase,
    observeVaultSyncingUseCase: ObserveVaultSyncingUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(PasswordEditorUiState())
    val uiState: StateFlow<PasswordEditorUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<PasswordEditorUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PasswordEditorUiEvent> = mutableEvents.asSharedFlow()
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
            mutableUiState.value = PasswordEditorUiState(
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

    fun onAction(action: PasswordEditorUiAction) {
        when (action) {
            is PasswordEditorUiAction.DisplayHintChanged -> updateState { copy(displayHint = action.value) }
            is PasswordEditorUiAction.UsernameChanged -> updateState { copy(username = action.value) }
            is PasswordEditorUiAction.PasswordChanged -> updateState { copy(password = action.value) }
            is PasswordEditorUiAction.WebsiteUrlChanged -> updateState { copy(websiteUrl = action.value) }
            is PasswordEditorUiAction.NotesChanged -> updateState { copy(notes = action.value) }
            PasswordEditorUiAction.SaveClicked -> save()
            PasswordEditorUiAction.DeleteClicked -> delete()
            PasswordEditorUiAction.PublishDraftClicked -> publishDraft()
            PasswordEditorUiAction.DiscardDraftClicked -> discardDraft()
        }
    }

    private fun showOfficialDetail(detail: SecureItemDetail) {
        val content = detail.content as? PasswordSecureItemContent
        if (content == null) {
            showError(SecureItemCrudError.ItemNotFound)
            return
        }
        latestOfficialDetail = detail
        renderObservedState(officialContent = content)
    }

    private fun showDraftDetail(result: ObserveSecureItemDraftDetailResult.Success) {
        val content = result.detail.content as? PasswordSecureItemContent
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

    private fun renderObservedState(officialContent: PasswordSecureItemContent?) {
        val detail = latestOfficialDetail
        val resolvedOfficialContent = officialContent ?: (detail?.content as? PasswordSecureItemContent)
        val draftDetail = latestDraftDetail
        val draftContent = draftDetail?.content as? PasswordSecureItemContent
        val logicalItemId = draftDetail?.logicalItemId ?: detail?.logicalItemId ?: return

        mutableUiState.update { state ->
            val preserveDraft = state.hasUnsavedLocalChanges && state.logicalItemId == logicalItemId
            val username = when {
                preserveDraft -> state.username
                draftContent != null -> draftContent.username ?: draftContent.email.orEmpty()
                else -> resolvedOfficialContent?.username ?: resolvedOfficialContent?.email.orEmpty()
            }
            val password = when {
                preserveDraft -> state.password
                draftContent != null -> draftContent.password
                else -> resolvedOfficialContent?.password.orEmpty()
            }
            val websiteUrl = when {
                preserveDraft -> state.websiteUrl
                draftContent != null -> draftContent.website?.url.orEmpty()
                else -> resolvedOfficialContent?.website?.url.orEmpty()
            }
            val notes = when {
                preserveDraft -> state.notes
                draftContent != null -> draftContent.notes.orEmpty()
                else -> resolvedOfficialContent?.notes.orEmpty()
            }
            state.copy(
                logicalItemId = logicalItemId,
                displayHint = when {
                    preserveDraft -> state.displayHint
                    draftDetail != null -> draftDetail.displayHint
                    else -> detail?.displayHint.orEmpty()
                },
                username = username,
                password = password,
                websiteUrl = websiteUrl,
                notes = notes,
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

    private fun updateState(transform: PasswordEditorUiState.() -> PasswordEditorUiState) {
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
                    mutableUiState.value = PasswordEditorUiState(
                        isSyncing = mutableUiState.value.isSyncing,
                    )
                    mutableEvents.emit(PasswordEditorUiEvent.NavigateBack)
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
                        mutableUiState.value = PasswordEditorUiState(
                            isSyncing = mutableUiState.value.isSyncing,
                        )
                        mutableEvents.emit(PasswordEditorUiEvent.NavigateBack)
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
            val draft = SecurePasswordDraft(
                displayHint = state.displayHint,
                username = state.username.trimToNull(),
                email = null,
                password = state.password,
                website = state.websiteUrl.trimToNull()?.let { url ->
                    SecurePasswordWebsiteDraft(url = url)
                },
                notes = state.notes.trimToNull(),
            )
            val result = if (state.logicalItemId == null) {
                createSecurePasswordUseCase(draft)
            } else {
                updateSecurePasswordUseCase(state.logicalItemId, draft)
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
                mutableUiState.value = PasswordEditorUiState(
                    isSyncing = mutableUiState.value.isSyncing,
                )
                mutableEvents.emit(PasswordEditorUiEvent.NavigateBack)
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

private fun String.trimToNull(): String? = trim().takeIf { it.isNotBlank() }
