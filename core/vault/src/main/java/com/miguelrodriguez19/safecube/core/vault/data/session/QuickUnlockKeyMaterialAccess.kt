package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.model.quickunlock.VaultUnlockProvenance

/** Internal KEK boundary. Public quick-unlock APIs never receive or return this material. */
internal interface QuickUnlockKeyMaterialAccess {
    fun currentForEnrollment(): ByteArray?

    fun provenance(): VaultUnlockProvenance

    fun replaceAfterQuickUnlock(kek: ByteArray)
}
