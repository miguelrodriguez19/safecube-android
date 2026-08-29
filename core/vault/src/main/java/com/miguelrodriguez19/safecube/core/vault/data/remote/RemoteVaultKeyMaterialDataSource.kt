package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.network.generated.api.VaultKeyMaterialControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.InitVaultKeyMaterialRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.UpdateMasterWrappedKekRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.VaultKeyMaterialResponse
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.MasterWrapperUpdateConfirmation
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.VersionedVaultKeyMaterial
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
            val response = vaultKeyMaterialControllerApi.getVaultKeyMaterial()
            if (!response.isSuccessful) {
                VaultKeyMaterialRemoteResult.Error(
                    error = mapHttpError(response.code()),
                )
            } else {
                response.body()?.let { body ->
                    VaultKeyMaterialRemoteResult.Success(body.toDomainModel())
                } ?: VaultKeyMaterialRemoteResult.Error(
                    error = VaultKeyMaterialRemoteError.ContractViolation,
                )
            }
        }

    override suspend fun getVersionedKeyMaterial(): VaultKeyMaterialRemoteResult<VersionedVaultKeyMaterial> =
        executeSafely {
            val response = vaultKeyMaterialControllerApi.getVaultKeyMaterial()
            if (!response.isSuccessful) {
                VaultKeyMaterialRemoteResult.Error(
                    error = mapHttpError(response.code()),
                )
            } else {
                val body = response.body()
                val etag = response.strongOpaqueEtagOrNull()
                if (body == null || etag == null) {
                    VaultKeyMaterialRemoteResult.Error(
                        error = VaultKeyMaterialRemoteError.ContractViolation,
                    )
                } else {
                    VaultKeyMaterialRemoteResult.Success(
                        VersionedVaultKeyMaterial(
                            material = body.toDomainModel(),
                            etag = etag,
                        ),
                    )
                }
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
        ifMatch: String,
    ): VaultKeyMaterialRemoteResult<MasterWrapperUpdateConfirmation> = executeSafely {
        val response = vaultKeyMaterialControllerApi.updateMasterWrappedKek(
            ifMatch = ifMatch,
            updateMasterWrappedKekRequest = UpdateMasterWrappedKekRequest(
                newKekEncMaster = newKekEncMaster,
            ),
        )
        if (!response.isSuccessful) {
            VaultKeyMaterialRemoteResult.Error(
                error = mapHttpError(response.code()),
            )
        } else {
            val etag = response.strongOpaqueEtagOrNull()
            if (etag == null) {
                VaultKeyMaterialRemoteResult.Error(
                    error = VaultKeyMaterialRemoteError.ContractViolation,
                )
            } else {
                VaultKeyMaterialRemoteResult.Success(
                    MasterWrapperUpdateConfirmation(etag = etag),
                )
            }
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
        412 -> VaultKeyMaterialRemoteError.MasterKeyRevisionConflict
        428 -> VaultKeyMaterialRemoteError.PreconditionRequired
        else -> VaultKeyMaterialRemoteError.HttpError(
            failure = NetworkFailureClassifier.fromHttpStatus(statusCode),
        )
    }

    private fun VaultKeyMaterialResponse.toDomainModel(): VaultKeyMaterial = VaultKeyMaterial(
        accountId = accountId,
        kekEncMaster = kekEncMaster,
        kekEncRecovery = kekEncRecovery,
        kdfAlgorithm = kdfAlgorithm,
        kdfSalt = kdfSalt,
        kdfMemoryKib = kdfMemoryKib,
        kdfIterations = kdfIterations,
        kdfParallelism = kdfParallelism,
        kdfOutputLen = kdfOutputLen,
        cryptoVersion = cryptoVersion,
    )

    private fun Response<*>.strongOpaqueEtagOrNull(): String? = headers()
        .values(ETAG_HEADER)
        .singleOrNull()
        ?.takeIf(::isStrongOpaqueEtag)

    private fun isStrongOpaqueEtag(etag: String): Boolean =
        etag.length >= 2 &&
            !etag.startsWith(WEAK_ETAG_PREFIX) &&
            etag != WILDCARD_ETAG &&
            etag.first() == '"' &&
            etag.last() == '"' &&
            !etag.contains(',') &&
            !etag.contains('\r') &&
            !etag.contains('\n')

    private companion object {
        const val ETAG_HEADER = "ETag"
        const val WEAK_ETAG_PREFIX = "W/"
        const val WILDCARD_ETAG = "*"
    }
}
