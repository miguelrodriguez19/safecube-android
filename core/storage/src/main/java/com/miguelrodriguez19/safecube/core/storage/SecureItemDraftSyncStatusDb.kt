package com.miguelrodriguez19.safecube.core.storage

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus

enum class SecureItemDraftSyncStatusDb(
    val storageValue: String,
) {
    READY_TO_SYNC("READY_TO_SYNC"),
    CONFLICT("CONFLICT"),
    ;

    fun toDomain(): SecureItemDraftSyncStatus = when (this) {
        READY_TO_SYNC -> SecureItemDraftSyncStatus.READY_TO_SYNC
        CONFLICT -> SecureItemDraftSyncStatus.CONFLICT
    }

    companion object {
        fun fromStorageValue(value: String): SecureItemDraftSyncStatusDb? = entries.firstOrNull {
            it.storageValue == value
        }

        fun fromDomain(value: SecureItemDraftSyncStatus): SecureItemDraftSyncStatusDb = when (value) {
            SecureItemDraftSyncStatus.READY_TO_SYNC -> READY_TO_SYNC
            SecureItemDraftSyncStatus.CONFLICT -> CONFLICT
        }
    }
}
