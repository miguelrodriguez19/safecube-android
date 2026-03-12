package com.miguelrodriguez19.safecube.core.vault.domain.model.unlock

sealed interface VaultUnlockError {
    data object KeyMaterialUnavailable : VaultUnlockError

    data object InvalidCredential : VaultUnlockError

    data object InvalidCachedKeyMaterial : VaultUnlockError
}
