package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.ui

import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync.asUiLabel
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.action.PasswordEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.event.PasswordEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.state.PasswordEditorUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.viewmodel.PasswordEditorViewModel
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.SecureItemEditorScaffold

@Composable
fun PasswordEditorScreen(
    onBack: () -> Unit,
    onUnlockVault: () -> Unit,
    logicalItemId: String? = null,
    viewModel: PasswordEditorViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(logicalItemId) {
        viewModel.load(logicalItemId)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                PasswordEditorUiEvent.NavigateBack -> onBack()
                PasswordEditorUiEvent.NavigateToUnlock -> onUnlockVault()
            }
        }
    }

    PasswordEditorContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onBack = onBack,
    )
}

@Composable
private fun PasswordEditorContent(
    uiState: PasswordEditorUiState,
    onAction: (PasswordEditorUiAction) -> Unit,
    onBack: () -> Unit,
) {
    SecureItemEditorScaffold(
        title = if (uiState.isEditMode) "Edit password entry" else "New password entry",
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
            { onAction(PasswordEditorUiAction.PublishDraftClicked) }
        } else {
            null
        },
        onDiscardDraft = if (uiState.hasDraft) {
            { onAction(PasswordEditorUiAction.DiscardDraftClicked) }
        } else {
            null
        },
        isDraftActionInProgress = uiState.isDraftActionInProgress,
        errorMessage = uiState.errorMessage,
        onBack = onBack,
        onSave = { onAction(PasswordEditorUiAction.SaveClicked) },
        onRetryRead = { onAction(PasswordEditorUiAction.RetryReadClicked) },
        showSyncAction = false,
        onSyncNow = null,
        onDelete = if (uiState.isEditMode) {
            { onAction(PasswordEditorUiAction.DeleteClicked) }
        } else {
            null
        },
    ) {
        Column {
            OutlinedTextField(
                value = uiState.displayHint,
                onValueChange = { onAction(PasswordEditorUiAction.DisplayHintChanged(it)) },
                label = { Text("Display hint") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
                singleLine = true,
            )
            OutlinedTextField(
                value = uiState.username,
                onValueChange = { onAction(PasswordEditorUiAction.UsernameChanged(it)) },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
                singleLine = true,
            )
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { onAction(PasswordEditorUiAction.PasswordChanged(it)) },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
            )
            OutlinedTextField(
                value = uiState.websiteUrl,
                onValueChange = { onAction(PasswordEditorUiAction.WebsiteUrlChanged(it)) },
                label = { Text("Website URL") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isSaving,
                singleLine = true,
            )
            OutlinedTextField(
                value = uiState.notes,
                onValueChange = { onAction(PasswordEditorUiAction.NotesChanged(it)) },
                label = { Text("Notes") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp),
                enabled = !uiState.isSaving,
            )
        }
    }
}

@Composable
private fun SecureItemDraftType.asBannerLabel(): String = when (this) {
    SecureItemDraftType.CREATE -> stringResource(UiR.string.draft_banner_create)
    SecureItemDraftType.UPDATE -> stringResource(UiR.string.draft_banner_update)
    SecureItemDraftType.DELETE -> stringResource(UiR.string.draft_banner_delete)
}
