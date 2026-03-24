package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import kotlinx.serialization.Serializable

@Serializable
data class NoteSecureItemContent(
    val body: String,
) : SecureItemContent {
    init {
        require(body.isNotBlank()) { "body must not be blank." }
    }

    override val itemType: SecureItemType = SecureItemType.NOTE
    override val schemaVersion: Int = NOTE_SCHEMA_VERSION

    companion object {
        const val NOTE_SCHEMA_VERSION: Int = 1
    }
}
