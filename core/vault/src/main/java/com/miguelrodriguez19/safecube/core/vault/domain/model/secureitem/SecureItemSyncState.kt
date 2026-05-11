package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

enum class SecureItemSyncState(
    val storageValue: String,
) {
    SYNCED("SYNCED"),
    PENDING_CREATE("PENDING_CREATE"),
    PENDING_UPDATE("PENDING_UPDATE"),
    PENDING_DELETE("PENDING_DELETE"),
    CONFLICT("CONFLICT");

    companion object {
        fun fromStorageValue(value: String): SecureItemSyncState? = entries.firstOrNull { state ->
            state.storageValue == value
        }
    }

    fun isPendingPushState(): Boolean = when (this) {
        PENDING_CREATE,
        PENDING_UPDATE,
        PENDING_DELETE,
        -> true

        SYNCED,
        CONFLICT,
        -> false
    }
}
