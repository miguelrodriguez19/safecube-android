package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.state

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
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
    val draftSyncStatus: SecureItemDraftSyncStatus? = null,
    val lastDraftError: String? = null,
    val requiresSaveAsNew: Boolean = false,
    val isDraftActionInProgress: Boolean = false,
    val hasUnsavedLocalChanges: Boolean = false,
    val errorMessage: String? = null,
) {
    val isEditMode: Boolean
        get() = logicalItemId != null

    val hasPendingSync: Boolean
        get() = draftSyncStatus == SecureItemDraftSyncStatus.READY_TO_SYNC

    val hasConflict: Boolean
        get() = draftSyncStatus == SecureItemDraftSyncStatus.CONFLICT
}
