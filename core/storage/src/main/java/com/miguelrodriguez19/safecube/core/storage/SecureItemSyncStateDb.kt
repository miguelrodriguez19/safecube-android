package com.miguelrodriguez19.safecube.core.storage

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState

enum class SecureItemSyncStateDb(
    val storageValue: String,
) {
    SYNCED("SYNCED"),
    PENDING_CREATE("PENDING_CREATE"),
    PENDING_UPDATE("PENDING_UPDATE"),
    PENDING_DELETE("PENDING_DELETE"),
    CONFLICT("CONFLICT"),
    ;

    fun toDomain(): SecureItemSyncState = when (this) {
        SYNCED -> SecureItemSyncState.SYNCED
        PENDING_CREATE -> SecureItemSyncState.PENDING_CREATE
        PENDING_UPDATE -> SecureItemSyncState.PENDING_UPDATE
        PENDING_DELETE -> SecureItemSyncState.PENDING_DELETE
        CONFLICT -> SecureItemSyncState.CONFLICT
    }

    companion object {
        fun fromStorageValue(value: String): SecureItemSyncStateDb? = entries.firstOrNull { it.storageValue == value }

        fun fromDomain(value: SecureItemSyncState): SecureItemSyncStateDb = when (value) {
            SecureItemSyncState.SYNCED -> SYNCED
            SecureItemSyncState.PENDING_CREATE -> PENDING_CREATE
            SecureItemSyncState.PENDING_UPDATE -> PENDING_UPDATE
            SecureItemSyncState.PENDING_DELETE -> PENDING_DELETE
            SecureItemSyncState.CONFLICT -> CONFLICT
        }
    }
}
