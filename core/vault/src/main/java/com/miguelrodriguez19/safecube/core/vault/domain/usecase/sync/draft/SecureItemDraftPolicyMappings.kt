package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import java.time.Instant

internal fun SecureItem.toSyncDraft(
    draftType: SecureItemDraftType,
    draftSyncStatus: SecureItemDraftSyncStatus,
    lastSyncError: String?,
    baseItemRevision: Long? = itemRevision,
): SecureItemSyncDraft = SecureItemSyncDraft(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = itemType,
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    lastSyncedAt = lastSyncedAt,
    draftType = draftType,
    draftSyncStatus = draftSyncStatus,
    baseItemRevision = baseItemRevision,
    lastSyncError = lastSyncError,
)

internal fun SecureItemSyncDraft.toOfficialItem(
    remoteItemId: java.util.UUID?,
    payloadVersion: Long,
    itemRevision: Long,
    changeSequence: Long,
    updatedAt: Instant,
    deletedAt: Instant?,
): SecureItem = SecureItem(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = itemType,
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    itemRevision = itemRevision,
    changeSequence = changeSequence,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncState = SecureItemSyncState.SYNCED,
    lastSyncedAt = updatedAt,
    lastSyncError = null,
)
