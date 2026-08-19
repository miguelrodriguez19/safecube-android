package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import androidx.compose.ui.res.stringResource
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.state.SecureItemEditorState
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync.SyncIconButton

@Composable
internal fun SecureItemEditorScaffold(
    title: String,
    isEditMode: Boolean,
    isLoading: Boolean,
    isSaving: Boolean,
    editorState: SecureItemEditorState,
    isSyncing: Boolean,
    syncStatusLabel: String?,
    lastSyncMessage: String?,
    lastSyncErrorMessage: String?,
    draftBannerMessage: String?,
    showDraftActions: Boolean,
    publishDraftAsNew: Boolean,
    onPublishDraft: (() -> Unit)?,
    onDiscardDraft: (() -> Unit)?,
    isDraftActionInProgress: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onRetryRead: () -> Unit,
    showSyncAction: Boolean,
    onSyncNow: (() -> Unit)?,
    onDelete: (() -> Unit)?,
    content: @Composable ColumnScope.() -> Unit,
) {
    val canMutate = editorState == SecureItemEditorState.EditableContent
    val canShowContent = editorState == SecureItemEditorState.EditableContent ||
        editorState == SecureItemEditorState.Saving

    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 2.dp,
                modifier = Modifier.statusBarsPadding(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        IconButton(
                            onClick = onBack,
                            enabled = !isSaving,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                            )
                        }
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                    if (showSyncAction && onSyncNow != null) {
                        SyncIconButton(
                            isSyncing = isSyncing,
                            enabled = !isSyncing,
                            onClick = onSyncNow,
                            contentDescription = stringResource(UiR.string.sync_now_action),
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            syncStatusLabel?.let { statusLabel ->
                Text(
                    text = statusLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
            lastSyncMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            lastSyncErrorMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            draftBannerMessage?.let { message ->
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
            if (showDraftActions) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (onDiscardDraft != null) {
                        OutlinedButton(
                            onClick = onDiscardDraft,
                            enabled = canMutate && !isDraftActionInProgress,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(stringResource(UiR.string.draft_action_discard))
                        }
                    }
                    if (onPublishDraft != null) {
                        Button(
                            onClick = onPublishDraft,
                            enabled = canMutate && !isDraftActionInProgress,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text(
                                if (isDraftActionInProgress) {
                                    stringResource(UiR.string.sync_status_syncing)
                                } else {
                                    stringResource(
                                        if (publishDraftAsNew) {
                                            UiR.string.draft_action_save_as_new
                                        } else {
                                            UiR.string.draft_action_publish
                                        },
                                    )
                                },
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                Text(
                    text = stringResource(UiR.string.loading),
                    style = MaterialTheme.typography.bodyMedium,
                )
            } else if (canShowContent) {
                content()
            }

            errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            if (isEditMode && onDelete != null) {
                OutlinedButton(
                    onClick = onDelete,
                    enabled = canMutate && !isDraftActionInProgress,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(UiR.string.action_delete))
                }
            }

            if (editorState == SecureItemEditorState.CorruptedPayload ||
                editorState == SecureItemEditorState.LocalStorageFailure
            ) {
                OutlinedButton(
                    onClick = onRetryRead,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(UiR.string.retry))
                }
            }

            Button(
                onClick = onSave,
                enabled = canMutate && !isDraftActionInProgress,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    if (isSaving) {
                        stringResource(UiR.string.loading)
                    } else {
                        stringResource(UiR.string.action_save)
                    },
                )
            }
        }
    }
}
