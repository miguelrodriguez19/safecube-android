package com.miguelrodriguez19.safecube.feature.vault.presentation.home.state

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import java.time.Instant
import java.util.UUID

data class VaultHomeUiState(
    val items: List<VaultItemSummaryUiModel> = emptyList(),
    val isSyncing: Boolean = false,
    val lastSyncResult: VaultSyncResult? = null,
    val lastSyncError: VaultSyncError? = null,
)

data class VaultItemSummaryUiModel(
    val logicalItemId: UUID,
    val displayHint: String,
    val itemType: SecureItemType,
    val updatedAt: Instant,
    val syncState: SecureItemSyncState,
    val lastSyncError: String?,
) {
    val isPendingSync: Boolean
        get() = when (syncState) {
            SecureItemSyncState.PENDING_CREATE,
            SecureItemSyncState.PENDING_UPDATE,
            SecureItemSyncState.PENDING_DELETE,
            -> true

            SecureItemSyncState.SYNCED,
            SecureItemSyncState.CONFLICT,
            -> false
        }

    val isConflict: Boolean
        get() = syncState == SecureItemSyncState.CONFLICT
}
