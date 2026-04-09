package com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.event

sealed interface NoteEditorUiEvent {
    data object NavigateBack : NoteEditorUiEvent
}
