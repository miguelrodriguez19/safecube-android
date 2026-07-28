package com.miguelrodriguez19.safecube.core.vault.domain.usecase.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteUpdateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemEncryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.CurrentInstantProvider
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationIdGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemIdGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftSyncCoordinator
import com.miguelrodriguez19.safecube.core.vault.test.testEncryptedPayload
import com.miguelrodriguez19.safecube.core.vault.test.testSecureItem
import com.miguelrodriguez19.safecube.core.vault.test.testSecureItemDraft
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureItemDraftSyncCoordinatorTest {
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()
    private val secureItemCryptoService = mockk<SecureItemCryptoService>()
    private val secureItemMutationIdGenerator = mockk<SecureItemMutationIdGenerator>()
    private val secureItemIdGenerator = mockk<SecureItemIdGenerator>()
    private val now = Instant.parse("2024-05-01T12:00:00Z")
    private val currentInstantProvider = object : CurrentInstantProvider {
        override fun now(): Instant = now
    }

    private val target = SecureItemDraftSyncCoordinator(
        secureItemRepository = secureItemRepository,
        secureItemDraftRepository = secureItemDraftRepository,
        secureItemCryptoService = secureItemCryptoService,
        currentInstantProvider = currentInstantProvider,
        secureItemMutationIdGenerator = secureItemMutationIdGenerator,
        secureItemIdGenerator = secureItemIdGenerator,
    )

    @Test
    fun `prepare draft for sync when conflict update exists then rebases and resets it to ready`() = runBlocking {
        val official = testSecureItem(
            payloadVersion = 7,
            itemRevision = 12,
            updatedAt = Instant.parse("2024-05-01T11:00:00Z"),
        )
        val draft = testSecureItemDraft(
            logicalItemId = official.logicalItemId,
            remoteItemId = official.remoteItemId,
            draftType = SecureItemDraftType.UPDATE,
            draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
            payloadVersion = 4,
            lastSyncError = "Conflict",
        )
        val upsertedDraft = slot<com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft>()
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRepository.getItem(draft.logicalItemId) } returns official
        every { secureItemMutationIdGenerator.generate() } returns java.util.UUID.randomUUID()
        every { secureItemCryptoService.decrypt(any()) } returns SecureItemDecryptionResult.Success(
            NoteSecureItemContent("rebased body"),
        )
        every {
            secureItemCryptoService.encrypt(draft.logicalItemId, 8, NoteSecureItemContent("rebased body"))
        } returns SecureItemEncryptionResult.Success(testEncryptedPayload(payload = byteArrayOf(9)))
        coJustRun { secureItemDraftRepository.upsert(capture(upsertedDraft)) }

        val result = target.prepareDraftForSync(draft.logicalItemId)

        assertTrue(result)
        assertEquals(8, upsertedDraft.captured.payloadVersion)
        assertEquals(SecureItemDraftSyncStatus.READY_TO_SYNC, upsertedDraft.captured.draftSyncStatus)
        assertEquals(official.itemRevision, upsertedDraft.captured.baseItemRevision)
        assertEquals(now, upsertedDraft.captured.updatedAt)
        assertNull(upsertedDraft.captured.lastSyncError)
    }

    @Test
    fun `prepare conflicted update over remote tombstone creates a new encrypted create draft`() = runBlocking {
        val deletedOfficial = testSecureItem(
            payloadVersion = 7,
            itemRevision = 13,
            changeSequence = 20,
            deletedAt = now.minusSeconds(30),
        )
        val draft = testSecureItemDraft(
            logicalItemId = deletedOfficial.logicalItemId,
            remoteItemId = deletedOfficial.remoteItemId,
            payloadVersion = 8,
            draftType = SecureItemDraftType.UPDATE,
            draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
            baseItemRevision = 13,
        )
        val newLogicalItemId = java.util.UUID.randomUUID()
        val newMutationId = java.util.UUID.randomUUID()
        val replacement = slot<com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft>()
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRepository.getItem(draft.logicalItemId) } returns deletedOfficial
        every { secureItemCryptoService.decrypt(any()) } returns SecureItemDecryptionResult.Success(
            NoteSecureItemContent("local proposal"),
        )
        every { secureItemIdGenerator.generate() } returns newLogicalItemId
        every { secureItemMutationIdGenerator.generate() } returns newMutationId
        every {
            secureItemCryptoService.encrypt(
                newLogicalItemId,
                1,
                NoteSecureItemContent("local proposal"),
            )
        } returns SecureItemEncryptionResult.Success(testEncryptedPayload(payload = byteArrayOf(7)))
        coEvery {
            secureItemDraftRepository.replace(draft.logicalItemId, capture(replacement))
        } returns true

        assertTrue(target.prepareDraftForSync(draft.logicalItemId))
        assertEquals(newLogicalItemId, replacement.captured.logicalItemId)
        assertNull(replacement.captured.remoteItemId)
        assertEquals(1L, replacement.captured.payloadVersion)
        assertEquals(SecureItemDraftType.CREATE, replacement.captured.draftType)
        assertNull(replacement.captured.baseItemRevision)
        assertEquals(newMutationId, replacement.captured.mutationId)
    }

    @Test
    fun `officialize update rejects mismatched mutation without deleting draft`() = runBlocking {
        val draft = testSecureItemDraft()
        val result = RemoteUpdateSecureItemResult(
            itemId = requireNotNull(draft.remoteItemId),
            mutationId = java.util.UUID.randomUUID(),
            payloadVersion = draft.payloadVersion,
            itemRevision = 3,
            changeSequence = 30,
            updatedAt = now,
        )

        assertFalse(target.officializeUpdatedDraft(draft, result))
        coVerify(exactly = 0) { secureItemRepository.officializeDraft(any(), any()) }
        coVerify(exactly = 0) { secureItemDraftRepository.delete(any()) }
    }

    @Test
    fun `officialize update rejects mismatched payload version without deleting draft`() = runBlocking {
        val draft = testSecureItemDraft()
        val result = RemoteUpdateSecureItemResult(
            itemId = requireNotNull(draft.remoteItemId),
            mutationId = draft.mutationId,
            payloadVersion = draft.payloadVersion + 1,
            itemRevision = 3,
            changeSequence = 30,
            updatedAt = now,
        )

        assertFalse(target.officializeUpdatedDraft(draft, result))
        coVerify(exactly = 0) { secureItemRepository.officializeDraft(any(), any()) }
        coVerify(exactly = 0) { secureItemDraftRepository.delete(any()) }
    }
}
