package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.generated.api.VaultKeyMaterialControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.InitVaultKeyMaterialRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.UpdateMasterWrappedKekRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.VaultKeyMaterialResponse
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.VaultKeyMaterialRemoteResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant
import java.util.concurrent.CancellationException
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
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
                        failure = NetworkFailureClassifier.malformedResponse(200),
                    ),
                ),
                result,
            )
        }

    @Test
    fun `getKeyMaterial when successful response contains body then maps vault key material`() = runBlocking {
        val accountId = java.util.UUID.randomUUID()
        val api = mockk<VaultKeyMaterialControllerApi>()
        val target = RemoteVaultKeyMaterialDataSource(api)

        coEvery { api.getVaultKeyMaterial() } returns Response.success(
            VaultKeyMaterialResponse(
                accountId = accountId,
                kekEncMaster = byteArrayOf(1, 2, 3),
                kekEncRecovery = byteArrayOf(4, 5, 6),
                kdfAlgorithm = "argon2id",
                kdfSalt = byteArrayOf(7, 8, 9),
                kdfMemoryKib = 65536,
                kdfIterations = 3,
                kdfParallelism = 1,
                kdfOutputLen = 32,
                cryptoVersion = "v1",
                createdAt = Instant.parse("2026-04-10T10:15:30Z"),
                updatedAt = Instant.parse("2026-04-10T10:16:30Z"),
            ),
        )

        val result = target.getKeyMaterial()

        assertTrue(result is VaultKeyMaterialRemoteResult.Success)
        val value = (result as VaultKeyMaterialRemoteResult.Success).value
        assertEquals(accountId, value.accountId)
        assertTrue(value.kekEncMaster.contentEquals(byteArrayOf(1, 2, 3)))
        assertTrue(value.kekEncRecovery.contentEquals(byteArrayOf(4, 5, 6)))
        assertEquals("argon2id", value.kdfAlgorithm)
        assertTrue(value.kdfSalt.contentEquals(byteArrayOf(7, 8, 9)))
        assertEquals(65536, value.kdfMemoryKib)
        assertEquals(3, value.kdfIterations)
        assertEquals(1, value.kdfParallelism)
        assertEquals(32, value.kdfOutputLen)
        assertEquals("v1", value.cryptoVersion)
        coVerify(exactly = 1) { api.getVaultKeyMaterial() }
        confirmVerified(api)
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

    @Test
    fun `getKeyMaterial when http error is generic then returns http error with body`() = runBlocking {
        val api = mockk<VaultKeyMaterialControllerApi>()
        val target = RemoteVaultKeyMaterialDataSource(api)

        coEvery { api.getVaultKeyMaterial() } returns Response.error(
            500,
            ResponseBody.create(null, """{"error":"boom"}"""),
        )

        val result = target.getKeyMaterial()

        assertEquals(
            VaultKeyMaterialRemoteResult.Error(
                VaultKeyMaterialRemoteError.HttpError(
                    statusCode = 500,
                    errorBody = """{"error":"boom"}""",
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { api.getVaultKeyMaterial() }
        confirmVerified(api)
    }

    @Test
    fun `getKeyMaterial when http error has no error body then returns generic http error with null body`() = runBlocking {
        val api = mockk<VaultKeyMaterialControllerApi>()
        val response = mockk<Response<VaultKeyMaterialResponse>>()
        val target = RemoteVaultKeyMaterialDataSource(api)

        coEvery { api.getVaultKeyMaterial() } returns response
        every { response.body() } returns null
        every { response.isSuccessful } returns false
        every { response.code() } returns 500
        every { response.errorBody() } returns null

        val result = target.getKeyMaterial()

        assertEquals(
            VaultKeyMaterialRemoteResult.Error(
                VaultKeyMaterialRemoteError.HttpError(
                    statusCode = 500,
                    errorBody = null,
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { api.getVaultKeyMaterial() }
        confirmVerified(api)
    }

    @Test
    fun `getKeyMaterial when error body cannot be read then returns generic http error with null body`() = runBlocking {
        val api = mockk<VaultKeyMaterialControllerApi>()
        val response = mockk<Response<VaultKeyMaterialResponse>>()
        val unreadableErrorBody = mockk<ResponseBody>()
        val target = RemoteVaultKeyMaterialDataSource(api)

        coEvery { api.getVaultKeyMaterial() } returns response
        every { response.body() } returns null
        every { response.isSuccessful } returns false
        every { response.code() } returns 500
        every { response.errorBody() } returns unreadableErrorBody
        every { unreadableErrorBody.string() } throws IllegalStateException("boom")

        val result = target.getKeyMaterial()

        assertEquals(
            VaultKeyMaterialRemoteResult.Error(
                VaultKeyMaterialRemoteError.HttpError(
                    statusCode = 500,
                    errorBody = null,
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { api.getVaultKeyMaterial() }
        confirmVerified(api)
    }

    @Test
    fun `initKeyMaterial when api throws cancellation exception then rethrows cancellation exception`() = runBlocking {
        val api = object : VaultKeyMaterialControllerApi {
            override suspend fun getVaultKeyMaterial(): Response<VaultKeyMaterialResponse> = Response.success(null)

            override suspend fun initVaultKeyMaterial(
                initVaultKeyMaterialRequest: InitVaultKeyMaterialRequest,
            ): Response<Unit> {
                throw CancellationException("cancelled")
            }

            override suspend fun updateMasterWrappedKek(
                updateMasterWrappedKekRequest: UpdateMasterWrappedKekRequest,
            ): Response<Unit> = Response.success(Unit)
        }
        val target = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = api,
        )

        assertThrows(CancellationException::class.java) {
            kotlinx.coroutines.runBlocking {
                target.initKeyMaterial(
                    vaultKeyMaterial = sampleVaultKeyMaterial(),
                )
            }
        }
        Unit
    }

    @Test
    fun `initKeyMaterial when successful then returns success and forwards request`() = runBlocking {
        val api = mockk<VaultKeyMaterialControllerApi>()
        val request = slot<InitVaultKeyMaterialRequest>()
        val target = RemoteVaultKeyMaterialDataSource(api)
        val vaultKeyMaterial = sampleVaultKeyMaterial()

        coEvery { api.initVaultKeyMaterial(capture(request)) } returns Response.success(Unit)

        val result = target.initKeyMaterial(vaultKeyMaterial)

        assertEquals(VaultKeyMaterialRemoteResult.Success(Unit), result)
        assertTrue(request.isCaptured)
        assertTrue(request.captured.kekEncMaster.contentEquals(vaultKeyMaterial.kekEncMaster))
        assertTrue(request.captured.kekEncRecovery.contentEquals(vaultKeyMaterial.kekEncRecovery))
        assertEquals(vaultKeyMaterial.kdfAlgorithm, request.captured.kdfAlgorithm)
        assertTrue(request.captured.kdfSalt.contentEquals(vaultKeyMaterial.kdfSalt))
        assertEquals(vaultKeyMaterial.cryptoVersion, request.captured.cryptoVersion)
        assertEquals(vaultKeyMaterial.kdfMemoryKib, request.captured.kdfMemoryKib)
        assertEquals(vaultKeyMaterial.kdfIterations, request.captured.kdfIterations)
        assertEquals(vaultKeyMaterial.kdfParallelism, request.captured.kdfParallelism)
        assertEquals(vaultKeyMaterial.kdfOutputLen, request.captured.kdfOutputLen)
        coVerify(exactly = 1) { api.initVaultKeyMaterial(any()) }
        confirmVerified(api)
    }

    @Test
    fun `initKeyMaterial when error body cannot be read then returns generic http error with null body`() = runBlocking {
        val api = mockk<VaultKeyMaterialControllerApi>()
        val unreadableErrorBody = mockk<ResponseBody>()
        val response = mockk<Response<Unit>>()
        val target = RemoteVaultKeyMaterialDataSource(api)

        coEvery { api.initVaultKeyMaterial(any()) } returns response
        every { response.isSuccessful } returns false
        every { response.code() } returns 500
        every { response.errorBody() } returns unreadableErrorBody
        every { unreadableErrorBody.string() } throws IllegalStateException("boom")

        val result = target.initKeyMaterial(sampleVaultKeyMaterial())

        assertEquals(
            VaultKeyMaterialRemoteResult.Error(
                VaultKeyMaterialRemoteError.HttpError(
                    statusCode = 500,
                    errorBody = null,
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { api.initVaultKeyMaterial(any()) }
        confirmVerified(api)
    }

    @Test
    fun `updateMasterWrappedKek when api throws then maps to network error`() = runBlocking {
        val api = object : VaultKeyMaterialControllerApi {
            override suspend fun getVaultKeyMaterial(): Response<VaultKeyMaterialResponse> = Response.success(null)

            override suspend fun initVaultKeyMaterial(
                initVaultKeyMaterialRequest: InitVaultKeyMaterialRequest,
            ): Response<Unit> = Response.success(Unit)

            override suspend fun updateMasterWrappedKek(
                updateMasterWrappedKekRequest: UpdateMasterWrappedKekRequest,
            ): Response<Unit> {
                throw IllegalStateException("boom")
            }
        }
        val target = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = api,
        )

        val result = target.updateMasterWrappedKek(byteArrayOf(1, 2, 3))

        assertTrue(result is VaultKeyMaterialRemoteResult.Error)
        assertTrue((result as VaultKeyMaterialRemoteResult.Error).error is VaultKeyMaterialRemoteError.NetworkError)
    }

    @Test
    fun `updateMasterWrappedKek when successful then returns success and forwards new wrapped kek`() = runBlocking {
        val api = mockk<VaultKeyMaterialControllerApi>()
        val request = slot<UpdateMasterWrappedKekRequest>()
        val target = RemoteVaultKeyMaterialDataSource(api)

        coEvery { api.updateMasterWrappedKek(capture(request)) } returns Response.success(Unit)

        val result = target.updateMasterWrappedKek(byteArrayOf(1, 2, 3))

        assertEquals(VaultKeyMaterialRemoteResult.Success(Unit), result)
        assertTrue(request.isCaptured)
        assertTrue(request.captured.newKekEncMaster.contentEquals(byteArrayOf(1, 2, 3)))
        coVerify(exactly = 1) { api.updateMasterWrappedKek(any()) }
        confirmVerified(api)
    }

    @Test
    fun `updateMasterWrappedKek when response is 404 then maps to vault not initialized`() = runBlocking {
        val api = object : VaultKeyMaterialControllerApi {
            override suspend fun getVaultKeyMaterial(): Response<VaultKeyMaterialResponse> = Response.success(null)

            override suspend fun initVaultKeyMaterial(
                initVaultKeyMaterialRequest: InitVaultKeyMaterialRequest,
            ): Response<Unit> = Response.success(Unit)

            override suspend fun updateMasterWrappedKek(
                updateMasterWrappedKekRequest: UpdateMasterWrappedKekRequest,
            ): Response<Unit> = Response.error(
                404,
                okhttp3.ResponseBody.create(null, """{"error":"not found"}"""),
            )
        }
        val target = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = api,
        )

        val result = target.updateMasterWrappedKek(byteArrayOf(1, 2, 3))

        assertEquals(
            VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.VaultNotInitialized),
            result,
        )
    }
}

private fun sampleVaultKeyMaterial() = com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial(
    kekEncMaster = byteArrayOf(1, 2, 3),
    kekEncRecovery = byteArrayOf(4, 5, 6),
    kdfAlgorithm = "argon2id",
    kdfSalt = byteArrayOf(7, 8, 9),
    kdfMemoryKib = 65536,
    kdfIterations = 3,
    kdfParallelism = 1,
    kdfOutputLen = 32,
    cryptoVersion = "v1",
)
