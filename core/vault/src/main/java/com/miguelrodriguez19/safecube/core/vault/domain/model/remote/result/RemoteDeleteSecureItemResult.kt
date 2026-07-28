package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result

import java.time.Instant
import java.util.UUID

data class RemoteDeleteSecureItemResult(
    val itemId: UUID,
    val mutationId: UUID,
    val payloadVersion: Long,
    val itemRevision: Long,
    val changeSequence: Long,
    val deletedAt: Instant,
)
