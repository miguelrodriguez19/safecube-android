package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult

@Composable
internal fun SecureItemSyncState.asUiLabel(): String = when (this) {
    SecureItemSyncState.SYNCED -> stringResource(UiR.string.sync_status_synced)
}

@Composable
internal fun VaultSyncError.asUiLabel(): String = toUiCategory().asUiLabel()

@Composable
internal fun VaultSyncUiErrorCategory.asUiLabel(): String = when (this) {
    VaultSyncUiErrorCategory.OfflineOrTimeout -> stringResource(UiR.string.sync_error_offline_timeout)
    VaultSyncUiErrorCategory.ServiceUnavailable -> stringResource(UiR.string.sync_error_service_unavailable)
    VaultSyncUiErrorCategory.SessionRequired -> stringResource(UiR.string.sync_error_session_required)
    VaultSyncUiErrorCategory.Conflict -> stringResource(UiR.string.sync_error_conflict)
    VaultSyncUiErrorCategory.ProtocolIntegrity -> stringResource(UiR.string.sync_error_protocol_integrity)
    VaultSyncUiErrorCategory.StorageOrCrypto -> stringResource(UiR.string.sync_error_storage_crypto)
}

@Composable
internal fun VaultSyncResult.asUiLabel(): String = when (this) {
    is VaultSyncResult.Success -> {
        val uploadedLabel = pluralStringResource(
            UiR.plurals.sync_uploaded_count,
            uploadedCount,
            uploadedCount,
        )
        val downloadedLabel = pluralStringResource(
            UiR.plurals.sync_downloaded_count,
            downloadedCount,
            downloadedCount,
        )
        val conflictLabel = pluralStringResource(
            UiR.plurals.sync_conflict_count,
            conflictCount,
            conflictCount,
        )

        stringResource(
            UiR.string.sync_last_result_success,
            uploadedLabel,
            downloadedLabel,
            conflictLabel,
        )
    }

    is VaultSyncResult.Error -> stringResource(
        UiR.string.sync_last_result_error_with_reason,
        reason.asUiLabel(),
    )
}
