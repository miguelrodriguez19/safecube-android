package com.miguelrodriguez19.safecube.core.vault.domain.model.remote

import java.time.Instant
import java.util.UUID

data class RemoteSecureItem(
    val itemId: UUID,
    val itemType: String,
    val schemaVersion: Int,
    val displayHint: String,
    val payload: ByteArray,
    val payloadVersion: Long,
    val itemRevision: Long,
    val changeSequence: Long,
    val updatedAt: Instant,
    val deletedAt: Instant?,
)
