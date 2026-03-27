package com.miguelrodriguez19.safecube.core.vault.domain.service

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType

data class EncryptedSecureItemPayload(
    val itemType: SecureItemType,
    val schemaVersion: Int,
    val payload: ByteArray,
)
