package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

import java.time.Instant
import java.util.UUID

data class SecureItemSyncDraft(
    val logicalItemId: UUID,
    val remoteItemId: UUID? = null,
    val itemType: SecureItemType,
    val schemaVersion: Int,
    val displayHint: String,
    val payload: ByteArray,
    val payloadVersion: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val mutationId: UUID = UUID.randomUUID(),
    val deletedAt: Instant? = null,
    val lastSyncedAt: Instant? = null,
    val draftType: SecureItemDraftType,
    val draftSyncStatus: SecureItemDraftSyncStatus,
    val baseItemRevision: Long? = null,
    val lastSyncError: String? = null,
) {
    init {
        require(displayHint.isNotBlank()) { "displayHint must not be blank." }
        require(schemaVersion > 0) { "schemaVersion must be positive." }
        require(payload.isNotEmpty()) { "payload must not be empty." }
        require(payloadVersion > 0) { "payloadVersion must be positive." }
        require(baseItemRevision == null || baseItemRevision > 0) {
            "baseItemRevision must be positive when present."
        }
        require(
            (draftType == SecureItemDraftType.CREATE && baseItemRevision == null) ||
                (draftType != SecureItemDraftType.CREATE && baseItemRevision != null),
        ) { "Only CREATE drafts may omit baseItemRevision." }
        require(lastSyncError?.isNotBlank() != false) {
            "lastSyncError must not be blank when present."
        }
    }
}
