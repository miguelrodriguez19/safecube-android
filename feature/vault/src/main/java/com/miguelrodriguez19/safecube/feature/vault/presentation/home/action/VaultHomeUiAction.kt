package com.miguelrodriguez19.safecube.feature.vault.presentation.home.action

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import java.util.UUID

sealed interface VaultHomeUiAction {
    data object CreatePasswordClicked : VaultHomeUiAction

    data object CreateNoteClicked : VaultHomeUiAction

    data class EditItemClicked(
        val logicalItemId: UUID,
        val itemType: SecureItemType,
    ) : VaultHomeUiAction

    data object DismissEditor : VaultHomeUiAction

    data class PasswordDisplayHintChanged(val value: String) : VaultHomeUiAction

    data class PasswordUsernameChanged(val value: String) : VaultHomeUiAction

    data class PasswordEmailChanged(val value: String) : VaultHomeUiAction

    data class PasswordValueChanged(val value: String) : VaultHomeUiAction

    data class NoteDisplayHintChanged(val value: String) : VaultHomeUiAction

    data class NoteBodyChanged(val value: String) : VaultHomeUiAction

    data object SaveEditorClicked : VaultHomeUiAction

    data object DeleteItemClicked : VaultHomeUiAction
}
