package com.miguelrodriguez19.safecube.core.vault.domain.repository

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial

sealed interface VaultKeyMaterialLocalReadResult {
    data object Absent : VaultKeyMaterialLocalReadResult

    data class Present(
        val value: VaultKeyMaterial,
    ) : VaultKeyMaterialLocalReadResult

    data object Corrupted : VaultKeyMaterialLocalReadResult
}
