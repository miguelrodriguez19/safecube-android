package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.generated.api.VaultControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.CreateSecureItemRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.CreateSecureItemResult
import com.miguelrodriguez19.safecube.core.network.generated.model.ListSecureItemChangesResponse
import com.miguelrodriguez19.safecube.core.network.generated.model.SecureItemChangeResponse
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteCreateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteDeleteSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteUpdateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteCreateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class RemoteSecureItemDataSourceTest {
    private val vaultControllerApi = mockk<VaultControllerApi>()
    private val target = RemoteSecureItemDataSource(vaultControllerApi)

    @Test
    fun `create forwards idempotency and client payload version`() = runBlocking {
        val mutationId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val createdAt = Instant.parse("2026-04-20T12:00:00Z")
        val body = slot<CreateSecureItemRequest>()
        val request = RemoteCreateSecureItemRequest(
            itemType = "NOTE",
            schemaVersion = 1,
            displayHint = "New note",
            payload = byteArrayOf(1, 2, 3),
            payloadVersion = 4,
            mutationId = mutationId,
        )
        coEvery {
            vaultControllerApi.createVaultItem(mutationId, capture(body))
        } returns Response.success(
            CreateSecureItemResult(
                itemId = itemId,
                mutationId = mutationId,
                payloadVersion = 4,
                itemRevision = 1,
                changeSequence = 8,
                updatedAt = createdAt,
            ),
        )

        val result = target.createVaultItem(request)

        assertEquals(
            SecureItemRemoteResult.Success(
                RemoteCreateSecureItemResult(
                    itemId = itemId,
                    mutationId = mutationId,
                    payloadVersion = 4,
                    itemRevision = 1,
                    changeSequence = 8,
                    updatedAt = createdAt,
                ),
            ),
            result,
        )
        assertEquals(4, body.captured.payloadVersion)
    }

    @Test
    fun `update sends quoted base revision and maps stale revision`() = runBlocking {
        val itemId = UUID.randomUUID()
        val mutationId = UUID.randomUUID()
        val request = RemoteUpdateSecureItemRequest(
            itemType = "NOTE",
            schemaVersion = 1,
            displayHint = "Updated",
            payload = byteArrayOf(4),
            payloadVersion = 9,
            baseItemRevision = 5,
            mutationId = mutationId,
        )
        coEvery {
            vaultControllerApi.updateVaultItem(itemId, mutationId, "\"5\"", any())
        } returns Response.error(412, ResponseBody.create(null, """{"error":"stale"}"""))

        val result = target.updateVaultItem(itemId, request)

        assertEquals(
            SecureItemRemoteResult.Error(SecureItemRemoteError.PreconditionFailed),
            result,
        )
        coVerify(exactly = 1) {
            vaultControllerApi.updateVaultItem(itemId, mutationId, "\"5\"", any())
        }
    }

    @Test
    fun `delete maps idempotency key reuse to integrity conflict`() = runBlocking {
        val itemId = UUID.randomUUID()
        val mutationId = UUID.randomUUID()
        coEvery {
            vaultControllerApi.deleteVaultItem(itemId, mutationId, "\"7\"")
        } returns Response.error(409, ResponseBody.create(null, """{"error":"reuse"}"""))

        val result = target.deleteVaultItem(
            itemId,
            RemoteDeleteSecureItemRequest(
                baseItemRevision = 7,
                mutationId = mutationId,
            ),
        )

        assertEquals(
            SecureItemRemoteResult.Error(SecureItemRemoteError.IdempotencyConflict),
            result,
        )
    }

    @Test
    fun `changes maps complete snapshots and cursor metadata`() = runBlocking {
        val itemId = UUID.randomUUID()
        val updatedAt = Instant.parse("2026-04-20T12:00:00Z")
        coEvery {
            vaultControllerApi.listVaultItemChanges(after = 40, limit = 25)
        } returns Response.success(
            ListSecureItemChangesResponse(
                items = listOf(
                    SecureItemChangeResponse(
                        itemId = itemId,
                        itemType = "NOTE",
                        schemaVersion = 1,
                        displayHint = "Remote",
                        payload = byteArrayOf(1, 2),
                        payloadVersion = 3,
                        itemRevision = 6,
                        changeSequence = 41,
                        updatedAt = updatedAt,
                        deletedAt = null,
                    ),
                ),
                nextCursor = 41,
                hasMore = false,
            ),
        )

        val result = target.listVaultItemChanges(after = 40, limit = 25)

        assertTrue(result is SecureItemRemoteResult.Success)
        val page = (result as SecureItemRemoteResult.Success).value
        assertEquals(41, page.nextCursor)
        assertEquals(6, page.items.single().itemRevision)
        assertEquals(41, page.items.single().changeSequence)
    }
}
