package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request

import java.util.UUID

data class RemoteUpdateSecureItemRequest(
    val itemType: String,
    val schemaVersion: Int,
    val displayHint: String,
    val payload: ByteArray,
    val payloadVersion: Long,
    val baseItemRevision: Long,
    val mutationId: UUID,
)
