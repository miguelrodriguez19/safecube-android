package com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.event

sealed interface ChangePassphraseUiEvent {
    data object ClearFields : ChangePassphraseUiEvent

    data object NavigateToUnlock : ChangePassphraseUiEvent
}
