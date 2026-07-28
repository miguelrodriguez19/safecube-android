package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request

import java.util.UUID

data class RemoteCreateSecureItemRequest(
    val itemType: String,
    val schemaVersion: Int,
    val displayHint: String,
    val payload: ByteArray,
    val payloadVersion: Long,
    val mutationId: UUID,
)
