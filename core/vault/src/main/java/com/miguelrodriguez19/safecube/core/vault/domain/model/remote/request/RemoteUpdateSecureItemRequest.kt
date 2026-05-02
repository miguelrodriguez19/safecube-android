package com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request

data class RemoteUpdateSecureItemRequest(
    val itemType: String,
    val schemaVersion: Int,
    val displayHint: String,
    val payload: ByteArray,
)
