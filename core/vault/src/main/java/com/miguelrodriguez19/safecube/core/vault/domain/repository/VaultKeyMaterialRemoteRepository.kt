package com.miguelrodriguez19.safecube.core.vault.domain.repository

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VaultKeyMaterialRemoteResult

interface VaultKeyMaterialRemoteRepository {
    suspend fun getKeyMaterial(): VaultKeyMaterialRemoteResult<VaultKeyMaterial>

    suspend fun initKeyMaterial(
        vaultKeyMaterial: VaultKeyMaterial,
    ): VaultKeyMaterialRemoteResult<Unit>

    suspend fun updateMasterWrappedKek(
        newKekEncMaster: ByteArray,
    ): VaultKeyMaterialRemoteResult<Unit>
}
