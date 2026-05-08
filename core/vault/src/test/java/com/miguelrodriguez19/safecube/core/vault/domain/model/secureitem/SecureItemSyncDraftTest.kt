package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureItemSyncDraftTest {

    @Test
    fun `constructor when values are valid then creates draft`() {
        val target = sampleSyncDraft()

        assertEquals(SecureItemDraftType.UPDATE, target.draftType)
        assertEquals(1L, target.basePayloadVersion)
        assertEquals(SecureItemType.NOTE, target.itemType)
    }

    @Test
    fun `constructor when display hint is blank then throws illegal argument exception`() {
        val throwable = kotlin.runCatching {
            sampleSyncDraft(displayHint = " ")
        }.exceptionOrNull()

        requireNotNull(throwable)
        assertEquals("displayHint must not be blank.", throwable.message)
    }

    @Test
    fun `constructor when schema version is not positive then throws illegal argument exception`() {
        val throwable = kotlin.runCatching {
            sampleSyncDraft(schemaVersion = 0)
        }.exceptionOrNull()

        requireNotNull(throwable)
        assertEquals("schemaVersion must be positive.", throwable.message)
    }

    @Test
    fun `constructor when payload is empty then throws illegal argument exception`() {
        val throwable = kotlin.runCatching {
            sampleSyncDraft(payload = byteArrayOf())
        }.exceptionOrNull()

        requireNotNull(throwable)
        assertEquals("payload must not be empty.", throwable.message)
    }

    @Test
    fun `constructor when payload version is not positive then throws illegal argument exception`() {
        val throwable = kotlin.runCatching {
            sampleSyncDraft(payloadVersion = 0)
        }.exceptionOrNull()

        requireNotNull(throwable)
        assertEquals("payloadVersion must be positive.", throwable.message)
    }

    @Test
    fun `constructor when base payload version is not positive then throws illegal argument exception`() {
        val throwable = kotlin.runCatching {
            sampleSyncDraft(basePayloadVersion = 0)
        }.exceptionOrNull()

        requireNotNull(throwable)
        assertEquals("basePayloadVersion must be positive.", throwable.message)
    }

    @Test
    fun `constructor when last sync error is blank then throws illegal argument exception`() {
        val throwable = kotlin.runCatching {
            sampleSyncDraft(lastSyncError = "")
        }.exceptionOrNull()

        requireNotNull(throwable)
        assertEquals("lastSyncError must not be blank when present.", throwable.message)
    }

    @Test
    fun `constructor when last publish error is blank then throws illegal argument exception`() {
        val throwable = kotlin.runCatching {
            sampleSyncDraft(lastPublishError = "")
        }.exceptionOrNull()

        requireNotNull(throwable)
        assertEquals("lastPublishError must not be blank when present.", throwable.message)
    }

    @Test
    fun `constructor when delete draft is provided then keeps delete draft type`() {
        val target = sampleSyncDraft(draftType = SecureItemDraftType.DELETE)

        assertTrue(target.deletedAt == null)
        assertEquals(SecureItemDraftType.DELETE, target.draftType)
    }
}

private fun sampleSyncDraft(
    displayHint: String = "Draft item",
    schemaVersion: Int = 1,
    payload: ByteArray = byteArrayOf(1, 2, 3),
    payloadVersion: Long = 2,
    draftType: SecureItemDraftType = SecureItemDraftType.UPDATE,
    basePayloadVersion: Long = 1,
    lastSyncError: String? = null,
    lastPublishError: String? = null,
): SecureItemSyncDraft {
    val now = Instant.now().truncatedTo(ChronoUnit.MILLIS)
    return SecureItemSyncDraft(
        logicalItemId = UUID.randomUUID(),
        remoteItemId = UUID.randomUUID(),
        itemType = SecureItemType.NOTE,
        schemaVersion = schemaVersion,
        displayHint = displayHint,
        payload = payload,
        payloadVersion = payloadVersion,
        createdAt = now.minus(1, ChronoUnit.DAYS),
        updatedAt = now,
        deletedAt = null,
        lastSyncedAt = now.minus(1, ChronoUnit.HOURS),
        lastSyncError = lastSyncError,
        draftType = draftType,
        basePayloadVersion = basePayloadVersion,
        baseUpdatedAt = now.minus(2, ChronoUnit.HOURS),
        lastPublishError = lastPublishError,
    )
}
