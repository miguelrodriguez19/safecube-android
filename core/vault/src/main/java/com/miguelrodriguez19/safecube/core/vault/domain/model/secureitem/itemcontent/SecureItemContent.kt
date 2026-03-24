package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType

sealed interface SecureItemContent {
    val itemType: SecureItemType
    val schemaVersion: Int
}