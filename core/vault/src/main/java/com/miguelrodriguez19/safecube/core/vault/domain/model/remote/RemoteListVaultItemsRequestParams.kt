package com.miguelrodriguez19.safecube.core.vault.domain.model.remote

import java.time.Instant

data class RemoteListVaultItemsRequestParams(
    val createdAfter: Instant? = null,
    val updatedAfter: Instant? = null,
    val type: String? = null,
    val labels: Set<String>? = null,
    val includeDeleted: Boolean = false,
    val limit: Int? = null,
    val order: String? = "DISPLAY_NAME_ASC",
)
