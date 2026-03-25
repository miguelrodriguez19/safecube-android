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
    val deletedAt: Instant? = null,
) {
    init {
        require(displayHint.isNotBlank()) { "displayHint must not be blank." }
        require(schemaVersion > 0) { "schemaVersion must be positive." }
        require(payload.isNotEmpty()) { "payload must not be empty." }
        require(payloadVersion > 0) { "payloadVersion must be positive." }
    }
}
