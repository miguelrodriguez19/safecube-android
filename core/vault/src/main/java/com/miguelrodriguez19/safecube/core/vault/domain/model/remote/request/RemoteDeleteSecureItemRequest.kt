package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request

import java.util.UUID

data class RemoteDeleteSecureItemRequest(
    val baseItemRevision: Long,
    val mutationId: UUID,
)
