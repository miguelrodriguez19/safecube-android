package com.miguelrodriguez19.safecube.core.vault.domain.model.quickunlock

/** The only unlock path allowed to create a local quick-unlock enrollment. */
enum class VaultUnlockProvenance {
    None,
    Passphrase,
    RecoveryKey,
    QuickUnlock,
}
