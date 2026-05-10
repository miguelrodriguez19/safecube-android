package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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
        )
            .allowMainThreadQueries()
            .build()
        target = database.secureItemDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `observeActiveItems when database contains active and deleted rows then emits active rows ordered by updatedAt descending`() = runBlocking {
        val newestActiveItem = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "NOTE",
            payload = byteArrayOf(9, 9, 9),
            updatedAt = Instant.parse("2026-04-09T09:30:00Z"),
        )
        val deletedItem = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "PASSWORD",
            payload = byteArrayOf(8, 8, 8),
            updatedAt = Instant.parse("2026-04-09T09:00:00Z"),
            deletedAt = Instant.parse("2026-04-09T09:45:00Z"),
        )
        val oldestActiveItem = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "PASSWORD",
            payload = byteArrayOf(7, 7, 7),
            updatedAt = Instant.parse("2026-04-09T08:00:00Z"),
        )

        target.insert(oldestActiveItem)
        target.insert(deletedItem)
        target.insert(newestActiveItem)

        val result = target.observeActiveItems().first()

        assertEquals(
            listOf(newestActiveItem.logicalItemId, oldestActiveItem.logicalItemId),
            result.map { it.logicalItemId },
        )
        assertEquals(listOf("NOTE", "PASSWORD"), result.map { it.itemType })
        assertArrayEquals(byteArrayOf(9, 9, 9), result.first().payload)
    }

    @Test
    fun `observeItem when row exists then emits matching secure item entity`() = runBlocking {
        val item = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = UUID.randomUUID(),
            itemType = "PASSWORD",
            payload = byteArrayOf(4, 5, 6),
            updatedAt = Instant.parse("2026-04-09T12:00:00Z"),
        )
        target.insert(item)

        val result = target.observeItem(item.logicalItemId).first()

        assertNotNull(result)
        assertEquals(item.logicalItemId, result?.logicalItemId)
        assertEquals(item.remoteItemId, result?.remoteItemId)
        assertEquals(item.displayHint, result?.displayHint)
        assertArrayEquals(item.payload, result?.payload)
    }

    @Test
    fun `getItem when row exists then returns matching secure item entity`() = runBlocking {
        val item = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "NOTE",
            payload = byteArrayOf(1, 2, 3, 4),
            updatedAt = Instant.parse("2026-04-09T13:00:00Z"),
        )
        target.insert(item)

        val result = target.getItem(item.logicalItemId)

        assertNotNull(result)
        assertEquals(item.logicalItemId, result?.logicalItemId)
        assertEquals(item.itemType, result?.itemType)
        assertEquals(item.payloadVersion, result?.payloadVersion)
        assertEquals(item.createdAt, result?.createdAt)
        assertEquals(item.updatedAt, result?.updatedAt)
        assertArrayEquals(item.payload, result?.payload)
    }

    @Test
    fun `getItem when timestamps contain nanos then preserves full precision`() = runBlocking {
        val createdAt = Instant.parse("2026-05-10T12:38:07.814455123Z")
        val updatedAt = Instant.parse("2026-05-10T12:38:08.999000456Z")
        val deletedAt = Instant.parse("2026-05-10T12:39:09.000123789Z")
        val item = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "NOTE",
            payload = byteArrayOf(1, 2, 3, 4),
            updatedAt = updatedAt,
            deletedAt = deletedAt,
            lastSyncedAt = Instant.parse("2026-05-10T12:40:10.111222333Z"),
        ).copy(createdAt = createdAt)
        target.insert(item)

        val result = target.getItem(item.logicalItemId)

        assertNotNull(result)
        assertEquals(createdAt, result?.createdAt)
        assertEquals(updatedAt, result?.updatedAt)
        assertEquals(deletedAt, result?.deletedAt)
        assertEquals(item.lastSyncedAt, result?.lastSyncedAt)
    }

    @Test
    fun `softDelete when row exists then preserves row and excludes it from active list`() = runBlocking {
        val item = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "PASSWORD",
            payload = byteArrayOf(2, 4, 6),
            updatedAt = Instant.parse("2026-04-09T14:00:00Z"),
        )
        val deletedAt = Instant.parse("2026-04-09T15:00:00Z")
        target.insert(item)

        val updatedRows = target.softDelete(item.logicalItemId, deletedAt, SecureItemSyncStateDb.PENDING_DELETE)
        val activeItems = target.observeActiveItems().first()
        val persistedItem = target.getItem(item.logicalItemId)

        assertEquals(1, updatedRows)
        assertTrue(activeItems.isEmpty())
        assertNotNull(persistedItem)
        assertEquals(deletedAt, persistedItem?.deletedAt)
        assertEquals(deletedAt, persistedItem?.updatedAt)
        assertEquals(SecureItemSyncStateDb.PENDING_DELETE, persistedItem?.syncState)
        assertArrayEquals(item.payload, persistedItem?.payload)
    }

    @Test
    fun `findByRemoteItemId when row exists then returns matching entity`() = runBlocking {
        val remoteItemId = UUID.randomUUID()
        val item = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = remoteItemId,
            itemType = "NOTE",
            payload = byteArrayOf(5, 4, 3),
            updatedAt = Instant.now(),
        )
        target.insert(item)

        val result = target.findByRemoteItemId(remoteItemId)

        assertNotNull(result)
        assertEquals(item.logicalItemId, result?.logicalItemId)
        assertEquals(remoteItemId, result?.remoteItemId)
    }

    @Test
    fun `getPendingSyncItemsOrdered when mixed states then returns only pending sorted by updatedAt ascending`() = runBlocking {
        val now = Instant.now()

        val synced = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "PASSWORD",
            payload = byteArrayOf(1),
            updatedAt = now,
            syncState = SecureItemSyncStateDb.SYNCED,
        )
        val pendingCreate = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "NOTE",
            payload = byteArrayOf(2),
            updatedAt = now.minus(1, ChronoUnit.HOURS),
            syncState = SecureItemSyncStateDb.PENDING_CREATE,
        )
        val pendingDelete = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "PASSWORD",
            payload = byteArrayOf(3),
            updatedAt = now.minus(2, ChronoUnit.HOURS),
            syncState = SecureItemSyncStateDb.PENDING_DELETE,
            deletedAt = now.minus(2, ChronoUnit.HOURS),
        )
        target.insert(synced)
        target.insert(pendingCreate)
        target.insert(pendingDelete)

        val result = target.getPendingSyncItemsOrdered(
            pendingCreateState = SecureItemSyncStateDb.PENDING_CREATE,
            pendingUpdateState = SecureItemSyncStateDb.PENDING_UPDATE,
            pendingDeleteState = SecureItemSyncStateDb.PENDING_DELETE,
        )

        assertEquals(
            listOf(pendingDelete.logicalItemId, pendingCreate.logicalItemId),
            result.map { it.logicalItemId },
        )
        assertTrue(result.all { it.syncState != SecureItemSyncStateDb.SYNCED })
    }

    @Test
    fun `markPendingCreate when row exists then updates sync state and clears error`() = runBlocking {
        val item = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "NOTE",
            payload = byteArrayOf(8, 8, 8),
            updatedAt = Instant.now(),
            syncState = SecureItemSyncStateDb.CONFLICT,
            lastSyncError = "old error",
        )
        target.insert(item)

        val updatedRows = target.markPendingCreate(
            logicalItemId = item.logicalItemId,
            syncState = SecureItemSyncStateDb.PENDING_CREATE,
        )
        val persisted = target.getItem(item.logicalItemId)

        assertEquals(1, updatedRows)
        assertEquals(SecureItemSyncStateDb.PENDING_CREATE, persisted?.syncState)
        assertNull(persisted?.lastSyncError)
    }

    @Test
    fun `markPendingUpdate when row exists then updates sync state and clears error`() = runBlocking {
        val item = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "NOTE",
            payload = byteArrayOf(7, 7, 7),
            updatedAt = Instant.now(),
            syncState = SecureItemSyncStateDb.CONFLICT,
            lastSyncError = "old error",
        )
        target.insert(item)

        val updatedRows = target.markPendingUpdate(
            logicalItemId = item.logicalItemId,
            syncState = SecureItemSyncStateDb.PENDING_UPDATE,
        )
        val persisted = target.getItem(item.logicalItemId)

        assertEquals(1, updatedRows)
        assertEquals(SecureItemSyncStateDb.PENDING_UPDATE, persisted?.syncState)
        assertNull(persisted?.lastSyncError)
    }

    @Test
    fun `markPendingDelete when row exists then updates deletedAt updatedAt and sync state`() = runBlocking {
        val item = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "PASSWORD",
            payload = byteArrayOf(6, 6, 6),
            updatedAt = Instant.now(),
        )
        val deletedAt = item.updatedAt.plus(15, ChronoUnit.MINUTES)
        target.insert(item)

        val updatedRows = target.markPendingDelete(
            logicalItemId = item.logicalItemId,
            deletedAt = deletedAt,
            syncState = SecureItemSyncStateDb.PENDING_DELETE,
        )
        val persisted = target.getItem(item.logicalItemId)
        val activeRows = target.observeActiveItems().first()

        assertEquals(1, updatedRows)
        assertEquals(SecureItemSyncStateDb.PENDING_DELETE, persisted?.syncState)
        assertEquals(deletedAt, persisted?.deletedAt)
        assertEquals(deletedAt, persisted?.updatedAt)
        assertNull(persisted?.lastSyncError)
        assertTrue(activeRows.isEmpty())
    }

    @Test
    fun `markSynced when row exists then updates remote metadata and clears errors`() = runBlocking {
        val item = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "PASSWORD",
            payload = byteArrayOf(1, 3, 5),
            updatedAt = Instant.now(),
            syncState = SecureItemSyncStateDb.PENDING_UPDATE,
            lastSyncError = "old error",
        )
        val remoteItemId = UUID.randomUUID()
        val updatedAt = item.updatedAt.plus(30, ChronoUnit.MINUTES)
        val deletedAt = updatedAt.plus(10, ChronoUnit.MINUTES)
        val lastSyncedAt = updatedAt.plus(11, ChronoUnit.MINUTES)
        target.insert(item)

        val updatedRows = target.markSynced(
            logicalItemId = item.logicalItemId,
            remoteItemId = remoteItemId,
            payloadVersion = 9,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
            syncState = SecureItemSyncStateDb.SYNCED,
            lastSyncedAt = lastSyncedAt,
        )
        val persisted = target.getItem(item.logicalItemId)

        assertEquals(1, updatedRows)
        assertEquals(remoteItemId, persisted?.remoteItemId)
        assertEquals(9L, persisted?.payloadVersion)
        assertEquals(updatedAt, persisted?.updatedAt)
        assertEquals(deletedAt, persisted?.deletedAt)
        assertEquals(SecureItemSyncStateDb.SYNCED, persisted?.syncState)
        assertEquals(lastSyncedAt, persisted?.lastSyncedAt)
        assertNull(persisted?.lastSyncError)
    }

    @Test
    fun `markConflict when row exists then persists conflict state and error`() = runBlocking {
        val item = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = "NOTE",
            payload = byteArrayOf(2, 2, 2),
            updatedAt = Instant.now()
        )
        target.insert(item)

        val updatedRows = target.markConflict(
            logicalItemId = item.logicalItemId,
            syncState = SecureItemSyncStateDb.CONFLICT,
            lastSyncError = "stale write",
        )
        val persisted = target.getItem(item.logicalItemId)

        assertEquals(1, updatedRows)
        assertEquals(SecureItemSyncStateDb.CONFLICT, persisted?.syncState)
        assertEquals("stale write", persisted?.lastSyncError)
    }

    @Test
    fun `applyRemoteDelete when remote row exists then marks row deleted and synced`() = runBlocking {
        val remoteItemId = UUID.randomUUID()
        val item = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = remoteItemId,
            itemType = "NOTE",
            payload = byteArrayOf(4, 4, 4),
            updatedAt = Instant.now(),
            syncState = SecureItemSyncStateDb.PENDING_UPDATE,
            lastSyncError = "old error",
        )
        val deletedAt = item.updatedAt.plus(30, ChronoUnit.MINUTES)
        val lastSyncedAt = deletedAt.plus(31, ChronoUnit.MINUTES)
        target.insert(item)

        val updatedRows = target.applyRemoteDelete(
            remoteItemId = remoteItemId,
            deletedAt = deletedAt,
            syncState = SecureItemSyncStateDb.SYNCED,
            lastSyncedAt = lastSyncedAt,
        )
        val persisted = target.getItem(item.logicalItemId)
        val active = target.observeActiveItems().first()

        assertEquals(1, updatedRows)
        assertEquals(deletedAt, persisted?.deletedAt)
        assertEquals(deletedAt, persisted?.updatedAt)
        assertEquals(SecureItemSyncStateDb.SYNCED, persisted?.syncState)
        assertEquals(lastSyncedAt, persisted?.lastSyncedAt)
        assertNull(persisted?.lastSyncError)
        assertTrue(active.isEmpty())
    }

    @Test
    fun `applyRemoteDelete when remote row does not exist then returns zero rows`() = runBlocking {
        val now = Instant.now()
        val updatedRows = target.applyRemoteDelete(
            remoteItemId = UUID.randomUUID(),
            deletedAt = now,
            syncState = SecureItemSyncStateDb.SYNCED,
            lastSyncedAt = now.plus(1, ChronoUnit.MINUTES),
        )

        assertEquals(0, updatedRows)
        assertTrue(target.observeActiveItems().first().isEmpty())
    }

    @Test
    fun `observeItem when row does not exist then emits null`() = runBlocking {
        val result = target.observeItem(UUID.randomUUID()).first()

        assertNull(result)
    }
}

private fun sampleEntity(
    logicalItemId: UUID,
    remoteItemId: UUID? = UUID.randomUUID(),
    itemType: String,
    payload: ByteArray,
    updatedAt: Instant,
    deletedAt: Instant? = null,
    syncState: SecureItemSyncStateDb = SecureItemSyncStateDb.SYNCED,
    lastSyncedAt: Instant? = null,
    lastSyncError: String? = null,
): SecureItemEntity = SecureItemEntity(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = itemType,
    schemaVersion = 1,
    displayHint = "Example item",
    payload = payload,
    payloadVersion = 1,
    createdAt = Instant.now(),
    updatedAt = updatedAt,
    deletedAt = deletedAt,
    syncState = syncState,
    lastSyncedAt = lastSyncedAt,
    lastSyncError = lastSyncError,
)
