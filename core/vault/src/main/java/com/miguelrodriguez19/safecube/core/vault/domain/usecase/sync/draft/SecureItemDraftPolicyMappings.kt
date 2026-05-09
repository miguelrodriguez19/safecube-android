package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import java.time.Instant

internal fun SecureItem.toSyncDraft(
    draftType: SecureItemDraftType,
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
    lastSyncError = lastSyncError,
    draftType = draftType,
    basePayloadVersion = payloadVersion,
    baseUpdatedAt = updatedAt,
    lastPublishError = null,
)

internal fun SecureItemSyncDraft.toPublishedOfficialItem(
    payloadVersion: Long,
    updatedAt: Instant,
): SecureItem = SecureItem(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = itemType,
    schemaVersion = schemaVersion,
    displayHint = displayHint,
    payload = payload,
    payloadVersion = payloadVersion,
    createdAt = createdAt,
    updatedAt = updatedAt,
    deletedAt = null,
    syncState = SecureItemSyncState.SYNCED,
    lastSyncedAt = updatedAt,
    lastSyncError = null,
)
