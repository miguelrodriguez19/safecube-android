package com.miguelrodriguez19.safecube.feature.vault.presentation.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.action.VaultHomeUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultEditorUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultHomeUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultItemSummaryUiModel
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.viewmodel.VaultHomeViewModel
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.AppTab
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.NavigationBar
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun VaultScreen(
    onVault: () -> Unit,
    onVaultFolders: () -> Unit,
    onSettings: () -> Unit,
    viewModel: VaultHomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    VaultContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onVault = onVault,
        onVaultFolders = onVaultFolders,
        onSettings = onSettings,
    )
}

@Composable
private fun VaultContent(
    uiState: VaultHomeUiState,
    onAction: (VaultHomeUiAction) -> Unit,
    onVault: () -> Unit,
    onVaultFolders: () -> Unit,
    onSettings: () -> Unit,
) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp) {
                Text(
                    text = "Vault",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                )
            }
        },
        bottomBar = {
            NavigationBar(
                selectedTab = AppTab.Vault,
                onVault = onVault,
                onVaultFolders = onVaultFolders,
                onSettings = onSettings,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            VaultPrimaryActions(onAction = onAction)

            uiState.screenMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            if (uiState.items.isEmpty()) {
                VaultEmptyState(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            } else {
                VaultItemsList(
                    items = uiState.items,
                    onAction = onAction,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }

    when (val editor = uiState.editor) {
        null -> Unit
        is VaultEditorUiState.Password -> PasswordEditorDialog(
            editor = editor,
            onAction = onAction,
        )

        is VaultEditorUiState.Note -> NoteEditorDialog(
            editor = editor,
            onAction = onAction,
        )
    }
}

@Composable
private fun VaultPrimaryActions(
    onAction: (VaultHomeUiAction) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = { onAction(VaultHomeUiAction.CreatePasswordClicked) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("New password entry")
        }
        OutlinedButton(
            onClick = { onAction(VaultHomeUiAction.CreateNoteClicked) },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("New note")
        }
    }
}

@Composable
private fun VaultEmptyState(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Your vault is empty",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "Create your first password entry or secure note. Items stored locally will appear here automatically.",
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun VaultItemsList(
    items: List<VaultItemSummaryUiModel>,
    onAction: (VaultHomeUiAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = items,
            key = { it.logicalItemId },
        ) { item ->
            VaultItemCard(
                item = item,
                onClick = {
                    onAction(
                        VaultHomeUiAction.EditItemClicked(
                            logicalItemId = item.logicalItemId,
                            itemType = item.itemType,
                        ),
                    )
                },
            )
        }
    }
}

@Composable
private fun VaultItemCard(
    item: VaultItemSummaryUiModel,
    onClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = item.displayHint,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = item.itemType.asLabel(),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = "Updated ${item.updatedAt.asListTimestamp()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PasswordEditorDialog(
    editor: VaultEditorUiState.Password,
    onAction: (VaultHomeUiAction) -> Unit,
) {
    EditorDialogFrame(
        title = if (editor.logicalItemId == null) "New password entry" else "Edit password entry",
        editor = editor,
        onDismiss = { onAction(VaultHomeUiAction.DismissEditor) },
        onDelete = {
            if (editor.logicalItemId != null) {
                onAction(VaultHomeUiAction.DeleteItemClicked)
            }
        },
        onSave = { onAction(VaultHomeUiAction.SaveEditorClicked) },
    ) {
        OutlinedTextField(
            value = editor.displayHint,
            onValueChange = { onAction(VaultHomeUiAction.PasswordDisplayHintChanged(it)) },
            label = { Text("Display hint") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !editor.isLoading && !editor.isSaving,
            singleLine = true,
        )
        OutlinedTextField(
            value = editor.username,
            onValueChange = { onAction(VaultHomeUiAction.PasswordUsernameChanged(it)) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !editor.isLoading && !editor.isSaving,
            singleLine = true,
        )
        OutlinedTextField(
            value = editor.email,
            onValueChange = { onAction(VaultHomeUiAction.PasswordEmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !editor.isLoading && !editor.isSaving,
            singleLine = true,
        )
        OutlinedTextField(
            value = editor.password,
            onValueChange = { onAction(VaultHomeUiAction.PasswordValueChanged(it)) },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !editor.isLoading && !editor.isSaving,
            singleLine = true,
        )
    }
}

@Composable
private fun NoteEditorDialog(
    editor: VaultEditorUiState.Note,
    onAction: (VaultHomeUiAction) -> Unit,
) {
    EditorDialogFrame(
        title = if (editor.logicalItemId == null) "New secure note" else "Edit secure note",
        editor = editor,
        onDismiss = { onAction(VaultHomeUiAction.DismissEditor) },
        onDelete = {
            if (editor.logicalItemId != null) {
                onAction(VaultHomeUiAction.DeleteItemClicked)
            }
        },
        onSave = { onAction(VaultHomeUiAction.SaveEditorClicked) },
    ) {
        OutlinedTextField(
            value = editor.displayHint,
            onValueChange = { onAction(VaultHomeUiAction.NoteDisplayHintChanged(it)) },
            label = { Text("Display hint") },
            modifier = Modifier.fillMaxWidth(),
            enabled = !editor.isLoading && !editor.isSaving,
            singleLine = true,
        )
        OutlinedTextField(
            value = editor.body,
            onValueChange = { onAction(VaultHomeUiAction.NoteBodyChanged(it)) },
            label = { Text("Note body") },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 160.dp),
            enabled = !editor.isLoading && !editor.isSaving,
        )
    }
}

@Composable
private fun EditorDialogFrame(
    title: String,
    editor: VaultEditorUiState,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onSave: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            tonalElevation = 3.dp,
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                )

                if (editor.isLoading) {
                    Text(
                        text = "Loading item...",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                } else {
                    content()
                }

                editor.errorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    if (editor.logicalItemId != null) {
                        OutlinedButton(
                            onClick = onDelete,
                            enabled = !editor.isLoading && !editor.isSaving,
                            modifier = Modifier.weight(1f),
                        ) {
                            Text("Delete")
                        }
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        enabled = !editor.isSaving,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = onSave,
                        enabled = !editor.isLoading && !editor.isSaving,
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(if (editor.isSaving) "Saving..." else "Save")
                    }
                }
            }
        }
    }
}

private fun SecureItemType.asLabel(): String = when (this) {
    SecureItemType.PASSWORD -> "Password"
    SecureItemType.NOTE -> "Note"
}

private fun Instant.asListTimestamp(): String = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm", Locale.US)
    .withZone(ZoneId.systemDefault())
    .format(this)
