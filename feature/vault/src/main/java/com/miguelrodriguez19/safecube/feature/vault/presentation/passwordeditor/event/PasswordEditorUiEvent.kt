package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.event

sealed interface PasswordEditorUiEvent {
    data object NavigateBack : PasswordEditorUiEvent
}
