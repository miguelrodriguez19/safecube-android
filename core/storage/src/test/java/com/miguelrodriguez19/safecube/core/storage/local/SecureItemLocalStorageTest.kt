package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.AppDatabase
import com.miguelrodriguez19.safecube.core.storage.SecureItemDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointEntity
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncStateDb
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
}

private fun localSampleEntity() = com.miguelrodriguez19.safecube.core.storage.SecureItemEntity(
    logicalItemId = UUID.randomUUID(),
    remoteItemId = UUID.randomUUID(),
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
