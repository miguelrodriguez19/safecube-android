package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import java.time.Instant
import java.util.UUID

data class SecureItemDraftDetail(
    val logicalItemId: UUID,
    val remoteItemId: UUID?,
    val draftType: SecureItemDraftType,
    val draftSyncStatus: SecureItemDraftSyncStatus,
    val itemType: SecureItemType,
    val displayHint: String,
    val payloadVersion: Long,
    val updatedAt: Instant,
    val lastSyncError: String?,
    val content: SecureItemContent,
    val requiresSaveAsNew: Boolean = false,
)
