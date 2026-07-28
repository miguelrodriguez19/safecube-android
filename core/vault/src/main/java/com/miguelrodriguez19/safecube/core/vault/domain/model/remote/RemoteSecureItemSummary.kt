package com.miguelrodriguez19.safecube.core.vault.domain.model.remote

import java.time.Instant
import java.util.UUID

data class RemoteSecureItemSummary(
    val itemId: UUID,
    val itemType: String,
    val schemaVersion: Int,
    val displayHint: String,
    val payloadVersion: Long,
    val itemRevision: Long,
    val changeSequence: Long,
    val updatedAt: Instant,
    val deletedAt: Instant?,
)
