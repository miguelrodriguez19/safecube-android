package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordWebsiteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.CreateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.UpdateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.action.PasswordEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.event.PasswordEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.state.PasswordEditorUiState
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
class PasswordEditorViewModel @Inject constructor(
    private val observeSecureItemDetailUseCase: ObserveSecureItemDetailUseCase,
    private val createSecurePasswordUseCase: CreateSecurePasswordUseCase,
    private val updateSecurePasswordUseCase: UpdateSecurePasswordUseCase,
    private val softDeleteSecureItemUseCase: SoftDeleteSecureItemUseCase,
    observeVaultSyncingUseCase: ObserveVaultSyncingUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(PasswordEditorUiState())
    val uiState: StateFlow<PasswordEditorUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<PasswordEditorUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PasswordEditorUiEvent> = mutableEvents.asSharedFlow()
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

    fun onAction(action: PasswordEditorUiAction) {
        when (action) {
            is PasswordEditorUiAction.DisplayHintChanged -> updateState { copy(displayHint = action.value) }
            is PasswordEditorUiAction.UsernameChanged -> updateState { copy(username = action.value) }
            is PasswordEditorUiAction.PasswordChanged -> updateState { copy(password = action.value) }
            is PasswordEditorUiAction.WebsiteUrlChanged -> updateState { copy(websiteUrl = action.value) }
            is PasswordEditorUiAction.NotesChanged -> updateState { copy(notes = action.value) }
            PasswordEditorUiAction.SaveClicked -> save()
            PasswordEditorUiAction.DeleteClicked -> delete()
        }
    }

    private fun showDetail(result: ObserveSecureItemDetailResult.Success) {
        val content = result.detail.content as? PasswordSecureItemContent
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
                    username = content.username ?: content.email.orEmpty(),
                    password = content.password,
                    websiteUrl = content.website?.url.orEmpty(),
                    notes = content.notes.orEmpty(),
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

    private fun updateState(transform: PasswordEditorUiState.() -> PasswordEditorUiState) {
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

private fun String.trimToNull(): String? = trim().takeIf { it.isNotBlank() }
