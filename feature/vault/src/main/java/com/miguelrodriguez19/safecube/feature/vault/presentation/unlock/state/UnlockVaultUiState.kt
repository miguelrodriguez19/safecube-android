package com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.state

import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.isLoading
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.isRetryable
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptRequest
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultLockReason

data class UnlockVaultUiState(
    val passphrase: String = "",
    val passphraseErrorRes: Int? = null,
    val operationState: VaultUiOperationState = VaultUiOperationState.Idle,
    val errorMessageRes: Int? = null,
    val showQuickUnlockOffer: Boolean = false,
    val hasQuickUnlockEnrollment: Boolean = false,
    val canRetryQuickUnlock: Boolean = false,
    val pendingQuickUnlockPrompt: QuickUnlockPromptRequest? = null,
    val quickUnlockPromptPresented: Boolean = false,
    val lockReason: VaultLockReason? = null,
) {
    val isLoading: Boolean
        get() = operationState.isLoading

    val isRetryable: Boolean
        get() = operationState.isRetryable
}
