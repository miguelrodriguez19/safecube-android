package com.miguelrodriguez19.safecube.core.vault.domain.model.unlock

import com.miguelrodriguez19.safecube.core.vault.domain.model.UnlockedKeyring

sealed interface VaultUnlockResult {
    data class Unlocked(
        val keyring: UnlockedKeyring,
    ) : VaultUnlockResult

    data class Error(
        val reason: VaultUnlockError,
    ) : VaultUnlockResult
}
