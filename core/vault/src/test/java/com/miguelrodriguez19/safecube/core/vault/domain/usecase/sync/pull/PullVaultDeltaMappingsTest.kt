package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.test.testSecureItem
import com.miguelrodriguez19.safecube.core.vault.test.testVaultKeyMaterial
import java.time.Instant
import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PullVaultDeltaMappingsTest {
    @Test
    fun `account identity mapping supports absent material and absent account`() {
        assertNull(null.accountIdOrNull())
        assertNull(testVaultKeyMaterial().copy(accountId = null).accountIdOrNull())
        val accountId = UUID.randomUUID()
        assertEquals(accountId, testVaultKeyMaterial(accountId).accountIdOrNull())
    }

    @Test
    fun `remote snapshot mapping preserves protocol metadata`() {
        val logicalId = UUID.randomUUID()
        val createdAt = Instant.parse("2024-01-01T00:00:00Z")
        val deletedAt = Instant.parse("2024-02-01T00:00:00Z")
        val remote = RemoteSecureItem(
            itemId = UUID.randomUUID(),
            itemType = "NOTE",
            schemaVersion = 2,
            displayHint = "Remote",
            payload = byteArrayOf(7, 8),
            payloadVersion = 4,
            itemRevision = 9,
            changeSequence = 17,
            updatedAt = Instant.parse("2024-03-01T00:00:00Z"),
            deletedAt = deletedAt,
        )

        val local = remote.toLocalSecureItem(logicalId, SecureItemType.NOTE, createdAt)

        assertEquals(logicalId, local.logicalItemId)
        assertEquals(remote.itemId, local.remoteItemId)
        assertEquals(remote.payloadVersion, local.payloadVersion)
        assertEquals(remote.itemRevision, local.itemRevision)
        assertEquals(remote.changeSequence, local.changeSequence)
        assertEquals(createdAt, local.createdAt)
        assertEquals(deletedAt, local.deletedAt)
        assertEquals(SecureItemSyncState.SYNCED, local.syncState)
        assertEquals(remote.updatedAt, local.lastSyncedAt)
    }

    @Test
    fun `official state comparison checks every protocol and content field`() {
        val base = testSecureItem(
            payload = byteArrayOf(1, 2, 3),
            payloadVersion = 4,
            itemRevision = 5,
            changeSequence = 6,
            createdAt = Instant.parse("2024-01-01T00:00:00Z"),
            updatedAt = Instant.parse("2024-01-02T00:00:00Z"),
            deletedAt = Instant.parse("2024-01-03T00:00:00Z"),
        )
        assertTrue(base.matchesOfficialRemoteState(base.copy(payload = base.payload.copyOf())))

        val mismatches: List<SecureItem> = listOf(
            base.copy(logicalItemId = UUID.randomUUID()),
            base.copy(remoteItemId = UUID.randomUUID()),
            base.copy(itemType = SecureItemType.PASSWORD),
            base.copy(schemaVersion = base.schemaVersion + 1),
            base.copy(displayHint = "Different"),
            base.copy(payload = byteArrayOf(9)),
            base.copy(payloadVersion = base.payloadVersion + 1),
            base.copy(itemRevision = base.itemRevision + 1),
            base.copy(changeSequence = base.changeSequence + 1),
            base.copy(createdAt = base.createdAt.plusSeconds(1)),
            base.copy(updatedAt = base.updatedAt.plusSeconds(1)),
            base.copy(deletedAt = base.deletedAt?.plusSeconds(1)),
        )

        mismatches.forEach { assertFalse(base.matchesOfficialRemoteState(it)) }
    }
}
