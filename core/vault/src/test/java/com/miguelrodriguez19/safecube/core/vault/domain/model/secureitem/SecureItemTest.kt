package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

import java.time.Instant
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Test

class SecureItemTest {
    @Test
    fun `creates secure item with canonical metadata`() {
        val updatedAt = Instant.parse("2026-03-23T12:00:00Z")
        val payload = byteArrayOf(1, 2, 3)
        val item = SecureItem(
            logicalItemId = UUID.fromString("4f89ab0e-453f-4be5-b261-95068f2ad6f0"),
            remoteItemId = UUID.fromString("ae9b2bea-c2dc-4c0f-bf73-42bd61c4f997"),
            itemType = SecureItemType.PASSWORD,
            schemaVersion = 1,
            displayHint = "Example account",
            payload = payload,
            payloadVersion = 1,
            updatedAt = updatedAt,
        )

        assertEquals("Example account", item.displayHint)
        assertEquals(SecureItemType.PASSWORD, item.itemType)
        assertEquals(1, item.schemaVersion)
        assertEquals(1L, item.payloadVersion)
        assertEquals(updatedAt, item.updatedAt)
        assertEquals(payload.toList(), item.payload.toList())
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects blank display hint`() {
        SecureItem(
            logicalItemId = UUID.randomUUID(),
            itemType = SecureItemType.NOTE,
            schemaVersion = 1,
            displayHint = " ",
            payload = byteArrayOf(1),
            payloadVersion = 1,
            updatedAt = Instant.parse("2026-03-23T12:00:00Z"),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects empty payload`() {
        SecureItem(
            logicalItemId = UUID.randomUUID(),
            itemType = SecureItemType.NOTE,
            schemaVersion = 1,
            displayHint = "Example",
            payload = byteArrayOf(),
            payloadVersion = 1,
            updatedAt = Instant.parse("2026-03-23T12:00:00Z"),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects non positive payload version`() {
        SecureItem(
            logicalItemId = UUID.randomUUID(),
            itemType = SecureItemType.NOTE,
            schemaVersion = 1,
            displayHint = "Example",
            payload = byteArrayOf(1),
            payloadVersion = 0,
            updatedAt = Instant.parse("2026-03-23T12:00:00Z"),
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `rejects non positive schema version`() {
        SecureItem(
            logicalItemId = UUID.randomUUID(),
            itemType = SecureItemType.NOTE,
            schemaVersion = 0,
            displayHint = "Example",
            payload = byteArrayOf(1),
            payloadVersion = 1,
            updatedAt = Instant.parse("2026-03-23T12:00:00Z"),
        )
    }
}
