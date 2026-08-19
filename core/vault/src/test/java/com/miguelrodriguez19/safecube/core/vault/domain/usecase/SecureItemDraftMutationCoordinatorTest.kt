package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoError
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemEncryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.CurrentInstantProvider
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemDraftMutationCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemIdGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationIdGenerator
import com.miguelrodriguez19.safecube.core.vault.test.FakeVaultSessionManager
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
import org.junit.Assert.assertNull
import org.junit.Test

class SecureItemDraftMutationCoordinatorTest {
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()
    private val secureItemCryptoService = mockk<SecureItemCryptoService>()
    private val vaultSessionManager = FakeVaultSessionManager(VaultState.Unlocked)
    private val secureItemIdGenerator = mockk<SecureItemIdGenerator>()
    private val secureItemMutationIdGenerator = mockk<SecureItemMutationIdGenerator>()
    private val now = Instant.parse("2024-02-01T10:15:30Z")
    private val currentInstantProvider = object : CurrentInstantProvider {
        override fun now(): Instant = now
    }

    private val target = SecureItemDraftMutationCoordinator(
        secureItemRepository = secureItemRepository,
        secureItemDraftRepository = secureItemDraftRepository,
        secureItemCryptoService = secureItemCryptoService,
        vaultSessionManager = vaultSessionManager,
        secureItemIdGenerator = secureItemIdGenerator,
        secureItemMutationIdGenerator = secureItemMutationIdGenerator,
        currentInstantProvider = currentInstantProvider,
    )

    @Test
    fun `create stores a ready create draft`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val draftSlot = slot<SecureItemSyncDraft>()
        every { secureItemIdGenerator.generate() } returns logicalItemId
        every { secureItemMutationIdGenerator.generate() } returns UUID.randomUUID()
        every {
            secureItemCryptoService.encrypt(logicalItemId, 1, any())
        } returns SecureItemEncryptionResult.Success(testEncryptedPayload())
        coJustRun { secureItemDraftRepository.upsert(capture(draftSlot)) }

        val result = target.create(
            displayHint = "New note",
            content = NoteSecureItemContent("Body"),
        )

        assertEquals(SecureItemMutationResult.Success(logicalItemId), result)
        assertEquals(SecureItemDraftType.CREATE, draftSlot.captured.draftType)
        assertEquals(SecureItemDraftSyncStatus.READY_TO_SYNC, draftSlot.captured.draftSyncStatus)
        assertNull(draftSlot.captured.remoteItemId)
    }

    @Test
    fun `update official item creates ready update draft with next payload version`() = runBlocking {
        val official = testSecureItem(
            payloadVersion = 4,
            itemRevision = 9,
            updatedAt = Instant.parse("2024-02-01T10:00:00Z"),
        )
        val draftSlot = slot<SecureItemSyncDraft>()
        coEvery { secureItemDraftRepository.getDraft(official.logicalItemId) } returns null
        coEvery { secureItemRepository.getItem(official.logicalItemId) } returns official
        every { secureItemMutationIdGenerator.generate() } returns UUID.randomUUID()
        every {
            secureItemCryptoService.encrypt(official.logicalItemId, 5, any())
        } returns SecureItemEncryptionResult.Success(testEncryptedPayload())
        coJustRun { secureItemDraftRepository.upsert(capture(draftSlot)) }

        val result = target.update(
            logicalItemId = official.logicalItemId,
            displayHint = "Edited note",
            expectedItemType = SecureItemType.NOTE,
            content = NoteSecureItemContent("Edited body"),
        )

        assertEquals(SecureItemMutationResult.Success(official.logicalItemId), result)
        assertEquals(SecureItemDraftType.UPDATE, draftSlot.captured.draftType)
        assertEquals(5, draftSlot.captured.payloadVersion)
        assertEquals(9L, draftSlot.captured.baseItemRevision)
        assertEquals(SecureItemDraftSyncStatus.READY_TO_SYNC, draftSlot.captured.draftSyncStatus)
    }

    @Test
    fun `editing conflicted draft preserves conflict and original base until publish`() = runBlocking {
        val official = testSecureItem(
            payloadVersion = 8,
            itemRevision = 6,
        )
        val originalMutationId = UUID.randomUUID()
        val nextMutationId = UUID.randomUUID()
        val conflict = testSecureItemDraft(
            logicalItemId = official.logicalItemId,
            remoteItemId = official.remoteItemId,
            payloadVersion = 7,
            mutationId = originalMutationId,
            draftType = SecureItemDraftType.UPDATE,
            draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
            baseItemRevision = 5,
            lastSyncError = "Remote item changed.",
        )
        val draftSlot = slot<SecureItemSyncDraft>()
        coEvery { secureItemDraftRepository.getDraft(official.logicalItemId) } returns conflict
        coEvery { secureItemRepository.getItem(official.logicalItemId) } returns official
        every { secureItemMutationIdGenerator.generate() } returns nextMutationId
        every {
            secureItemCryptoService.encrypt(official.logicalItemId, 9, any())
        } returns SecureItemEncryptionResult.Success(testEncryptedPayload())
        coJustRun { secureItemDraftRepository.upsert(capture(draftSlot)) }

        val result = target.update(
            logicalItemId = official.logicalItemId,
            displayHint = "Edited conflict",
            expectedItemType = SecureItemType.NOTE,
            content = NoteSecureItemContent("Edited local proposal"),
        )

        assertEquals(SecureItemMutationResult.Success(official.logicalItemId), result)
        assertEquals(SecureItemDraftSyncStatus.CONFLICT, draftSlot.captured.draftSyncStatus)
        assertEquals(5L, draftSlot.captured.baseItemRevision)
        assertEquals(9L, draftSlot.captured.payloadVersion)
        assertEquals(nextMutationId, draftSlot.captured.mutationId)
        assertEquals("Remote item changed.", draftSlot.captured.lastSyncError)
    }

    @Test
    fun `soft delete when item exists only as create draft then deletes local draft`() = runBlocking {
        val createDraft = testSecureItemDraft(
            remoteItemId = null,
            draftType = SecureItemDraftType.CREATE,
        )
        coEvery { secureItemDraftRepository.getDraft(createDraft.logicalItemId) } returns createDraft
        coEvery { secureItemDraftRepository.delete(createDraft.logicalItemId) } returns true

        val result = target.softDelete(createDraft.logicalItemId)

        assertEquals(SecureItemMutationResult.Success(createDraft.logicalItemId), result)
        coVerify(exactly = 0) { secureItemRepository.getItem(any()) }
    }

    @Test
    fun `create and update reject mutations while vault is locked`() = runBlocking {
        vaultSessionManager.setState(VaultState.Locked)

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked),
            target.create("Note", NoteSecureItemContent("Body")),
        )
        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked),
            target.update(
                UUID.randomUUID(),
                "Note",
                SecureItemType.NOTE,
                NoteSecureItemContent("Body"),
            ),
        )
        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked),
            target.softDelete(UUID.randomUUID()),
        )
        coVerify(exactly = 0) { secureItemDraftRepository.upsert(any()) }
        coVerify(exactly = 0) { secureItemDraftRepository.getDraft(any()) }
        coVerify(exactly = 0) { secureItemRepository.getItem(any()) }
    }

    @Test
    fun `create validates display hint before allocating identity`() = runBlocking {
        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("displayHint must not be blank."),
            ),
            target.create("   ", NoteSecureItemContent("Body")),
        )
        coVerify(exactly = 0) { secureItemDraftRepository.upsert(any()) }
    }

    @Test
    fun `create maps every encryption error`() = runBlocking {
        val errors = listOf(
            SecureItemCryptoError.VaultLocked to SecureItemCrudError.VaultLocked,
            SecureItemCryptoError.AccountIdUnavailable to SecureItemCrudError.VaultLocked,
            SecureItemCryptoError.MalformedPayload to encryptionValidationError(),
            SecureItemCryptoError.CryptographicFailure to encryptionValidationError(),
            SecureItemCryptoError.ContentDecodingFailed(
                SecureItemContentDecodeError.InvalidPayload,
            ) to encryptionValidationError(),
        )
        every { secureItemIdGenerator.generate() } returns UUID.randomUUID()

        errors.forEach { (cryptoError, expected) ->
            every {
                secureItemCryptoService.encrypt(any(), 1, any())
            } returns SecureItemEncryptionResult.Error(cryptoError)

            assertEquals(
                SecureItemMutationResult.Error(expected),
                target.create("Valid", NoteSecureItemContent("Body")),
            )
        }
    }

    @Test
    fun `update validates item existence type tombstone and display hint`() = runBlocking {
        val missingId = UUID.randomUUID()
        coEvery { secureItemDraftRepository.getDraft(missingId) } returns null
        coEvery { secureItemRepository.getItem(missingId) } returns null
        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound),
            target.update(missingId, "Note", SecureItemType.NOTE, NoteSecureItemContent("Body")),
        )

        val password = testSecureItem(itemType = SecureItemType.PASSWORD)
        coEvery { secureItemDraftRepository.getDraft(password.logicalItemId) } returns null
        coEvery { secureItemRepository.getItem(password.logicalItemId) } returns password
        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Secure item type mismatch."),
            ),
            target.update(
                password.logicalItemId,
                "Password",
                SecureItemType.NOTE,
                NoteSecureItemContent("Body"),
            ),
        )

        val deleted = testSecureItem(deletedAt = now)
        coEvery { secureItemDraftRepository.getDraft(deleted.logicalItemId) } returns null
        coEvery { secureItemRepository.getItem(deleted.logicalItemId) } returns deleted
        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound),
            target.update(
                deleted.logicalItemId,
                "Deleted",
                SecureItemType.NOTE,
                NoteSecureItemContent("Body"),
            ),
        )

        val official = testSecureItem()
        coEvery { secureItemDraftRepository.getDraft(official.logicalItemId) } returns null
        coEvery { secureItemRepository.getItem(official.logicalItemId) } returns official
        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("displayHint must not be blank."),
            ),
            target.update(
                official.logicalItemId,
                "\t",
                SecureItemType.NOTE,
                NoteSecureItemContent("Body"),
            ),
        )
    }

    @Test
    fun `updating create and delete drafts preserves the correct lifecycle`() = runBlocking {
        val create = testSecureItemDraft(
            remoteItemId = null,
            payloadVersion = 2,
            draftType = SecureItemDraftType.CREATE,
        )
        val createSlot = slot<SecureItemSyncDraft>()
        coEvery { secureItemDraftRepository.getDraft(create.logicalItemId) } returns create
        coEvery { secureItemRepository.getItem(create.logicalItemId) } returns null
        every { secureItemMutationIdGenerator.generate() } returns UUID.randomUUID()
        every {
            secureItemCryptoService.encrypt(create.logicalItemId, 3, any())
        } returns SecureItemEncryptionResult.Success(testEncryptedPayload())
        coJustRun { secureItemDraftRepository.upsert(capture(createSlot)) }

        assertEquals(
            SecureItemMutationResult.Success(create.logicalItemId),
            target.update(
                create.logicalItemId,
                " Local create ",
                SecureItemType.NOTE,
                NoteSecureItemContent("Edited"),
            ),
        )
        assertEquals(SecureItemDraftType.CREATE, createSlot.captured.draftType)
        assertNull(createSlot.captured.baseItemRevision)
        assertEquals("Local create", createSlot.captured.displayHint)

        val official = testSecureItem(payloadVersion = 4, itemRevision = 8)
        val delete = testSecureItemDraft(
            logicalItemId = official.logicalItemId,
            remoteItemId = official.remoteItemId,
            payloadVersion = 4,
            draftType = SecureItemDraftType.DELETE,
            deletedAt = now,
            baseItemRevision = 8,
        )
        val updateSlot = slot<SecureItemSyncDraft>()
        coEvery { secureItemDraftRepository.getDraft(official.logicalItemId) } returns delete
        coEvery { secureItemRepository.getItem(official.logicalItemId) } returns official
        every {
            secureItemCryptoService.encrypt(official.logicalItemId, 5, any())
        } returns SecureItemEncryptionResult.Success(testEncryptedPayload())
        coJustRun { secureItemDraftRepository.upsert(capture(updateSlot)) }

        assertEquals(
            SecureItemMutationResult.Success(official.logicalItemId),
            target.update(
                official.logicalItemId,
                "Restore",
                SecureItemType.NOTE,
                NoteSecureItemContent("Restored"),
            ),
        )
        assertEquals(SecureItemDraftType.UPDATE, updateSlot.captured.draftType)
        assertEquals(SecureItemDraftSyncStatus.READY_TO_SYNC, updateSlot.captured.draftSyncStatus)
    }

    @Test
    fun `update uses local only official as create and maps encryption failure`() = runBlocking {
        val localOnly = testSecureItem(
            remoteItemId = null,
            payloadVersion = 1,
            itemRevision = 1,
        )
        coEvery { secureItemDraftRepository.getDraft(localOnly.logicalItemId) } returns null
        coEvery { secureItemRepository.getItem(localOnly.logicalItemId) } returns localOnly
        every {
            secureItemCryptoService.encrypt(localOnly.logicalItemId, 2, any())
        } returns SecureItemEncryptionResult.Error(SecureItemCryptoError.CryptographicFailure)

        assertEquals(
            SecureItemMutationResult.Error(encryptionValidationError()),
            target.update(
                localOnly.logicalItemId,
                "Local",
                SecureItemType.NOTE,
                NoteSecureItemContent("Body"),
            ),
        )
    }

    @Test
    fun `soft delete reports missing states and creates delete draft for official`() = runBlocking {
        val failedCreate = testSecureItemDraft(
            remoteItemId = null,
            draftType = SecureItemDraftType.CREATE,
        )
        coEvery { secureItemDraftRepository.getDraft(failedCreate.logicalItemId) } returns failedCreate
        coEvery { secureItemDraftRepository.delete(failedCreate.logicalItemId) } returns false
        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound),
            target.softDelete(failedCreate.logicalItemId),
        )

        val missingId = UUID.randomUUID()
        coEvery { secureItemDraftRepository.getDraft(missingId) } returns null
        coEvery { secureItemRepository.getItem(missingId) } returns null
        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound),
            target.softDelete(missingId),
        )

        val deleted = testSecureItem(deletedAt = now)
        coEvery { secureItemDraftRepository.getDraft(deleted.logicalItemId) } returns null
        coEvery { secureItemRepository.getItem(deleted.logicalItemId) } returns deleted
        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound),
            target.softDelete(deleted.logicalItemId),
        )

        val official = testSecureItem(itemRevision = 12, payloadVersion = 7)
        val slot = slot<SecureItemSyncDraft>()
        val mutationId = UUID.randomUUID()
        coEvery { secureItemDraftRepository.getDraft(official.logicalItemId) } returns null
        coEvery { secureItemRepository.getItem(official.logicalItemId) } returns official
        every { secureItemMutationIdGenerator.generate() } returns mutationId
        coJustRun { secureItemDraftRepository.upsert(capture(slot)) }

        assertEquals(
            SecureItemMutationResult.Success(official.logicalItemId),
            target.softDelete(official.logicalItemId),
        )
        assertEquals(SecureItemDraftType.DELETE, slot.captured.draftType)
        assertEquals(12L, slot.captured.baseItemRevision)
        assertEquals(7L, slot.captured.payloadVersion)
        assertEquals(mutationId, slot.captured.mutationId)
        assertEquals(now, slot.captured.deletedAt)
    }

    private fun encryptionValidationError() =
        SecureItemCrudError.ValidationError("Unable to encrypt secure item.")
}
