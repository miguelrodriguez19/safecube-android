package com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.action

sealed interface NoteEditorUiAction {
    data class DisplayHintChanged(val value: String) : NoteEditorUiAction

    data class BodyChanged(val value: String) : NoteEditorUiAction

    data object SaveClicked : NoteEditorUiAction

    data object DeleteClicked : NoteEditorUiAction

    data object PublishDraftClicked : NoteEditorUiAction

    data object DiscardDraftClicked : NoteEditorUiAction

    data object RetryReadClicked : NoteEditorUiAction
}
