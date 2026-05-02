package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.generated.api.VaultControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.CreateSecureItemRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.CreateSecureItemResult
import com.miguelrodriguez19.safecube.core.network.generated.model.DeleteSecureItemResult
import com.miguelrodriguez19.safecube.core.network.generated.model.ListSecureItemsResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.SecureItemResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.SecureItemSummaryResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.UpdateSecureItemRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.UpdateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteCreateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteListVaultItemsRequestParams
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteUpdateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteCreateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.slot
import java.time.OffsetDateTime
import java.util.UUID
import java.util.concurrent.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.time.Instant
import java.time.temporal.ChronoUnit

class RemoteSecureItemDataSourceTest {
    private val vaultControllerApi = mockk<VaultControllerApi>()
    private val target = RemoteSecureItemDataSource(vaultControllerApi)

    @Test
    fun `listVaultItems when successful response contains body then maps summaries`() =
        runBlocking {
            val itemId = UUID.fromString("e93c52d6-c95f-48ef-9fbf-6bc012882edf")
            val createdAfter = Instant.now()
            val updatedAfter = createdAfter.plus(1, ChronoUnit.HOURS)
            coEvery {
                vaultControllerApi.listVaultItems(
                    createdAfter = createdAfter,
                    updatedAfter = updatedAfter,
                    type = "NOTE",
                    labels = setOf("work"),
                    includeDeleted = true,
                    limit = 25,
                    order = "UPDATED_AT_DESC",
                )
            } returns Response.success(
                ListSecureItemsResponse(
                    items = listOf(
                        SecureItemSummaryResponse(
                            itemId = itemId,
                            itemType = "NOTE",
                            schemaVersion = 1,
                            displayHint = "Work note",
                            payloadVersion = 3,
                            updatedAt = updatedAfter.plus(1, ChronoUnit.HOURS),
                            deletedAt = null,
                        ),
                    ),
                ),
            )

            val result = target.listVaultItems(
                requestParams = RemoteListVaultItemsRequestParams(
                    createdAfter = createdAfter,
                    updatedAfter = updatedAfter,
                    type = "NOTE",
                    labels = setOf("work"),
                    includeDeleted = true,
                    limit = 25,
                    order = "UPDATED_AT_DESC",
                ),
            )

            assertTrue(result is SecureItemRemoteResult.Success)
            val value = (result as SecureItemRemoteResult.Success).value
            assertEquals(1, value.size)
            assertEquals(itemId, value.first().itemId)
            assertEquals("NOTE", value.first().itemType)
            assertEquals(3, value.first().payloadVersion)
            coVerify(exactly = 1) {
                vaultControllerApi.listVaultItems(
                    createdAfter = createdAfter,
                    updatedAfter = updatedAfter,
                    type = "NOTE",
                    labels = setOf("work"),
                    includeDeleted = true,
                    limit = 25,
                    order = "UPDATED_AT_DESC",
                )
            }
            confirmVerified(vaultControllerApi)
        }

    @Test
    fun `getVaultItem when status is 404 then maps to item not found`() = runBlocking {
        val remoteItemId = UUID.fromString("9f2a5b91-168f-4189-9eca-17e5ff8dbd1d")
        coEvery { vaultControllerApi.getVaultItem(remoteItemId) } returns Response.error(
            404,
            ResponseBody.create(null, """{"error":"Not found"}"""),
        )

        val result = target.getVaultItem(remoteItemId)

        assertEquals(
            SecureItemRemoteResult.Error(SecureItemRemoteError.ItemNotFound),
            result,
        )
        coVerify(exactly = 1) { vaultControllerApi.getVaultItem(remoteItemId) }
        confirmVerified(vaultControllerApi)
    }

    @Test
    fun `createVaultItem when successful then forwards request and maps result`() = runBlocking {
        val requestSlot = slot<CreateSecureItemRequest>()
        val remoteRequest = RemoteCreateSecureItemRequest(
            itemType = "PASSWORD",
            schemaVersion = 1,
            displayHint = "Github",
            payload = byteArrayOf(1, 2, 3),
        )
        val itemId = UUID.randomUUID()
        val createdAt = Instant.now()
        coEvery { vaultControllerApi.createVaultItem(capture(requestSlot)) } returns Response.success(
            CreateSecureItemResult(
                itemId = itemId,
                createdAt = createdAt,
            ),
        )

        val result = target.createVaultItem(remoteRequest)

        assertEquals(
            SecureItemRemoteResult.Success(
                RemoteCreateSecureItemResult(
                    itemId = itemId,
                    createdAt = createdAt,
                ),
            ),
            result,
        )
        assertTrue(requestSlot.isCaptured)
        assertEquals("PASSWORD", requestSlot.captured.itemType)
        assertEquals(1, requestSlot.captured.schemaVersion)
        assertEquals("Github", requestSlot.captured.displayHint)
        assertArrayEquals(byteArrayOf(1, 2, 3), requestSlot.captured.payload)
        coVerify(exactly = 1) { vaultControllerApi.createVaultItem(any()) }
        confirmVerified(vaultControllerApi)
    }

    @Test
    fun `updateVaultItem when status is 409 then maps to conflict`() = runBlocking {
        val remoteItemId = UUID.fromString("a9f0e2cf-c43e-4531-9563-bb80ce71b486")
        val remoteRequest = RemoteUpdateSecureItemRequest(
            itemType = "NOTE",
            schemaVersion = 1,
            displayHint = "Updated",
            payload = byteArrayOf(4, 5, 6),
        )
        coEvery { vaultControllerApi.updateVaultItem(remoteItemId, any()) } returns Response.error(
            409,
            ResponseBody.create(null, """{"error":"Conflict"}"""),
        )

        val result = target.updateVaultItem(remoteItemId, remoteRequest)

        assertEquals(
            SecureItemRemoteResult.Error(SecureItemRemoteError.Conflict),
            result,
        )
        coVerify(exactly = 1) { vaultControllerApi.updateVaultItem(remoteItemId, any()) }
        confirmVerified(vaultControllerApi)
    }

    @Test
    fun `deleteVaultItem when status is 403 then maps to unauthorized`() = runBlocking {
        val remoteItemId = UUID.fromString("e16f7f85-3b25-4b95-b194-a3c365628e34")
        coEvery { vaultControllerApi.deleteVaultItem(remoteItemId) } returns Response.error(
            403,
            ResponseBody.create(null, """{"error":"Forbidden"}"""),
        )

        val result = target.deleteVaultItem(remoteItemId)

        assertEquals(
            SecureItemRemoteResult.Error(SecureItemRemoteError.Unauthorized),
            result,
        )
        coVerify(exactly = 1) { vaultControllerApi.deleteVaultItem(remoteItemId) }
        confirmVerified(vaultControllerApi)
    }

    @Test
    fun `createVaultItem when api throws cancellation then rethrows cancellation`() {
        val api = object : VaultControllerApi {
            override suspend fun createVaultItem(createSecureItemRequest: CreateSecureItemRequest): Response<CreateSecureItemResult> {
                throw CancellationException("cancelled")
            }

            override suspend fun deleteVaultItem(itemId: UUID): Response<DeleteSecureItemResult> =
                Response.success(
                    DeleteSecureItemResult(itemId = itemId, deletedAt = Instant.now())
                )

            override suspend fun getVaultItem(itemId: UUID): Response<SecureItemResponse> =
                Response.success(
                    SecureItemResponse(
                        itemId = itemId,
                        itemType = "NOTE",
                        schemaVersion = 1,
                        displayHint = "hint",
                        payload = byteArrayOf(1),
                        payloadVersion = 1,
                        updatedAt = Instant.now(),
                        deletedAt = null,
                    ),
                )

            override suspend fun listVaultItems(
                createdAfter: Instant?,
                updatedAfter: Instant?,
                type: String?,
                labels: @JvmSuppressWildcards Set<String>?,
                includeDeleted: Boolean?,
                limit: Int?,
                order: String?
            ): Response<ListSecureItemsResponse> =
                Response.success(ListSecureItemsResponse(emptyList()))

            override suspend fun updateVaultItem(
                itemId: UUID,
                updateSecureItemRequest: UpdateSecureItemRequest,
            ): Response<UpdateSecureItemResult> = Response.success(
                UpdateSecureItemResult(
                    itemId = itemId,
                    payloadVersion = 2,
                    updatedAt = Instant.now(),
                ),
            )
        }
        val cancellationTarget = RemoteSecureItemDataSource(api)

        assertThrows(CancellationException::class.java) {
            runBlocking {
                cancellationTarget.createVaultItem(
                    RemoteCreateSecureItemRequest(
                        itemType = "NOTE",
                        schemaVersion = 1,
                        displayHint = "x",
                        payload = byteArrayOf(1),
                    ),
                )
            }
        }
    }

    @Test
    fun `getVaultItem when status is generic then returns http error with code and body`() =
        runBlocking {
            val remoteItemId = UUID.fromString("7cb520f1-157f-4ab0-a762-4f33ce4f43b0")
            coEvery { vaultControllerApi.getVaultItem(remoteItemId) } returns Response.error(
                500,
                ResponseBody.create(null, """{"error":"boom"}"""),
            )

            val result = target.getVaultItem(remoteItemId)

            assertEquals(
                SecureItemRemoteResult.Error(
                    SecureItemRemoteError.HttpError(
                        statusCode = 500,
                        errorBody = """{"error":"boom"}""",
                    ),
                ),
                result,
            )
        }

    @Test
    fun `listVaultItems when api throws then maps to network error`() = runBlocking {
        coEvery {
            vaultControllerApi.listVaultItems(
                createdAfter = null,
                updatedAfter = null,
                type = null,
                labels = null,
                includeDeleted = false,
                limit = null,
                order = "DISPLAY_NAME_ASC",
            )
        } throws IllegalStateException("boom")

        val result = target.listVaultItems(
            requestParams = RemoteListVaultItemsRequestParams(),
        )

        assertTrue(result is SecureItemRemoteResult.Error)
        assertTrue((result as SecureItemRemoteResult.Error).error is SecureItemRemoteError.NetworkError)
    }
}
