package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

enum class SecureItemDraftType {
    CREATE,
    UPDATE,
    DELETE;

    fun isCreateDraft(): Boolean = this == CREATE

}
