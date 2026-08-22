package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.note.event

sealed interface NoteEditorUiEvent {
    data object NavigateBack : NoteEditorUiEvent

    data object NavigateToUnlock : NoteEditorUiEvent
}
