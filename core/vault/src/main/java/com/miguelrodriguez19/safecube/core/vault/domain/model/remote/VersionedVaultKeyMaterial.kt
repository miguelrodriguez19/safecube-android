package com.miguelrodriguez19.safecube.core.vault.domain.model.remote

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial

data class VersionedVaultKeyMaterial(
    val material: VaultKeyMaterial,
    val etag: String,
)
