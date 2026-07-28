package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

enum class SecureItemSyncState(
    val storageValue: String,
) {
    SYNCED("SYNCED");

    companion object {
        fun fromStorageValue(value: String): SecureItemSyncState? = entries.firstOrNull { state ->
            state.storageValue == value
        }
    }
}
