package com.miguelrodriguez19.safecube.core.vault.data.remote

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.network.generated.api.VaultKeyMaterialControllerApi
import com.miguelrodriguez19.safecube.core.network.generated.model.InitVaultKeyMaterialRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.UpdateMasterWrappedKekRequest
import com.miguelrodriguez19.safecube.core.network.generated.model.VaultKeyMaterialResponse
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.MasterWrapperUpdateConfirmation
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
import okhttp3.Headers
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response

class RemoteVaultKeyMaterialDataSourceTest {
    @Test
    fun `getKeyMaterial when successful response body is missing then maps to contract violation`() =
        runBlocking {
            val api = object : VaultKeyMaterialControllerApi {
                override suspend fun getVaultKeyMaterial(): Response<VaultKeyMaterialResponse> =
                    Response.success(null, Headers.headersOf("ETag", "\"master-1\""))

                override suspend fun initVaultKeyMaterial(
                    initVaultKeyMaterialRequest: InitVaultKeyMaterialRequest,
                ): Response<Unit> = Response.success(Unit)

                override suspend fun updateMasterWrappedKek(
                    ifMatch: String,
                    updateMasterWrappedKekRequest: UpdateMasterWrappedKekRequest,
                ): Response<Unit> = Response.success(
                    Unit,
                    Headers.headersOf("ETag", "\"master-2\""),
                )
            }
            val target = RemoteVaultKeyMaterialDataSource(
                vaultKeyMaterialControllerApi = api,
            )

            val result = target.getKeyMaterial()

            assertEquals(
                VaultKeyMaterialRemoteResult.Error(
                    VaultKeyMaterialRemoteError.ContractViolation,
                ),
                result,
            )
        }

    @Test
    fun `getVersionedKeyMaterial when successful response contains body and etag then maps both`() = runBlocking {
        val accountId = java.util.UUID.randomUUID()
        val api = mockk<VaultKeyMaterialControllerApi>()
        val target = RemoteVaultKeyMaterialDataSource(api)

        coEvery { api.getVaultKeyMaterial() } returns successWithEtag(
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

        val result = target.getVersionedKeyMaterial()

        assertTrue(result is VaultKeyMaterialRemoteResult.Success)
        val value = (result as VaultKeyMaterialRemoteResult.Success).value
        assertEquals("\"master-1\"", value.etag)
        assertEquals(accountId, value.material.accountId)
        assertTrue(value.material.kekEncMaster.contentEquals(byteArrayOf(1, 2, 3)))
        assertTrue(value.material.kekEncRecovery.contentEquals(byteArrayOf(4, 5, 6)))
        assertEquals("argon2id", value.material.kdfAlgorithm)
        assertTrue(value.material.kdfSalt.contentEquals(byteArrayOf(7, 8, 9)))
        assertEquals(65536, value.material.kdfMemoryKib)
        assertEquals(3, value.material.kdfIterations)
        assertEquals(1, value.material.kdfParallelism)
        assertEquals(32, value.material.kdfOutputLen)
        assertEquals("v1", value.material.cryptoVersion)
        coVerify(exactly = 1) { api.getVaultKeyMaterial() }
        confirmVerified(api)
    }

    @Test
    fun `getKeyMaterial when versioned response is successful then maps material and discards etag`() = runBlocking {
        val api = mockk<VaultKeyMaterialControllerApi>()
        val target = RemoteVaultKeyMaterialDataSource(api)
        val response = sampleResponse()

        coEvery { api.getVaultKeyMaterial() } returns successWithEtag(response)

        val result = target.getKeyMaterial()

        assertTrue(result is VaultKeyMaterialRemoteResult.Success)
        assertEquals(
            response.accountId,
            (result as VaultKeyMaterialRemoteResult.Success).value.accountId,
        )
        coVerify(exactly = 1) { api.getVaultKeyMaterial() }
        confirmVerified(api)
    }

    @Test
    fun `getVersionedKeyMaterial when etag is missing then returns contract violation`() = runBlocking {
        val api = mockk<VaultKeyMaterialControllerApi>()
        val target = RemoteVaultKeyMaterialDataSource(api)

        coEvery { api.getVaultKeyMaterial() } returns Response.success(sampleResponse())

        val result = target.getVersionedKeyMaterial()

        assertEquals(
            VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.ContractViolation),
            result,
        )
        coVerify(exactly = 1) { api.getVaultKeyMaterial() }
        confirmVerified(api)
    }

    @Test
    fun `getKeyMaterial when etag is missing then returns material`() = runBlocking {
        val api = mockk<VaultKeyMaterialControllerApi>()
        val target = RemoteVaultKeyMaterialDataSource(api)
        val response = sampleResponse()

        coEvery { api.getVaultKeyMaterial() } returns Response.success(response)

        val result = target.getKeyMaterial()

        assertEquals(VaultKeyMaterialRemoteResult.Success(response.toExpectedDomainModel()), result)
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
                ifMatch: String,
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
    fun `getKeyMaterial when http error is generic then discards error body`() = runBlocking {
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
                    failure = NetworkFailureClassifier.fromHttpStatus(500),
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { api.getVaultKeyMaterial() }
        confirmVerified(api)
    }

    @Test
    fun `getKeyMaterial when http error has no body then returns generic http error`() = runBlocking {
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
                    failure = NetworkFailureClassifier.fromHttpStatus(500),
                ),
            ),
            result,
        )
        coVerify(exactly = 1) { api.getVaultKeyMaterial() }
        confirmVerified(api)
    }

    @Test
    fun `getKeyMaterial when error body cannot be read then returns generic http error`() = runBlocking {
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
                    failure = NetworkFailureClassifier.fromHttpStatus(500),
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
                ifMatch: String,
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
    fun `initKeyMaterial when error body cannot be read then returns generic http error`() = runBlocking {
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
                    failure = NetworkFailureClassifier.fromHttpStatus(500),
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
                ifMatch: String,
                updateMasterWrappedKekRequest: UpdateMasterWrappedKekRequest,
            ): Response<Unit> {
                throw IllegalStateException("boom")
            }
        }
        val target = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = api,
        )

        val result = target.updateMasterWrappedKek(
            newKekEncMaster = byteArrayOf(1, 2, 3),
            ifMatch = "\"master-1\"",
        )

        assertTrue(result is VaultKeyMaterialRemoteResult.Error)
        assertTrue((result as VaultKeyMaterialRemoteResult.Error).error is VaultKeyMaterialRemoteError.NetworkError)
    }

    @Test
    fun `updateMasterWrappedKek when successful then returns success and forwards new wrapped kek`() = runBlocking {
        val api = mockk<VaultKeyMaterialControllerApi>()
        val request = slot<UpdateMasterWrappedKekRequest>()
        val target = RemoteVaultKeyMaterialDataSource(api)

        coEvery {
            api.updateMasterWrappedKek(
                ifMatch = "\"master-1\"",
                updateMasterWrappedKekRequest = capture(request),
            )
        } returns Response.success(
            Unit,
            Headers.headersOf("ETag", "\"master-2\""),
        )

        val result = target.updateMasterWrappedKek(
            newKekEncMaster = byteArrayOf(1, 2, 3),
            ifMatch = "\"master-1\"",
        )

        assertEquals(
            VaultKeyMaterialRemoteResult.Success(
                MasterWrapperUpdateConfirmation(etag = "\"master-2\""),
            ),
            result,
        )
        assertTrue(request.isCaptured)
        assertTrue(request.captured.newKekEncMaster.contentEquals(byteArrayOf(1, 2, 3)))
        coVerify(exactly = 1) {
            api.updateMasterWrappedKek(
                ifMatch = "\"master-1\"",
                updateMasterWrappedKekRequest = any(),
            )
        }
        confirmVerified(api)
    }

    @Test
    fun `updateMasterWrappedKek when confirmation etag is missing then returns contract violation`() = runBlocking {
        val api = mockk<VaultKeyMaterialControllerApi>()
        val target = RemoteVaultKeyMaterialDataSource(api)

        coEvery {
            api.updateMasterWrappedKek(
                ifMatch = any(),
                updateMasterWrappedKekRequest = any(),
            )
        } returns Response.success(Unit)

        val result = target.updateMasterWrappedKek(
            newKekEncMaster = byteArrayOf(1, 2, 3),
            ifMatch = "\"master-1\"",
        )

        assertEquals(
            VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.ContractViolation),
            result,
        )
    }

    @Test
    fun `getVersionedKeyMaterial when etag is not a single strong opaque value then returns contract violation`() =
        runBlocking {
            val api = mockk<VaultKeyMaterialControllerApi>()
            val target = RemoteVaultKeyMaterialDataSource(api)
            val invalidEtags = listOf("", "master-1", "W/\"master-1\"", "*", "\"master-1\", \"master-2\"")

            invalidEtags.forEach { invalidEtag ->
                coEvery { api.getVaultKeyMaterial() } returns Response.success(
                    sampleResponse(),
                    Headers.headersOf("ETag", invalidEtag),
                )

                assertEquals(
                    VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.ContractViolation),
                    target.getVersionedKeyMaterial(),
                )
            }

            coEvery { api.getVaultKeyMaterial() } returns Response.success(
                sampleResponse(),
                Headers.Builder()
                    .add("ETag", "\"master-1\"")
                    .add("ETag", "\"master-2\"")
                    .build(),
            )

            assertEquals(
                VaultKeyMaterialRemoteResult.Error(VaultKeyMaterialRemoteError.ContractViolation),
                target.getVersionedKeyMaterial(),
            )
        }

    @Test
    fun `updateMasterWrappedKek when response is 404 then maps to vault not initialized`() = runBlocking {
        val api = object : VaultKeyMaterialControllerApi {
            override suspend fun getVaultKeyMaterial(): Response<VaultKeyMaterialResponse> = Response.success(null)

            override suspend fun initVaultKeyMaterial(
                initVaultKeyMaterialRequest: InitVaultKeyMaterialRequest,
            ): Response<Unit> = Response.success(Unit)

            override suspend fun updateMasterWrappedKek(
                ifMatch: String,
                updateMasterWrappedKekRequest: UpdateMasterWrappedKekRequest,
            ): Response<Unit> = Response.error(
                404,
                okhttp3.ResponseBody.create(null, """{"error":"not found"}"""),
            )
        }
        val target = RemoteVaultKeyMaterialDataSource(
            vaultKeyMaterialControllerApi = api,
        )

        val result = target.updateMasterWrappedKek(
            newKekEncMaster = byteArrayOf(1, 2, 3),
            ifMatch = "\"master-1\"",
        )

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

private fun sampleResponse(): VaultKeyMaterialResponse {
    val createdAt = Instant.parse("2026-08-27T10:00:00Z")
    return VaultKeyMaterialResponse(
        accountId = java.util.UUID.randomUUID(),
        kekEncMaster = byteArrayOf(1, 2, 3),
        kekEncRecovery = byteArrayOf(4, 5, 6),
        kdfAlgorithm = "argon2id",
        kdfSalt = byteArrayOf(7, 8, 9),
        kdfMemoryKib = 65536,
        kdfIterations = 3,
        kdfParallelism = 1,
        kdfOutputLen = 32,
        cryptoVersion = "v1",
        createdAt = createdAt,
        updatedAt = createdAt.plusSeconds(1),
    )
}

private fun VaultKeyMaterialResponse.toExpectedDomainModel() =
    com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial(
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

private fun <T> successWithEtag(body: T): Response<T> = Response.success(
    body,
    Headers.headersOf("ETag", "\"master-1\""),
)
