package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.AppDatabase
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftSyncStatusDb
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureItemDraftLocalStorageTest {
    private val secureItemDraftDao = mockk<SecureItemDraftDao>()
    private val appDatabase = mockk<AppDatabase>()
    private val secureItemDraftEntityMapper = mockk<SecureItemDraftEntityMapper>()
    private val target = SecureItemDraftLocalStorage(
        appDatabase = appDatabase,
        secureItemDraftDao = secureItemDraftDao,
        secureItemDraftEntityMapper = secureItemDraftEntityMapper,
    )

    @Test
    fun `observe drafts maps dao entities into domain drafts`() = runBlocking {
        val entity = localSampleDraftEntity()
        val draft = localSampleDomainDraft()
        every { secureItemDraftDao.observeDrafts() } returns flowOf(listOf(entity))
        every { secureItemDraftEntityMapper.toDomain(entity) } returns draft

        val result = target.observeDrafts().first()

        assertEquals(listOf(draft), result)
    }

    @Test
    fun `get syncable drafts ordered delegates requested status`() = runBlocking {
        val entity = localSampleDraftEntity(draftSyncStatus = SecureItemDraftSyncStatusDb.READY_TO_SYNC)
        val draft = localSampleDomainDraft(draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC)
        coEvery { secureItemDraftDao.getDraftsBySyncStatus(SecureItemDraftSyncStatusDb.READY_TO_SYNC) } returns listOf(entity)
        every { secureItemDraftEntityMapper.toDomain(entity) } returns draft

        val result = target.getSyncableDraftsOrdered(SecureItemDraftSyncStatus.READY_TO_SYNC)

        assertEquals(listOf(draft), result)
    }

    @Test
    fun `update status returns true when dao updates one row`() = runBlocking {
        val logicalItemId = java.util.UUID.randomUUID()
        coEvery {
            secureItemDraftDao.updateStatus(logicalItemId, SecureItemDraftSyncStatusDb.CONFLICT, "Conflict")
        } returns 1

        val result = target.updateStatus(logicalItemId, SecureItemDraftSyncStatus.CONFLICT, "Conflict")

        assertTrue(result)
        coVerify(exactly = 1) {
            secureItemDraftDao.updateStatus(logicalItemId, SecureItemDraftSyncStatusDb.CONFLICT, "Conflict")
        }
    }

    @Test
    fun `single draft reads map present entities and preserve absence`() = runBlocking {
        val logicalId = UUID.randomUUID()
        val remoteId = UUID.randomUUID()
        val entity = localSampleDraftEntity(logicalItemId = logicalId, remoteItemId = remoteId)
        val draft = localSampleDomainDraft(logicalItemId = logicalId, remoteItemId = remoteId)
        every { secureItemDraftEntityMapper.toDomain(entity) } returns draft

        every { secureItemDraftDao.observeDraft(logicalId) } returns flowOf(null)
        assertNull(target.observeDraft(logicalId).first())
        every { secureItemDraftDao.observeDraft(logicalId) } returns flowOf(entity)
        assertEquals(draft, target.observeDraft(logicalId).first())

        coEvery { secureItemDraftDao.getDraft(logicalId) } returns null
        assertNull(target.getDraft(logicalId))
        coEvery { secureItemDraftDao.getDraft(logicalId) } returns entity
        assertEquals(draft, target.getDraft(logicalId))

        coEvery { secureItemDraftDao.findByRemoteItemId(remoteId) } returns null
        assertNull(target.findByRemoteItemId(remoteId))
        coEvery { secureItemDraftDao.findByRemoteItemId(remoteId) } returns entity
        assertEquals(draft, target.findByRemoteItemId(remoteId))
    }

    @Test
    fun `upsert status and delete delegate mapped values and row counts`() = runBlocking {
        val draft = localSampleDomainDraft()
        val entity = localSampleDraftEntity(logicalItemId = draft.logicalItemId)
        every { secureItemDraftEntityMapper.toEntity(draft) } returns entity
        coJustRun { secureItemDraftDao.upsert(entity) }
        target.upsert(draft)

        coEvery {
            secureItemDraftDao.updateStatus(
                draft.logicalItemId,
                SecureItemDraftSyncStatusDb.CONFLICT,
                null,
            )
        } returns 0
        assertFalse(
            target.updateStatus(
                draft.logicalItemId,
                SecureItemDraftSyncStatus.CONFLICT,
                null,
            ),
        )

        coEvery { secureItemDraftDao.delete(draft.logicalItemId) } returnsMany listOf(0, 1)
        assertFalse(target.delete(draft.logicalItemId))
        assertTrue(target.delete(draft.logicalItemId))
    }
}

private fun localSampleDraftEntity(
    logicalItemId: UUID = UUID.randomUUID(),
    remoteItemId: UUID? = UUID.randomUUID(),
    draftSyncStatus: SecureItemDraftSyncStatusDb = SecureItemDraftSyncStatusDb.READY_TO_SYNC,
) = com.miguelrodriguez19.safecube.core.storage.SecureItemDraftEntity(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType.NOTE.wireName,
    schemaVersion = 1,
    displayHint = "Draft item",
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 2,
    mutationId = java.util.UUID.randomUUID(),
    createdAt = Instant.parse("2024-07-03T09:59:00Z"),
    updatedAt = Instant.parse("2024-07-03T10:00:00Z"),
    deletedAt = null,
    lastSyncedAt = Instant.parse("2024-07-03T09:59:30Z"),
    draftType = com.miguelrodriguez19.safecube.core.storage.SecureItemDraftTypeDb.UPDATE,
    draftSyncStatus = draftSyncStatus,
    baseItemRevision = 1,
    lastSyncError = null,
)

private fun localSampleDomainDraft(
    logicalItemId: UUID = UUID.randomUUID(),
    remoteItemId: UUID? = UUID.randomUUID(),
    draftSyncStatus: SecureItemDraftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
) = com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType.NOTE,
    schemaVersion = 1,
    displayHint = "Draft item",
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 2,
    mutationId = java.util.UUID.randomUUID(),
    createdAt = Instant.parse("2024-07-03T09:59:00Z"),
    updatedAt = Instant.parse("2024-07-03T10:00:00Z"),
    deletedAt = null,
    lastSyncedAt = Instant.parse("2024-07-03T09:59:30Z"),
    draftType = com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType.UPDATE,
    draftSyncStatus = draftSyncStatus,
    baseItemRevision = 1,
    lastSyncError = null,
)
