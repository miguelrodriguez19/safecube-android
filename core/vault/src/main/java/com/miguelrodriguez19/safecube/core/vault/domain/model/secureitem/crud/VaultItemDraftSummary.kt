package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import java.time.Instant
import java.util.UUID

data class VaultItemDraftSummary(
    val logicalItemId: UUID,
    val itemType: SecureItemType,
    val displayHint: String,
    val updatedAt: Instant,
    val draftType: SecureItemDraftType,
    val draftSyncStatus: SecureItemDraftSyncStatus,
    val lastSyncError: String?,
)
