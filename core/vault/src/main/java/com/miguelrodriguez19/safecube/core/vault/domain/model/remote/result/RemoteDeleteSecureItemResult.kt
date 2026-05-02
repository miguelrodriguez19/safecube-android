package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result

import java.time.Instant
import java.util.UUID

data class RemoteDeleteSecureItemResult(
    val itemId: UUID,
    val deletedAt: Instant,
)
