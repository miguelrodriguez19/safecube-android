package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.password.event

sealed interface PasswordEditorUiEvent {
    data object NavigateBack : PasswordEditorUiEvent

    data object NavigateToUnlock : PasswordEditorUiEvent
}
