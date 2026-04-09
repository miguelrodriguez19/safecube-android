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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NoteEditorViewModel @Inject constructor(
    private val observeSecureItemDetailUseCase: ObserveSecureItemDetailUseCase,
    private val createSecureNoteUseCase: CreateSecureNoteUseCase,
    private val updateSecureNoteUseCase: UpdateSecureNoteUseCase,
    private val softDeleteSecureItemUseCase: SoftDeleteSecureItemUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(NoteEditorUiState())
    val uiState: StateFlow<NoteEditorUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<NoteEditorUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<NoteEditorUiEvent> = mutableEvents.asSharedFlow()

    fun load(logicalItemId: String?) {
        if (logicalItemId == null) {
            mutableUiState.value = NoteEditorUiState()
            return
        }

        val parsedLogicalItemId = logicalItemId.toUuidOrNull()
        if (parsedLogicalItemId == null) {
            showError(SecureItemCrudError.ItemNotFound)
            return
        }

        mutableUiState.value = NoteEditorUiState(
            logicalItemId = parsedLogicalItemId,
            isLoading = true,
        )

        viewModelScope.launch {
            when (val result = observeSecureItemDetailUseCase(parsedLogicalItemId).first()) {
                is ObserveSecureItemDetailResult.Success -> showDetail(result)
                is ObserveSecureItemDetailResult.Error -> showError(result.reason)
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

        mutableUiState.update { state ->
            state.copy(
                logicalItemId = result.detail.logicalItemId,
                displayHint = result.detail.displayHint,
                body = content.body,
                isLoading = false,
                isSaving = false,
                errorMessage = null,
            )
        }
    }

    private fun updateState(transform: NoteEditorUiState.() -> NoteEditorUiState) {
        mutableUiState.update { state ->
            state.transform().copy(
                errorMessage = null,
                isLoading = false,
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
                mutableUiState.value = NoteEditorUiState()
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
}

private fun String.toUuidOrNull(): UUID? = runCatching { UUID.fromString(this) }.getOrNull()
