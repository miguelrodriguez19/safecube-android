package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.note.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.note.action.NoteEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.note.event.NoteEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.note.state.NoteEditorUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.note.viewmodel.NoteEditorViewModel
import com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.ui.SecureItemEditorScaffold
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync.asUiLabel

@Composable
fun NoteEditorScreen(
    onBack: () -> Unit,
    onUnlockVault: () -> Unit,
    logicalItemId: String? = null,
    viewModel: NoteEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(logicalItemId) {
        viewModel.load(logicalItemId)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                NoteEditorUiEvent.NavigateBack -> onBack()
                NoteEditorUiEvent.NavigateToUnlock -> onUnlockVault()
            }
        }
    }

    NoteEditorContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBack = onBack,
    )
}

@Composable
private fun NoteEditorContent(
    uiState: NoteEditorUiState,
    onAction: (NoteEditorUiAction) -> Unit,
    onBack: () -> Unit,
) {
    SecureItemEditorScaffold(
        title = if (uiState.isEditMode) "Edit secure note" else "New secure note",
        isEditMode = uiState.isEditMode,
        isLoading = uiState.isLoading,
        isSaving = uiState.isSaving,
        editorState = uiState.editorState,
        isSyncing = uiState.isSyncing,
        syncStatusLabel = if (uiState.isSyncing) stringResource(UiR.string.sync_status_syncing) else null,
        lastSyncMessage = null,
        lastSyncErrorMessage = uiState.lastDraftError,
        draftBannerMessage = uiState.draftType?.asBannerLabel(),
        showDraftActions = uiState.hasDraft,
        publishDraftAsNew = uiState.requiresSaveAsNew,
        onPublishDraft = if (uiState.hasConflict) {
            { onAction(NoteEditorUiAction.PublishDraftClicked) }
        } else {
            null
        },
        onDiscardDraft = if (uiState.hasDraft) {
            { onAction(NoteEditorUiAction.DiscardDraftClicked) }
        } else {
            null
        },
        isDraftActionInProgress = uiState.isDraftActionInProgress,
        errorMessage = uiState.errorMessage,
        onBack = onBack,
        onSave = { onAction(NoteEditorUiAction.SaveClicked) },
        onRetryRead = { onAction(NoteEditorUiAction.RetryReadClicked) },
        showSyncAction = false,
        onSyncNow = null,
        onDelete = if (uiState.isEditMode) {
            { onAction(NoteEditorUiAction.DeleteClicked) }
        } else {
            null
        },
    ) {
        OutlinedTextField(
            value = uiState.displayHint,
            onValueChange = { onAction(NoteEditorUiAction.DisplayHintChanged(it)) },
            label = { Text("Display hint") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !uiState.isSaving,
            singleLine = true,
        )
        OutlinedTextField(
            value = uiState.body,
            onValueChange = { onAction(NoteEditorUiAction.BodyChanged(it)) },
            label = { Text("Note body") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 200.dp),
            enabled = !uiState.isSaving,
        )
    }
}

@Composable
private fun SecureItemDraftType.asBannerLabel(): String = when (this) {
    SecureItemDraftType.CREATE -> stringResource(UiR.string.draft_banner_create)
    SecureItemDraftType.UPDATE -> stringResource(UiR.string.draft_banner_update)
    SecureItemDraftType.DELETE -> stringResource(UiR.string.draft_banner_delete)
}
