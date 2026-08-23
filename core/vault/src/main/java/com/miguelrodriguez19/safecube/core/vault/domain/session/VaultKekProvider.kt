package com.miguelrodriguez19.safecube.core.vault.domain.session

interface VaultKekProvider {
    fun snapshot(): ByteArray?
}
