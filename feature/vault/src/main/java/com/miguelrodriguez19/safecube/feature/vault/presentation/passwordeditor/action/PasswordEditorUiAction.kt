package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.action

sealed interface PasswordEditorUiAction {
    data class DisplayHintChanged(val value: String) : PasswordEditorUiAction

    data class UsernameChanged(val value: String) : PasswordEditorUiAction

    data class PasswordChanged(val value: String) : PasswordEditorUiAction

    data class WebsiteUrlChanged(val value: String) : PasswordEditorUiAction

    data class NotesChanged(val value: String) : PasswordEditorUiAction

    data object SaveClicked : PasswordEditorUiAction

    data object DeleteClicked : PasswordEditorUiAction

    data object PublishDraftClicked : PasswordEditorUiAction

    data object DiscardDraftClicked : PasswordEditorUiAction
}
