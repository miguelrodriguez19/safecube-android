package com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase

sealed interface ChangeVaultPassphraseResult {
    data object Success : ChangeVaultPassphraseResult

    data class Error(
        val reason: ChangeVaultPassphraseError,
    ) : ChangeVaultPassphraseResult
}
