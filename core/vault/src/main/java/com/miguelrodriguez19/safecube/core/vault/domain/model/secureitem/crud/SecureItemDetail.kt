package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import java.time.Instant
import java.util.UUID

data class SecureItemDetail(
    val logicalItemId: UUID,
    val remoteItemId: UUID?,
    val itemType: SecureItemType,
    val schemaVersion: Int,
    val displayHint: String,
    val payloadVersion: Long,
    val createdAt: Instant,
    val updatedAt: Instant,
    val content: SecureItemContent,
)
