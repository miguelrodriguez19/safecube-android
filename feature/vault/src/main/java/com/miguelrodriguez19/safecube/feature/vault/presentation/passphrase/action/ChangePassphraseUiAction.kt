package com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.action

sealed interface ChangePassphraseUiAction {
    data object FieldsChanged : ChangePassphraseUiAction

    data class Submit(
        val currentPassphrase: String,
        val newPassphrase: String,
        val confirmation: String,
    ) : ChangePassphraseUiAction
}
