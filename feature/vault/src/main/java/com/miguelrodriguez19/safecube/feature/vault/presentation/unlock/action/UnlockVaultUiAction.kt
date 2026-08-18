package com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.action

sealed interface UnlockVaultUiAction {
    data class PassphraseChanged(val value: String) : UnlockVaultUiAction
    data object Submit : UnlockVaultUiAction
    data object Retry : UnlockVaultUiAction
}
