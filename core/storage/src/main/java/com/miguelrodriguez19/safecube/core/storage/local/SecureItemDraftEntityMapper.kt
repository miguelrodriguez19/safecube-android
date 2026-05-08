package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftEntity
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
        createdAt = entity.createdAt,
        updatedAt = entity.updatedAt,
        deletedAt = entity.deletedAt,
        lastSyncedAt = entity.lastSyncedAt,
        lastSyncError = entity.lastSyncError,
        draftType = entity.draftType.toDomain(),
        basePayloadVersion = entity.basePayloadVersion,
        baseUpdatedAt = entity.baseUpdatedAt,
        lastPublishError = entity.lastPublishError,
    )

    fun toEntity(draft: SecureItemSyncDraft): SecureItemDraftEntity = SecureItemDraftEntity(
        logicalItemId = draft.logicalItemId,
        remoteItemId = draft.remoteItemId,
        itemType = draft.itemType.wireName,
        schemaVersion = draft.schemaVersion,
        displayHint = draft.displayHint,
        payload = draft.payload,
        payloadVersion = draft.payloadVersion,
        createdAt = draft.createdAt,
        updatedAt = draft.updatedAt,
        deletedAt = draft.deletedAt,
        lastSyncedAt = draft.lastSyncedAt,
        lastSyncError = draft.lastSyncError,
        draftType = SecureItemDraftTypeDb.fromDomain(draft.draftType),
        basePayloadVersion = draft.basePayloadVersion,
        baseUpdatedAt = draft.baseUpdatedAt,
        lastPublishError = draft.lastPublishError,
    )
}
