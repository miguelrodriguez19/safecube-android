package com.miguelrodriguez19.safecube.core.vault.domain.codec

sealed interface SecureItemContentDecodeError {
    data class UnsupportedItemType(
        val itemType: String,
    ) : SecureItemContentDecodeError

    data class UnsupportedSchemaVersion(
        val itemType: String,
        val schemaVersion: Int,
    ) : SecureItemContentDecodeError

    data class InvalidPayload(
        val message: String,
    ) : SecureItemContentDecodeError
}
