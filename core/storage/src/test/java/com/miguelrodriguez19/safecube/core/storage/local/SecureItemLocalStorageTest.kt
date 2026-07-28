package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.AppDatabase
import com.miguelrodriguez19.safecube.core.storage.SecureItemDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointEntity
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncStateDb
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureItemLocalStorageTest {
    private val secureItemDao = mockk<SecureItemDao>()
    private val appDatabase = mockk<AppDatabase>()
    private val secureItemDraftDao = mockk<SecureItemDraftDao>()
    private val secureItemSyncCheckpointDao = mockk<SecureItemSyncCheckpointDao>()
    private val secureItemDraftEntityMapper = mockk<SecureItemDraftEntityMapper>()
    private val target = SecureItemLocalStorage(
        appDatabase = appDatabase,
        secureItemDao = secureItemDao,
        secureItemDraftDao = secureItemDraftDao,
        secureItemSyncCheckpointDao = secureItemSyncCheckpointDao,
        secureItemDraftEntityMapper = secureItemDraftEntityMapper,
    )

    @Test
    fun `observe active items maps dao entities into domain items`() = runBlocking {
        val entity = localSampleEntity()
        every { secureItemDao.observeActiveItems() } returns flowOf(listOf(entity))

        val result = target.observeActiveItems().first()

        assertEquals(listOf(entity.logicalItemId), result.map { it.logicalItemId })
        assertEquals(listOf(SecureItemType.NOTE), result.map { it.itemType })
    }

    @Test
    fun `apply remote upsert inserts synced official item`() = runBlocking {
        val item = com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = UUID.randomUUID(),
            itemType = SecureItemType.NOTE,
            schemaVersion = 1,
            displayHint = "Official item",
            payload = byteArrayOf(1, 2, 3),
            payloadVersion = 1,
            itemRevision = 1,
            changeSequence = 1,
            createdAt = Instant.parse("2024-07-04T09:00:00Z"),
            updatedAt = Instant.parse("2024-07-04T10:00:00Z"),
            syncState = SecureItemSyncState.SYNCED,
            lastSyncedAt = null,
            lastSyncError = null,
        )
        val entitySlot = slot<com.miguelrodriguez19.safecube.core.storage.SecureItemEntity>()
        coEvery { secureItemDao.findByRemoteItemId(requireNotNull(item.remoteItemId)) } returns null
        coJustRun { secureItemDao.upsert(capture(entitySlot)) }

        val result = target.applyRemoteUpsert(item, lastSyncedAt = item.updatedAt)

        assertTrue(result)
        assertEquals(SecureItemSyncStateDb.SYNCED, entitySlot.captured.syncState)
        assertEquals(item.updatedAt, entitySlot.captured.lastSyncedAt)
    }

    @Test
    fun `update sync checkpoint persists checkpoint entity`() = runBlocking {
        val accountId = UUID.randomUUID()
        val lastAppliedChangeSequence = 42L
        val checkpointSlot = slot<SecureItemSyncCheckpointEntity>()
        coJustRun { secureItemSyncCheckpointDao.upsert(capture(checkpointSlot)) }

        target.updateSyncCheckpoint(accountId, lastAppliedChangeSequence)

        assertEquals(accountId, checkpointSlot.captured.accountId)
        assertEquals(lastAppliedChangeSequence, checkpointSlot.captured.lastAppliedChangeSequence)
    }

    @Test
    fun `single item reads map present entities and preserve absence`() = runBlocking {
        val logicalId = UUID.randomUUID()
        val remoteId = UUID.randomUUID()
        val entity = localSampleEntity(logicalItemId = logicalId, remoteItemId = remoteId)

        every { secureItemDao.observeItem(logicalId) } returns flowOf(null)
        assertNull(target.observeItem(logicalId).first())
        every { secureItemDao.observeItem(logicalId) } returns flowOf(entity)
        assertEquals(logicalId, target.observeItem(logicalId).first()?.logicalItemId)

        coEvery { secureItemDao.getItem(logicalId) } returns null
        assertNull(target.getItem(logicalId))
        coEvery { secureItemDao.getItem(logicalId) } returns entity
        assertEquals(logicalId, target.getItem(logicalId)?.logicalItemId)

        coEvery { secureItemDao.findByRemoteItemId(remoteId) } returns null
        assertNull(target.findByRemoteItemId(remoteId))
        coEvery { secureItemDao.findByRemoteItemId(remoteId) } returns entity
        assertEquals(remoteId, target.findByRemoteItemId(remoteId)?.remoteItemId)
    }

    @Test
    fun `local item mapping rejects unsupported persisted type`() {
        every {
            secureItemDao.observeActiveItems()
        } returns flowOf(listOf(localSampleEntity().copy(itemType = "UNKNOWN")))

        assertThrows(IllegalStateException::class.java) {
            runBlocking { target.observeActiveItems().first() }
        }
    }

    @Test
    fun `remote upsert rejects missing identity and updates existing row`() = runBlocking {
        val localOnly = domainItem(remoteItemId = null)
        assertFalse(target.applyRemoteUpsert(localOnly, localOnly.updatedAt))

        val remoteId = UUID.randomUUID()
        val existing = localSampleEntity(remoteItemId = remoteId)
        val incoming = domainItem(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = remoteId,
            displayHint = "Updated remote",
        )
        val entitySlot = slot<com.miguelrodriguez19.safecube.core.storage.SecureItemEntity>()
        coEvery { secureItemDao.findByRemoteItemId(remoteId) } returns existing
        coJustRun { secureItemDao.upsert(capture(entitySlot)) }

        assertTrue(target.applyRemoteUpsert(incoming, incoming.updatedAt))
        assertEquals(existing.logicalItemId, entitySlot.captured.logicalItemId)
        assertEquals("Updated remote", entitySlot.captured.displayHint)
        assertEquals(SecureItemSyncStateDb.SYNCED, entitySlot.captured.syncState)
    }

    @Test
    fun `remote delete and checkpoint reads reflect dao row counts`() = runBlocking {
        val remoteId = UUID.randomUUID()
        val deletedAt = Instant.parse("2024-07-04T11:00:00Z")
        coEvery {
            secureItemDao.applyRemoteDelete(
                remoteId,
                deletedAt,
                2,
                8,
                SecureItemSyncStateDb.SYNCED,
                deletedAt,
            )
        } returnsMany listOf(0, 1)

        assertFalse(target.applyRemoteDelete(remoteId, deletedAt, 2, 8, deletedAt))
        assertTrue(target.applyRemoteDelete(remoteId, deletedAt, 2, 8, deletedAt))

        val accountId = UUID.randomUUID()
        coEvery {
            secureItemSyncCheckpointDao.getLastAppliedChangeSequence(accountId)
        } returnsMany listOf(null, 42)
        assertNull(target.getSyncCheckpoint(accountId))
        assertEquals(42L, target.getSyncCheckpoint(accountId))
    }
}

private fun localSampleEntity(
    logicalItemId: UUID = UUID.randomUUID(),
    remoteItemId: UUID? = UUID.randomUUID(),
) = com.miguelrodriguez19.safecube.core.storage.SecureItemEntity(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = SecureItemType.NOTE.wireName,
    schemaVersion = 1,
    displayHint = "Official item",
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 1,
    itemRevision = 1,
    changeSequence = 1,
    createdAt = Instant.parse("2024-07-04T08:59:00Z"),
    updatedAt = Instant.parse("2024-07-04T09:00:00Z"),
    deletedAt = null,
    syncState = SecureItemSyncStateDb.SYNCED,
    lastSyncedAt = Instant.parse("2024-07-04T09:00:00Z"),
    lastSyncError = null,
)

private fun domainItem(
    logicalItemId: UUID = UUID.randomUUID(),
    remoteItemId: UUID?,
    displayHint: String = "Official item",
) = SecureItem(
    logicalItemId = logicalItemId,
    remoteItemId = remoteItemId,
    itemType = SecureItemType.NOTE,
    schemaVersion = 1,
    displayHint = displayHint,
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 2,
    itemRevision = 3,
    changeSequence = 4,
    createdAt = Instant.parse("2024-07-04T09:00:00Z"),
    updatedAt = Instant.parse("2024-07-04T10:00:00Z"),
)
