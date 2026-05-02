package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemEntity
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointEntity
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncStateDb
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureItemLocalStorageTest {

    private val secureItemDao = mockk<SecureItemDao>()
    private val secureItemSyncCheckpointDao = mockk<SecureItemSyncCheckpointDao>()

    private val target = SecureItemLocalStorage(
        secureItemDao = secureItemDao,
        secureItemSyncCheckpointDao = secureItemSyncCheckpointDao,
    )

    @Test
    fun `observeActiveItems when dao emits entities then maps them into domain items`() =
        runBlocking {
            val firstEntity = sampleEntity(
                itemType = SecureItemType.PASSWORD,
                payload = byteArrayOf(1, 2, 3),
            )
            val secondEntity = sampleEntity(
                itemType = SecureItemType.NOTE,
                payload = byteArrayOf(4, 5, 6),
                deletedAt = Instant.parse("2026-03-24T11:00:00Z"),
            )
            every { secureItemDao.observeActiveItems() } returns flowOf(
                listOf(
                    firstEntity,
                    secondEntity
                )
            )

            val result = target.observeActiveItems().first()

            assertEquals(
                listOf(firstEntity.logicalItemId, secondEntity.logicalItemId),
                result.map { it.logicalItemId })
            assertEquals(
                listOf(SecureItemType.PASSWORD, SecureItemType.NOTE),
                result.map { it.itemType })
            assertArrayEquals(firstEntity.payload, result[0].payload)
            assertEquals(secondEntity.deletedAt, result[1].deletedAt)
            assertEquals(SecureItemSyncState.SYNCED, result[0].syncState)
            verify(exactly = 1) { secureItemDao.observeActiveItems() }
            confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
        }

    @Test
    fun `observeItem when dao emits entity then maps it into domain item`() = runBlocking {
        val entity = sampleEntity()
        val logicalItemId = entity.logicalItemId
        every { secureItemDao.observeItem(logicalItemId) } returns flowOf(entity)

        val result = target.observeItem(logicalItemId).first()

        requireNotNull(result)
        assertEquals(entity.logicalItemId, result.logicalItemId)
        assertEquals(SecureItemType.PASSWORD, result.itemType)
        assertEquals(entity.displayHint, result.displayHint)
        assertArrayEquals(entity.payload, result.payload)
        assertEquals(SecureItemSyncState.SYNCED, result.syncState)
        verify(exactly = 1) { secureItemDao.observeItem(logicalItemId) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `observeItem when dao emits null then returns null`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        every { secureItemDao.observeItem(logicalItemId) } returns flowOf(null)

        val result = target.observeItem(logicalItemId).first()

        assertNull(result)
        verify(exactly = 1) { secureItemDao.observeItem(logicalItemId) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `observeItem when dao emits unsupported item type then throws illegal state exception`() =
        runBlocking {
            val logicalItemId = UUID.randomUUID()
            every { secureItemDao.observeItem(logicalItemId) } returns flowOf(
                sampleEntity(logicalItemId = logicalItemId).copy(itemType = "CARD"),
            )

            val throwable =
                kotlin.runCatching { target.observeItem(logicalItemId).first() }.exceptionOrNull()

            requireNotNull(throwable)
            assertTrue(throwable is IllegalStateException)
            assertEquals("Unsupported SecureItemType 'CARD' in local storage.", throwable.message)
            verify(exactly = 1) { secureItemDao.observeItem(logicalItemId) }
            confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
        }

    @Test
    fun `getItem when dao finds entity then maps it into domain item`() = runBlocking {
        val entity = sampleEntity()
        val logicalItemId = entity.logicalItemId
        coEvery { secureItemDao.getItem(logicalItemId) } returns entity

        val result = target.getItem(logicalItemId)

        requireNotNull(result)
        assertEquals(entity.logicalItemId, result.logicalItemId)
        assertEquals(entity.createdAt, result.createdAt)
        assertEquals(entity.updatedAt, result.updatedAt)
        assertArrayEquals(entity.payload, result.payload)
        coVerify(exactly = 1) { secureItemDao.getItem(logicalItemId) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `getItem when dao returns null then returns null`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDao.getItem(logicalItemId) } returns null

        val result = target.getItem(logicalItemId)

        assertNull(result)
        coVerify(exactly = 1) { secureItemDao.getItem(logicalItemId) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `insert when item is provided then delegates mapped entity to dao`() = runBlocking {
        val item = sampleDomainItem()
        val entitySlot = slot<SecureItemEntity>()
        coEvery { secureItemDao.insert(capture(entitySlot)) } returns Unit

        target.insert(item)

        assertEquals(item.logicalItemId, entitySlot.captured.logicalItemId)
        assertEquals(item.remoteItemId, entitySlot.captured.remoteItemId)
        assertEquals(item.itemType.wireName, entitySlot.captured.itemType)
        assertEquals(item.schemaVersion, entitySlot.captured.schemaVersion)
        assertEquals(item.displayHint, entitySlot.captured.displayHint)
        assertArrayEquals(item.payload, entitySlot.captured.payload)
        assertEquals(item.payloadVersion, entitySlot.captured.payloadVersion)
        assertEquals(item.createdAt, entitySlot.captured.createdAt)
        assertEquals(item.updatedAt, entitySlot.captured.updatedAt)
        assertEquals(item.deletedAt, entitySlot.captured.deletedAt)
        assertEquals(SecureItemSyncStateDb.fromDomain(item.syncState), entitySlot.captured.syncState)
        assertEquals(item.lastSyncedAt, entitySlot.captured.lastSyncedAt)
        assertEquals(item.lastSyncError, entitySlot.captured.lastSyncError)
        coVerify(exactly = 1) { secureItemDao.insert(any()) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `update when item is provided then delegates mapped entity to dao`() = runBlocking {
        val item = sampleDomainItem(
            itemType = SecureItemType.NOTE,
            payload = byteArrayOf(9, 8, 7),
            deletedAt = Instant.parse("2026-03-24T11:00:00Z"),
        )
        val entitySlot = slot<SecureItemEntity>()
        coEvery { secureItemDao.update(capture(entitySlot)) } returns Unit

        target.update(item)

        assertEquals(item.logicalItemId, entitySlot.captured.logicalItemId)
        assertEquals(item.itemType.wireName, entitySlot.captured.itemType)
        assertArrayEquals(item.payload, entitySlot.captured.payload)
        assertEquals(item.deletedAt, entitySlot.captured.deletedAt)
        assertEquals(SecureItemSyncStateDb.fromDomain(item.syncState), entitySlot.captured.syncState)
        assertEquals(item.lastSyncedAt, entitySlot.captured.lastSyncedAt)
        assertEquals(item.lastSyncError, entitySlot.captured.lastSyncError)
        coVerify(exactly = 1) { secureItemDao.update(any()) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `softDelete when dao updates one row then returns true`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val deletedAt = Instant.parse("2026-03-24T12:00:00Z")
        coEvery { secureItemDao.softDelete(logicalItemId, deletedAt, SecureItemSyncStateDb.PENDING_DELETE) } returns 1

        val result = target.softDelete(
            logicalItemId = logicalItemId,
            deletedAt = deletedAt,
        )

        assertTrue(result)
        coVerify(exactly = 1) { secureItemDao.softDelete(logicalItemId, deletedAt, SecureItemSyncStateDb.PENDING_DELETE) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `softDelete when dao updates no rows then returns false`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val deletedAt = Instant.parse("2026-03-24T12:00:00Z")
        coEvery { secureItemDao.softDelete(logicalItemId, deletedAt, SecureItemSyncStateDb.PENDING_DELETE) } returns 0

        val result = target.softDelete(
            logicalItemId = logicalItemId,
            deletedAt = deletedAt,
        )

        assertFalse(result)
        coVerify(exactly = 1) { secureItemDao.softDelete(logicalItemId, deletedAt, SecureItemSyncStateDb.PENDING_DELETE) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `getPendingSyncItemsOrdered when dao returns entities then maps into domain items`() = runBlocking {
        val first = sampleEntity(
            itemType = SecureItemType.PASSWORD,
            payload = byteArrayOf(1, 1, 1),
        )
        val second = sampleEntity(
            itemType = SecureItemType.NOTE,
            payload = byteArrayOf(2, 2, 2),
            deletedAt = Instant.parse("2026-03-24T12:00:00Z"),
        ).copy(syncState = SecureItemSyncStateDb.PENDING_DELETE)
        coEvery {
            secureItemDao.getPendingSyncItemsOrdered(
                pendingCreateState = SecureItemSyncStateDb.PENDING_CREATE,
                pendingUpdateState = SecureItemSyncStateDb.PENDING_UPDATE,
                pendingDeleteState = SecureItemSyncStateDb.PENDING_DELETE,
            )
        } returns listOf(first, second)

        val result = target.getPendingSyncItemsOrdered()

        assertEquals(listOf(first.logicalItemId, second.logicalItemId), result.map { it.logicalItemId })
        assertEquals(listOf(SecureItemType.PASSWORD, SecureItemType.NOTE), result.map { it.itemType })
        assertEquals(listOf(SecureItemSyncState.SYNCED, SecureItemSyncState.PENDING_DELETE), result.map { it.syncState })
        coVerify(exactly = 1) {
            secureItemDao.getPendingSyncItemsOrdered(
                pendingCreateState = SecureItemSyncStateDb.PENDING_CREATE,
                pendingUpdateState = SecureItemSyncStateDb.PENDING_UPDATE,
                pendingDeleteState = SecureItemSyncStateDb.PENDING_DELETE,
            )
        }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `findByRemoteItemId when dao returns entity then maps into domain item`() = runBlocking {
        val entity = sampleEntity(itemType = SecureItemType.NOTE)
        val remoteItemId = requireNotNull(entity.remoteItemId)
        coEvery { secureItemDao.findByRemoteItemId(remoteItemId) } returns entity

        val result = target.findByRemoteItemId(remoteItemId)

        requireNotNull(result)
        assertEquals(entity.logicalItemId, result.logicalItemId)
        assertEquals(entity.itemType, result.itemType.wireName)
        coVerify(exactly = 1) { secureItemDao.findByRemoteItemId(remoteItemId) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `findByRemoteItemId when dao returns null then returns null`() = runBlocking {
        val remoteItemId = UUID.randomUUID()
        coEvery { secureItemDao.findByRemoteItemId(remoteItemId) } returns null

        val result = target.findByRemoteItemId(remoteItemId)

        assertNull(result)
        coVerify(exactly = 1) { secureItemDao.findByRemoteItemId(remoteItemId) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `markPendingCreate when dao updates one row then returns true`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDao.markPendingCreate(logicalItemId, SecureItemSyncStateDb.PENDING_CREATE) } returns 1

        val result = target.markPendingCreate(logicalItemId)

        assertTrue(result)
        coVerify(exactly = 1) { secureItemDao.markPendingCreate(logicalItemId, SecureItemSyncStateDb.PENDING_CREATE) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `markPendingUpdate when dao updates one row then returns true`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDao.markPendingUpdate(logicalItemId, SecureItemSyncStateDb.PENDING_UPDATE) } returns 1

        val result = target.markPendingUpdate(logicalItemId)

        assertTrue(result)
        coVerify(exactly = 1) { secureItemDao.markPendingUpdate(logicalItemId, SecureItemSyncStateDb.PENDING_UPDATE) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `markPendingDelete when dao updates one row then returns true`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val deletedAt = Instant.parse("2026-03-24T12:30:00Z")
        coEvery {
            secureItemDao.markPendingDelete(logicalItemId, deletedAt, SecureItemSyncStateDb.PENDING_DELETE)
        } returns 1

        val result = target.markPendingDelete(
            logicalItemId = logicalItemId,
            deletedAt = deletedAt,
        )

        assertTrue(result)
        coVerify(exactly = 1) {
            secureItemDao.markPendingDelete(logicalItemId, deletedAt, SecureItemSyncStateDb.PENDING_DELETE)
        }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `markSynced when dao updates one row then returns true`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val remoteItemId = UUID.randomUUID()
        val updatedAt = Instant.parse("2026-03-24T13:00:00Z")
        val deletedAt = Instant.parse("2026-03-24T13:15:00Z")
        val lastSyncedAt = Instant.parse("2026-03-24T13:16:00Z")
        coEvery {
            secureItemDao.markSynced(
                logicalItemId = logicalItemId,
                remoteItemId = remoteItemId,
                payloadVersion = 2,
                updatedAt = updatedAt,
                deletedAt = deletedAt,
                syncState = SecureItemSyncStateDb.SYNCED,
                lastSyncedAt = lastSyncedAt,
            )
        } returns 1

        val result = target.markSynced(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            payloadVersion = 2,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
            lastSyncedAt = lastSyncedAt,
        )

        assertTrue(result)
        coVerify(exactly = 1) {
            secureItemDao.markSynced(
                logicalItemId = logicalItemId,
                remoteItemId = remoteItemId,
                payloadVersion = 2,
                updatedAt = updatedAt,
                deletedAt = deletedAt,
                syncState = SecureItemSyncStateDb.SYNCED,
                lastSyncedAt = lastSyncedAt,
            )
        }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `markConflict when dao updates one row then returns true`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDao.markConflict(logicalItemId, SecureItemSyncStateDb.CONFLICT, "conflict") } returns 1

        val result = target.markConflict(
            logicalItemId = logicalItemId,
            lastSyncError = "conflict",
        )

        assertTrue(result)
        coVerify(exactly = 1) { secureItemDao.markConflict(logicalItemId, SecureItemSyncStateDb.CONFLICT, "conflict") }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `applyRemoteUpsert when remote item does not exist then inserts synced copy`() = runBlocking {
        val lastSyncedAt = Instant.parse("2026-03-24T14:00:00Z")
        val item = sampleDomainItem(
            syncState = SecureItemSyncState.PENDING_UPDATE,
            lastSyncError = "timeout",
        )
        val remoteItemId = requireNotNull(item.remoteItemId)
        val upserted = slot<SecureItemEntity>()
        coEvery { secureItemDao.findByRemoteItemId(remoteItemId) } returns null
        coEvery { secureItemDao.upsert(capture(upserted)) } returns Unit

        val result = target.applyRemoteUpsert(
            item = item,
            lastSyncedAt = lastSyncedAt,
        )

        assertTrue(result)
        assertEquals(item.logicalItemId, upserted.captured.logicalItemId)
        assertEquals(remoteItemId, upserted.captured.remoteItemId)
        assertEquals(SecureItemSyncStateDb.SYNCED, upserted.captured.syncState)
        assertEquals(lastSyncedAt, upserted.captured.lastSyncedAt)
        assertNull(upserted.captured.lastSyncError)
        coVerify(exactly = 1) { secureItemDao.findByRemoteItemId(remoteItemId) }
        coVerify(exactly = 1) { secureItemDao.upsert(any()) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `applyRemoteUpsert when remote item exists then updates existing entity preserving logical id`() = runBlocking {
        val remoteItemId = UUID.randomUUID()
        val existingEntity = sampleEntity(
            logicalItemId = UUID.randomUUID(),
            itemType = SecureItemType.PASSWORD,
            payload = byteArrayOf(9, 9),
        ).copy(
            remoteItemId = remoteItemId,
            syncState = SecureItemSyncStateDb.PENDING_UPDATE,
            lastSyncError = "old error",
        )
        val remoteItem = sampleDomainItem(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = remoteItemId,
            itemType = SecureItemType.NOTE,
            payload = byteArrayOf(4, 4, 4),
            payloadVersion = 8,
            updatedAt = Instant.parse("2026-03-24T14:40:00Z"),
            deletedAt = Instant.parse("2026-03-24T14:41:00Z"),
            syncState = SecureItemSyncState.PENDING_CREATE,
            lastSyncError = "new error",
        )
        val lastSyncedAt = Instant.parse("2026-03-24T14:50:00Z")
        val upserted = slot<SecureItemEntity>()
        coEvery { secureItemDao.findByRemoteItemId(remoteItemId) } returns existingEntity
        coEvery { secureItemDao.upsert(capture(upserted)) } returns Unit

        val result = target.applyRemoteUpsert(
            item = remoteItem,
            lastSyncedAt = lastSyncedAt,
        )

        assertTrue(result)
        assertEquals(existingEntity.logicalItemId, upserted.captured.logicalItemId)
        assertEquals(remoteItemId, upserted.captured.remoteItemId)
        assertEquals(SecureItemType.NOTE.wireName, upserted.captured.itemType)
        assertArrayEquals(remoteItem.payload, upserted.captured.payload)
        assertEquals(remoteItem.payloadVersion, upserted.captured.payloadVersion)
        assertEquals(remoteItem.updatedAt, upserted.captured.updatedAt)
        assertEquals(remoteItem.deletedAt, upserted.captured.deletedAt)
        assertEquals(SecureItemSyncStateDb.SYNCED, upserted.captured.syncState)
        assertEquals(lastSyncedAt, upserted.captured.lastSyncedAt)
        assertNull(upserted.captured.lastSyncError)
        coVerify(exactly = 1) { secureItemDao.findByRemoteItemId(remoteItemId) }
        coVerify(exactly = 1) { secureItemDao.upsert(any()) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `applyRemoteUpsert when remote item id is missing then returns false`() = runBlocking {
        val item = sampleDomainItem(remoteItemId = null)

        val result = target.applyRemoteUpsert(
            item = item,
            lastSyncedAt = Instant.parse("2026-03-24T14:00:00Z"),
        )

        assertFalse(result)
        coVerify(exactly = 0) { secureItemDao.findByRemoteItemId(any()) }
        coVerify(exactly = 0) { secureItemDao.upsert(any()) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `applyRemoteDelete when dao updates one row then returns true`() = runBlocking {
        val remoteItemId = UUID.randomUUID()
        val deletedAt = Instant.parse("2026-03-24T15:00:00Z")
        val lastSyncedAt = Instant.parse("2026-03-24T15:01:00Z")
        coEvery {
            secureItemDao.applyRemoteDelete(remoteItemId, deletedAt, SecureItemSyncStateDb.SYNCED, lastSyncedAt)
        } returns 1

        val result = target.applyRemoteDelete(
            remoteItemId = remoteItemId,
            deletedAt = deletedAt,
            lastSyncedAt = lastSyncedAt,
        )

        assertTrue(result)
        coVerify(exactly = 1) {
            secureItemDao.applyRemoteDelete(remoteItemId, deletedAt, SecureItemSyncStateDb.SYNCED, lastSyncedAt)
        }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `getSyncCheckpoint when dao has value then returns value`() = runBlocking {
        val accountId = UUID.randomUUID()
        val lastPulledAt = Instant.parse("2026-03-24T16:00:00Z")
        coEvery { secureItemSyncCheckpointDao.getLastPulledAt(accountId) } returns lastPulledAt

        val result = target.getSyncCheckpoint(accountId)

        assertEquals(lastPulledAt, result)
        coVerify(exactly = 1) { secureItemSyncCheckpointDao.getLastPulledAt(accountId) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }

    @Test
    fun `updateSyncCheckpoint when called then upserts checkpoint entity`() = runBlocking {
        val accountId = UUID.randomUUID()
        val lastPulledAt = Instant.parse("2026-03-24T16:05:00Z")
        val checkpoint = slot<SecureItemSyncCheckpointEntity>()
        coEvery { secureItemSyncCheckpointDao.upsert(capture(checkpoint)) } returns Unit

        target.updateSyncCheckpoint(
            accountId = accountId,
            lastPulledAt = lastPulledAt,
        )

        assertEquals(accountId, checkpoint.captured.accountId)
        assertEquals(lastPulledAt, checkpoint.captured.lastPulledAt)
        coVerify(exactly = 1) { secureItemSyncCheckpointDao.upsert(any()) }
        confirmVerified(secureItemDao, secureItemSyncCheckpointDao)
    }
}

private fun sampleEntity(
    logicalItemId: UUID = UUID.randomUUID(),
    itemType: SecureItemType = SecureItemType.PASSWORD,
    payload: ByteArray = byteArrayOf(1, 2, 3),
    deletedAt: Instant? = null,
): SecureItemEntity = SecureItemEntity(
    logicalItemId = logicalItemId,
    remoteItemId = UUID.randomUUID(),
    itemType = itemType.wireName,
    schemaVersion = 1,
    displayHint = "Example account",
    payload = payload,
    payloadVersion = 1,
    createdAt = Instant.parse("2026-03-24T09:00:00Z"),
    updatedAt = Instant.parse("2026-03-24T10:00:00Z"),
    deletedAt = deletedAt,
    syncState = SecureItemSyncStateDb.SYNCED,
    lastSyncedAt = Instant.parse("2026-03-24T08:00:00Z"),
    lastSyncError = null,
)

private fun sampleDomainItem(
    logicalItemId: UUID = UUID.randomUUID(),
    remoteItemId: UUID? = UUID.randomUUID(),
    itemType: SecureItemType = SecureItemType.PASSWORD,
    payload: ByteArray = byteArrayOf(1, 2, 3),
    payloadVersion: Long = 1,
    updatedAt: Instant? = null,
    deletedAt: Instant? = null,
    syncState: SecureItemSyncState = SecureItemSyncState.PENDING_UPDATE,
    lastSyncError: String? = "Network timeout",
): SecureItem {
    val createdAt = Instant.now().minus(3, ChronoUnit.DAYS)
    return SecureItem(
        logicalItemId = logicalItemId,
        remoteItemId = remoteItemId,
        itemType = itemType,
        schemaVersion = 1,
        displayHint = "Example account",
        payload = payload,
        payloadVersion = payloadVersion,
        createdAt = createdAt,
        updatedAt = updatedAt ?: createdAt.plus(2, ChronoUnit.DAYS),
        deletedAt = deletedAt,
        syncState = syncState,
        lastSyncedAt = createdAt.plus(1, ChronoUnit.HOURS),
        lastSyncError = lastSyncError,
    )
}
