package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.state

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
