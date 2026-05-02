package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result

import java.time.Instant
import java.util.UUID

data class RemoteCreateSecureItemResult(
    val itemId: UUID,
    val createdAt: Instant,
)
