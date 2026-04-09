package com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.state

import java.util.UUID

data class NoteEditorUiState(
    val logicalItemId: UUID? = null,
    val displayHint: String = "",
    val body: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
) {
    val isEditMode: Boolean
        get() = logicalItemId != null
}
