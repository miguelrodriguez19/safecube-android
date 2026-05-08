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
class SecureItemDraftDaoIntegrationTest {

    private lateinit var database: AppDatabase
    private lateinit var target: SecureItemDraftDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
        target = database.secureItemDraftDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `observeDrafts when database contains drafts then emits rows ordered by updatedAt descending`() = runBlocking {
        val now = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        val newestDraft = sampleDraftEntity(
            logicalItemId = UUID.randomUUID(),
            updatedAt = now,
            payload = byteArrayOf(9, 9, 9),
        )
        val oldestDraft = sampleDraftEntity(
            logicalItemId = UUID.randomUUID(),
            updatedAt = now.minus(1, ChronoUnit.HOURS),
            payload = byteArrayOf(1, 1, 1),
            draftType = SecureItemDraftTypeDb.DELETE,
        )
        target.upsert(oldestDraft)
        target.upsert(newestDraft)

        val result = target.observeDrafts().first()

        assertEquals(
            listOf(newestDraft.logicalItemId, oldestDraft.logicalItemId),
            result.map { it.logicalItemId },
        )
        assertEquals(
            listOf(SecureItemDraftTypeDb.UPDATE, SecureItemDraftTypeDb.DELETE),
            result.map { it.draftType },
        )
    }

    @Test
    fun `observeDraft when row exists then emits matching draft entity`() = runBlocking {
        val draft = sampleDraftEntity(
            logicalItemId = UUID.randomUUID(),
            updatedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
            payload = byteArrayOf(4, 5, 6),
        )
        target.upsert(draft)

        val result = target.observeDraft(draft.logicalItemId).first()

        assertNotNull(result)
        assertEquals(draft.logicalItemId, result?.logicalItemId)
        assertEquals(draft.remoteItemId, result?.remoteItemId)
        assertEquals(draft.draftType, result?.draftType)
        assertArrayEquals(draft.payload, result?.payload)
    }

    @Test
    fun `getDraft when row exists then returns matching draft entity`() = runBlocking {
        val draft = sampleDraftEntity(
            logicalItemId = UUID.randomUUID(),
            updatedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
            payload = byteArrayOf(7, 8, 9),
            draftType = SecureItemDraftTypeDb.DELETE,
        )
        target.upsert(draft)

        val result = target.getDraft(draft.logicalItemId)

        assertNotNull(result)
        assertEquals(draft.logicalItemId, result?.logicalItemId)
        assertEquals(draft.basePayloadVersion, result?.basePayloadVersion)
        assertEquals(draft.baseUpdatedAt, result?.baseUpdatedAt)
    }

    @Test
    fun `findByRemoteItemId when row exists then returns matching draft entity`() = runBlocking {
        val remoteItemId = UUID.randomUUID()
        val draft = sampleDraftEntity(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = remoteItemId,
            updatedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
            payload = byteArrayOf(2, 3, 4),
        )
        target.upsert(draft)

        val result = target.findByRemoteItemId(remoteItemId)

        assertNotNull(result)
        assertEquals(draft.logicalItemId, result?.logicalItemId)
        assertEquals(remoteItemId, result?.remoteItemId)
    }

    @Test
    fun `upsert when row already exists then replaces stored draft`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val initialDraft = sampleDraftEntity(
            logicalItemId = logicalItemId,
            updatedAt = Instant.now().minus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS),
            payload = byteArrayOf(1, 2, 3),
        )
        val updatedDraft = initialDraft.copy(
            displayHint = "Updated draft",
            payload = byteArrayOf(3, 2, 1),
            payloadVersion = 5,
            draftType = SecureItemDraftTypeDb.DELETE,
        )
        target.upsert(initialDraft)

        target.upsert(updatedDraft)

        val result = target.getDraft(logicalItemId)

        assertNotNull(result)
        assertEquals("Updated draft", result?.displayHint)
        assertEquals(5L, result?.payloadVersion)
        assertEquals(SecureItemDraftTypeDb.DELETE, result?.draftType)
        assertArrayEquals(byteArrayOf(3, 2, 1), result?.payload)
    }

    @Test
    fun `delete when row exists then removes stored draft`() = runBlocking {
        val draft = sampleDraftEntity(
            logicalItemId = UUID.randomUUID(),
            updatedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS),
            payload = byteArrayOf(6, 6, 6),
        )
        target.upsert(draft)

        val deletedRows = target.delete(draft.logicalItemId)

        assertEquals(1, deletedRows)
        assertNull(target.getDraft(draft.logicalItemId))
        assertTrue(target.observeDrafts().first().isEmpty())
    }
}

private fun sampleDraftEntity(
    logicalItemId: UUID,
    updatedAt: Instant,
    payload: ByteArray,
    remoteItemId: UUID? = UUID.randomUUID(),
    draftType: SecureItemDraftTypeDb = SecureItemDraftTypeDb.UPDATE,
): SecureItemDraftEntity {
    val createdAt = updatedAt.minus(1, ChronoUnit.DAYS).truncatedTo(ChronoUnit.MILLIS)
    val lastSyncedAt = updatedAt.minus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS)
    return SecureItemDraftEntity(
        logicalItemId = logicalItemId,
        remoteItemId = remoteItemId,
        itemType = "NOTE",
        schemaVersion = 1,
        displayHint = "Draft item",
        payload = payload,
        payloadVersion = 2,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = null,
        lastSyncedAt = lastSyncedAt,
        lastSyncError = null,
        draftType = draftType,
        basePayloadVersion = 1,
        baseUpdatedAt = updatedAt.minus(2, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS),
        lastPublishError = null,
    )
}
