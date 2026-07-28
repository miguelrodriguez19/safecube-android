package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.AppDatabase
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftSyncStatusDb
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
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
}

private fun localSampleDraftEntity(
    draftSyncStatus: SecureItemDraftSyncStatusDb = SecureItemDraftSyncStatusDb.READY_TO_SYNC,
) = com.miguelrodriguez19.safecube.core.storage.SecureItemDraftEntity(
    logicalItemId = java.util.UUID.randomUUID(),
    remoteItemId = java.util.UUID.randomUUID(),
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
    draftSyncStatus: SecureItemDraftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
) = com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft(
    logicalItemId = java.util.UUID.randomUUID(),
    remoteItemId = java.util.UUID.randomUUID(),
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
