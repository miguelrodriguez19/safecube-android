package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem

import com.miguelrodriguez19.safecube.core.vault.test.testSecureItemDraft
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class SecureItemSyncDraftTest {
    @Test
    fun `constructor preserves draft sync status and sync error`() {
        val draft = testSecureItemDraft(
            draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
            lastSyncError = "Conflict",
        )

        assertEquals(SecureItemDraftSyncStatus.CONFLICT, draft.draftSyncStatus)
        assertEquals("Conflict", draft.lastSyncError)
    }

    @Test
    fun `constructor when last sync error is blank then throws`() {
        assertThrows(IllegalArgumentException::class.java) {
            testSecureItemDraft(lastSyncError = " ")
        }
    }
}
