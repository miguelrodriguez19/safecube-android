package com.miguelrodriguez19.safecube.core.storage

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState

enum class SecureItemSyncStateDb(
    val storageValue: String,
) {
    SYNCED("SYNCED"),
    ;

    fun toDomain(): SecureItemSyncState = when (this) {
        SYNCED -> SecureItemSyncState.SYNCED
    }

    companion object {
        fun fromStorageValue(value: String): SecureItemSyncStateDb? = entries.firstOrNull { it.storageValue == value }

        fun fromDomain(value: SecureItemSyncState): SecureItemSyncStateDb = when (value) {
            SecureItemSyncState.SYNCED -> SYNCED
        }
    }
}
