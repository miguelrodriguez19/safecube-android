package com.miguelrodriguez19.safecube.feature.vault.presentation.create.action

sealed interface CreateVaultUiAction {
    data class PassphraseChanged(val value: String) : CreateVaultUiAction
    data object Submit : CreateVaultUiAction
    data object Retry : CreateVaultUiAction
}
