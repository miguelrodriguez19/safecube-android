package com.miguelrodriguez19.safecube.core.vault.domain.repository

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial

interface VaultKeyMaterialLocalRepository {
    fun read(): VaultKeyMaterialLocalReadResult

    fun save(vaultKeyMaterial: VaultKeyMaterial)

    fun get(): VaultKeyMaterial?

    fun clear()
}
