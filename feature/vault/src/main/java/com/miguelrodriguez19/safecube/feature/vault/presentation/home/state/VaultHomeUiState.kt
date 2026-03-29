package com.miguelrodriguez19.safecube.feature.vault.presentation.home.state

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import java.time.Instant
import java.util.UUID

data class VaultHomeUiState(
    val items: List<VaultItemSummaryUiModel> = emptyList(),
    val screenMessage: String? = null,
    val editor: VaultEditorUiState? = null,
)

data class VaultItemSummaryUiModel(
    val logicalItemId: UUID,
    val displayHint: String,
    val itemType: SecureItemType,
    val updatedAt: Instant,
)

sealed interface VaultEditorUiState {
    val logicalItemId: UUID?
    val displayHint: String
    val isLoading: Boolean
    val isSaving: Boolean
    val errorMessage: String?

    data class Password(
        override val logicalItemId: UUID? = null,
        override val displayHint: String = "",
        val username: String = "",
        val email: String = "",
        val password: String = "",
        override val isLoading: Boolean = false,
        override val isSaving: Boolean = false,
        override val errorMessage: String? = null,
    ) : VaultEditorUiState

    data class Note(
        override val logicalItemId: UUID? = null,
        override val displayHint: String = "",
        val body: String = "",
        override val isLoading: Boolean = false,
        override val isSaving: Boolean = false,
        override val errorMessage: String? = null,
    ) : VaultEditorUiState
}
