package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

data class EncodedSecureItemContent(
    val itemType: SecureItemType,
    val schemaVersion: Int,
    val payload: ByteArray,
)
