package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftEntity
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftTypeDb
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class SecureItemDraftLocalStorageTest {

    private val secureItemDraftDao = mockk<SecureItemDraftDao>()
    private val secureItemDraftEntityMapper = mockk<SecureItemDraftEntityMapper>()

    private val target = SecureItemDraftLocalStorage(
        secureItemDraftDao = secureItemDraftDao,
        secureItemDraftEntityMapper = secureItemDraftEntityMapper,
    )

    @Test
    fun `observeDrafts when dao emits entities then maps them into domain drafts`() = runBlocking {
        val firstEntity = sampleDraftEntity()
        val secondEntity = sampleDraftEntity()
        val firstDraft = sampleDomainDraft()
        val secondDraft = sampleDomainDraft()
        every { secureItemDraftDao.observeDrafts() } returns flowOf(
            listOf(
                firstEntity,
                secondEntity
            )
        )
        every { secureItemDraftEntityMapper.toDomain(firstEntity) } returns firstDraft
        every { secureItemDraftEntityMapper.toDomain(secondEntity) } returns secondDraft

        val result = target.observeDrafts().first()

        assertEquals(
            listOf(firstDraft, secondDraft),
            result,
        )
        verify(exactly = 1) { secureItemDraftDao.observeDrafts() }
        verify(exactly = 1) { secureItemDraftEntityMapper.toDomain(firstEntity) }
        verify(exactly = 1) { secureItemDraftEntityMapper.toDomain(secondEntity) }
        confirmVerified(secureItemDraftDao, secureItemDraftEntityMapper)
    }

    @Test
    fun `observeDraft when dao emits entity then maps it into domain draft`() = runBlocking {
        val entity = sampleDraftEntity()
        val draft = sampleDomainDraft()
        every { secureItemDraftDao.observeDraft(entity.logicalItemId) } returns flowOf(entity)
        every { secureItemDraftEntityMapper.toDomain(entity) } returns draft

        val result = target.observeDraft(entity.logicalItemId).first()

        assertEquals(draft, result)
        verify(exactly = 1) { secureItemDraftDao.observeDraft(entity.logicalItemId) }
        verify(exactly = 1) { secureItemDraftEntityMapper.toDomain(entity) }
        confirmVerified(secureItemDraftDao, secureItemDraftEntityMapper)
    }

    @Test
    fun `getDraft when dao finds entity then maps it into domain draft`() = runBlocking {
        val entity = sampleDraftEntity()
        val draft = sampleDomainDraft()
        coEvery { secureItemDraftDao.getDraft(entity.logicalItemId) } returns entity
        every { secureItemDraftEntityMapper.toDomain(entity) } returns draft

        val result = target.getDraft(entity.logicalItemId)

        assertEquals(draft, result)
        coVerify(exactly = 1) { secureItemDraftDao.getDraft(entity.logicalItemId) }
        verify(exactly = 1) { secureItemDraftEntityMapper.toDomain(entity) }
        confirmVerified(secureItemDraftDao, secureItemDraftEntityMapper)
    }

    @Test
    fun `findByRemoteItemId when dao finds entity then maps it into domain draft`() = runBlocking {
        val entity = sampleDraftEntity()
        val draft = sampleDomainDraft()
        val remoteItemId = requireNotNull(entity.remoteItemId)
        coEvery { secureItemDraftDao.findByRemoteItemId(remoteItemId) } returns entity
        every { secureItemDraftEntityMapper.toDomain(entity) } returns draft

        val result = target.findByRemoteItemId(remoteItemId)

        assertEquals(draft, result)
        coVerify(exactly = 1) { secureItemDraftDao.findByRemoteItemId(remoteItemId) }
        verify(exactly = 1) { secureItemDraftEntityMapper.toDomain(entity) }
        confirmVerified(secureItemDraftDao, secureItemDraftEntityMapper)
    }

    @Test
    fun `upsert when draft is provided then delegates mapped entity to dao`() = runBlocking {
        val draft = sampleDomainDraft()
        val entity = sampleDraftEntity()
        every { secureItemDraftEntityMapper.toEntity(draft) } returns entity
        coEvery { secureItemDraftDao.upsert(entity) } returns Unit

        target.upsert(draft)

        verify(exactly = 1) { secureItemDraftEntityMapper.toEntity(draft) }
        coVerify(exactly = 1) { secureItemDraftDao.upsert(entity) }
        confirmVerified(secureItemDraftDao, secureItemDraftEntityMapper)
    }

    @Test
    fun `delete when dao removes one row then returns true`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDraftDao.delete(logicalItemId) } returns 1

        val result = target.delete(logicalItemId)

        assertTrue(result)
        coVerify(exactly = 1) { secureItemDraftDao.delete(logicalItemId) }
        confirmVerified(secureItemDraftDao, secureItemDraftEntityMapper)
    }

    @Test
    fun `delete when dao removes no rows then returns false`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDraftDao.delete(logicalItemId) } returns 0

        val result = target.delete(logicalItemId)

        assertFalse(result)
        coVerify(exactly = 1) { secureItemDraftDao.delete(logicalItemId) }
        confirmVerified(secureItemDraftDao, secureItemDraftEntityMapper)
    }

    @Test
    fun `getDraft when dao returns null then returns null`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDraftDao.getDraft(logicalItemId) } returns null

        val result = target.getDraft(logicalItemId)

        assertNull(result)
        coVerify(exactly = 1) { secureItemDraftDao.getDraft(logicalItemId) }
        confirmVerified(secureItemDraftDao, secureItemDraftEntityMapper)
    }
    
    private fun sampleDraftEntity(
        logicalItemId: UUID = UUID.randomUUID(),
        remoteItemId: UUID? = UUID.randomUUID(),
        itemType: SecureItemType = SecureItemType.PASSWORD,
        payload: ByteArray = byteArrayOf(1, 2, 3),
        draftType: SecureItemDraftTypeDb = SecureItemDraftTypeDb.UPDATE,
    ): SecureItemDraftEntity {
        val updatedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        return SecureItemDraftEntity(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            itemType = itemType.wireName,
            schemaVersion = 1,
            displayHint = "Draft item",
            payload = payload,
            payloadVersion = 2,
            createdAt = updatedAt.minus(1, ChronoUnit.DAYS),
            updatedAt = updatedAt,
            deletedAt = null,
            lastSyncedAt = updatedAt.minus(1, ChronoUnit.HOURS),
            lastSyncError = null,
            draftType = draftType,
            basePayloadVersion = 1,
            baseUpdatedAt = updatedAt.minus(2, ChronoUnit.HOURS),
            lastPublishError = null,
        )
    }

    private fun sampleDomainDraft(
        logicalItemId: UUID = UUID.randomUUID(),
        remoteItemId: UUID? = UUID.randomUUID(),
        itemType: SecureItemType = SecureItemType.PASSWORD,
        payload: ByteArray = byteArrayOf(9, 8, 7),
        draftType: SecureItemDraftType = SecureItemDraftType.UPDATE,
    ): SecureItemSyncDraft {
        val updatedAt = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        return SecureItemSyncDraft(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItemId,
            itemType = itemType,
            schemaVersion = 1,
            displayHint = "Draft domain item",
            payload = payload,
            payloadVersion = 2,
            createdAt = updatedAt.minus(1, ChronoUnit.DAYS),
            updatedAt = updatedAt,
            deletedAt = null,
            lastSyncedAt = updatedAt.minus(1, ChronoUnit.HOURS),
            lastSyncError = null,
            draftType = draftType,
            basePayloadVersion = 1,
            baseUpdatedAt = updatedAt.minus(2, ChronoUnit.HOURS),
            lastPublishError = null,
        )
    }
}
