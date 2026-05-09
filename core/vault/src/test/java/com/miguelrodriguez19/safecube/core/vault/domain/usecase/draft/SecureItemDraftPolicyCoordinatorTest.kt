package com.miguelrodriguez19.safecube.core.vault.domain.usecase.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftPolicyCoordinator
import io.mockk.coEvery
import io.mockk.coVerifyOrder
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureItemDraftPolicyCoordinatorTest {
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()

    private val target = SecureItemDraftPolicyCoordinator(
        secureItemRepository = secureItemRepository,
        secureItemDraftRepository = secureItemDraftRepository,
    )

    @Test
    fun `replaceOfficialItemWithRemoteAndDraft when local and remote items are valid then stores draft before applying remote official`() = runBlocking {
        val localItem = sampleSecureItem(syncState = SecureItemSyncState.PENDING_UPDATE)
        val remoteItem = sampleSecureItem(
            logicalItemId = localItem.logicalItemId,
            remoteItemId = localItem.remoteItemId,
            syncState = SecureItemSyncState.SYNCED,
        )
        val lastSyncedAt = Instant.now()
        coEvery { secureItemDraftRepository.upsert(any()) } returns Unit
        coEvery { secureItemRepository.applyRemoteUpsert(remoteItem, lastSyncedAt) } returns true

        val result = target.replaceOfficialItemWithRemoteAndDraft(
            localItem = localItem,
            remoteItem = remoteItem,
            draftType = SecureItemDraftType.UPDATE,
            lastSyncedAt = lastSyncedAt,
        )

        assertTrue(result)
        coVerifyOrder {
            secureItemDraftRepository.upsert(any())
            secureItemRepository.applyRemoteUpsert(remoteItem, lastSyncedAt)
        }
    }

    @Test
    fun `replaceOfficialItemWithRemoteAndDraft when remote official cannot be applied then returns false`() = runBlocking {
        val localItem = sampleSecureItem(syncState = SecureItemSyncState.PENDING_UPDATE)
        val remoteItem = sampleSecureItem(
            logicalItemId = localItem.logicalItemId,
            remoteItemId = localItem.remoteItemId,
            syncState = SecureItemSyncState.SYNCED,
        )
        val lastSyncedAt = Instant.now()
        coEvery { secureItemDraftRepository.upsert(any()) } returns Unit
        coEvery { secureItemRepository.applyRemoteUpsert(remoteItem, lastSyncedAt) } returns false

        val result = target.replaceOfficialItemWithRemoteAndDraft(
            localItem = localItem,
            remoteItem = remoteItem,
            draftType = SecureItemDraftType.UPDATE,
            lastSyncedAt = lastSyncedAt,
        )

        assertFalse(result)
    }

    @Test
    fun `applyRemoteDeleteAndDiscardLocalChanges when draft exists then applies delete and removes draft`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val deletedAt = Instant.now()
        val draft = sampleDraft(logicalItemId = logicalItemId, remoteItemId = remoteItemId)
        coEvery { secureItemRepository.applyRemoteDelete(remoteItemId, deletedAt, deletedAt) } returns true
        coEvery { secureItemDraftRepository.getDraft(logicalItemId) } returns draft
        coEvery { secureItemDraftRepository.delete(logicalItemId) } returns true

        val result = target.applyRemoteDeleteAndDiscardLocalChanges(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            deletedAt = deletedAt,
            lastSyncedAt = deletedAt,
        )

        assertTrue(result)
        coVerifyOrder {
            secureItemRepository.applyRemoteDelete(remoteItemId, deletedAt, deletedAt)
            secureItemDraftRepository.getDraft(logicalItemId)
            secureItemDraftRepository.delete(logicalItemId)
        }
    }

    @Test
    fun `applyRemoteDeleteAndDiscardLocalChanges when draft does not exist then succeeds without delete`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val deletedAt = Instant.now()
        coEvery { secureItemRepository.applyRemoteDelete(remoteItemId, deletedAt, deletedAt) } returns true
        coEvery { secureItemDraftRepository.getDraft(logicalItemId) } returns null

        val result = target.applyRemoteDeleteAndDiscardLocalChanges(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            deletedAt = deletedAt,
            lastSyncedAt = deletedAt,
        )

        assertTrue(result)
    }

    @Test
    fun `applyRemoteDeleteAndDiscardLocalChanges when remote delete cannot be applied then returns false`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val deletedAt = Instant.now()
        coEvery { secureItemRepository.applyRemoteDelete(remoteItemId, deletedAt, deletedAt) } returns false

        val result = target.applyRemoteDeleteAndDiscardLocalChanges(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            deletedAt = deletedAt,
            lastSyncedAt = deletedAt,
        )

        assertFalse(result)
    }

    @Test
    fun `finalizePublishedUpdate when remote apply succeeds then applies official version and deletes draft`() = runBlocking {
        val draft = sampleDraft()
        val remoteUpdatedAt = Instant.now()
        coEvery { secureItemRepository.applyRemoteUpsert(any(), remoteUpdatedAt) } returns true
        coEvery { secureItemDraftRepository.delete(draft.logicalItemId) } returns true

        val result = target.finalizePublishedUpdate(
            draft = draft,
            remotePayloadVersion = 6,
            remoteUpdatedAt = remoteUpdatedAt,
        )

        assertTrue(result)
        coVerifyOrder {
            secureItemRepository.applyRemoteUpsert(any(), remoteUpdatedAt)
            secureItemDraftRepository.delete(draft.logicalItemId)
        }
    }

    @Test
    fun `finalizePublishedUpdate when official row cannot be updated then returns false`() = runBlocking {
        val draft = sampleDraft()
        val remoteUpdatedAt = Instant.now()
        coEvery { secureItemRepository.applyRemoteUpsert(any(), remoteUpdatedAt) } returns false

        val result = target.finalizePublishedUpdate(
            draft = draft,
            remotePayloadVersion = 6,
            remoteUpdatedAt = remoteUpdatedAt,
        )

        assertFalse(result)
    }

    @Test
    fun `finalizePublishedDelete when remote delete can be applied then deletes draft and returns true`() = runBlocking {
        val draft = sampleDraft()
        val deletedAt = Instant.now()
        coEvery { secureItemRepository.applyRemoteDelete(requireNotNull(draft.remoteItemId), deletedAt, deletedAt) } returns true
        coEvery { secureItemDraftRepository.delete(draft.logicalItemId) } returns true

        val result = target.finalizePublishedDelete(
            draft = draft,
            deletedAt = deletedAt,
        )

        assertTrue(result)
        coVerifyOrder {
            secureItemRepository.applyRemoteDelete(requireNotNull(draft.remoteItemId), deletedAt, deletedAt)
            secureItemDraftRepository.delete(draft.logicalItemId)
        }
    }

    @Test
    fun `finalizePublishedDelete when draft has no remote id then returns false`() = runBlocking {
        val draft = sampleDraft(remoteItemId = null)

        val result = target.finalizePublishedDelete(
            draft = draft,
            deletedAt = Instant.now(),
        )

        assertFalse(result)
    }

    @Test
    fun `finalizePublishedDelete when official tombstone cannot be applied then returns false`() = runBlocking {
        val draft = sampleDraft()
        val deletedAt = Instant.now()
        coEvery { secureItemRepository.applyRemoteDelete(requireNotNull(draft.remoteItemId), deletedAt, deletedAt) } returns false

        val result = target.finalizePublishedDelete(
            draft = draft,
            deletedAt = deletedAt,
        )

        assertFalse(result)
    }

    @Test
    fun `discardDraft when repository removes draft then returns true`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDraftRepository.delete(logicalItemId) } returns true

        val result = target.discardDraft(logicalItemId)

        assertTrue(result)
    }

    @Test
    fun `discardDraft when repository cannot remove draft then returns false`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDraftRepository.delete(logicalItemId) } returns false

        val result = target.discardDraft(logicalItemId)

        assertFalse(result)
    }

    private fun sampleSecureItem(
        logicalItemId: UUID = UUID.randomUUID(),
        remoteItemId: UUID? = UUID.randomUUID(),
        syncState: SecureItemSyncState,
    ): SecureItem {
        val updatedAt = Instant.now()
        return SecureItem(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            itemType = SecureItemType.NOTE,
            schemaVersion = 1,
            displayHint = "item",
            payload = byteArrayOf(1, 2, 3),
            payloadVersion = 2,
            createdAt = updatedAt.minusSeconds(3600),
            updatedAt = updatedAt,
            deletedAt = null,
            syncState = syncState,
            lastSyncedAt = null,
            lastSyncError = null,
        )
    }

    private fun sampleDraft(
        logicalItemId: UUID = UUID.randomUUID(),
        remoteItemId: UUID? = UUID.randomUUID(),
    ): SecureItemSyncDraft {
        val updatedAt = Instant.now()
        return SecureItemSyncDraft(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            itemType = SecureItemType.NOTE,
            schemaVersion = 1,
            displayHint = "draft",
            payload = byteArrayOf(9, 8, 7),
            payloadVersion = 3,
            createdAt = updatedAt.minusSeconds(3600),
            updatedAt = updatedAt,
            deletedAt = null,
            lastSyncedAt = null,
            lastSyncError = null,
            draftType = SecureItemDraftType.UPDATE,
            basePayloadVersion = 2,
            baseUpdatedAt = updatedAt.minusSeconds(60),
            lastPublishError = null,
        )
    }
}
