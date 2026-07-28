package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.data.client.NetworkClientFactory
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.generated.api.VaultControllerApi
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteCreateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteDeleteSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteListVaultItemsRequestParams
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.request.RemoteUpdateSecureItemRequest
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import java.util.UUID
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.Instant

class RemoteSecureItemDataSourceIntegrationTest {
    private lateinit var server: MockWebServer

    @Before
    fun setUp() {
        server = MockWebServer().also(MockWebServer::start)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `listVaultItems when response is successful then returns summaries`() = runBlocking {
        val itemId = UUID.fromString("f4a6fd99-f08a-4fc6-a3f7-f1cbc59e6254")
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "items":[
                        {
                          "itemId":"$itemId",
                          "itemType":"NOTE",
                          "schemaVersion":1,
                          "displayHint":"List item",
                          "payloadVersion":2,
                          "itemRevision":3,
                          "changeSequence":14,
                          "updatedAt":"2026-04-20T11:00:00Z",
                          "deletedAt":null
                        }
                      ]
                    }
                    """.trimIndent(),
                ),
        )
        val target = RemoteSecureItemDataSource(
            vaultControllerApi = createVaultApi(server),
        )
        val now = Instant.now()
        val result = target.listVaultItems(
            requestParams = RemoteListVaultItemsRequestParams(
                createdAfter = now,
                updatedAfter = now,
                includeDeleted = true,
                limit = 30,
            ),
        )

        assertTrue(result is SecureItemRemoteResult.Success)
        val value = (result as SecureItemRemoteResult.Success).value
        assertEquals(1, value.size)
        assertEquals(itemId, value.first().itemId)
        val request = server.takeRequest()
        assertTrue(request.path?.startsWith("/vault/items?") == true)
        assertEquals("GET", request.method)
        assertTrue(request.path?.contains("createdAfter=") == true)
        assertTrue(request.path?.contains("includeDeleted=true") == true)
        assertTrue(request.path?.contains("limit=30") == true)
    }

    @Test
    fun `getVaultItem when response is successful then returns secure item`() = runBlocking {
        val itemId = UUID.fromString("962dd775-640a-4f07-baf5-e4be0f6f8594")
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "itemId":"$itemId",
                      "itemType":"PASSWORD",
                      "schemaVersion":1,
                      "displayHint":"Github",
                      "payload":"AQID",
                      "payloadVersion":4,
                      "itemRevision":5,
                      "changeSequence":15,
                      "updatedAt":"2026-04-20T11:30:00Z",
                      "deletedAt":null
                    }
                    """.trimIndent(),
                ),
        )
        val target = RemoteSecureItemDataSource(
            vaultControllerApi = createVaultApi(server),
        )

        val result = target.getVaultItem(itemId)

        assertTrue(result is SecureItemRemoteResult.Success)
        val value = (result as SecureItemRemoteResult.Success).value
        assertEquals(itemId, value.itemId)
        assertArrayEquals(byteArrayOf(1, 2, 3), value.payload)
        val request = server.takeRequest()
        assertEquals("/vault/items/$itemId", request.path)
        assertEquals("GET", request.method)
    }

    @Test
    fun `createVaultItem when response is successful then returns created result`() = runBlocking {
        val itemId = UUID.fromString("1baf1d87-7736-4f14-b587-f4c3f4f4655c")
        val mutationId = UUID.randomUUID()
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "itemId":"$itemId",
                      "mutationId":"$mutationId",
                      "payloadVersion":1,
                      "itemRevision":1,
                      "changeSequence":16,
                      "updatedAt":"2026-04-20T12:00:00Z"
                    }
                    """.trimIndent(),
                ),
        )
        val target = RemoteSecureItemDataSource(
            vaultControllerApi = createVaultApi(server),
        )

        val result = target.createVaultItem(
            request = RemoteCreateSecureItemRequest(
                itemType = "NOTE",
                schemaVersion = 1,
                displayHint = "New note",
                payload = byteArrayOf(1, 2, 3),
                payloadVersion = 1,
                mutationId = mutationId,
            ),
        )

        assertTrue(result is SecureItemRemoteResult.Success)
        val request = server.takeRequest()
        assertEquals("/vault/items", request.path)
        assertEquals("POST", request.method)
        assertEquals(mutationId.toString(), request.getHeader("Idempotency-Key"))
        assertTrue(request.body.readUtf8().contains("\"displayHint\":\"New note\""))
    }

    @Test
    fun `updateVaultItem when status is 409 then maps to idempotency conflict`() = runBlocking {
        val itemId = UUID.fromString("7cecde73-4f60-4dff-96a0-55f9cb4f4f12")
        server.enqueue(
            MockResponse()
                .setResponseCode(409)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"error":"Conflict"}"""),
        )
        val target = RemoteSecureItemDataSource(
            vaultControllerApi = createVaultApi(server),
        )

        val result = target.updateVaultItem(
            remoteItemId = itemId,
            request = RemoteUpdateSecureItemRequest(
                itemType = "NOTE",
                schemaVersion = 1,
                displayHint = "Updated note",
                payload = byteArrayOf(9, 8, 7),
                payloadVersion = 4,
                baseItemRevision = 3,
                mutationId = UUID.randomUUID(),
            ),
        )

        assertEquals(
            SecureItemRemoteResult.Error(SecureItemRemoteError.IdempotencyConflict),
            result,
        )
        val request = server.takeRequest()
        assertEquals("/vault/items/$itemId", request.path)
        assertEquals("PUT", request.method)
        assertEquals("\"3\"", request.getHeader("If-Match"))
    }

    @Test
    fun `deleteVaultItem when status is 404 then maps to item not found`() = runBlocking {
        val itemId = UUID.fromString("7060f607-8de7-483f-9507-e4d05fca0a44")
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"error":"Not found"}"""),
        )
        val target = RemoteSecureItemDataSource(
            vaultControllerApi = createVaultApi(server),
        )

        val mutationId = UUID.randomUUID()
        val result = target.deleteVaultItem(
            itemId,
            RemoteDeleteSecureItemRequest(
                baseItemRevision = 4,
                mutationId = mutationId,
            ),
        )

        assertEquals(
            SecureItemRemoteResult.Error(SecureItemRemoteError.ItemNotFound),
            result,
        )
        val request = server.takeRequest()
        assertEquals("/vault/items/$itemId", request.path)
        assertEquals("DELETE", request.method)
        assertEquals("\"4\"", request.getHeader("If-Match"))
        assertEquals(mutationId.toString(), request.getHeader("Idempotency-Key"))
    }

    private fun createVaultApi(server: MockWebServer): VaultControllerApi =
        NetworkClientFactory.createService(
            config = NetworkConfig(
                baseUrl = server.url("/").toString(),
                isDebug = false,
            ),
        )
}
