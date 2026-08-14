package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.generated.api.VaultKeyMaterialControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.InitVaultKeyMaterialRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.UpdateMasterWrappedKekRequest
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialRemoteRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import retrofit2.Response

@Singleton
class RemoteVaultKeyMaterialDataSource @Inject constructor(
    private val vaultKeyMaterialControllerApi: VaultKeyMaterialControllerApi,
) : VaultKeyMaterialRemoteRepository {
    override suspend fun getKeyMaterial(): VaultKeyMaterialRemoteResult<VaultKeyMaterial> =
        executeSafely {
            executeWithBody { vaultKeyMaterialControllerApi.getVaultKeyMaterial() }
                .mapSuccess { response ->
                    VaultKeyMaterial(
                        accountId = response.accountId,
                        kekEncMaster = response.kekEncMaster,
                        kekEncRecovery = response.kekEncRecovery,
                        kdfAlgorithm = response.kdfAlgorithm,
                        kdfSalt = response.kdfSalt,
                        kdfMemoryKib = response.kdfMemoryKib,
                        kdfIterations = response.kdfIterations,
                        kdfParallelism = response.kdfParallelism,
                        kdfOutputLen = response.kdfOutputLen,
                        cryptoVersion = response.cryptoVersion,
                    )
                }
        }

    override suspend fun initKeyMaterial(
        vaultKeyMaterial: VaultKeyMaterial,
    ): VaultKeyMaterialRemoteResult<Unit> = executeSafely {
        executeWithoutBody {
            vaultKeyMaterialControllerApi.initVaultKeyMaterial(
                InitVaultKeyMaterialRequest(
                    kekEncMaster = vaultKeyMaterial.kekEncMaster,
                    kekEncRecovery = vaultKeyMaterial.kekEncRecovery,
                    kdfAlgorithm = vaultKeyMaterial.kdfAlgorithm,
                    kdfSalt = vaultKeyMaterial.kdfSalt,
                    cryptoVersion = vaultKeyMaterial.cryptoVersion,
                    kdfMemoryKib = vaultKeyMaterial.kdfMemoryKib,
                    kdfIterations = vaultKeyMaterial.kdfIterations,
                    kdfParallelism = vaultKeyMaterial.kdfParallelism,
                    kdfOutputLen = vaultKeyMaterial.kdfOutputLen,
                ),
            )
        }
    }

    override suspend fun updateMasterWrappedKek(
        newKekEncMaster: ByteArray,
    ): VaultKeyMaterialRemoteResult<Unit> = executeSafely {
        executeWithoutBody {
            vaultKeyMaterialControllerApi.updateMasterWrappedKek(
                UpdateMasterWrappedKekRequest(
                    newKekEncMaster = newKekEncMaster,
                ),
            )
        }
    }

    private suspend fun <T> executeWithBody(
        call: suspend () -> Response<T>,
    ): VaultKeyMaterialRemoteResult<T> {
        val response = call()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            VaultKeyMaterialRemoteResult.Success(body)
        } else if (response.isSuccessful) {
            VaultKeyMaterialRemoteResult.Error(
                error = VaultKeyMaterialRemoteError.HttpError(
                    failure = NetworkFailureClassifier.malformedResponse(response.code()),
                ),
            )
        } else {
            VaultKeyMaterialRemoteResult.Error(
                error = mapHttpError(
                    statusCode = response.code(),
                ),
            )
        }
    }

    private suspend fun executeWithoutBody(
        call: suspend () -> Response<Unit>,
    ): VaultKeyMaterialRemoteResult<Unit> {
        val response = call()
        return if (response.isSuccessful) {
            VaultKeyMaterialRemoteResult.Success(Unit)
        } else {
            VaultKeyMaterialRemoteResult.Error(
                error = mapHttpError(
                    statusCode = response.code(),
                ),
            )
        }
    }

    private suspend fun <T> executeSafely(
        execute: suspend () -> VaultKeyMaterialRemoteResult<T>,
    ): VaultKeyMaterialRemoteResult<T> = try {
        execute()
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (throwable: Throwable) {
        VaultKeyMaterialRemoteResult.Error(
            error = VaultKeyMaterialRemoteError.NetworkError(
                NetworkFailureClassifier.fromThrowable(throwable),
            ),
        )
    }

    private fun mapHttpError(
        statusCode: Int,
    ): VaultKeyMaterialRemoteError = when (statusCode) {
        401 -> VaultKeyMaterialRemoteError.Unauthorized
        403 -> VaultKeyMaterialRemoteError.Forbidden
        404 -> VaultKeyMaterialRemoteError.VaultNotInitialized
        409 -> VaultKeyMaterialRemoteError.VaultAlreadyInitialized
        else -> VaultKeyMaterialRemoteError.HttpError(
            failure = NetworkFailureClassifier.fromHttpStatus(statusCode),
        )
    }

    private inline fun <T, R> VaultKeyMaterialRemoteResult<T>.mapSuccess(
        transform: (T) -> R,
    ): VaultKeyMaterialRemoteResult<R> = when (this) {
        is VaultKeyMaterialRemoteResult.Success -> VaultKeyMaterialRemoteResult.Success(
            transform(value),
        )
        is VaultKeyMaterialRemoteResult.Error -> this
    }
}
