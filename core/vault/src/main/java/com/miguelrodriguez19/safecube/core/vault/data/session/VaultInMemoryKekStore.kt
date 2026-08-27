package com.miguelrodriguez19.safecube.core.vault.data.session

import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultKekProvider
import com.miguelrodriguez19.safecube.core.vault.domain.model.quickunlock.VaultUnlockProvenance
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VaultInMemoryKekStore @Inject constructor() : VaultKekProvider, QuickUnlockKeyMaterialAccess {
    private var kek: ByteArray? = null
    private var unlockProvenance = VaultUnlockProvenance.None

    fun replace(
        newKek: ByteArray,
        provenance: VaultUnlockProvenance = VaultUnlockProvenance.Passphrase,
    ) {
        clear()
        kek = newKek.copyOf()
        unlockProvenance = provenance
    }

    override fun snapshot(): ByteArray? = kek?.copyOf()

    internal fun currentReference(): ByteArray? = kek

    override fun currentForEnrollment(): ByteArray? = kek?.copyOf()

    override fun provenance(): VaultUnlockProvenance = unlockProvenance

    override fun replaceAfterQuickUnlock(kek: ByteArray) {
        replace(kek, VaultUnlockProvenance.QuickUnlock)
    }

    fun clear() {
        kek?.fill(0)
        kek = null
        unlockProvenance = VaultUnlockProvenance.None
    }
}
