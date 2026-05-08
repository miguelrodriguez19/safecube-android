package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftEntity
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftTypeDb
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class SecureItemDraftEntityMapperTest {

    private val target = SecureItemDraftEntityMapper()

    @Test
    fun `toDomain when entity is valid then maps it into sync draft`() {
        val entity = sampleDraftEntity(
            itemType = SecureItemType.NOTE,
            draftType = SecureItemDraftTypeDb.DELETE,
            payload = byteArrayOf(4, 5, 6),
        )

        val result = target.toDomain(entity)

        assertEquals(entity.logicalItemId, result.logicalItemId)
        assertEquals(entity.remoteItemId, result.remoteItemId)
        assertEquals(SecureItemType.NOTE, result.itemType)
        assertEquals(SecureItemDraftType.DELETE, result.draftType)
        assertEquals(entity.basePayloadVersion, result.basePayloadVersion)
        assertEquals(entity.baseUpdatedAt, result.baseUpdatedAt)
        assertArrayEquals(entity.payload, result.payload)
    }

    @Test
    fun `toDomain when item type is unsupported then throws illegal state exception`() {
        val entity = sampleDraftEntity().copy(itemType = "CARD")

        val throwable = kotlin.runCatching {
            target.toDomain(entity)
        }.exceptionOrNull()

        requireNotNull(throwable)
        assertTrue(throwable is IllegalStateException)
        assertEquals("Unsupported SecureItemType 'CARD' in local draft storage.", throwable.message)
    }

    @Test
    fun `toEntity when domain draft is valid then maps it into storage entity`() {
        val draft = sampleDomainDraft(
            itemType = SecureItemType.PASSWORD,
            draftType = SecureItemDraftType.UPDATE,
            payload = byteArrayOf(9, 8, 7),
        )

        val result = target.toEntity(draft)

        assertEquals(draft.logicalItemId, result.logicalItemId)
        assertEquals(draft.remoteItemId, result.remoteItemId)
        assertEquals(SecureItemType.PASSWORD.wireName, result.itemType)
        assertEquals(SecureItemDraftTypeDb.UPDATE, result.draftType)
        assertEquals(draft.basePayloadVersion, result.basePayloadVersion)
        assertEquals(draft.baseUpdatedAt, result.baseUpdatedAt)
        assertArrayEquals(draft.payload, result.payload)
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
