package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftEntity
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftSyncStatusDb
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftTypeDb
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import javax.inject.Inject

class SecureItemDraftEntityMapper @Inject internal constructor() {
    fun toDomain(entity: SecureItemDraftEntity): SecureItemSyncDraft = SecureItemSyncDraft(
        logicalItemId = entity.logicalItemId,
        remoteItemId = entity.remoteItemId,
        itemType = SecureItemType.fromWireName(entity.itemType)
            ?: error("Unsupported SecureItemType '${entity.itemType}' in local draft storage."),
        schemaVersion = entity.schemaVersion,
        displayHint = entity.displayHint,
        payload = entity.payload,
        payloadVersion = entity.payloadVersion,
        mutationId = entity.mutationId,
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        deletedAt = entity.deletedAt,
        draftType = entity.draftType.toDomain(),
        draftSyncStatus = entity.draftSyncStatus.toDomain(),
        baseItemRevision = entity.baseItemRevision,
        lastSyncedAt = entity.lastSyncedAt,
        lastSyncError = entity.lastSyncError,
    )

    fun toEntity(draft: SecureItemSyncDraft): SecureItemDraftEntity = SecureItemDraftEntity(
        logicalItemId = draft.logicalItemId,
        remoteItemId = draft.remoteItemId,
        itemType = draft.itemType.wireName,
        schemaVersion = draft.schemaVersion,
        displayHint = draft.displayHint,
        payload = draft.payload,
        payloadVersion = draft.payloadVersion,
        mutationId = draft.mutationId,
        createdAt = draft.createdAt,
        updatedAt = draft.updatedAt,
        deletedAt = draft.deletedAt,
        draftType = SecureItemDraftTypeDb.fromDomain(draft.draftType),
        draftSyncStatus = SecureItemDraftSyncStatusDb.fromDomain(draft.draftSyncStatus),
        baseItemRevision = draft.baseItemRevision,
        lastSyncedAt = draft.lastSyncedAt,
        lastSyncError = draft.lastSyncError,
    )
}
