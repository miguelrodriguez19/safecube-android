package com.miguelrodriguez19.safecube.core.storage

import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class StorageTypeConvertersTest {

    private val target = StorageTypeConverters()

    @Test
    fun `fromInstant when instant contains nanos then stores epoch nanos`() {
        val instant = Instant.parse("2026-05-10T12:38:07.814455123Z")

        val result = target.fromInstant(instant)

        assertEquals(1_778_416_687_814_455_123L, result)
    }

    @Test
    fun `toInstant when epoch nanos is provided then restores full precision instant`() {
        val epochNanos = 1_778_416_687_814_455_123L

        val result = target.toInstant(epochNanos)

        assertEquals(Instant.parse("2026-05-10T12:38:07.814455123Z"), result)
    }

    @Test
    fun `fromSecureItemDraftTypeDb when draft type is provided then returns storage value`() {
        val result = target.fromSecureItemDraftTypeDb(SecureItemDraftTypeDb.UPDATE)

        assertEquals("UPDATE", result)
    }

    @Test
    fun `toSecureItemDraftTypeDb when storage value is supported then returns enum`() {
        val result = target.toSecureItemDraftTypeDb("DELETE")

        assertEquals(SecureItemDraftTypeDb.DELETE, result)
    }

    @Test
    fun `toSecureItemDraftTypeDb when storage value is unsupported then throws illegal argument exception`() {
        val throwable = kotlin.runCatching {
            target.toSecureItemDraftTypeDb("RESTORE")
        }.exceptionOrNull()

        requireNotNull(throwable)
        assertEquals(
            "Unsupported SecureItemDraftTypeDb value: RESTORE",
            throwable.message,
        )
    }
}
