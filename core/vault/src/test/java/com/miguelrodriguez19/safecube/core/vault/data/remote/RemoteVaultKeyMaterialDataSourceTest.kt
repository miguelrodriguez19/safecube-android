package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.generated.api.VaultKeyMaterialControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.InitVaultKeyMaterialRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.UpdateMasterWrappedKekRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.VaultKeyMaterialResponse
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VaultKeyMaterialRemoteResult
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class RemoteVaultKeyMaterialDataSourceTest {
    @Test
    fun `getKeyMaterial when successful response body is missing then maps to http error`() =
        runBlocking {
            val api = object : VaultKeyMaterialControllerApi {
                override suspend fun getVaultKeyMaterial(): Response<VaultKeyMaterialResponse> =
                    Response.success(null)

                override suspend fun initVaultKeyMaterial(
                    initVaultKeyMaterialRequest: InitVaultKeyMaterialRequest,
                ): Response<Unit> = Response.success(Unit)

                override suspend fun updateMasterWrappedKek(
                    updateMasterWrappedKekRequest: UpdateMasterWrappedKekRequest,
                ): Response<Unit> = Response.success(Unit)
            }
            val target = RemoteVaultKeyMaterialDataSource(
                vaultKeyMaterialControllerApi = api,
            )

            val result = target.getKeyMaterial()

            assertEquals(
                VaultKeyMaterialRemoteResult.Error(
                    VaultKeyMaterialRemoteError.HttpError(
                        statusCode = 200,
                        errorBody = "Missing response body on successful response.",
                    ),
                ),
                result,
            )
        }

    @Test
    fun `getKeyMaterial when api throws then maps to network error`() = runBlocking {
        val api = object : VaultKeyMaterialControllerApi {
            override suspend fun getVaultKeyMaterial(): Response<VaultKeyMaterialResponse> {
                throw IllegalStateException("boom")
            }

            override suspend fun initVaultKeyMaterial(
                initVaultKeyMaterialRequest: InitVaultKeyMaterialRequest,
            ): Response<Unit> = Response.success(Unit)

            override suspend fun updateMasterWrappedKek(
                updateMasterWrappedKekRequest: UpdateMasterWrappedKekRequest,
            ): Response<Unit> = Response.success(Unit)
        }
        val target = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = api,
        )

        val result = target.getKeyMaterial()

        assertTrue(result is VaultKeyMaterialRemoteResult.Error)
        assertTrue((result as VaultKeyMaterialRemoteResult.Error).error is VaultKeyMaterialRemoteError.NetworkError)
    }
}
