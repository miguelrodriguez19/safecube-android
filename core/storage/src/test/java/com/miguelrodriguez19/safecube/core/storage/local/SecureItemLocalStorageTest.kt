package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemEntity
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
import java.time.temporal.ChronoUnit

class SecureItemLocalStorageTest {

    private val secureItemDao = mockk<SecureItemDao>()

    private val target = SecureItemLocalStorage(secureItemDao)

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
            confirmVerified(secureItemDao)
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
        confirmVerified(secureItemDao)
    }

    @Test
    fun `observeItem when dao emits null then returns null`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        every { secureItemDao.observeItem(logicalItemId) } returns flowOf(null)

        val result = target.observeItem(logicalItemId).first()

        assertNull(result)
        verify(exactly = 1) { secureItemDao.observeItem(logicalItemId) }
        confirmVerified(secureItemDao)
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
            confirmVerified(secureItemDao)
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
        confirmVerified(secureItemDao)
    }

    @Test
    fun `getItem when dao returns null then returns null`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDao.getItem(logicalItemId) } returns null

        val result = target.getItem(logicalItemId)

        assertNull(result)
        coVerify(exactly = 1) { secureItemDao.getItem(logicalItemId) }
        confirmVerified(secureItemDao)
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
        assertEquals(item.syncState.storageValue, entitySlot.captured.syncState)
        assertEquals(item.lastSyncedAt, entitySlot.captured.lastSyncedAt)
        assertEquals(item.lastSyncError, entitySlot.captured.lastSyncError)
        coVerify(exactly = 1) { secureItemDao.insert(any()) }
        confirmVerified(secureItemDao)
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
        assertEquals(item.syncState.storageValue, entitySlot.captured.syncState)
        assertEquals(item.lastSyncedAt, entitySlot.captured.lastSyncedAt)
        assertEquals(item.lastSyncError, entitySlot.captured.lastSyncError)
        coVerify(exactly = 1) { secureItemDao.update(any()) }
        confirmVerified(secureItemDao)
    }

    @Test
    fun `softDelete when dao updates one row then returns true`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val deletedAt = Instant.parse("2026-03-24T12:00:00Z")
        coEvery { secureItemDao.softDelete(logicalItemId, deletedAt) } returns 1

        val result = target.softDelete(
            logicalItemId = logicalItemId,
            deletedAt = deletedAt,
        )

        assertTrue(result)
        coVerify(exactly = 1) { secureItemDao.softDelete(logicalItemId, deletedAt) }
        confirmVerified(secureItemDao)
    }

    @Test
    fun `softDelete when dao updates no rows then returns false`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val deletedAt = Instant.parse("2026-03-24T12:00:00Z")
        coEvery { secureItemDao.softDelete(logicalItemId, deletedAt) } returns 0

        val result = target.softDelete(
            logicalItemId = logicalItemId,
            deletedAt = deletedAt,
        )

        assertFalse(result)
        coVerify(exactly = 1) { secureItemDao.softDelete(logicalItemId, deletedAt) }
        confirmVerified(secureItemDao)
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
    syncState = SecureItemSyncState.SYNCED.storageValue,
    lastSyncedAt = Instant.parse("2026-03-24T08:00:00Z"),
    lastSyncError = null,
)

private fun sampleDomainItem(
    logicalItemId: UUID = UUID.randomUUID(),
    itemType: SecureItemType = SecureItemType.PASSWORD,
    payload: ByteArray = byteArrayOf(1, 2, 3),
    deletedAt: Instant? = null,
): SecureItem {
    val createdAt = Instant.now().minus(3, ChronoUnit.DAYS)
    return SecureItem(
        logicalItemId = logicalItemId,
        remoteItemId = UUID.randomUUID(),
        itemType = itemType,
        schemaVersion = 1,
        displayHint = "Example account",
        payload = payload,
        payloadVersion = 1,
        createdAt = createdAt,
        updatedAt = createdAt.plus(2, ChronoUnit.DAYS),
        deletedAt = deletedAt,
        syncState = SecureItemSyncState.PENDING_UPDATE,
        lastSyncedAt = createdAt.plus(1, ChronoUnit.HOURS),
        lastSyncError = "Network timeout",
    )
}
