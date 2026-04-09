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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PasswordEditorViewModel @Inject constructor(
    private val observeSecureItemDetailUseCase: ObserveSecureItemDetailUseCase,
    private val createSecurePasswordUseCase: CreateSecurePasswordUseCase,
    private val updateSecurePasswordUseCase: UpdateSecurePasswordUseCase,
    private val softDeleteSecureItemUseCase: SoftDeleteSecureItemUseCase,
) : ViewModel() {
    private val mutableUiState = MutableStateFlow(PasswordEditorUiState())
    val uiState: StateFlow<PasswordEditorUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<PasswordEditorUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<PasswordEditorUiEvent> = mutableEvents.asSharedFlow()

    fun load(logicalItemId: String?) {
        if (logicalItemId == null) {
            mutableUiState.value = PasswordEditorUiState()
            return
        }

        val parsedLogicalItemId = logicalItemId.toUuidOrNull()
        if (parsedLogicalItemId == null) {
            showError(SecureItemCrudError.ItemNotFound)
            return
        }

        mutableUiState.value = PasswordEditorUiState(
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

        mutableUiState.update { state ->
            state.copy(
                logicalItemId = result.detail.logicalItemId,
                displayHint = result.detail.displayHint,
                username = content.username ?: content.email.orEmpty(),
                password = content.password,
                websiteUrl = content.website?.url.orEmpty(),
                notes = content.notes.orEmpty(),
                isLoading = false,
                isSaving = false,
                errorMessage = null,
            )
        }
    }

    private fun updateState(transform: PasswordEditorUiState.() -> PasswordEditorUiState) {
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
                mutableUiState.value = PasswordEditorUiState()
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
}

private fun String.toUuidOrNull(): UUID? = runCatching { UUID.fromString(this) }.getOrNull()

private fun String.trimToNull(): String? = trim().takeIf { it.isNotBlank() }
