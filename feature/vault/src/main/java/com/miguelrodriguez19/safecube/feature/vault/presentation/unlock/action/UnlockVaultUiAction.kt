package com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.action

sealed interface UnlockVaultUiAction {
    data class PassphraseChanged(val value: String) : UnlockVaultUiAction
    data object ScreenEntered : UnlockVaultUiAction
    data object Submit : UnlockVaultUiAction
    data object Retry : UnlockVaultUiAction
    data object RetryQuickUnlock : UnlockVaultUiAction
    data object EnableQuickUnlock : UnlockVaultUiAction
    data object DeclineQuickUnlock : UnlockVaultUiAction
    data class QuickUnlockPromptSucceeded(val operationId: String) : UnlockVaultUiAction
    data class QuickUnlockPromptCancelled(val operationId: String) : UnlockVaultUiAction
}
