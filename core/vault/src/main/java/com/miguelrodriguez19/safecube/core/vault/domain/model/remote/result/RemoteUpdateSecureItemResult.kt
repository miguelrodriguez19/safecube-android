package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result

import java.time.Instant
import java.util.UUID

data class RemoteUpdateSecureItemResult(
    val itemId: UUID,
    val mutationId: UUID,
    val payloadVersion: Long,
    val itemRevision: Long,
    val changeSequence: Long,
    val updatedAt: Instant,
)
