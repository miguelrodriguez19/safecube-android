package com.miguelrodriguez19.safecube.core.storage

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType

enum class SecureItemDraftTypeDb(
    val storageValue: String,
) {
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    ;

    fun toDomain(): SecureItemDraftType = when (this) {
        UPDATE -> SecureItemDraftType.UPDATE
        DELETE -> SecureItemDraftType.DELETE
    }

    companion object {
        fun fromStorageValue(value: String): SecureItemDraftTypeDb? = entries.firstOrNull {
            it.storageValue == value
        }

        fun fromDomain(value: SecureItemDraftType): SecureItemDraftTypeDb = when (value) {
            SecureItemDraftType.UPDATE -> UPDATE
            SecureItemDraftType.DELETE -> DELETE
        }
    }
}
