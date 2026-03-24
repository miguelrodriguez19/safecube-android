package com.miguelrodriguez19.safecube.core.storage

import java.time.Instant
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StorageTypeConvertersTest {
    private val converters = StorageTypeConverters()

    @Test
    fun `uuid converter roundtrip preserves value`() {
        val uuid = UUID.fromString("4f89ab0e-453f-4be5-b261-95068f2ad6f0")

        val persisted = converters.fromUuid(uuid)
        val restored = converters.toUuid(persisted)

        assertEquals(uuid, restored)
    }

    @Test
    fun `uuid converter returns null for blank value`() {
        assertNull(converters.toUuid(" "))
    }

    @Test
    fun `instant converter roundtrip preserves value`() {
        val instant = Instant.parse("2026-03-24T10:15:30Z")

        val persisted = converters.fromInstant(instant)
        val restored = converters.toInstant(persisted)

        assertEquals(instant, restored)
    }

    @Test
    fun `instant converter returns null for null value`() {
        assertNull(converters.fromInstant(null))
        assertNull(converters.toInstant(null))
    }
}
