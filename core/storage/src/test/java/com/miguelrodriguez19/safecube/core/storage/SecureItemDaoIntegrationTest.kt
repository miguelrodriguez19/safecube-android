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
class SecureItemDaoIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var target: SecureItemDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        target = database.secureItemDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `observe active items returns non deleted rows ordered by updatedAt descending`() = runBlocking {
        val activeNewest = sampleEntity(updatedAt = Instant.parse("2024-07-01T12:00:00Z"))
        val deleted = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = UUID.randomUUID(),
            updatedAt = Instant.parse("2024-07-01T11:00:00Z"),
            deletedAt = Instant.parse("2024-07-01T11:30:00Z"),
        )
        val activeOldest = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = UUID.randomUUID(),
            updatedAt = Instant.parse("2024-07-01T10:00:00Z"),
        )
        target.upsert(activeOldest)
        target.upsert(deleted)
        target.upsert(activeNewest)

        val result = target.observeActiveItems().first()

        assertEquals(listOf(activeNewest.logicalItemId, activeOldest.logicalItemId), result.map { it.logicalItemId })
    }

    @Test
    fun `apply remote delete marks official row as deleted and synced`() = runBlocking {
        val entity = sampleEntity()
        val deletedAt = Instant.parse("2024-07-01T15:00:00Z")
        target.upsert(entity)

        val updatedRows = target.applyRemoteDelete(
            remoteItemId = requireNotNull(entity.remoteItemId),
            deletedAt = deletedAt,
            itemRevision = 2,
            changeSequence = 2,
            syncState = SecureItemSyncStateDb.SYNCED,
            lastSyncedAt = deletedAt,
        )
        val persisted = target.getItem(entity.logicalItemId)

        assertEquals(1, updatedRows)
        assertEquals(deletedAt, persisted?.deletedAt)
        assertEquals(SecureItemSyncStateDb.SYNCED, persisted?.syncState)
        assertEquals(deletedAt, persisted?.lastSyncedAt)
    }
}

private fun sampleEntity(
    logicalItemId: UUID = UUID.randomUUID(),
    remoteItemId: UUID? = UUID.randomUUID(),
    updatedAt: Instant = Instant.parse("2024-07-01T10:00:00Z"),
    deletedAt: Instant? = null,
): SecureItemEntity = SecureItemEntity(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = "NOTE",
    schemaVersion = 1,
    displayHint = "Official item",
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 1,
    itemRevision = 1,
    changeSequence = 1,
    createdAt = updatedAt.minusSeconds(60),
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncState = SecureItemSyncStateDb.SYNCED,
    lastSyncedAt = updatedAt,
    lastSyncError = null,
)
