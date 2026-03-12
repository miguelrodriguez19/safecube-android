package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.generated.model.InitVaultKeyMaterialRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.UpdateMasterWrappedKekRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.VaultKeyMaterialResponse

interface VaultKeyMaterialDataSource {
    suspend fun getKeyMaterial(): VaultKeyMaterialRemoteResult<VaultKeyMaterialResponse>

    suspend fun initKeyMaterial(
        request: InitVaultKeyMaterialRequest,
    ): VaultKeyMaterialRemoteResult<Unit>

    suspend fun updateMasterWrappedKek(
        request: UpdateMasterWrappedKekRequest,
    ): VaultKeyMaterialRemoteResult<Unit>
}
