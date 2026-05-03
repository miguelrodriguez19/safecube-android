package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import android.util.Log
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteCreateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteDeleteSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteUpdateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.isPendingPushState
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.localDeleteTimestamp
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.PushLocalVaultChangesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PushLocalVaultChangesUseCaseTest {
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val secureItemRemoteRepository = mockk<SecureItemRemoteRepository>()

    private val target = PushLocalVaultChangesUseCase(
        secureItemRepository = secureItemRepository,
        secureItemRemoteRepository = secureItemRemoteRepository,
    )

    @Test
    fun `invoke when pending create succeeds then marks synced with new remote id`() = runBlocking {
        val logicalItemId = UUID.fromString("42f00fd3-b62f-47d8-b54d-3f6de5f5ea53")
        val remoteItemId = UUID.fromString("8c93f886-389f-4a49-8030-25f0c93d2b23")
        val createdAt = Instant.parse("2026-05-02T10:00:00Z")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = null,
            syncState = SecureItemSyncState.PENDING_CREATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.createVaultItem(any()) } returns SecureItemRemoteResult.Success(
            RemoteCreateSecureItemResult(
                itemId = remoteItemId,
                createdAt = createdAt,
            ),
        )
        coEvery {
            secureItemRepository.markSynced(
                logicalItemId = logicalItemId,
                remoteItemId = remoteItemId,
                payloadVersion = item.payloadVersion,
                updatedAt = createdAt,
                deletedAt = null,
                lastSyncedAt = createdAt,
            )
        } returns true

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 1,
                conflictCount = 0,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
    }

    @Test
    fun `invoke when pending update succeeds then uses remote payload version and updatedAt`() = runBlocking {
        val logicalItemId = UUID.fromString("0ff6b4f8-03fd-46b0-8aa4-fab2eb06e6f1")
        val remoteItemId = UUID.fromString("7bbf25f2-8ce8-4fba-8fe5-cf61b9bba89d")
        val remoteUpdatedAt = Instant.parse("2026-05-02T11:00:00Z")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
            payloadVersion = 9,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.updateVaultItem(remoteItemId, any()) } returns SecureItemRemoteResult.Success(
            RemoteUpdateSecureItemResult(
                itemId = remoteItemId,
                payloadVersion = 12,
                updatedAt = remoteUpdatedAt,
            ),
        )
        coEvery {
            secureItemRepository.markSynced(
                logicalItemId = logicalItemId,
                remoteItemId = remoteItemId,
                payloadVersion = 12,
                updatedAt = remoteUpdatedAt,
                deletedAt = null,
                lastSyncedAt = remoteUpdatedAt,
            )
        } returns true

        val result = target()

        assertTrue(result is PushLocalVaultChangesResult.Success)
        val success = result as PushLocalVaultChangesResult.Success
        assertEquals(1, success.syncedCount)
        assertEquals(0, success.conflictCount)
        assertEquals(0, success.keptPendingCount)
    }

    @Test
    fun `invoke when pending delete with remote id succeeds then marks synced deleted`() = runBlocking {
        val logicalItemId = UUID.fromString("448957f0-b479-4b4f-a17f-c374c8e2d9f4")
        val remoteItemId = UUID.fromString("8b646789-d06c-4383-90f7-12fef551635d")
        val deletedAt = Instant.parse("2026-05-02T12:00:00Z")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_DELETE,
            deletedAt = Instant.parse("2026-05-02T11:59:00Z"),
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.deleteVaultItem(remoteItemId) } returns SecureItemRemoteResult.Success(
            RemoteDeleteSecureItemResult(
                itemId = remoteItemId,
                deletedAt = deletedAt,
            ),
        )
        coEvery {
            secureItemRepository.markSynced(
                logicalItemId = logicalItemId,
                remoteItemId = remoteItemId,
                payloadVersion = item.payloadVersion,
                updatedAt = deletedAt,
                deletedAt = deletedAt,
                lastSyncedAt = deletedAt,
            )
        } returns true

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 1,
                conflictCount = 0,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
    }

    @Test
    fun `invoke when pending delete without remote id then resolves only locally`() = runBlocking {
        val logicalItemId = UUID.fromString("80ae2778-f3cf-4473-a3f2-e7ec0ed75727")
        val deletedAt = Instant.parse("2026-05-02T13:00:00Z")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = null,
            syncState = SecureItemSyncState.PENDING_DELETE,
            deletedAt = deletedAt,
            updatedAt = deletedAt,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery {
            secureItemRepository.markSynced(
                logicalItemId = logicalItemId,
                remoteItemId = null,
                payloadVersion = item.payloadVersion,
                updatedAt = deletedAt,
                deletedAt = deletedAt,
                lastSyncedAt = deletedAt,
            )
        } returns true

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 1,
                conflictCount = 0,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 1,
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRemoteRepository.deleteVaultItem(any()) }
    }

    @Test
    fun `invoke when delete returns not found then resolves as already deleted`() = runBlocking {
        val logicalItemId = UUID.fromString("eb4386c2-2e1d-45de-a6ec-f9cce08f40a7")
        val remoteItemId = UUID.fromString("6fb57539-7b2f-4baf-a17d-54fab74b96d3")
        val localDeletedAt = Instant.parse("2026-05-02T14:00:00Z")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_DELETE,
            deletedAt = localDeletedAt,
            updatedAt = localDeletedAt,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.deleteVaultItem(remoteItemId) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.ItemNotFound,
        )
        coEvery {
            secureItemRepository.markSynced(
                logicalItemId = logicalItemId,
                remoteItemId = remoteItemId,
                payloadVersion = item.payloadVersion,
                updatedAt = localDeletedAt,
                deletedAt = localDeletedAt,
                lastSyncedAt = localDeletedAt,
            )
        } returns true

        val result = target()

        assertTrue(result is PushLocalVaultChangesResult.Success)
        val success = result as PushLocalVaultChangesResult.Success
        assertEquals(1, success.syncedCount)
        assertEquals(0, success.conflictCount)
    }

    @Test
    fun `invoke when update returns not found then marks conflict`() = runBlocking {
        val logicalItemId = UUID.fromString("f673d6f2-8e5c-4e2f-ba9c-b2f906a83240")
        val remoteItemId = UUID.fromString("a5f96d8f-8f95-44b7-9090-58d998034911")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.updateVaultItem(remoteItemId, any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.ItemNotFound,
        )
        coEvery { secureItemRepository.markConflict(logicalItemId, any()) } returns true

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 0,
                conflictCount = 1,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.markConflict(logicalItemId, any()) }
    }

    @Test
    fun `invoke when create has network error then keeps item pending`() = runBlocking {
        val item = sampleItem(
            logicalItemId = UUID.fromString("b2c7140b-41d4-4caf-a9dc-23b40f1a5cdb"),
            remoteItemId = null,
            syncState = SecureItemSyncState.PENDING_CREATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.createVaultItem(any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.NetworkError(RuntimeException("offline")),
        )

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 0,
                conflictCount = 0,
                keptPendingCount = 1,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRepository.markSynced(any(), any(), any(), any(), any(), any()) }
        coVerify(exactly = 0) { secureItemRepository.markConflict(any(), any()) }
    }

    @Test
    fun `invoke when local mark synced fails then returns fatal error`() = runBlocking {
        val logicalItemId = UUID.fromString("0f4fa4b3-93a3-495c-a31e-c1b7e53730d3")
        val remoteItemId = UUID.fromString("e3c97fd4-2bea-4695-bbae-4183c2051841")
        val createdAt = Instant.parse("2026-05-02T15:00:00Z")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = null,
            syncState = SecureItemSyncState.PENDING_CREATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.createVaultItem(any()) } returns SecureItemRemoteResult.Success(
            RemoteCreateSecureItemResult(
                itemId = remoteItemId,
                createdAt = createdAt,
            ),
        )
        coEvery { secureItemRepository.markSynced(any(), any(), any(), any(), any(), any()) } returns false

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    logicalItemId = logicalItemId,
                    operation = "CREATE",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when pending create already has remote id then marks conflict`() = runBlocking {
        val logicalItemId = UUID.fromString("b57f2ef2-72fc-4d3d-b2d1-648ecfb1ba44")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = UUID.fromString("cf2ef736-9df6-4d55-9c3a-e08abf25f75a"),
            syncState = SecureItemSyncState.PENDING_CREATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRepository.markConflict(logicalItemId, any()) } returns true

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 0,
                conflictCount = 1,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRemoteRepository.createVaultItem(any()) }
    }

    @Test
    fun `invoke when pending update has no remote id then marks conflict`() = runBlocking {
        val logicalItemId = UUID.fromString("8ad89f09-2e67-4328-9035-d2c4b5a91237")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = null,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRepository.markConflict(logicalItemId, any()) } returns true

        val result = target()

        assertTrue(result is PushLocalVaultChangesResult.Success)
        val success = result as PushLocalVaultChangesResult.Success
        assertEquals(1, success.conflictCount)
        assertEquals(0, success.keptPendingCount)
    }

    @Test
    fun `invoke when update returns unauthorized then keeps pending`() = runBlocking {
        val remoteItemId = UUID.fromString("2a5e3210-a46d-44c6-aa06-c5296d40996f")
        val item = sampleItem(
            logicalItemId = UUID.fromString("9a736fdb-df44-46dc-bdc2-fde5b0877f04"),
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.updateVaultItem(remoteItemId, any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Unauthorized,
        )

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 0,
                conflictCount = 0,
                keptPendingCount = 1,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRepository.markConflict(any(), any()) }
    }

    @Test
    fun `invoke when delete returns conflict then marks conflict`() = runBlocking {
        val logicalItemId = UUID.fromString("ea32c041-b06c-4a1f-9108-1a40f0f1e7dc")
        val remoteItemId = UUID.fromString("8ef4908c-e6c0-4869-b001-cdcf6cf273d4")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_DELETE,
            deletedAt = Instant.parse("2026-05-02T16:00:00Z"),
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.deleteVaultItem(remoteItemId) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Conflict,
        )
        coEvery { secureItemRepository.markConflict(logicalItemId, any()) } returns true

        val result = target()

        assertTrue(result is PushLocalVaultChangesResult.Success)
        val success = result as PushLocalVaultChangesResult.Success
        assertEquals(1, success.conflictCount)
        assertEquals(0, success.syncedCount)
    }

    @Test
    fun `invoke when mark conflict fails then returns fatal error`() = runBlocking {
        val logicalItemId = UUID.fromString("9b2f2d34-f5f4-4db3-b2f8-49d89f4f37af")
        val remoteItemId = UUID.fromString("b269f794-29a4-4c37-9e7f-4c0913a94c1a")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.updateVaultItem(remoteItemId, any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Conflict,
        )
        coEvery { secureItemRepository.markConflict(logicalItemId, any()) } returns false

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    logicalItemId = logicalItemId,
                    operation = "MARK_CONFLICT",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when state is synced or conflict then keeps pending without remote calls`() = runBlocking {
        val syncedItem = sampleItem(
            logicalItemId = UUID.fromString("7cc553b8-f260-4a0f-bbd4-5701ae5406b0"),
            remoteItemId = UUID.fromString("cd7ce11c-f283-4a5b-a44a-2f4f19032f8f"),
            syncState = SecureItemSyncState.SYNCED,
        )
        val conflictItem = sampleItem(
            logicalItemId = UUID.fromString("789a5843-cf5c-4b74-a1cf-5fd7ff9a93ab"),
            remoteItemId = UUID.fromString("baf6f9c1-5f81-47eb-b307-643ea2da3eb9"),
            syncState = SecureItemSyncState.CONFLICT,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(syncedItem, conflictItem)

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 2,
                syncedCount = 0,
                conflictCount = 0,
                keptPendingCount = 2,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRemoteRepository.createVaultItem(any()) }
        coVerify(exactly = 0) { secureItemRemoteRepository.updateVaultItem(any(), any()) }
        coVerify(exactly = 0) { secureItemRemoteRepository.deleteVaultItem(any()) }
    }

    @Test
    fun `invoke when create returns conflict then marks conflict`() = runBlocking {
        val logicalItemId = UUID.fromString("4c605158-6565-4659-ba5d-2dc42f4d748f")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = null,
            syncState = SecureItemSyncState.PENDING_CREATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.createVaultItem(any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Conflict,
        )
        coEvery { secureItemRepository.markConflict(logicalItemId, any()) } returns true

        val result = target()

        assertTrue(result is PushLocalVaultChangesResult.Success)
        val success = result as PushLocalVaultChangesResult.Success
        assertEquals(1, success.conflictCount)
    }

    @Test
    fun `invoke when local delete resolution cannot mark synced then returns fatal error`() = runBlocking {
        val logicalItemId = UUID.fromString("a3ca7d88-4c9b-444f-9bd2-e7f52210ba0e")
        val deletedAt = Instant.parse("2026-05-02T17:00:00Z")
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = null,
            syncState = SecureItemSyncState.PENDING_DELETE,
            deletedAt = deletedAt,
            updatedAt = deletedAt,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRepository.markSynced(any(), any(), any(), any(), any(), any()) } returns false

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    logicalItemId = logicalItemId,
                    operation = "LOCAL_DELETE_RESOLUTION",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when delete returns unauthorized then keeps pending`() = runBlocking {
        val remoteItemId = UUID.fromString("d25d0e7d-39ce-4115-8cfe-863b4f44af98")
        val item = sampleItem(
            logicalItemId = UUID.fromString("66ce6c50-9145-4596-8ffa-16185b1f10bc"),
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_DELETE,
            deletedAt = Instant.parse("2026-05-02T18:00:00Z"),
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.deleteVaultItem(remoteItemId) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Unauthorized,
        )

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 0,
                conflictCount = 0,
                keptPendingCount = 1,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
    }

    @Test
    fun `sync mappings should classify pending states and fallback delete timestamp`() {
        assertTrue(SecureItemSyncState.PENDING_CREATE.isPendingPushState())
        assertTrue(SecureItemSyncState.PENDING_UPDATE.isPendingPushState())
        assertTrue(SecureItemSyncState.PENDING_DELETE.isPendingPushState())
        assertTrue(!SecureItemSyncState.SYNCED.isPendingPushState())
        assertTrue(!SecureItemSyncState.CONFLICT.isPendingPushState())

        val updatedAt = Instant.parse("2026-05-02T19:00:00Z")
        val itemWithoutDeletedAt = sampleItem(
            logicalItemId = UUID.fromString("25899d7c-8061-47df-8cfa-0a60fbec8998"),
            remoteItemId = null,
            syncState = SecureItemSyncState.PENDING_DELETE,
            deletedAt = null,
            updatedAt = updatedAt,
        )
        assertEquals(updatedAt, itemWithoutDeletedAt.localDeleteTimestamp())
    }

    private fun sampleItem(
        logicalItemId: UUID,
        remoteItemId: UUID?,
        syncState: SecureItemSyncState,
        payloadVersion: Long = 3,
        deletedAt: Instant? = null,
        updatedAt: Instant = Instant.parse("2026-05-02T09:00:00Z"),
    ): SecureItem = SecureItem(
        logicalItemId = logicalItemId,
        remoteItemId = remoteItemId,
        itemType = SecureItemType.NOTE,
        schemaVersion = 1,
        displayHint = "note",
        payload = byteArrayOf(1, 2, 3),
        payloadVersion = payloadVersion,
        createdAt = Instant.parse("2026-05-02T08:00:00Z"),
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        syncState = syncState,
        lastSyncedAt = null,
        lastSyncError = null,
    )
}
