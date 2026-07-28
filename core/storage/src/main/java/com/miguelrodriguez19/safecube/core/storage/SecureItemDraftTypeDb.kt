package com.miguelrodriguez19.safecube.core.storage

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType

enum class SecureItemDraftTypeDb(
    val storageValue: String,
) {
    CREATE("CREATE"),
    UPDATE("UPDATE"),
    DELETE("DELETE"),
    ;

    fun toDomain(): SecureItemDraftType = when (this) {
        CREATE -> SecureItemDraftType.CREATE
        UPDATE -> SecureItemDraftType.UPDATE
        DELETE -> SecureItemDraftType.DELETE
    }

    companion object {
        fun fromStorageValue(value: String): SecureItemDraftTypeDb? = entries.firstOrNull {
            it.storageValue == value
        }

        fun fromDomain(value: SecureItemDraftType): SecureItemDraftTypeDb = when (value) {
            SecureItemDraftType.CREATE -> CREATE
            SecureItemDraftType.UPDATE -> UPDATE
            SecureItemDraftType.DELETE -> DELETE
        }
    }
}
