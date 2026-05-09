package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.state

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import java.util.UUID

data class PasswordEditorUiState(
    val logicalItemId: UUID? = null,
    val displayHint: String = "",
    val username: String = "",
    val password: String = "",
    val websiteUrl: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isSyncing: Boolean = false,
    val hasDraft: Boolean = false,
    val draftType: SecureItemDraftType? = null,
    val lastPublishError: String? = null,
    val lastDraftError: String? = null,
    val isDraftActionInProgress: Boolean = false,
    val itemSyncState: SecureItemSyncState? = null,
    val itemSyncError: String? = null,
    val hasUnsavedLocalChanges: Boolean = false,
    val errorMessage: String? = null,
) {
    val isEditMode: Boolean
        get() = logicalItemId != null

    val hasPendingSync: Boolean
        get() = when (itemSyncState) {
            SecureItemSyncState.PENDING_CREATE,
            SecureItemSyncState.PENDING_UPDATE,
            SecureItemSyncState.PENDING_DELETE,
            -> true
            SecureItemSyncState.SYNCED,
            SecureItemSyncState.CONFLICT,
            null,
            -> false
        }

    val hasConflict: Boolean
        get() = itemSyncState == SecureItemSyncState.CONFLICT
}
