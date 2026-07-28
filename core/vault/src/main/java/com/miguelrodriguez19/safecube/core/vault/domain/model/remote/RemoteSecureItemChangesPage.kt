package com.miguelrodriguez19.safecube.core.vault.domain.model.remote

data class RemoteSecureItemChangesPage(
    val items: List<RemoteSecureItem>,
    val nextCursor: Long,
    val hasMore: Boolean,
)
