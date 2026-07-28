package com.miguelrodriguez19.safecube.feature.vault.presentation.home.state

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
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
    val isDirty: Boolean = false,
)

data class VaultItemSummaryUiModel(
    val logicalItemId: UUID,
    val displayHint: String,
    val itemType: SecureItemType,
    val updatedAt: Instant,
    val hasDraft: Boolean,
    val draftType: SecureItemDraftType?,
    val draftSyncStatus: SecureItemDraftSyncStatus?,
    val lastDraftError: String?,
) {
    val isDraftPendingSync: Boolean
        get() = draftSyncStatus == SecureItemDraftSyncStatus.READY_TO_SYNC

    val isDraftConflict: Boolean
        get() = draftSyncStatus == SecureItemDraftSyncStatus.CONFLICT
}
