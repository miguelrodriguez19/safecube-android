package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.NetworkClientFactory
import com.miguelrodriguez19.safecube.core.network.NetworkConfig
import com.miguelrodriguez19.safecube.core.network.generated.api.VaultKeyMaterialControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.InitVaultKeyMaterialRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.UpdateMasterWrappedKekRequest
import java.time.OffsetDateTime
import java.util.UUID
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response

class RemoteVaultKeyMaterialDataSourceTest {
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
    fun `getKeyMaterial returns success with parsed payload`() = runBlocking {
        val accountId = UUID.randomUUID()
        val createdAt = OffsetDateTime.parse("2026-03-09T12:10:45Z")
        val updatedAt = OffsetDateTime.parse("2026-03-09T12:11:45Z")
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("Content-Type", "application/json")
                .setBody(
                    """
                    {
                      "accountId":"$accountId",
                      "kekEncMaster":[1,2,3],
                      "kekEncRecovery":[4,5,6],
                      "kdfAlgorithm":"argon2id",
                      "kdfSalt":[7,8,9],
                      "kdfMemoryKib":65536,
                      "kdfIterations":3,
                      "kdfParallelism":1,
                      "kdfOutputLen":32,
                      "cryptoVersion":"v1",
                      "createdAt":"$createdAt",
                      "updatedAt":"$updatedAt"
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
        assertEquals("argon2id", value.kdfAlgorithm)
        assertEquals(65536, value.kdfMemoryKib)
        assertEquals(createdAt, value.createdAt)
        assertEquals(updatedAt, value.updatedAt)

        val request = server.takeRequest()
        assertEquals("/vault/keys", request.path)
        assertEquals("GET", request.method)
    }

    @Test
    fun `getKeyMaterial maps 404 into VaultNotInitialized`() = runBlocking {
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
    fun `initKeyMaterial maps 409 into VaultAlreadyInitialized`() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(409)
                .addHeader("Content-Type", "application/json")
                .setBody("""{"error":"Vault already initialized"}"""),
        )
        val dataSource = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = createVaultApi(server),
        )

        val result = dataSource.initKeyMaterial(
            request = InitVaultKeyMaterialRequest(
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
    }

    @Test
    fun `updateMasterWrappedKek maps 401 into Unauthorized`() = runBlocking {
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
            request = UpdateMasterWrappedKekRequest(
                newKekEncMaster = byteArrayOf(10, 11, 12),
            ),
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
    fun `getKeyMaterial maps 403 into Unauthorized`() = runBlocking {
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
            VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.Unauthorized),
            result,
        )
    }

    @Test
    fun `getKeyMaterial maps successful response with missing body into HttpError`() = runBlocking {
        val apiWithMissingBody = object : VaultKeyMaterialControllerApi {
            override suspend fun getVaultKeyMaterial(): Response<com.miguelrodriguez19.safecube.core.network.generated.model.VaultKeyMaterialResponse> =
                Response.success(null)

            override suspend fun initVaultKeyMaterial(
                initVaultKeyMaterialRequest: InitVaultKeyMaterialRequest,
            ): Response<Unit> = Response.success(Unit)

            override suspend fun updateMasterWrappedKek(
                updateMasterWrappedKekRequest: UpdateMasterWrappedKekRequest,
            ): Response<Unit> = Response.success(Unit)
        }
        val dataSource = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = apiWithMissingBody,
        )

        val result = dataSource.getKeyMaterial()

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
    fun `maps thrown exception into network error`() = runBlocking {
        val failingApi = object : VaultKeyMaterialControllerApi {
            override suspend fun getVaultKeyMaterial(): Response<com.miguelrodriguez19.safecube.core.network.generated.model.VaultKeyMaterialResponse> {
                throw IllegalStateException("boom")
            }

            override suspend fun initVaultKeyMaterial(
                initVaultKeyMaterialRequest: InitVaultKeyMaterialRequest,
            ): Response<Unit> = Response.success(Unit)

            override suspend fun updateMasterWrappedKek(
                updateMasterWrappedKekRequest: UpdateMasterWrappedKekRequest,
            ): Response<Unit> = Response.success(Unit)
        }
        val dataSource = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = failingApi,
        )

        val result = dataSource.getKeyMaterial()

        assertTrue(result is VaultKeyMaterialRemoteResult.Error)
        assertTrue((result as VaultKeyMaterialRemoteResult.Error).error is VaultKeyMaterialRemoteError.NetworkError)
    }

    private fun createVaultApi(server: MockWebServer): VaultKeyMaterialControllerApi =
        NetworkClientFactory.createService(
            config = NetworkConfig(
                baseUrl = server.url("/").toString(),
                isDebug = false,
            ),
        )
}
