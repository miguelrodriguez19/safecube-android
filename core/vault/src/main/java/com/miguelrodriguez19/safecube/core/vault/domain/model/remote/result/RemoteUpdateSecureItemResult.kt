package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result

import java.time.Instant
import java.util.UUID

data class RemoteUpdateSecureItemResult(
    val itemId: UUID,
    val payloadVersion: Long,
    val updatedAt: Instant,
)
