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
    val deletedAt: Instant? = null,
    val lastSyncedAt: Instant? = null,
    val lastSyncError: String? = null,
    val draftType: SecureItemDraftType,
    val basePayloadVersion: Long,
    val baseUpdatedAt: Instant,
    val lastPublishError: String? = null,
) {
    init {
        require(displayHint.isNotBlank()) { "displayHint must not be blank." }
        require(schemaVersion > 0) { "schemaVersion must be positive." }
        require(payload.isNotEmpty()) { "payload must not be empty." }
        require(payloadVersion > 0) { "payloadVersion must be positive." }
        require(basePayloadVersion > 0) { "basePayloadVersion must be positive." }
        require(lastSyncError?.isNotBlank() != false) {
            "lastSyncError must not be blank when present."
        }
        require(lastPublishError?.isNotBlank() != false) {
            "lastPublishError must not be blank when present."
        }
    }
}
