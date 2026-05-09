package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import java.util.UUID

data class VaultItemDraftSummary(
    val logicalItemId: UUID,
    val draftType: SecureItemDraftType,
    val lastPublishError: String?,
)
