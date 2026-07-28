package com.miguelrodriguez19.safecube.core.storage.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.miguelrodriguez19.safecube.core.storage.AppDatabase
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
}
