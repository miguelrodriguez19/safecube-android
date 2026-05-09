package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteCreateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteDeleteSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteUpdateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftPolicyCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.PushLocalVaultChangesUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.isPendingPushState
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.localDeleteTimestamp
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
    private val secureItemDraftPolicyCoordinator = mockk<SecureItemDraftPolicyCoordinator>()

    private val target = PushLocalVaultChangesUseCase(
        secureItemRepository = secureItemRepository,
        secureItemRemoteRepository = secureItemRemoteRepository,
        secureItemDraftPolicyCoordinator = secureItemDraftPolicyCoordinator,
    )

    @Test
    fun `invoke when pending create succeeds then marks synced with new remote id`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val createdAt = Instant.now()
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
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val remoteUpdatedAt = Instant.now()
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
    fun `invoke when pending update returns conflict then stores update draft and applies remote official`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.updateVaultItem(remoteItemId, any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Conflict,
        )
        coEvery { secureItemRemoteRepository.getVaultItem(remoteItemId) } returns SecureItemRemoteResult.Success(
            sampleRemoteItem(
                remoteItemId = remoteItemId,
                updatedAt = updatedAt,
            ),
        )
        coEvery {
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = item,
                remoteItem = any(),
                draftType = SecureItemDraftType.UPDATE,
                lastSyncedAt = updatedAt,
            )
        } returns true

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
        coVerify(exactly = 1) {
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = item,
                remoteItem = any(),
                draftType = SecureItemDraftType.UPDATE,
                lastSyncedAt = updatedAt,
            )
        }
        coVerify(exactly = 0) { secureItemRepository.markConflict(any(), any()) }
    }

    @Test
    fun `invoke when pending update returns not found then applies remote tombstone and counts conflict`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
            updatedAt = updatedAt,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.updateVaultItem(remoteItemId, any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.ItemNotFound,
        )
        coEvery {
            secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                logicalItemId = logicalItemId,
                remoteItemId = remoteItemId,
                deletedAt = updatedAt,
                lastSyncedAt = updatedAt,
            )
        } returns true

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
        coVerify(exactly = 1) {
            secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                logicalItemId = logicalItemId,
                remoteItemId = remoteItemId,
                deletedAt = updatedAt,
                lastSyncedAt = updatedAt,
            )
        }
        coVerify(exactly = 0) { secureItemRepository.markConflict(any(), any()) }
    }

    @Test
    fun `invoke when pending delete with remote id succeeds then marks synced deleted`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val deletedAt = Instant.now()
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_DELETE,
            deletedAt = deletedAt.minusSeconds(10),
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
        val logicalItemId = UUID.randomUUID()
        val deletedAt = Instant.now()
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
    fun `invoke when pending delete returns conflict then stores delete draft and applies remote official`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_DELETE,
            deletedAt = updatedAt.minusSeconds(10),
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.deleteVaultItem(remoteItemId) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Conflict,
        )
        coEvery { secureItemRemoteRepository.getVaultItem(remoteItemId) } returns SecureItemRemoteResult.Success(
            sampleRemoteItem(
                remoteItemId = remoteItemId,
                updatedAt = updatedAt,
            ),
        )
        coEvery {
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = item,
                remoteItem = any(),
                draftType = SecureItemDraftType.DELETE,
                lastSyncedAt = updatedAt,
            )
        } returns true

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
        coVerify(exactly = 1) {
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = item,
                remoteItem = any(),
                draftType = SecureItemDraftType.DELETE,
                lastSyncedAt = updatedAt,
            )
        }
    }

    @Test
    fun `invoke when delete returns not found then resolves as already deleted`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val localDeletedAt = Instant.now()
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
            secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                logicalItemId = logicalItemId,
                remoteItemId = remoteItemId,
                deletedAt = localDeletedAt,
                lastSyncedAt = localDeletedAt,
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
    fun `invoke when create has network error then keeps item pending`() = runBlocking {
        val item = sampleItem(
            logicalItemId = UUID.randomUUID(),
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
    }

    @Test
    fun `invoke when pending create already has remote id then marks conflict`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = UUID.randomUUID(),
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
    }

    @Test
    fun `invoke when pending create gets conflict then marks conflict`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
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
    }

    @Test
    fun `invoke when update conflict cannot fetch remote official then falls back to conflict marker`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.updateVaultItem(remoteItemId, any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Conflict,
        )
        coEvery { secureItemRemoteRepository.getVaultItem(remoteItemId) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Unauthorized,
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
    fun `invoke when pending update has no remote id then marks conflict`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = null,
            syncState = SecureItemSyncState.PENDING_UPDATE,
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
    }

    @Test
    fun `invoke when pending update returns unauthorized then keeps item pending`() = runBlocking {
        val remoteItemId = UUID.randomUUID()
        val item = sampleItem(
            logicalItemId = UUID.randomUUID(),
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
    }

    @Test
    fun `invoke when pending update returns http error then keeps item pending`() = runBlocking {
        val remoteItemId = UUID.randomUUID()
        val item = sampleItem(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.updateVaultItem(remoteItemId, any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.HttpError(
                statusCode = 500,
                errorBody = "{}",
            ),
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
    fun `invoke when delete without remote id cannot be resolved locally then returns fatal error`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val deletedAt = Instant.now()
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
        } returns false

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
    fun `invoke when update draft resolution cannot update local state then returns fatal error`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.updateVaultItem(remoteItemId, any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Conflict,
        )
        coEvery { secureItemRemoteRepository.getVaultItem(remoteItemId) } returns SecureItemRemoteResult.Success(
            sampleRemoteItem(
                remoteItemId = remoteItemId,
                updatedAt = updatedAt,
            ),
        )
        coEvery {
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = item,
                remoteItem = any(),
                draftType = SecureItemDraftType.UPDATE,
                lastSyncedAt = updatedAt,
            )
        } returns false

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    logicalItemId = logicalItemId,
                    operation = "UPDATE_CONFLICT_DRAFT_RESOLUTION",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when delete returns network error then keeps item pending`() = runBlocking {
        val remoteItemId = UUID.randomUUID()
        val item = sampleItem(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_DELETE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.deleteVaultItem(remoteItemId) } returns SecureItemRemoteResult.Error(
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
    }

    @Test
    fun `invoke when delete draft resolution cannot update local state then returns fatal error`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_DELETE,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.deleteVaultItem(remoteItemId) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Conflict,
        )
        coEvery { secureItemRemoteRepository.getVaultItem(remoteItemId) } returns SecureItemRemoteResult.Success(
            sampleRemoteItem(
                remoteItemId = remoteItemId,
                updatedAt = updatedAt,
            ),
        )
        coEvery {
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = item,
                remoteItem = any(),
                draftType = SecureItemDraftType.DELETE,
                lastSyncedAt = updatedAt,
            )
        } returns false

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    logicalItemId = logicalItemId,
                    operation = "DELETE_CONFLICT_DRAFT_RESOLUTION",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when delete not found resolution cannot update local state then returns fatal error`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val deletedAt = Instant.now()
        val item = sampleItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncState.PENDING_DELETE,
            deletedAt = deletedAt,
            updatedAt = deletedAt,
        )
        coEvery { secureItemRepository.getPendingSyncItemsOrdered() } returns listOf(item)
        coEvery { secureItemRemoteRepository.deleteVaultItem(remoteItemId) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.ItemNotFound,
        )
        coEvery {
            secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                logicalItemId = logicalItemId,
                remoteItemId = remoteItemId,
                deletedAt = deletedAt,
                lastSyncedAt = deletedAt,
            )
        } returns false

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.LocalStateUpdateFailed(
                    logicalItemId = logicalItemId,
                    operation = "DELETE_NOT_FOUND_RESOLUTION",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when local mark synced fails then returns fatal error`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val createdAt = Instant.now()
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
    fun `invoke when state is synced or conflict then keeps pending without remote calls`() = runBlocking {
        val syncedItem = sampleItem(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = UUID.randomUUID(),
            syncState = SecureItemSyncState.SYNCED,
        )
        val conflictItem = sampleItem(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = UUID.randomUUID(),
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
    fun `sync mappings should classify pending states and fallback delete timestamp`() {
        assertTrue(SecureItemSyncState.PENDING_CREATE.isPendingPushState())
        assertTrue(SecureItemSyncState.PENDING_UPDATE.isPendingPushState())
        assertTrue(SecureItemSyncState.PENDING_DELETE.isPendingPushState())
        assertTrue(!SecureItemSyncState.SYNCED.isPendingPushState())
        assertTrue(!SecureItemSyncState.CONFLICT.isPendingPushState())

        val updatedAt = Instant.now()
        val itemWithoutDeletedAt = sampleItem(
            logicalItemId = UUID.randomUUID(),
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
        updatedAt: Instant = Instant.now(),
    ): SecureItem = SecureItem(
        logicalItemId = logicalItemId,
        remoteItemId = remoteItemId,
        itemType = SecureItemType.NOTE,
        schemaVersion = 1,
        displayHint = "note",
        payload = byteArrayOf(1, 2, 3),
        payloadVersion = payloadVersion,
        createdAt = updatedAt.minusSeconds(3600),
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        syncState = syncState,
        lastSyncedAt = null,
        lastSyncError = null,
    )

    private fun sampleRemoteItem(
        remoteItemId: UUID,
        updatedAt: Instant,
    ): RemoteSecureItem = RemoteSecureItem(
        itemId = remoteItemId,
        itemType = SecureItemType.NOTE.wireName,
        schemaVersion = 1,
        displayHint = "remote",
        payload = byteArrayOf(9, 8, 7),
        payloadVersion = 7,
        updatedAt = updatedAt,
        deletedAt = null,
    )
}
