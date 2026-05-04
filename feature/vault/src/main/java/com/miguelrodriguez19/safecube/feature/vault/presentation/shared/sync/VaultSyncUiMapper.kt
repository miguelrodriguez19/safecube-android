package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult

@Composable
internal fun SecureItemSyncState.asUiLabel(): String = when (this) {
    SecureItemSyncState.SYNCED -> stringResource(UiR.string.sync_status_synced)
    SecureItemSyncState.PENDING_CREATE,
    SecureItemSyncState.PENDING_UPDATE,
    SecureItemSyncState.PENDING_DELETE,
    -> stringResource(UiR.string.sync_status_pending)

    SecureItemSyncState.CONFLICT -> stringResource(UiR.string.sync_status_conflict)
}

@Composable
internal fun VaultSyncError.asUiLabel(): String = when (this) {
    is VaultSyncError.InvalidVaultState -> stringResource(UiR.string.sync_error_invalid_vault_state)
    is VaultSyncError.PushFailed -> stringResource(UiR.string.sync_error_push_failed)
    is VaultSyncError.PullFailed -> stringResource(UiR.string.sync_error_pull_failed)
}

@Composable
internal fun VaultSyncResult.asUiLabel(): String = when (this) {
    is VaultSyncResult.Success -> stringResource(
        UiR.string.sync_last_result_success,
        uploadedCount,
        downloadedCount,
        conflictCount,
    )

    is VaultSyncResult.Error -> stringResource(
        UiR.string.sync_last_result_error_with_reason,
        reason.asUiLabel(),
    )
}
