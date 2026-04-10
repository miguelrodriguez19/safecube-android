package com.miguelrodriguez19.safecube.core.storage

import java.time.Instant
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class StorageTypeConvertersTest {
    private val target = StorageTypeConverters()

    @Test
    fun `uuid converter when value is valid then preserves value through roundtrip`() {
        val uuid = UUID.fromString("4f89ab0e-453f-4be5-b261-95068f2ad6f0")

        val persisted = target.fromUuid(uuid)
        val restored = target.toUuid(persisted)

        assertEquals(uuid, restored)
    }

    @Test
    fun `uuid converter when value is blank then returns null`() {
        val result = target.toUuid(" ")

        assertNull(result)
    }

    @Test
    fun `instant converter when value is valid then preserves value through roundtrip`() {
        val instant = Instant.parse("2026-03-24T10:15:30Z")

        val persisted = target.fromInstant(instant)
        val restored = target.toInstant(persisted)

        assertEquals(instant, restored)
    }

    @Test
    fun `instant converter when value is null then returns null`() {
        val persisted = target.fromInstant(null)
        val restored = target.toInstant(null)

        assertNull(persisted)
        assertNull(restored)
    }
}
