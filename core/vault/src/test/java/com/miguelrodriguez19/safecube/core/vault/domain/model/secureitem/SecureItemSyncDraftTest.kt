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

    @Test
    fun `constructor rejects malformed encrypted draft metadata`() {
        listOf<() -> Unit>(
            { testSecureItemDraft(displayHint = " ") },
            { testSecureItemDraft().copy(schemaVersion = 0) },
            { testSecureItemDraft(payload = byteArrayOf()) },
            { testSecureItemDraft(payloadVersion = 0) },
            { testSecureItemDraft(baseItemRevision = 0) },
            {
                testSecureItemDraft(
                    draftType = SecureItemDraftType.CREATE,
                    baseItemRevision = 1,
                )
            },
            {
                testSecureItemDraft(
                    draftType = SecureItemDraftType.UPDATE,
                    baseItemRevision = null,
                )
            },
            {
                testSecureItemDraft(
                    draftType = SecureItemDraftType.DELETE,
                    baseItemRevision = null,
                )
            },
        ).forEach { construction ->
            assertThrows(IllegalArgumentException::class.java) { construction() }
        }
    }

    @Test
    fun `constructor accepts create without base and mutations with positive base`() {
        testSecureItemDraft(
            draftType = SecureItemDraftType.CREATE,
            remoteItemId = null,
            baseItemRevision = null,
        )
        testSecureItemDraft(
            draftType = SecureItemDraftType.UPDATE,
            baseItemRevision = 1,
        )
        testSecureItemDraft(
            draftType = SecureItemDraftType.DELETE,
            deletedAt = java.time.Instant.parse("2024-01-02T00:00:00Z"),
            baseItemRevision = 1,
        )
    }
}
