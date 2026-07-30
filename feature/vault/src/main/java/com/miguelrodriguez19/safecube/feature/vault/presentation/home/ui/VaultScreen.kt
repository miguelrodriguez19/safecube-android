package com.miguelrodriguez19.safecube.feature.vault.presentation.home.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultHomeUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultItemSummaryUiModel
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.viewmodel.VaultHomeViewModel
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync.SyncIconButton
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.AppTab
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.NavigationBar
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

@Composable
fun VaultScreen(
    onVault: () -> Unit,
    onCreatePassword: () -> Unit,
    onCreateNote: () -> Unit,
    onEditPassword: (UUID) -> Unit,
    onEditNote: (UUID) -> Unit,
    onVaultFolders: () -> Unit,
    onSettings: () -> Unit,
    viewModel: VaultHomeViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val syncingMessage = stringResource(UiR.string.sync_status_syncing)

    DisposableEffect(viewModel) {
        viewModel.onVaultScreenShown()
        onDispose {
            viewModel.onVaultScreenHidden()
        }
    }

    VaultContent(
        uiState = uiState,
        onCreatePassword = onCreatePassword,
        onCreateNote = onCreateNote,
        onEditPassword = onEditPassword,
        onEditNote = onEditNote,
        onVault = onVault,
        onVaultFolders = onVaultFolders,
        onSettings = onSettings,
        onSyncNow = {
            Toast.makeText(context, syncingMessage, Toast.LENGTH_SHORT).show()
            viewModel.syncNow()
        },
    )
}

@Composable
private fun VaultContent(
    uiState: VaultHomeUiState,
    onCreatePassword: () -> Unit,
    onCreateNote: () -> Unit,
    onEditPassword: (UUID) -> Unit,
    onEditNote: (UUID) -> Unit,
    onVault: () -> Unit,
    onVaultFolders: () -> Unit,
    onSettings: () -> Unit,
    onSyncNow: () -> Unit,
) {
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(UiR.string.vault_label),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                    SyncIconButton(
                        isSyncing = uiState.isSyncing,
                        onClick = onSyncNow,
                        contentDescription = stringResource(UiR.string.sync_now_action),
                    )
                }
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
            VaultSyncFeedback(
                uiState = uiState,
                modifier = Modifier.fillMaxWidth(),
            )
            VaultPrimaryActions(
                onCreatePassword = onCreatePassword,
                onCreateNote = onCreateNote,
            )

            if (uiState.items.isEmpty()) {
                VaultEmptyState(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                )
            } else {
                VaultItemsList(
                    items = uiState.items,
                    onEditPassword = onEditPassword,
                    onEditNote = onEditNote,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun VaultPrimaryActions(
    onCreatePassword: () -> Unit,
    onCreateNote: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Button(
            onClick = onCreatePassword,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("New password entry")
        }
        OutlinedButton(
            onClick = onCreateNote,
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
    onEditPassword: (UUID) -> Unit,
    onEditNote: (UUID) -> Unit,
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
                    when (item.itemType) {
                        SecureItemType.PASSWORD -> onEditPassword(item.logicalItemId)
                        SecureItemType.NOTE -> onEditNote(item.logicalItemId)
                    }
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
            if (item.hasDraft) {
                Text(
                    text = stringResource(UiR.string.draft_badge_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
                item.draftType?.let { draftType ->
                    Text(
                        text = draftType.asLabel(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                item.lastDraftError?.let { lastDraftError ->
                    Text(
                        text = stringResource(
                            UiR.string.draft_last_sync_error_with_reason,
                            lastDraftError,
                        ),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
            if (item.isDraftConflict) {
                Text(
                    text = stringResource(UiR.string.sync_status_conflict),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            } else if (item.isDraftPendingSync) {
                Text(
                    text = stringResource(UiR.string.sync_status_pending),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
            Text(
                text = "Updated ${item.updatedAt.asListTimestamp()}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun VaultSyncFeedback(
    uiState: VaultHomeUiState,
    modifier: Modifier = Modifier,
) {
    val syncResult = uiState.lastSyncResult
    val message = when {
        uiState.isSyncing -> stringResource(UiR.string.sync_status_syncing)
        syncResult is VaultSyncResult.Success -> {
            val uploadedLabel = pluralStringResource(
                UiR.plurals.sync_uploaded_count,
                syncResult.uploadedCount,
                syncResult.uploadedCount,
            )
            val downloadedLabel = pluralStringResource(
                UiR.plurals.sync_downloaded_count,
                syncResult.downloadedCount,
                syncResult.downloadedCount,
            )
            val conflictLabel = pluralStringResource(
                UiR.plurals.sync_conflict_count,
                syncResult.conflictCount,
                syncResult.conflictCount,
            )

            stringResource(
                UiR.string.sync_last_result_success,
                uploadedLabel,
                downloadedLabel,
                conflictLabel,
            )
        }

        syncResult is VaultSyncResult.Error -> stringResource(
            UiR.string.sync_last_result_error_with_reason,
            syncResult.reason.asUiLabel(),
        )

        else -> null
    } ?: return

    Text(
        text = message,
        modifier = modifier,
        style = MaterialTheme.typography.bodyMedium,
        color = if (uiState.lastSyncError == null) {
            MaterialTheme.colorScheme.onSurface
        } else {
            MaterialTheme.colorScheme.error
        },
    )
}

private fun SecureItemType.asLabel(): String = when (this) {
    SecureItemType.PASSWORD -> "Password"
    SecureItemType.NOTE -> "Note"
}

@Composable
private fun SecureItemDraftType.asLabel(): String = when (this) {
    SecureItemDraftType.CREATE -> stringResource(UiR.string.draft_type_create)
    SecureItemDraftType.UPDATE -> stringResource(UiR.string.draft_type_update)
    SecureItemDraftType.DELETE -> stringResource(UiR.string.draft_type_delete)
}

@Composable
private fun VaultSyncError.asUiLabel(): String = when (this) {
    is VaultSyncError.InvalidVaultState -> stringResource(UiR.string.sync_error_invalid_vault_state)
    is VaultSyncError.PushFailed -> stringResource(UiR.string.sync_error_push_failed)
    is VaultSyncError.PullFailed -> stringResource(UiR.string.sync_error_pull_failed)
}

private fun Instant.asListTimestamp(): String = DateTimeFormatter
    .ofPattern("yyyy-MM-dd HH:mm", Locale.US)
    .withZone(ZoneId.systemDefault())
    .format(this)
