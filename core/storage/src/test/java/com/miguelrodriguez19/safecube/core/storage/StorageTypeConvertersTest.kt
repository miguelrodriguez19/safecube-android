package com.miguelrodriguez19.safecube.core.storage

import org.junit.Assert.assertEquals
import org.junit.Test

class StorageTypeConvertersTest {

    private val target = StorageTypeConverters()

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
