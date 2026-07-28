package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SecureItemDraftDaoIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var target: SecureItemDraftDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        target = database.secureItemDraftDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `observe drafts returns rows ordered by updatedAt descending`() = runBlocking {
        val oldest = sampleDraftEntity(
            logicalItemId = UUID.randomUUID(),
            updatedAt = Instant.parse("2024-07-02T10:00:00Z"),
            draftType = SecureItemDraftTypeDb.UPDATE,
        )
        val newest = sampleDraftEntity(
            logicalItemId = UUID.randomUUID(),
            updatedAt = Instant.parse("2024-07-02T12:00:00Z"),
            draftType = SecureItemDraftTypeDb.CREATE,
        )
        target.upsert(oldest)
        target.upsert(newest)

        val result = target.observeDrafts().first()

        assertEquals(listOf(newest.logicalItemId, oldest.logicalItemId), result.map { it.logicalItemId })
    }

    @Test
    fun `get drafts by sync status filters and sorts ascending`() = runBlocking {
        val olderReady = sampleDraftEntity(
            logicalItemId = UUID.randomUUID(),
            updatedAt = Instant.parse("2024-07-02T09:00:00Z"),
            draftSyncStatus = SecureItemDraftSyncStatusDb.READY_TO_SYNC,
        )
        val conflict = sampleDraftEntity(
            logicalItemId = UUID.randomUUID(),
            updatedAt = Instant.parse("2024-07-02T10:00:00Z"),
            draftSyncStatus = SecureItemDraftSyncStatusDb.CONFLICT,
            lastSyncError = "Conflict",
        )
        val newerReady = sampleDraftEntity(
            logicalItemId = UUID.randomUUID(),
            updatedAt = Instant.parse("2024-07-02T11:00:00Z"),
            draftSyncStatus = SecureItemDraftSyncStatusDb.READY_TO_SYNC,
        )
        target.upsert(newerReady)
        target.upsert(conflict)
        target.upsert(olderReady)

        val result = target.getDraftsBySyncStatus(SecureItemDraftSyncStatusDb.READY_TO_SYNC)

        assertEquals(listOf(olderReady.logicalItemId, newerReady.logicalItemId), result.map { it.logicalItemId })
    }

    @Test
    fun `update status persists conflict metadata`() = runBlocking {
        val draft = sampleDraftEntity()
        target.upsert(draft)

        val updatedRows = target.updateStatus(
            logicalItemId = draft.logicalItemId,
            draftSyncStatus = SecureItemDraftSyncStatusDb.CONFLICT,
            lastSyncError = "Conflict",
        )
        val persisted = target.getDraft(draft.logicalItemId)

        assertEquals(1, updatedRows)
        assertEquals(SecureItemDraftSyncStatusDb.CONFLICT, persisted?.draftSyncStatus)
        assertEquals("Conflict", persisted?.lastSyncError)
        assertTrue(target.delete(draft.logicalItemId) == 1)
    }
}

private fun sampleDraftEntity(
    logicalItemId: UUID = UUID.randomUUID(),
    remoteItemId: UUID? = UUID.randomUUID(),
    updatedAt: Instant = Instant.parse("2024-07-02T10:00:00Z"),
    draftType: SecureItemDraftTypeDb = SecureItemDraftTypeDb.UPDATE,
    draftSyncStatus: SecureItemDraftSyncStatusDb = SecureItemDraftSyncStatusDb.READY_TO_SYNC,
    lastSyncError: String? = null,
): SecureItemDraftEntity = SecureItemDraftEntity(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = "NOTE",
    schemaVersion = 1,
    displayHint = "Draft item",
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 2,
    mutationId = UUID.randomUUID(),
    createdAt = updatedAt.minusSeconds(60),
    updatedAt = updatedAt,
    deletedAt = null,
    lastSyncedAt = updatedAt.minusSeconds(30),
    draftType = draftType,
    draftSyncStatus = draftSyncStatus,
    baseItemRevision = if (draftType == SecureItemDraftTypeDb.CREATE) null else 1,
    lastSyncError = lastSyncError,
)
