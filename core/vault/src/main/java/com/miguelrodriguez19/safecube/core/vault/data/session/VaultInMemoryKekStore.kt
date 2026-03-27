package com.miguelrodriguez19.safecube.core.vault.data.session

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class VaultInMemoryKekStore @Inject constructor() {
    private var kek: ByteArray? = null

    fun replace(newKek: ByteArray) {
        clear()
        kek = newKek.copyOf()
    }

    fun snapshot(): ByteArray? = kek?.copyOf()

    internal fun currentReference(): ByteArray? = kek

    fun clear() {
        kek?.fill(0)
        kek = null
    }
}
