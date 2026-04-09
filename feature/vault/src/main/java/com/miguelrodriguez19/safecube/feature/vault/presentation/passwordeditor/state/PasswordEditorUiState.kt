package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.state

import java.util.UUID

data class PasswordEditorUiState(
    val logicalItemId: UUID? = null,
    val displayHint: String = "",
    val username: String = "",
    val password: String = "",
    val websiteUrl: String = "",
    val notes: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val isEditMode: Boolean
        get() = logicalItemId != null
}
