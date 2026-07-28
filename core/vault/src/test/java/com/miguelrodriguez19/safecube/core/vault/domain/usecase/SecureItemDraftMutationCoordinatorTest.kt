package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
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
}
