package com.miguelrodriguez19.safecube.core.storage

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SecureItemDraftTypeDbTest {

    @Test
    fun `toDomain when value is update then returns update`() {
        val result = SecureItemDraftTypeDb.UPDATE.toDomain()

        assertEquals(SecureItemDraftType.UPDATE, result)
    }

    @Test
    fun `toDomain when value is delete then returns delete`() {
        val result = SecureItemDraftTypeDb.DELETE.toDomain()

        assertEquals(SecureItemDraftType.DELETE, result)
    }

    @Test
    fun `fromStorageValue when value is supported then returns enum`() {
        val updateResult = SecureItemDraftTypeDb.fromStorageValue("UPDATE")
        val deleteResult = SecureItemDraftTypeDb.fromStorageValue("DELETE")

        assertEquals(SecureItemDraftTypeDb.UPDATE, updateResult)
        assertEquals(SecureItemDraftTypeDb.DELETE, deleteResult)
    }

    @Test
    fun `fromStorageValue when value is unsupported then returns null`() {
        val result = SecureItemDraftTypeDb.fromStorageValue("RESTORE")

        assertNull(result)
    }

    @Test
    fun `fromDomain when value is update then returns update db enum`() {
        val result = SecureItemDraftTypeDb.fromDomain(SecureItemDraftType.UPDATE)

        assertEquals(SecureItemDraftTypeDb.UPDATE, result)
    }

    @Test
    fun `fromDomain when value is delete then returns delete db enum`() {
        val result = SecureItemDraftTypeDb.fromDomain(SecureItemDraftType.DELETE)

        assertEquals(SecureItemDraftTypeDb.DELETE, result)
    }
}
