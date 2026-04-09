package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import java.time.Instant
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
            logicalItemId = UUID.fromString("c555a6fc-df5d-4597-a211-45ea08e2a034"),
            itemType = "NOTE",
            payload = byteArrayOf(9, 9, 9),
            updatedAt = Instant.parse("2026-04-09T09:30:00Z"),
        )
        val deletedItem = sampleEntity(
            logicalItemId = UUID.fromString("62662317-ef51-4b48-ab7a-7ef44f34c241"),
            itemType = "PASSWORD",
            payload = byteArrayOf(8, 8, 8),
            updatedAt = Instant.parse("2026-04-09T09:00:00Z"),
            deletedAt = Instant.parse("2026-04-09T09:45:00Z"),
        )
        val oldestActiveItem = sampleEntity(
            logicalItemId = UUID.fromString("e7b39efd-f819-45f3-8c7c-27ac46fd7301"),
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
            logicalItemId = UUID.fromString("489ed0ea-fcf7-42aa-91cc-7169cb1e59b8"),
            remoteItemId = UUID.fromString("dfba8694-5f4c-4c81-a37a-8d6d8068bd5c"),
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
            logicalItemId = UUID.fromString("3c5a8dda-cf81-4a6d-b2d7-63bc97f8786f"),
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
    fun `softDelete when row exists then preserves row and excludes it from active list`() = runBlocking {
        val item = sampleEntity(
            logicalItemId = UUID.fromString("d32f0b88-cf7e-42ae-aa94-ee361d399622"),
            itemType = "PASSWORD",
            payload = byteArrayOf(2, 4, 6),
            updatedAt = Instant.parse("2026-04-09T14:00:00Z"),
        )
        val deletedAt = Instant.parse("2026-04-09T15:00:00Z")
        target.insert(item)

        val updatedRows = target.softDelete(item.logicalItemId, deletedAt)
        val activeItems = target.observeActiveItems().first()
        val persistedItem = target.getItem(item.logicalItemId)

        assertEquals(1, updatedRows)
        assertTrue(activeItems.isEmpty())
        assertNotNull(persistedItem)
        assertEquals(deletedAt, persistedItem?.deletedAt)
        assertEquals(deletedAt, persistedItem?.updatedAt)
        assertArrayEquals(item.payload, persistedItem?.payload)
    }

    @Test
    fun `observeItem when row does not exist then emits null`() = runBlocking {
        val result = target.observeItem(UUID.fromString("5c6d2f84-44e1-496d-b40a-055695328301")).first()

        assertNull(result)
    }
}

private fun sampleEntity(
    logicalItemId: UUID,
    remoteItemId: UUID? = UUID.fromString("6e177701-a2c8-44f2-a69b-f62df543155c"),
    itemType: String,
    payload: ByteArray,
    updatedAt: Instant,
    deletedAt: Instant? = null,
): SecureItemEntity = SecureItemEntity(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = itemType,
    schemaVersion = 1,
    displayHint = "Example item",
    payload = payload,
    payloadVersion = 1,
    createdAt = Instant.parse("2026-04-09T07:00:00Z"),
    updatedAt = updatedAt,
    deletedAt = deletedAt,
)
