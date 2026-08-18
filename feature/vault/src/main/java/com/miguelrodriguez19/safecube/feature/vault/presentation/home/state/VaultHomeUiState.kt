package com.miguelrodriguez19.safecube.feature.vault.presentation.home.state

import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync.VaultSyncUiErrorCategory
import java.time.Instant
import java.util.UUID

data class VaultHomeUiState(
    val items: List<VaultItemSummaryUiModel> = emptyList(),
    val contentState: VaultHomeContentState = VaultHomeContentState.InitialLoading,
    val localReadError: VaultHomeLocalReadError? = null,
    val isSyncing: Boolean = false,
    val lastSyncResult: VaultSyncResult? = null,
    val lastSyncError: VaultSyncError? = null,
    val syncErrorCategory: VaultSyncUiErrorCategory? = null,
    val isDirty: Boolean = false,
) {
    val isInitialLoading: Boolean
        get() = contentState == VaultHomeContentState.InitialLoading

    val isEmpty: Boolean
        get() = contentState == VaultHomeContentState.Empty

    val hasLocalReadError: Boolean
        get() = contentState == VaultHomeContentState.Error

    val isSyncRetryable: Boolean
        get() = lastSyncError?.retryDecision == RetryDecision.Retryable
}

sealed interface VaultHomeContentState {
    data object InitialLoading : VaultHomeContentState
    data object Content : VaultHomeContentState
    data object Empty : VaultHomeContentState
    data object Error : VaultHomeContentState
}

enum class VaultHomeLocalReadError {
    StorageOrCrypto,
}

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
