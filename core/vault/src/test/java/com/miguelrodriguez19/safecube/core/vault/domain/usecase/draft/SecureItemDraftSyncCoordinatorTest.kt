package com.miguelrodriguez19.safecube.core.vault.domain.usecase.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteCreateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteDeleteSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteUpdateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoError
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
import java.util.UUID
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

    @Test
    fun `officialization validates each response identity before persisting`() = runBlocking {
        val create = testSecureItemDraft(
            remoteItemId = null,
            draftType = SecureItemDraftType.CREATE,
        )
        assertFalse(
            target.officializeCreatedDraft(
                create,
                createResult(create, mutationId = UUID.randomUUID()),
            ),
        )
        assertFalse(
            target.officializeCreatedDraft(
                create,
                createResult(create, payloadVersion = create.payloadVersion + 1),
            ),
        )

        val update = testSecureItemDraft(draftType = SecureItemDraftType.UPDATE)
        assertFalse(
            target.officializeUpdatedDraft(
                update,
                updateResult(update, itemId = UUID.randomUUID()),
            ),
        )

        val delete = testSecureItemDraft(
            draftType = SecureItemDraftType.DELETE,
            deletedAt = now,
        )
        assertFalse(
            target.officializeDeletedDraft(
                delete,
                deleteResult(delete, itemId = UUID.randomUUID()),
            ),
        )
        assertFalse(
            target.officializeDeletedDraft(
                delete,
                deleteResult(delete, mutationId = UUID.randomUUID()),
            ),
        )
        assertFalse(
            target.officializeDeletedDraft(
                delete,
                deleteResult(delete, payloadVersion = delete.payloadVersion + 1),
            ),
        )
        coVerify(exactly = 0) { secureItemRepository.officializeDraft(any(), any()) }
    }

    @Test
    fun `create update and delete officialization persist decryptable candidates`() = runBlocking {
        val create = testSecureItemDraft(
            remoteItemId = null,
            draftType = SecureItemDraftType.CREATE,
        )
        val update = testSecureItemDraft(draftType = SecureItemDraftType.UPDATE)
        val delete = testSecureItemDraft(
            draftType = SecureItemDraftType.DELETE,
            deletedAt = now,
        )
        val candidates = mutableListOf<SecureItem>()
        every {
            secureItemCryptoService.decrypt(capture(candidates))
        } returns SecureItemDecryptionResult.Success(NoteSecureItemContent("verified"))
        coEvery {
            secureItemRepository.officializeDraft(any(), any())
        } returns true

        assertTrue(target.officializeCreatedDraft(create, createResult(create)))
        assertTrue(target.officializeUpdatedDraft(update, updateResult(update)))
        assertTrue(target.officializeDeletedDraft(delete, deleteResult(delete)))
        assertEquals(3, candidates.size)
        assertNull(candidates[0].deletedAt)
        assertNull(candidates[1].deletedAt)
        assertEquals(now, candidates[2].deletedAt)
        assertEquals(now, candidates[2].updatedAt)
    }

    @Test
    fun `officialization preserves draft when candidate is unreadable or persistence throws`() = runBlocking {
        val draft = testSecureItemDraft(draftType = SecureItemDraftType.UPDATE)
        every {
            secureItemCryptoService.decrypt(any())
        } returns SecureItemDecryptionResult.Error(SecureItemCryptoError.CryptographicFailure)
        assertFalse(target.officializeUpdatedDraft(draft, updateResult(draft)))

        every {
            secureItemCryptoService.decrypt(any())
        } returns SecureItemDecryptionResult.Success(NoteSecureItemContent("verified"))
        coEvery {
            secureItemRepository.officializeDraft(any(), any())
        } throws IllegalStateException("room")
        assertFalse(target.officializeUpdatedDraft(draft, updateResult(draft)))
    }

    @Test
    fun `conflict and discard helpers report repository success and failure`() = runBlocking {
        val draft = testSecureItemDraft()
        val remote = testSecureItem(logicalItemId = draft.logicalItemId)
        coEvery {
            secureItemRepository.replaceOfficialWithConflictedDraft(any(), any(), any())
        } returns true
        assertTrue(
            target.replaceOfficialWithRemoteAndConflictedDraft(
                draft,
                remote,
                now,
                "Conflict",
            ),
        )

        coEvery {
            secureItemRepository.replaceOfficialWithConflictedDraft(any(), any(), any())
        } throws IllegalStateException("room")
        assertFalse(
            target.replaceOfficialWithRemoteAndConflictedDraft(
                draft,
                remote,
                now,
                "Conflict",
            ),
        )

        coEvery {
            secureItemDraftRepository.updateStatus(
                draft.logicalItemId,
                SecureItemDraftSyncStatus.CONFLICT,
                "Conflict",
            )
        } returns true
        assertTrue(target.markDraftConflict(draft.logicalItemId, "Conflict"))

        coEvery { secureItemDraftRepository.delete(draft.logicalItemId) } returns true
        assertTrue(target.discardDraft(draft.logicalItemId))
        coEvery {
            secureItemDraftRepository.delete(draft.logicalItemId)
        } throws IllegalStateException("room")
        assertFalse(target.discardDraft(draft.logicalItemId))
    }

    @Test
    fun `already deleted draft resolves only against a persisted tombstone`() = runBlocking {
        val draft = testSecureItemDraft(
            draftType = SecureItemDraftType.DELETE,
            deletedAt = now,
        )
        coEvery { secureItemRepository.getItem(draft.logicalItemId) } returns null
        assertFalse(target.resolveAlreadyDeletedDraft(draft))

        coEvery {
            secureItemRepository.getItem(draft.logicalItemId)
        } returns testSecureItem(logicalItemId = draft.logicalItemId)
        assertFalse(target.resolveAlreadyDeletedDraft(draft))

        coEvery {
            secureItemRepository.getItem(draft.logicalItemId)
        } returns testSecureItem(logicalItemId = draft.logicalItemId, deletedAt = now)
        coEvery { secureItemDraftRepository.delete(draft.logicalItemId) } returns false
        assertFalse(target.resolveAlreadyDeletedDraft(draft))
        coEvery { secureItemDraftRepository.delete(draft.logicalItemId) } returns true
        assertTrue(target.resolveAlreadyDeletedDraft(draft))
    }

    @Test
    fun `prepare rejects absent create and structurally invalid update drafts`() = runBlocking {
        val missing = UUID.randomUUID()
        coEvery { secureItemDraftRepository.getDraft(missing) } returns null
        assertFalse(target.prepareDraftForSync(missing))

        val create = testSecureItemDraft(
            remoteItemId = null,
            draftType = SecureItemDraftType.CREATE,
        )
        coEvery { secureItemDraftRepository.getDraft(create.logicalItemId) } returns create
        assertFalse(target.prepareDraftForSync(create.logicalItemId))

        val withoutOfficial = testSecureItemDraft(draftType = SecureItemDraftType.UPDATE)
        coEvery {
            secureItemDraftRepository.getDraft(withoutOfficial.logicalItemId)
        } returns withoutOfficial
        coEvery { secureItemRepository.getItem(withoutOfficial.logicalItemId) } returns null
        assertFalse(target.prepareDraftForSync(withoutOfficial.logicalItemId))

        val officialWithoutRemote = testSecureItem(remoteItemId = null)
        val update = testSecureItemDraft(
            logicalItemId = officialWithoutRemote.logicalItemId,
            remoteItemId = null,
            draftType = SecureItemDraftType.UPDATE,
        )
        coEvery { secureItemDraftRepository.getDraft(update.logicalItemId) } returns update
        coEvery {
            secureItemRepository.getItem(update.logicalItemId)
        } returns officialWithoutRemote
        assertFalse(target.prepareDraftForSync(update.logicalItemId))
    }

    @Test
    fun `prepare update preserves conflict when decryption or encryption fails`() = runBlocking {
        val official = testSecureItem(payloadVersion = 4, itemRevision = 8)
        val draft = testSecureItemDraft(
            logicalItemId = official.logicalItemId,
            remoteItemId = official.remoteItemId,
            payloadVersion = 5,
            draftType = SecureItemDraftType.UPDATE,
        )
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRepository.getItem(draft.logicalItemId) } returns official
        every {
            secureItemCryptoService.decrypt(any())
        } returns SecureItemDecryptionResult.Error(SecureItemCryptoError.MalformedPayload)
        assertFalse(target.prepareDraftForSync(draft.logicalItemId))

        every {
            secureItemCryptoService.decrypt(any())
        } returns SecureItemDecryptionResult.Success(NoteSecureItemContent("local"))
        every {
            secureItemCryptoService.encrypt(draft.logicalItemId, 6, any())
        } returns SecureItemEncryptionResult.Error(SecureItemCryptoError.CryptographicFailure)
        assertFalse(target.prepareDraftForSync(draft.logicalItemId))
    }

    @Test
    fun `prepare save as new preserves old draft when crypto fails`() = runBlocking {
        val tombstone = testSecureItem(
            payloadVersion = 5,
            itemRevision = 9,
            deletedAt = now,
        )
        val draft = testSecureItemDraft(
            logicalItemId = tombstone.logicalItemId,
            remoteItemId = tombstone.remoteItemId,
            payloadVersion = 6,
            draftType = SecureItemDraftType.UPDATE,
        )
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRepository.getItem(draft.logicalItemId) } returns tombstone
        every {
            secureItemCryptoService.decrypt(any())
        } returns SecureItemDecryptionResult.Error(SecureItemCryptoError.MalformedPayload)
        assertFalse(target.prepareDraftForSync(draft.logicalItemId))

        val newId = UUID.randomUUID()
        every {
            secureItemCryptoService.decrypt(any())
        } returns SecureItemDecryptionResult.Success(NoteSecureItemContent("local"))
        every { secureItemIdGenerator.generate() } returns newId
        every {
            secureItemCryptoService.encrypt(newId, 1, any())
        } returns SecureItemEncryptionResult.Error(SecureItemCryptoError.CryptographicFailure)
        assertFalse(target.prepareDraftForSync(draft.logicalItemId))
    }

    @Test
    fun `prepare delete rebases only against active remote official`() = runBlocking {
        val missing = testSecureItemDraft(
            draftType = SecureItemDraftType.DELETE,
            deletedAt = now,
        )
        coEvery { secureItemDraftRepository.getDraft(missing.logicalItemId) } returns missing
        coEvery { secureItemRepository.getItem(missing.logicalItemId) } returns null
        assertFalse(target.prepareDraftForSync(missing.logicalItemId))

        val deletedOfficial = testSecureItem(deletedAt = now)
        val deletedDraft = testSecureItemDraft(
            logicalItemId = deletedOfficial.logicalItemId,
            remoteItemId = deletedOfficial.remoteItemId,
            draftType = SecureItemDraftType.DELETE,
            deletedAt = now,
        )
        coEvery {
            secureItemDraftRepository.getDraft(deletedDraft.logicalItemId)
        } returns deletedDraft
        coEvery {
            secureItemRepository.getItem(deletedDraft.logicalItemId)
        } returns deletedOfficial
        assertFalse(target.prepareDraftForSync(deletedDraft.logicalItemId))

        val localOfficial = testSecureItem(remoteItemId = null)
        val localDraft = testSecureItemDraft(
            logicalItemId = localOfficial.logicalItemId,
            remoteItemId = null,
            draftType = SecureItemDraftType.DELETE,
            deletedAt = now,
        )
        coEvery { secureItemDraftRepository.getDraft(localDraft.logicalItemId) } returns localDraft
        coEvery { secureItemRepository.getItem(localDraft.logicalItemId) } returns localOfficial
        assertFalse(target.prepareDraftForSync(localDraft.logicalItemId))

        val official = testSecureItem(itemRevision = 17)
        val ready = testSecureItemDraft(
            logicalItemId = official.logicalItemId,
            remoteItemId = official.remoteItemId,
            draftType = SecureItemDraftType.DELETE,
            deletedAt = now.minusSeconds(1),
        )
        val slot = slot<SecureItemSyncDraft>()
        val mutationId = UUID.randomUUID()
        coEvery { secureItemDraftRepository.getDraft(ready.logicalItemId) } returns ready
        coEvery { secureItemRepository.getItem(ready.logicalItemId) } returns official
        every { secureItemMutationIdGenerator.generate() } returns mutationId
        coJustRun { secureItemDraftRepository.upsert(capture(slot)) }
        assertTrue(target.prepareDraftForSync(ready.logicalItemId))
        assertEquals(17L, slot.captured.baseItemRevision)
        assertEquals(mutationId, slot.captured.mutationId)
        assertEquals(now, slot.captured.deletedAt)
    }

    private fun createResult(
        draft: SecureItemSyncDraft,
        mutationId: UUID = draft.mutationId,
        payloadVersion: Long = draft.payloadVersion,
    ) = RemoteCreateSecureItemResult(
        itemId = UUID.randomUUID(),
        mutationId = mutationId,
        payloadVersion = payloadVersion,
        itemRevision = 1,
        changeSequence = 10,
        updatedAt = now,
    )

    private fun updateResult(
        draft: SecureItemSyncDraft,
        itemId: UUID = requireNotNull(draft.remoteItemId),
    ) = RemoteUpdateSecureItemResult(
        itemId = itemId,
        mutationId = draft.mutationId,
        payloadVersion = draft.payloadVersion,
        itemRevision = 2,
        changeSequence = 11,
        updatedAt = now,
    )

    private fun deleteResult(
        draft: SecureItemSyncDraft,
        itemId: UUID = requireNotNull(draft.remoteItemId),
        mutationId: UUID = draft.mutationId,
        payloadVersion: Long = draft.payloadVersion,
    ) = RemoteDeleteSecureItemResult(
        itemId = itemId,
        mutationId = mutationId,
        payloadVersion = payloadVersion,
        itemRevision = 2,
        changeSequence = 11,
        deletedAt = now,
    )
}
