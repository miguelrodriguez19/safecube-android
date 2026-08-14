package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.generated.api.VaultControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.CreateSecureItemRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.ErrorResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.UpdateSecureItemRequest
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemChangesPage
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteCreateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteDeleteSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteCreateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteDeleteSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteListVaultItemsRequestParams
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteUpdateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteUpdateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import retrofit2.Response

@Singleton
class RemoteSecureItemDataSource @Inject constructor(
    private val vaultControllerApi: VaultControllerApi,
    private val json: Json,
) : SecureItemRemoteRepository {
    override suspend fun listVaultItems(
        requestParams: RemoteListVaultItemsRequestParams,
    ): SecureItemRemoteResult<List<RemoteSecureItemSummary>> = executeSafely {
        executeWithBody {
            vaultControllerApi.listVaultItems(
                createdAfter = requestParams.createdAfter,
                updatedAfter = requestParams.updatedAfter,
                type = requestParams.type,
                labels = requestParams.labels,
                includeDeleted = requestParams.includeDeleted,
                limit = requestParams.limit,
                order = requestParams.order,
            )
        }.mapSuccess { response ->
            response.items.map { item ->
                RemoteSecureItemSummary(
                    itemId = item.itemId,
                    itemType = item.itemType,
                    schemaVersion = item.schemaVersion,
                    displayHint = item.displayHint,
                    payloadVersion = item.payloadVersion,
                    itemRevision = item.itemRevision,
                    changeSequence = item.changeSequence,
                    updatedAt = item.updatedAt,
                    deletedAt = item.deletedAt,
                )
            }
        }
    }

    override suspend fun getVaultItem(
        remoteItemId: UUID,
    ): SecureItemRemoteResult<RemoteSecureItem> = executeSafely {
        executeWithBody { vaultControllerApi.getVaultItem(remoteItemId) }.mapSuccess { response ->
            RemoteSecureItem(
                itemId = response.itemId,
                itemType = response.itemType,
                schemaVersion = response.schemaVersion,
                displayHint = response.displayHint,
                payload = response.payload,
                payloadVersion = response.payloadVersion,
                itemRevision = response.itemRevision,
                changeSequence = response.changeSequence,
                updatedAt = response.updatedAt,
                deletedAt = response.deletedAt,
            )
        }
    }

    override suspend fun listVaultItemChanges(
        after: Long,
        limit: Int,
    ): SecureItemRemoteResult<RemoteSecureItemChangesPage> = executeSafely {
        executeWithBody {
            vaultControllerApi.listVaultItemChanges(
                after = after,
                limit = limit,
            )
        }.mapSuccess { response ->
            RemoteSecureItemChangesPage(
                items = response.items.map { item ->
                    RemoteSecureItem(
                        itemId = item.itemId,
                        itemType = item.itemType,
                        schemaVersion = item.schemaVersion,
                        displayHint = item.displayHint,
                        payload = item.payload,
                        payloadVersion = item.payloadVersion,
                        itemRevision = item.itemRevision,
                        changeSequence = item.changeSequence,
                        updatedAt = item.updatedAt,
                        deletedAt = item.deletedAt,
                    )
                },
                nextCursor = response.nextCursor,
                hasMore = response.hasMore,
            )
        }
    }

    override suspend fun createVaultItem(
        request: RemoteCreateSecureItemRequest,
    ): SecureItemRemoteResult<RemoteCreateSecureItemResult> = executeSafely {
        executeWithBody {
            vaultControllerApi.createVaultItem(
                idempotencyKey = request.mutationId,
                createSecureItemRequest = CreateSecureItemRequest(
                    itemType = request.itemType,
                    schemaVersion = request.schemaVersion,
                    displayHint = request.displayHint,
                    payload = request.payload,
                    payloadVersion = request.payloadVersion,
                ),
            )
        }.mapSuccess { response ->
            RemoteCreateSecureItemResult(
                itemId = response.itemId,
                mutationId = response.mutationId,
                payloadVersion = response.payloadVersion,
                itemRevision = response.itemRevision,
                changeSequence = response.changeSequence,
                updatedAt = response.updatedAt,
            )
        }
    }

    override suspend fun updateVaultItem(
        remoteItemId: UUID,
        request: RemoteUpdateSecureItemRequest,
    ): SecureItemRemoteResult<RemoteUpdateSecureItemResult> = executeSafely {
        executeWithBody {
            vaultControllerApi.updateVaultItem(
                itemId = remoteItemId,
                idempotencyKey = request.mutationId,
                ifMatch = request.baseItemRevision.toIfMatchHeader(),
                updateSecureItemRequest = UpdateSecureItemRequest(
                    itemType = request.itemType,
                    schemaVersion = request.schemaVersion,
                    displayHint = request.displayHint,
                    payload = request.payload,
                    payloadVersion = request.payloadVersion,
                ),
            )
        }.mapSuccess { response ->
            RemoteUpdateSecureItemResult(
                itemId = response.itemId,
                mutationId = response.mutationId,
                payloadVersion = response.payloadVersion,
                itemRevision = response.itemRevision,
                changeSequence = response.changeSequence,
                updatedAt = response.updatedAt,
            )
        }
    }

    override suspend fun deleteVaultItem(
        remoteItemId: UUID,
        request: RemoteDeleteSecureItemRequest,
    ): SecureItemRemoteResult<RemoteDeleteSecureItemResult> = executeSafely {
        executeWithBody {
            vaultControllerApi.deleteVaultItem(
                itemId = remoteItemId,
                idempotencyKey = request.mutationId,
                ifMatch = request.baseItemRevision.toIfMatchHeader(),
            )
        }.mapSuccess { response ->
            RemoteDeleteSecureItemResult(
                itemId = response.itemId,
                mutationId = response.mutationId,
                payloadVersion = response.payloadVersion,
                itemRevision = response.itemRevision,
                changeSequence = response.changeSequence,
                deletedAt = response.deletedAt,
            )
        }
    }

    private suspend fun <T> executeWithBody(
        call: suspend () -> Response<T>,
    ): SecureItemRemoteResult<T> {
        val response = call()
        val body = response.body()
        return if (response.isSuccessful && body != null) {
            SecureItemRemoteResult.Success(body)
        } else if (response.isSuccessful) {
            SecureItemRemoteResult.Error(
                error = SecureItemRemoteError.HttpError(
                    failure = NetworkFailureClassifier.malformedResponse(response.code()),
                ),
            )
        } else {
            SecureItemRemoteResult.Error(
                error = mapHttpError(
                    statusCode = response.code(),
                    errorBody = runCatching { response.errorBody()?.string() }.getOrNull(),
                ),
            )
        }
    }

    private suspend fun <T> executeSafely(
        execute: suspend () -> SecureItemRemoteResult<T>,
    ): SecureItemRemoteResult<T> = try {
        execute()
    } catch (cancellationException: CancellationException) {
        throw cancellationException
    } catch (throwable: Throwable) {
        SecureItemRemoteResult.Error(
            error = SecureItemRemoteError.NetworkError(
                NetworkFailureClassifier.fromThrowable(throwable),
            ),
        )
    }

    private fun mapHttpError(
        statusCode: Int,
        errorBody: String?,
    ): SecureItemRemoteError = when (statusCode) {
        400 -> SecureItemRemoteError.ValidationFailed(
            fields = decodeErrorResponse(errorBody)?.fields.orEmpty(),
        )
        401 -> SecureItemRemoteError.Unauthorized
        403 -> SecureItemRemoteError.Forbidden
        404 -> SecureItemRemoteError.ItemNotFound
        409 -> SecureItemRemoteError.IdempotencyConflict
        412 -> SecureItemRemoteError.PreconditionFailed
        428 -> SecureItemRemoteError.PreconditionRequired
        else -> SecureItemRemoteError.HttpError(
            failure = NetworkFailureClassifier.fromHttpStatus(statusCode),
        )
    }

    private fun decodeErrorResponse(errorBody: String?): ErrorResponse? =
        errorBody?.let { body ->
            runCatching {
                json.decodeFromString<ErrorResponse>(body)
            }.getOrNull()
        }

    private inline fun <T, R> SecureItemRemoteResult<T>.mapSuccess(
        transform: (T) -> R,
    ): SecureItemRemoteResult<R> = when (this) {
        is SecureItemRemoteResult.Success -> SecureItemRemoteResult.Success(
            transform(value),
        )

        is SecureItemRemoteResult.Error -> this
    }

    private fun Long.toIfMatchHeader(): String = "\"$this\""

}
