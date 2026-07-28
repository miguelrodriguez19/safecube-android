package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftEntity
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftSyncStatusDb
import com.miguelrodriguez19.safecube.core.storage.SecureItemDraftTypeDb
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import java.time.Instant
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

class SecureItemDraftEntityMapperTest {
    private val target = SecureItemDraftEntityMapper()

    @Test
    fun `to domain maps sync status and sync error`() {
        val entity = sampleDraftEntity(
            draftType = SecureItemDraftTypeDb.CREATE,
            draftSyncStatus = SecureItemDraftSyncStatusDb.CONFLICT,
            lastSyncError = "Conflict",
        )

        val result = target.toDomain(entity)

        assertEquals(SecureItemDraftType.CREATE, result.draftType)
        assertEquals(SecureItemDraftSyncStatus.CONFLICT, result.draftSyncStatus)
        assertEquals("Conflict", result.lastSyncError)
    }

    @Test
    fun `to entity maps sync status and draft type`() {
        val draft = sampleDomainDraft(
            draftType = SecureItemDraftType.DELETE,
            draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
        )

        val result = target.toEntity(draft)

        assertEquals(SecureItemDraftTypeDb.DELETE, result.draftType)
        assertEquals(SecureItemDraftSyncStatusDb.READY_TO_SYNC, result.draftSyncStatus)
    }
}

private fun sampleDraftEntity(
    draftType: SecureItemDraftTypeDb = SecureItemDraftTypeDb.UPDATE,
    draftSyncStatus: SecureItemDraftSyncStatusDb = SecureItemDraftSyncStatusDb.READY_TO_SYNC,
    lastSyncError: String? = null,
): SecureItemDraftEntity {
    val now = Instant.parse("2024-07-03T10:00:00Z")
    return SecureItemDraftEntity(
        logicalItemId = UUID.randomUUID(),
        remoteItemId = UUID.randomUUID(),
        itemType = SecureItemType.NOTE.wireName,
        schemaVersion = 1,
        displayHint = "Draft item",
        payload = byteArrayOf(1, 2, 3),
        payloadVersion = 2,
        mutationId = UUID.randomUUID(),
        createdAt = now.minusSeconds(60),
        updatedAt = now,
        deletedAt = null,
        lastSyncedAt = now.minusSeconds(30),
        draftType = draftType,
        draftSyncStatus = draftSyncStatus,
        baseItemRevision = if (draftType == SecureItemDraftTypeDb.CREATE) null else 1,
        lastSyncError = lastSyncError,
    )
}

private fun sampleDomainDraft(
    draftType: SecureItemDraftType = SecureItemDraftType.UPDATE,
    draftSyncStatus: SecureItemDraftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
): SecureItemSyncDraft {
    val now = Instant.parse("2024-07-03T10:00:00Z")
    return SecureItemSyncDraft(
        logicalItemId = UUID.randomUUID(),
        remoteItemId = UUID.randomUUID(),
        itemType = SecureItemType.NOTE,
        schemaVersion = 1,
        displayHint = "Draft item",
        payload = byteArrayOf(1, 2, 3),
        payloadVersion = 2,
        mutationId = UUID.randomUUID(),
        createdAt = now.minusSeconds(60),
        updatedAt = now,
        deletedAt = null,
        lastSyncedAt = now.minusSeconds(30),
        draftType = draftType,
        draftSyncStatus = draftSyncStatus,
        baseItemRevision = if (draftType == SecureItemDraftType.CREATE) null else 1,
        lastSyncError = null,
    )
}
