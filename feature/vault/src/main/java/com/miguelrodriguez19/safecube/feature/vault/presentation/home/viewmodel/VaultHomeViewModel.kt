package com.miguelrodriguez19.safecube.feature.vault.presentation.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureNoteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultItemSummariesUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.CreateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.UpdateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.CreateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.UpdateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.action.VaultHomeUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultEditorUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultHomeUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultItemSummaryUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class VaultHomeViewModel @Inject constructor(
    observeVaultItemSummariesUseCase: ObserveVaultItemSummariesUseCase,
    private val observeSecureItemDetailUseCase: ObserveSecureItemDetailUseCase,
    private val createSecurePasswordUseCase: CreateSecurePasswordUseCase,
    private val updateSecurePasswordUseCase: UpdateSecurePasswordUseCase,
    private val createSecureNoteUseCase: CreateSecureNoteUseCase,
    private val updateSecureNoteUseCase: UpdateSecureNoteUseCase,
    private val softDeleteSecureItemUseCase: SoftDeleteSecureItemUseCase,
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
                            )
                        },
                    )
                }
            }
        }
    }

    fun onAction(action: VaultHomeUiAction) {
        when (action) {
            VaultHomeUiAction.CreatePasswordClicked -> openCreatePasswordEditor()
            VaultHomeUiAction.CreateNoteClicked -> openCreateNoteEditor()
            VaultHomeUiAction.DismissEditor -> dismissEditor()
            VaultHomeUiAction.SaveEditorClicked -> saveEditor()
            VaultHomeUiAction.DeleteItemClicked -> deleteCurrentItem()
            is VaultHomeUiAction.EditItemClicked -> openEditItemEditor(
                logicalItemId = action.logicalItemId,
                itemType = action.itemType,
            )

            is VaultHomeUiAction.PasswordDisplayHintChanged -> updatePasswordEditor { copy(displayHint = action.value) }
            is VaultHomeUiAction.PasswordUsernameChanged -> updatePasswordEditor { copy(username = action.value) }
            is VaultHomeUiAction.PasswordEmailChanged -> updatePasswordEditor { copy(email = action.value) }
            is VaultHomeUiAction.PasswordValueChanged -> updatePasswordEditor { copy(password = action.value) }
            is VaultHomeUiAction.NoteDisplayHintChanged -> updateNoteEditor { copy(displayHint = action.value) }
            is VaultHomeUiAction.NoteBodyChanged -> updateNoteEditor { copy(body = action.value) }
        }
    }

    private fun openCreatePasswordEditor() {
        mutableUiState.update { state ->
            state.copy(
                screenMessage = null,
                editor = VaultEditorUiState.Password(),
            )
        }
    }

    private fun openCreateNoteEditor() {
        mutableUiState.update { state ->
            state.copy(
                screenMessage = null,
                editor = VaultEditorUiState.Note(),
            )
        }
    }

    private fun dismissEditor() {
        mutableUiState.update { state -> state.copy(editor = null) }
    }

    private fun openEditItemEditor(
        logicalItemId: UUID,
        itemType: SecureItemType,
    ) {
        mutableUiState.update { state ->
            state.copy(
                screenMessage = null,
                editor = when (itemType) {
                    SecureItemType.PASSWORD -> VaultEditorUiState.Password(
                        logicalItemId = logicalItemId,
                        isLoading = true,
                    )

                    SecureItemType.NOTE -> VaultEditorUiState.Note(
                        logicalItemId = logicalItemId,
                        isLoading = true,
                    )
                },
            )
        }

        viewModelScope.launch {
            when (val result = observeSecureItemDetailUseCase(logicalItemId).first()) {
                is ObserveSecureItemDetailResult.Success -> showDetailEditor(result)
                is ObserveSecureItemDetailResult.Error -> {
                    mutableUiState.update { state ->
                        state.copy(
                            editor = null,
                            screenMessage = mapCrudError(result.reason),
                        )
                    }
                }
            }
        }
    }

    private fun showDetailEditor(result: ObserveSecureItemDetailResult.Success) {
        val content = result.detail.content
        mutableUiState.update { state ->
            state.copy(
                editor = when (content) {
                    is PasswordSecureItemContent -> VaultEditorUiState.Password(
                        logicalItemId = result.detail.logicalItemId,
                        displayHint = result.detail.displayHint,
                        username = content.username.orEmpty(),
                        email = content.email.orEmpty(),
                        password = content.password,
                    )

                    is NoteSecureItemContent -> VaultEditorUiState.Note(
                        logicalItemId = result.detail.logicalItemId,
                        displayHint = result.detail.displayHint,
                        body = content.body,
                    )
                },
            )
        }
    }

    private fun updatePasswordEditor(transform: VaultEditorUiState.Password.() -> VaultEditorUiState.Password) {
        mutableUiState.update { state ->
            val editor = state.editor as? VaultEditorUiState.Password ?: return@update state
            state.copy(
                editor = editor.transform().copy(
                    errorMessage = null,
                    isLoading = false,
                ),
            )
        }
    }

    private fun updateNoteEditor(transform: VaultEditorUiState.Note.() -> VaultEditorUiState.Note) {
        mutableUiState.update { state ->
            val editor = state.editor as? VaultEditorUiState.Note ?: return@update state
            state.copy(
                editor = editor.transform().copy(
                    errorMessage = null,
                    isLoading = false,
                ),
            )
        }
    }

    private fun saveEditor() {
        when (val editor = mutableUiState.value.editor) {
            null -> Unit
            is VaultEditorUiState.Password -> savePassword(editor)
            is VaultEditorUiState.Note -> saveNote(editor)
        }
    }

    private fun savePassword(editor: VaultEditorUiState.Password) {
        if (editor.isLoading || editor.isSaving) return

        mutableUiState.update { state ->
            state.copy(editor = editor.copy(isSaving = true, errorMessage = null))
        }

        viewModelScope.launch {
            val draft = SecurePasswordDraft(
                displayHint = editor.displayHint,
                username = editor.username.trim().takeIf { it.isNotBlank() },
                email = editor.email.trim().takeIf { it.isNotBlank() },
                password = editor.password,
            )
            val result = if (editor.logicalItemId == null) {
                createSecurePasswordUseCase(draft)
            } else {
                updateSecurePasswordUseCase(editor.logicalItemId, draft)
            }
            handleMutationResult(result, editor)
        }
    }

    private fun saveNote(editor: VaultEditorUiState.Note) {
        if (editor.isLoading || editor.isSaving) return

        mutableUiState.update { state ->
            state.copy(editor = editor.copy(isSaving = true, errorMessage = null))
        }

        viewModelScope.launch {
            val draft = SecureNoteDraft(
                displayHint = editor.displayHint,
                body = editor.body,
            )
            val result = if (editor.logicalItemId == null) {
                createSecureNoteUseCase(draft)
            } else {
                updateSecureNoteUseCase(editor.logicalItemId, draft)
            }
            handleMutationResult(result, editor)
        }
    }

    private fun handleMutationResult(
        result: SecureItemMutationResult,
        editor: VaultEditorUiState,
    ) {
        when (result) {
            is SecureItemMutationResult.Success -> {
                mutableUiState.update { state ->
                    state.copy(
                        editor = null,
                        screenMessage = null,
                    )
                }
            }

            is SecureItemMutationResult.Error -> {
                mutableUiState.update { state ->
                    state.copy(
                        editor = when (editor) {
                            is VaultEditorUiState.Password -> editor.copy(
                                isSaving = false,
                                errorMessage = mapCrudError(result.reason),
                            )

                            is VaultEditorUiState.Note -> editor.copy(
                                isSaving = false,
                                errorMessage = mapCrudError(result.reason),
                            )
                        },
                    )
                }
            }
        }
    }

    private fun deleteCurrentItem() {
        val editor = mutableUiState.value.editor ?: return
        val logicalItemId = editor.logicalItemId ?: return
        if (editor.isLoading || editor.isSaving) return

        mutableUiState.update { state ->
            state.copy(
                editor = when (editor) {
                    is VaultEditorUiState.Password -> editor.copy(isSaving = true, errorMessage = null)
                    is VaultEditorUiState.Note -> editor.copy(isSaving = true, errorMessage = null)
                },
            )
        }

        viewModelScope.launch {
            when (val result = softDeleteSecureItemUseCase(logicalItemId)) {
                is SecureItemMutationResult.Success -> {
                    mutableUiState.update { state -> state.copy(editor = null, screenMessage = null) }
                }

                is SecureItemMutationResult.Error -> {
                    mutableUiState.update { state ->
                        state.copy(
                            editor = when (editor) {
                                is VaultEditorUiState.Password -> editor.copy(
                                    isSaving = false,
                                    errorMessage = mapCrudError(result.reason),
                                )

                                is VaultEditorUiState.Note -> editor.copy(
                                    isSaving = false,
                                    errorMessage = mapCrudError(result.reason),
                                )
                            },
                        )
                    }
                }
            }
        }
    }

    private fun mapCrudError(error: SecureItemCrudError): String = when (error) {
        SecureItemCrudError.VaultLocked -> "Vault is locked."
        SecureItemCrudError.ItemNotFound -> "Item not found."
        is SecureItemCrudError.ValidationError -> error.message
        SecureItemCrudError.CorruptedPayload -> "Item payload is corrupted."
    }
}
