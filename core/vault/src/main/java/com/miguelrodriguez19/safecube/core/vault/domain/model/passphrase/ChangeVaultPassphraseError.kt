package com.miguelrodriguez19.safecube.core.vault.domain.model.passphrase

import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError

sealed interface ChangeVaultPassphraseError {
    val retryDecision: RetryDecision
    val requiresConnection: Boolean

    data class InvalidVaultState(
        val currentState: VaultState,
    ) : ChangeVaultPassphraseError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
        override val requiresConnection: Boolean = false
    }

    data object LocalKeyMaterialUnavailable : ChangeVaultPassphraseError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
        override val requiresConnection: Boolean = false
    }

    data object InvalidLocalKeyMaterial : ChangeVaultPassphraseError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
        override val requiresConnection: Boolean = false
    }

    data object ActiveKekUnavailable : ChangeVaultPassphraseError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
        override val requiresConnection: Boolean = false
    }

    data object InvalidCurrentPassphrase : ChangeVaultPassphraseError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
        override val requiresConnection: Boolean = false
    }

    data object InvalidNewPassphrase : ChangeVaultPassphraseError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
        override val requiresConnection: Boolean = false
    }

    data object CryptoFailure : ChangeVaultPassphraseError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
        override val requiresConnection: Boolean = false
    }

    data class RemoteFailure(
        val error: VaultKeyMaterialRemoteError,
    ) : ChangeVaultPassphraseError {
        override val retryDecision: RetryDecision = error.failure.decision
        override val requiresConnection: Boolean = retryDecision == RetryDecision.Retryable
    }

    data object RemoteChangeNotApplied : ChangeVaultPassphraseError {
        override val retryDecision: RetryDecision = RetryDecision.Retryable
        override val requiresConnection: Boolean = true
    }

    data object ConcurrentRemoteChange : ChangeVaultPassphraseError {
        override val retryDecision: RetryDecision = RetryDecision.Terminal
        override val requiresConnection: Boolean = false
    }

    data object ReconciliationRequired : ChangeVaultPassphraseError {
        override val retryDecision: RetryDecision = RetryDecision.Retryable
        override val requiresConnection: Boolean = true
    }
}
