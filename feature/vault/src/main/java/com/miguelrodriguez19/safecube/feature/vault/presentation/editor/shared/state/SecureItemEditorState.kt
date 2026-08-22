package com.miguelrodriguez19.safecube.feature.vault.presentation.editor.shared.state

enum class SecureItemEditorState {
    Loading,
    EditableContent,
    Saving,
    NotFound,
    VaultLocked,
    CorruptedPayload,
    InconsistentOfficialDraft,
    LocalStorageFailure,
}
