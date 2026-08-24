package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.data.client.NetworkClientFactory
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.generated.api.VaultKeyMaterialControllerApi
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class RemoteVaultKeyMaterialDataSourceIntegrationTest {
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
    fun `getKeyMaterial when response is successful then returns parsed payload`() = runBlocking {
        val accountId = UUID.randomUUID()

        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "accountId":${accountId},
                      "kekEncMaster":"AQID",
                      "kekEncRecovery":"BAUG",
                      "kdfAlgorithm":"argon2id",
                      "kdfSalt":"BwgJ",
                      "kdfMemoryKib":65536,
                      "kdfIterations":3,
                      "kdfParallelism":1,
                      "kdfOutputLen":32,
                      "cryptoVersion":"v1",
                      "createdAt":"2026-03-09T12:10:45Z",
                      "updatedAt":"2026-03-09T12:11:45Z"
                    }
                    """.trimIndent(),
                ),
        )
        val dataSource = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = createVaultApi(server),
        )

        val result = dataSource.getKeyMaterial()

        assertTrue(result is VaultKeyMaterialRemoteResult.Success)
        val value = (result as VaultKeyMaterialRemoteResult.Success).value
        assertEquals(accountId, value.accountId)
        assertArrayEquals(byteArrayOf(1, 2, 3), value.kekEncMaster)
        assertArrayEquals(byteArrayOf(4, 5, 6), value.kekEncRecovery)
        assertEquals("argon2id", value.kdfAlgorithm)
        assertEquals(65536, value.kdfMemoryKib)
        assertEquals("v1", value.cryptoVersion)

        val request = server.takeRequest()
        assertEquals("/vault/keys", request.path)
        assertEquals("GET", request.method)
    }

    @Test
    fun `getKeyMaterial when status is 404 then maps to vault not initialized`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"error":"Vault key material not found"}"""),
        )
        val dataSource = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = createVaultApi(server),
        )

        val result = dataSource.getKeyMaterial()

        assertEquals(
            VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
            result,
        )
    }

    @Test
    fun `initKeyMaterial when status is 409 then maps to vault already initialized`() =
        runBlocking {
            server.enqueue(
                MockResponse()
                    .setResponseCode(409)
                    .addHeader("Content-Type", "application/json")
                    .setBody("""{"error":"Vault already initialized"}"""),
            )
            val dataSource = RemoteVaultKeyMaterialDataSource(
                vaultKeyMaterialControllerApi = createVaultApi(server),
            )

            val accountId = UUID.randomUUID()
            val result = dataSource.initKeyMaterial(
                vaultKeyMaterial = VaultKeyMaterial(
                    accountId = accountId,
                    kekEncMaster = byteArrayOf(1, 2, 3),
                    kekEncRecovery = byteArrayOf(4, 5, 6),
                    kdfAlgorithm = "argon2id",
                    kdfSalt = byteArrayOf(7, 8, 9),
                    cryptoVersion = "v1",
                    kdfMemoryKib = 65536,
                    kdfIterations = 3,
                    kdfParallelism = 1,
                    kdfOutputLen = 32,
                ),
            )

            assertEquals(
                VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultAlreadyInitialized),
                result,
            )

            val request = server.takeRequest()
            assertEquals("/vault/keys", request.path)
            assertEquals("POST", request.method)
            assertFalse(request.body.readUtf8().contains("accountId"))
        }

    @Test
    fun `updateMasterWrappedKek when status is 401 then maps to unauthorized`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(401)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"error":"Unauthorized"}"""),
        )
        val dataSource = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = createVaultApi(server),
        )

        val result = dataSource.updateMasterWrappedKek(
            newKekEncMaster = byteArrayOf(10, 11, 12),
        )

        assertEquals(
            VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.Unauthorized),
            result,
        )

        val request = server.takeRequest()
        assertEquals("/vault/keys/master", request.path)
        assertEquals("PUT", request.method)
    }

    @Test
    fun `getKeyMaterial when status is 403 then maps to forbidden`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(403)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"error":"Forbidden"}"""),
        )
        val dataSource = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = createVaultApi(server),
        )

        val result = dataSource.getKeyMaterial()

        assertEquals(
            VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.Forbidden),
            result,
        )
    }

    private fun createVaultApi(server: MockWebServer): VaultKeyMaterialControllerApi =
        NetworkClientFactory.createService(
            config = NetworkConfig(
                baseUrl = server.url("/").toString(),
            ),
        )
}
