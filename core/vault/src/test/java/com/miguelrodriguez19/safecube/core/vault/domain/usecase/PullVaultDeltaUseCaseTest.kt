package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteListVaultItemsRequestParams
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull.PullVaultDeltaUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PullVaultDeltaUseCaseTest {
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val secureItemRemoteRepository = mockk<SecureItemRemoteRepository>()
    private val vaultKeyMaterialLocalRepository = mockk<VaultKeyMaterialLocalRepository>()

    private val target = PullVaultDeltaUseCase(
        secureItemRepository = secureItemRepository,
        secureItemRemoteRepository = secureItemRemoteRepository,
        vaultKeyMaterialLocalRepository = vaultKeyMaterialLocalRepository,
    )

    @Test
    fun `invoke when remote delta is consistent then applies upsert and tombstone and updates checkpoint`() = runBlocking {
        val accountId = UUID.fromString("4e95e686-1787-4ebd-8c23-8455c7b343e1")
        val checkpoint = Instant.parse("2026-05-01T08:00:00Z")
        val itemId = UUID.fromString("93ff3d4d-c2d4-4a63-9d47-0f49f7a86b08")
        val tombstoneId = UUID.fromString("f4d28128-ad43-45a5-99f9-cf30b96ecff7")
        val itemUpdatedAt = Instant.parse("2026-05-01T09:00:00Z")
        val tombstoneUpdatedAt = Instant.parse("2026-05-01T10:00:00Z")
        val upsertedItem = slot<SecureItem>()
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns checkpoint
        coEvery {
            secureItemRemoteRepository.listVaultItems(
                requestParams = RemoteListVaultItemsRequestParams(
                    updatedAfter = checkpoint,
                    includeDeleted = true,
                    limit = null,
                ),
            )
        } returns SecureItemRemoteResult.Success(
            listOf(
                RemoteSecureItemSummary(
                    itemId = itemId,
                    itemType = "NOTE",
                    schemaVersion = 1,
                    displayHint = "newest",
                    payloadVersion = 4,
                    updatedAt = itemUpdatedAt,
                    deletedAt = null,
                ),
                RemoteSecureItemSummary(
                    itemId = itemId,
                    itemType = "NOTE",
                    schemaVersion = 1,
                    displayHint = "older duplicate",
                    payloadVersion = 3,
                    updatedAt = Instant.parse("2026-05-01T08:30:00Z"),
                    deletedAt = null,
                ),
                RemoteSecureItemSummary(
                    itemId = tombstoneId,
                    itemType = "PASSWORD",
                    schemaVersion = 1,
                    displayHint = "deleted",
                    payloadVersion = 3,
                    updatedAt = tombstoneUpdatedAt,
                    deletedAt = tombstoneUpdatedAt,
                ),
            ),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            RemoteSecureItem(
                itemId = itemId,
                itemType = "NOTE",
                schemaVersion = 1,
                displayHint = "newest",
                payload = byteArrayOf(9, 8, 7),
                payloadVersion = 4,
                updatedAt = itemUpdatedAt,
                deletedAt = null,
            ),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns null
        coEvery { secureItemRepository.findByRemoteItemId(tombstoneId) } returns null
        coEvery { secureItemRepository.applyRemoteUpsert(capture(upsertedItem), itemUpdatedAt) } returns true
        coEvery {
            secureItemRepository.applyRemoteDelete(
                remoteItemId = tombstoneId,
                deletedAt = tombstoneUpdatedAt,
                lastSyncedAt = tombstoneUpdatedAt,
            )
        } returns true
        coEvery { secureItemRepository.updateSyncCheckpoint(accountId, tombstoneUpdatedAt) } returns Unit
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)

        val result = target()

        assertTrue(result is PullVaultDeltaResult.Success)
        val success = result as PullVaultDeltaResult.Success
        assertEquals(2, success.processedSummaryCount)
        assertEquals(1, success.appliedUpsertCount)
        assertEquals(1, success.appliedDeleteCount)
        assertEquals(0, success.skippedDirtyOrConflictCount)
        assertEquals(tombstoneUpdatedAt, success.checkpointUpdatedTo)
        assertEquals(itemId, upsertedItem.captured.remoteItemId)
        assertEquals(SecureItemType.NOTE, upsertedItem.captured.itemType)

        coVerify(exactly = 1) { secureItemRemoteRepository.getVaultItem(itemId) }
        coVerify(exactly = 1) {
            secureItemRepository.applyRemoteDelete(tombstoneId, tombstoneUpdatedAt, tombstoneUpdatedAt)
        }
        coVerify(exactly = 1) { secureItemRepository.updateSyncCheckpoint(accountId, tombstoneUpdatedAt) }
    }

    @Test
    fun `invoke when local item is dirty then marks conflict and does not overwrite it`() = runBlocking {
        val accountId = UUID.fromString("93c501c7-ce8a-44fe-8683-faf3048a5087")
        val itemId = UUID.fromString("8af3e7eb-5512-4509-a97f-19d4d06f45ab")
        val updatedAt = Instant.parse("2026-05-01T11:00:00Z")
        val dirtyLocal = sampleLocalSecureItem(
            remoteItemId = itemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(
                RemoteSecureItemSummary(
                    itemId = itemId,
                    itemType = "NOTE",
                    schemaVersion = 1,
                    displayHint = "dirty item",
                    payloadVersion = 2,
                    updatedAt = updatedAt,
                    deletedAt = null,
                ),
            ),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            RemoteSecureItem(
                itemId = itemId,
                itemType = "NOTE",
                schemaVersion = 1,
                displayHint = "dirty item",
                payload = byteArrayOf(1, 1, 1),
                payloadVersion = 2,
                updatedAt = updatedAt,
                deletedAt = null,
            ),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns dirtyLocal
        coEvery { secureItemRepository.markConflict(dirtyLocal.logicalItemId, any()) } returns true
        coEvery { secureItemRepository.updateSyncCheckpoint(accountId, updatedAt) } returns Unit

        val result = target()

        assertTrue(result is PullVaultDeltaResult.Success)
        val success = result as PullVaultDeltaResult.Success
        assertEquals(1, success.processedSummaryCount)
        assertEquals(0, success.appliedUpsertCount)
        assertEquals(0, success.appliedDeleteCount)
        assertEquals(1, success.skippedDirtyOrConflictCount)
        assertEquals(updatedAt, success.checkpointUpdatedTo)

        coVerify(exactly = 0) { secureItemRepository.applyRemoteUpsert(any(), any()) }
        coVerify(exactly = 1) { secureItemRepository.markConflict(dirtyLocal.logicalItemId, any()) }
    }

    @Test
    fun `invoke when detail fetch fails then returns remote detail error and does not update checkpoint`() = runBlocking {
        val accountId = UUID.fromString("a8c7624f-f824-488a-b0cb-4273e0cfe932")
        val itemId = UUID.fromString("2f443401-53cb-49fc-bf59-1c5026d46c56")
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(
                RemoteSecureItemSummary(
                    itemId = itemId,
                    itemType = "NOTE",
                    schemaVersion = 1,
                    displayHint = "x",
                    payloadVersion = 1,
                    updatedAt = Instant.parse("2026-05-01T11:10:00Z"),
                    deletedAt = null,
                ),
            ),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Unauthorized,
        )

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.RemoteDetailFailed(
                    itemId = itemId,
                    error = SecureItemRemoteError.Unauthorized,
                ),
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRepository.updateSyncCheckpoint(any(), any()) }
        coVerify(exactly = 0) { secureItemRepository.applyRemoteUpsert(any(), any()) }
    }

    @Test
    fun `invoke when account id is unavailable then returns account unavailable error`() = runBlocking {
        every { vaultKeyMaterialLocalRepository.get() } returns null

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(PullVaultDeltaError.AccountIdUnavailable),
            result,
        )
        coVerify(exactly = 0) { secureItemRemoteRepository.listVaultItems(any()) }
        coVerify(exactly = 0) { secureItemRepository.getSyncCheckpoint(any()) }
    }

    @Test
    fun `invoke when remote list fails then returns remote list error`() = runBlocking {
        val accountId = UUID.fromString("6c586798-ddb1-4dd7-9308-cb9a495794f5")
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Unauthorized,
        )

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.RemoteListFailed(SecureItemRemoteError.Unauthorized),
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRemoteRepository.getVaultItem(any()) }
    }

    @Test
    fun `invoke when remote item type is unsupported then returns unsupported type error`() = runBlocking {
        val accountId = UUID.fromString("6d89a90f-0212-4043-a64f-d9dd96096e42")
        val itemId = UUID.fromString("dfd8a90b-5b4d-4df3-97f2-230e19ebd703")
        val updatedAt = Instant.parse("2026-05-01T14:00:00Z")
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(
                RemoteSecureItemSummary(
                    itemId = itemId,
                    itemType = "CARD",
                    schemaVersion = 1,
                    displayHint = "bad type",
                    payloadVersion = 1,
                    updatedAt = updatedAt,
                    deletedAt = null,
                ),
            ),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            RemoteSecureItem(
                itemId = itemId,
                itemType = "CARD",
                schemaVersion = 1,
                displayHint = "bad type",
                payload = byteArrayOf(4, 5, 6),
                payloadVersion = 1,
                updatedAt = updatedAt,
                deletedAt = null,
            ),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns null

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.UnsupportedRemoteItemType(
                    itemId = itemId,
                    wireType = "CARD",
                ),
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRepository.applyRemoteUpsert(any(), any()) }
    }

    @Test
    fun `invoke when local upsert fails then returns local apply failed error`() = runBlocking {
        val accountId = UUID.fromString("0205171f-482f-41f4-ac24-b4f7c798f5d3")
        val itemId = UUID.fromString("f64da0e0-9455-4e8e-827d-39f8ca6f61cd")
        val updatedAt = Instant.parse("2026-05-01T15:00:00Z")
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(
                RemoteSecureItemSummary(
                    itemId = itemId,
                    itemType = "NOTE",
                    schemaVersion = 1,
                    displayHint = "needs upsert",
                    payloadVersion = 1,
                    updatedAt = updatedAt,
                    deletedAt = null,
                ),
            ),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            RemoteSecureItem(
                itemId = itemId,
                itemType = "NOTE",
                schemaVersion = 1,
                displayHint = "needs upsert",
                payload = byteArrayOf(7, 7, 7),
                payloadVersion = 1,
                updatedAt = updatedAt,
                deletedAt = null,
            ),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns null
        coEvery { secureItemRepository.applyRemoteUpsert(any(), any()) } returns false

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.LocalApplyFailed(
                    itemId = itemId,
                    operation = "UPSERT",
                ),
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRepository.updateSyncCheckpoint(any(), any()) }
    }

    @Test
    fun `invoke when remote list is empty then returns success without updating checkpoint`() = runBlocking {
        val accountId = UUID.fromString("f2dbf65e-395d-4afd-b302-38443033ab7c")
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(emptyList())

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Success(
                processedSummaryCount = 0,
                appliedUpsertCount = 0,
                appliedDeleteCount = 0,
                skippedDirtyOrConflictCount = 0,
                checkpointUpdatedTo = null,
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRepository.updateSyncCheckpoint(any(), any()) }
    }

    private fun sampleVaultKeyMaterial(accountId: UUID): VaultKeyMaterial = VaultKeyMaterial(
        accountId = accountId,
        kekEncMaster = byteArrayOf(1),
        kekEncRecovery = byteArrayOf(2),
        kdfAlgorithm = "argon2id",
        kdfSalt = byteArrayOf(3),
        kdfMemoryKib = 1,
        kdfIterations = 1,
        kdfParallelism = 1,
        kdfOutputLen = 1,
        cryptoVersion = "v1",
    )

    private fun sampleLocalSecureItem(
        remoteItemId: UUID,
        syncState: SecureItemSyncState,
    ): SecureItem = SecureItem(
        logicalItemId = UUID.fromString("dd9ec706-b773-4f02-8fdf-f15fbd6567ca"),
        remoteItemId = remoteItemId,
        itemType = SecureItemType.NOTE,
        schemaVersion = 1,
        displayHint = "local",
        payload = byteArrayOf(7, 7, 7),
        payloadVersion = 1,
        createdAt = Instant.parse("2026-05-01T10:00:00Z"),
        updatedAt = Instant.parse("2026-05-01T10:30:00Z"),
        deletedAt = null,
        syncState = syncState,
        lastSyncedAt = null,
        lastSyncError = null,
    )
}
