package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class SecureItemTest {
    @Test
    fun `secure item when metadata is valid then creates canonical aggregate`() {
        val createdAt = Instant.now().minus(1, ChronoUnit.DAYS)
        val updatedAt = Instant.now()
        val payload = byteArrayOf(1, 2, 3)

        val target = SecureItem(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = UUID.randomUUID(),
            itemType = SecureItemType.PASSWORD,
            schemaVersion = 1,
            displayHint = "Example account",
            payload = payload,
            payloadVersion = 1,
            createdAt = createdAt,
            updatedAt = updatedAt,
        )

        assertEquals("Example account", target.displayHint)
        assertEquals(SecureItemType.PASSWORD, target.itemType)
        assertEquals(1, target.schemaVersion)
        assertEquals(1L, target.payloadVersion)
        assertEquals(updatedAt, target.updatedAt)
        assertEquals(SecureItemSyncState.SYNCED, target.syncState)
        assertEquals(payload.toList(), target.payload.toList())
    }

    @Test
    fun `secure item when display hint is blank then throws illegal argument exception`() {
        val now = Instant.now()

        assertThrows(IllegalArgumentException::class.java) {
            SecureItem(
                logicalItemId = UUID.randomUUID(),
                itemType = SecureItemType.NOTE,
                schemaVersion = 1,
                displayHint = " ",
                payload = byteArrayOf(1),
                payloadVersion = 1,
                createdAt = now,
                updatedAt = now,
            )
        }
    }

    @Test
    fun `secure item when payload is empty then throws illegal argument exception`() {
        val now = Instant.now()

        assertThrows(IllegalArgumentException::class.java) {
            SecureItem(
                logicalItemId = UUID.randomUUID(),
                itemType = SecureItemType.NOTE,
                schemaVersion = 1,
                displayHint = "Example",
                payload = byteArrayOf(),
                payloadVersion = 1,
                createdAt = now,
                updatedAt = now,
            )
        }
    }

    @Test
    fun `secure item when payload version is not positive then throws illegal argument exception`() {
        val now = Instant.now()

        assertThrows(IllegalArgumentException::class.java) {
            SecureItem(
                logicalItemId = UUID.randomUUID(),
                itemType = SecureItemType.NOTE,
                schemaVersion = 1,
                displayHint = "Example",
                payload = byteArrayOf(1),
                payloadVersion = 0,
                createdAt = now,
                updatedAt = now,
            )
        }
    }

    @Test
    fun `secure item when schema version is not positive then throws illegal argument exception`() {
        val now = Instant.now()

        assertThrows(IllegalArgumentException::class.java) {
            SecureItem(
                logicalItemId = UUID.randomUUID(),
                itemType = SecureItemType.NOTE,
                schemaVersion = 0,
                displayHint = "Example",
                payload = byteArrayOf(1),
                payloadVersion = 1,
                createdAt = now,
                updatedAt = now,
            )
        }
    }

    @Test
    fun `secure item when last sync error is blank then throws illegal argument exception`() {
        val now = Instant.now()

        assertThrows(IllegalArgumentException::class.java) {
            SecureItem(
                logicalItemId = UUID.randomUUID(),
                itemType = SecureItemType.NOTE,
                schemaVersion = 1,
                displayHint = "Example",
                payload = byteArrayOf(1),
                payloadVersion = 1,
                createdAt = now,
                updatedAt = now,
                lastSyncError = " ",
            )
        }
    }
}
