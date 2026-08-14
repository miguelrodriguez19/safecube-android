package com.miguelrodriguez19.safecube.core.storage.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.miguelrodriguez19.safecube.core.storage.AppDatabase
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointEntity
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SecureItemOfficializationIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var target: SecureItemLocalStorage
    private val mapper = SecureItemDraftEntityMapper()

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        ).allowMainThreadQueries().build()
        target = SecureItemLocalStorage(
            appDatabase = database,
            secureItemDao = database.secureItemDao(),
            secureItemDraftDao = database.secureItemDraftDao(),
            secureItemSyncCheckpointDao = database.secureItemSyncCheckpointDao(),
            secureItemDraftEntityMapper = mapper,
        )
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `officialize persists official and deletes draft in one transaction`() = runBlocking {
        val draft = draft()
        database.secureItemDraftDao().upsert(mapper.toEntity(draft))

        assertTrue(target.officializeDraft(official(draft), draft.updatedAt))
        assertNotNull(database.secureItemDao().getItem(draft.logicalItemId))
        assertNull(database.secureItemDraftDao().getDraft(draft.logicalItemId))
    }

    @Test
    fun `officialize rolls official back when matching draft is missing`() = runBlocking {
        val draft = draft()

        assertFalse(target.officializeDraft(official(draft), draft.updatedAt))
        assertNull(database.secureItemDao().getItem(draft.logicalItemId))
    }

    @Test
    fun `clear all local data removes officials drafts and checkpoints atomically`() = runBlocking {
        val draft = seedLocalVault()

        target.clearAllLocalData()
        target.clearAllLocalData()

        assertNull(database.secureItemDao().getItem(draft.logicalItemId))
        assertNull(database.secureItemDraftDao().getDraft(draft.logicalItemId))
        assertNull(
            database.secureItemSyncCheckpointDao()
                .getLastAppliedChangeSequence(CHECKPOINT_ACCOUNT_ID),
        )
    }

    @Test
    fun `clear all local data rolls back every table when one delete fails`() = runBlocking {
        val draft = seedLocalVault()
        database.openHelper.writableDatabase.execSQL(
            """
            CREATE TRIGGER fail_checkpoint_delete
            BEFORE DELETE ON secure_item_sync_checkpoints
            BEGIN
                SELECT RAISE(ABORT, 'forced checkpoint delete failure');
            END
            """.trimIndent(),
        )

        val result = runCatching { target.clearAllLocalData() }

        assertTrue(result.isFailure)
        assertNotNull(database.secureItemDao().getItem(draft.logicalItemId))
        assertNotNull(database.secureItemDraftDao().getDraft(draft.logicalItemId))
        assertNotNull(
            database.secureItemSyncCheckpointDao()
                .getLastAppliedChangeSequence(CHECKPOINT_ACCOUNT_ID),
        )
    }

    @Test
    fun `apply remote page rolls back items drafts and checkpoint when checkpoint write fails`() = runBlocking {
        val draft = draft()
        val previousOfficial = official(draft)
        val nextOfficial = previousOfficial.copy(
            payload = byteArrayOf(9, 9, 9),
            itemRevision = previousOfficial.itemRevision + 1,
            changeSequence = previousOfficial.changeSequence + 1,
        )
        assertTrue(target.applyRemoteUpsert(previousOfficial, previousOfficial.updatedAt))
        database.secureItemDraftDao().upsert(mapper.toEntity(draft))
        database.secureItemSyncCheckpointDao().upsert(
            SecureItemSyncCheckpointEntity(
                accountId = CHECKPOINT_ACCOUNT_ID,
                lastAppliedChangeSequence = previousOfficial.changeSequence,
            ),
        )
        database.openHelper.writableDatabase.execSQL(
            """
            CREATE TRIGGER fail_checkpoint_insert
            BEFORE INSERT ON secure_item_sync_checkpoints
            BEGIN
                SELECT RAISE(ABORT, 'forced checkpoint insert failure');
            END
            """.trimIndent(),
        )

        assertFalse(
            target.applyRemotePage(
                accountId = CHECKPOINT_ACCOUNT_ID,
                items = listOf(nextOfficial),
                conflictedDrafts = emptyList(),
                draftsToDelete = emptySet(),
                lastAppliedChangeSequence = nextOfficial.changeSequence,
                lastSyncedAt = nextOfficial.updatedAt,
            ),
        )
        val persistedOfficial = database.secureItemDao().getItem(draft.logicalItemId)
        assertNotNull(persistedOfficial)
        assertEquals(previousOfficial.itemRevision, persistedOfficial?.itemRevision)
        assertEquals(previousOfficial.changeSequence, persistedOfficial?.changeSequence)
        assertArrayEquals(previousOfficial.payload, persistedOfficial?.payload)
        assertNotNull(database.secureItemDraftDao().getDraft(draft.logicalItemId))
        assertEquals(
            previousOfficial.changeSequence,
            database.secureItemSyncCheckpointDao()
                .getLastAppliedChangeSequence(CHECKPOINT_ACCOUNT_ID),
        )
    }

    private suspend fun seedLocalVault(): SecureItemSyncDraft {
        val draft = draft()
        assertTrue(target.applyRemoteUpsert(official(draft), draft.updatedAt))
        database.secureItemDraftDao().upsert(mapper.toEntity(draft))
        database.secureItemSyncCheckpointDao().upsert(
            SecureItemSyncCheckpointEntity(
                accountId = CHECKPOINT_ACCOUNT_ID,
                lastAppliedChangeSequence = 9,
            ),
        )
        return draft
    }

    private fun draft(): SecureItemSyncDraft {
        val createdAt = Instant.parse("2026-01-01T10:00:00Z")
        return SecureItemSyncDraft(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = UUID.randomUUID(),
            itemType = SecureItemType.NOTE,
            schemaVersion = 1,
            displayHint = "Draft",
            payload = byteArrayOf(1, 2, 3),
            payloadVersion = 2,
            createdAt = createdAt,
            updatedAt = createdAt.plusSeconds(60),
            mutationId = UUID.randomUUID(),
            draftType = SecureItemDraftType.UPDATE,
            draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
            baseItemRevision = 1,
        )
    }

    private fun official(draft: SecureItemSyncDraft): SecureItem = SecureItem(
        logicalItemId = draft.logicalItemId,
        remoteItemId = draft.remoteItemId,
        itemType = draft.itemType,
        schemaVersion = draft.schemaVersion,
        displayHint = draft.displayHint,
        payload = draft.payload,
        payloadVersion = draft.payloadVersion,
        itemRevision = 2,
        changeSequence = 9,
        createdAt = draft.createdAt,
        updatedAt = draft.updatedAt,
    )

    private companion object {
        val CHECKPOINT_ACCOUNT_ID: UUID =
            UUID.fromString("c9525655-4d94-4b87-b39f-47a337f8e50b")
    }
}
