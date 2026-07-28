package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

import java.time.Instant
import java.util.UUID

data class SecureItem(
    val logicalItemId: UUID,
    val remoteItemId: UUID? = null,
    val itemType: SecureItemType,
    val schemaVersion: Int,
    val displayHint: String,
    val payload: ByteArray,
    val payloadVersion: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val itemRevision: Long = 1,
    val changeSequence: Long = 1,
    val deletedAt: Instant? = null,
    val syncState: SecureItemSyncState = SecureItemSyncState.SYNCED,
    val lastSyncedAt: Instant? = null,
    val lastSyncError: String? = null,
) {
    init {
        require(displayHint.isNotBlank()) { "displayHint must not be blank." }
        require(schemaVersion > 0) { "schemaVersion must be positive." }
        require(payload.isNotEmpty()) { "payload must not be empty." }
        require(payloadVersion > 0) { "payloadVersion must be positive." }
        require(itemRevision > 0) { "itemRevision must be positive." }
        require(changeSequence > 0) { "changeSequence must be positive." }
        require(lastSyncError?.isNotBlank() != false) { "lastSyncError must not be blank when present." }
    }
}
