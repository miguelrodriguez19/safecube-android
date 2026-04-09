package com.miguelrodriguez19.safecube.feature.vault.presentation.home.state

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import java.time.Instant
import java.util.UUID

data class VaultHomeUiState(
    val items: List<VaultItemSummaryUiModel> = emptyList(),
)

data class VaultItemSummaryUiModel(
    val logicalItemId: UUID,
    val displayHint: String,
    val itemType: SecureItemType,
    val updatedAt: Instant,
)
