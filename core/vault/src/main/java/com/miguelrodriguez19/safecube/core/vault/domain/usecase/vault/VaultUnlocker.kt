package com.miguelrodriguez19.safecube.core.vault.domain.usecase.vault

import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockResult

interface VaultUnlocker {
    fun unlockWithPassphrase(passphrase: String): VaultUnlockResult

    fun unlockWithRecoveryKey(recoveryKey: ByteArray): VaultUnlockResult
}
