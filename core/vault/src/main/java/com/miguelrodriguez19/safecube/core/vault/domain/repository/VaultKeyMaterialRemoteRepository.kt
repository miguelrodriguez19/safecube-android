package com.miguelrodriguez19.safecube.core.vault.domain.repository

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.MasterWrapperUpdateConfirmation
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VersionedVaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult

interface VaultKeyMaterialRemoteRepository {
    suspend fun getKeyMaterial(): VaultKeyMaterialRemoteResult<VaultKeyMaterial>

    suspend fun getVersionedKeyMaterial(): VaultKeyMaterialRemoteResult<VersionedVaultKeyMaterial>

    suspend fun initKeyMaterial(
        vaultKeyMaterial: VaultKeyMaterial,
    ): VaultKeyMaterialRemoteResult<Unit>

    suspend fun updateMasterWrappedKek(
        newKekEncMaster: ByteArray,
        ifMatch: String,
    ): VaultKeyMaterialRemoteResult<MasterWrapperUpdateConfirmation>
}
