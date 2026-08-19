package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemChangesPage
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoError
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemPayloadIdentityReader
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull.PullVaultDeltaUseCase
import com.miguelrodriguez19.safecube.core.vault.test.testSecureItemDraft
import com.miguelrodriguez19.safecube.core.vault.test.testVaultKeyMaterial
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PullVaultDeltaUseCaseTest {
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()
    private val secureItemRemoteRepository = mockk<SecureItemRemoteRepository>()
    private val vaultKeyMaterialLocalRepository = mockk<VaultKeyMaterialLocalRepository>()
    private val secureItemPayloadIdentityReader = mockk<SecureItemPayloadIdentityReader>()
    private val secureItemCryptoService = mockk<SecureItemCryptoService>()

    private val target = PullVaultDeltaUseCase(
        secureItemRepository = secureItemRepository,
        secureItemDraftRepository = secureItemDraftRepository,
        secureItemRemoteRepository = secureItemRemoteRepository,
        vaultKeyMaterialLocalRepository = vaultKeyMaterialLocalRepository,
        secureItemPayloadIdentityReader = secureItemPayloadIdentityReader,
        secureItemCryptoService = secureItemCryptoService,
    )

    @Test
    fun `pull applies remote official and conflicted draft with one page transaction`() = runBlocking {
        val accountId = UUID.randomUUID()
        val logicalItemId = UUID.randomUUID()
        val remoteItem = remoteItem(
            itemRevision = 6,
            changeSequence = 21,
            payload = byteArrayOf(7, 8, 9),
        )
        val draft = testSecureItemDraft(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItem.itemId,
            baseItemRevision = 5,
        )
        val officialItems = slot<List<SecureItem>>()
        val conflicts = slot<List<SecureItemSyncDraft>>()

        everyBasePull(accountId, logicalItemId, remoteItem, draft)
        coEvery {
            secureItemRepository.applyRemotePage(
                accountId = accountId,
                items = capture(officialItems),
                conflictedDrafts = capture(conflicts),
                draftsToDelete = emptySet(),
                lastAppliedChangeSequence = 21,
                lastSyncedAt = remoteItem.updatedAt,
            )
        } returns true

        val result = target()

        assertTrue(result is PullVaultDeltaResult.Success)
        assertEquals(6L, officialItems.captured.single().itemRevision)
        assertEquals(21L, officialItems.captured.single().changeSequence)
        assertEquals(5L, conflicts.captured.single().baseItemRevision)
        assertEquals(SecureItemDraftSyncStatus.CONFLICT, conflicts.captured.single().draftSyncStatus)
    }

    @Test
    fun `pull removes draft when remote snapshot confirms the encrypted mutation`() = runBlocking {
        val accountId = UUID.randomUUID()
        val logicalItemId = UUID.randomUUID()
        val payload = byteArrayOf(4, 5, 6)
        val remoteItem = remoteItem(
            itemRevision = 6,
            changeSequence = 22,
            payloadVersion = 2,
            payload = payload,
        )
        val draft = testSecureItemDraft(
            logicalItemId = logicalItemId,
            remoteItemId = remoteItem.itemId,
            payloadVersion = 2,
            payload = payload,
            baseItemRevision = 5,
        )
        val draftsToDelete = slot<Set<UUID>>()

        everyBasePull(accountId, logicalItemId, remoteItem, draft)
        coEvery {
            secureItemRepository.applyRemotePage(
                accountId = accountId,
                items = any(),
                conflictedDrafts = emptyList(),
                draftsToDelete = capture(draftsToDelete),
                lastAppliedChangeSequence = 22,
                lastSyncedAt = remoteItem.updatedAt,
            )
        } returns true

        val result = target()

        assertTrue(result is PullVaultDeltaResult.Success)
        assertEquals(setOf(logicalItemId), draftsToDelete.captured)
    }

    @Test
    fun `pull accepts increasing account cursor values that are not contiguous`() = runBlocking {
        val accountId = UUID.randomUUID()
        val firstLogicalId = UUID.randomUUID()
        val secondLogicalId = UUID.randomUUID()
        val firstRemote = remoteItem(
            itemRevision = 2,
            changeSequence = 7,
            payload = byteArrayOf(1),
        )
        val secondRemote = remoteItem(
            itemRevision = 4,
            changeSequence = 10,
            payload = byteArrayOf(2),
        )
        io.mockk.every {
            vaultKeyMaterialLocalRepository.get()
        } returns testVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns 5
        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = 5, limit = 100)
        } returns SecureItemRemoteResult.Success(
            RemoteSecureItemChangesPage(
                items = listOf(firstRemote, secondRemote),
                nextCursor = 10,
                hasMore = false,
            ),
        )
        listOf(
            firstRemote to firstLogicalId,
            secondRemote to secondLogicalId,
        ).forEach { (remote, logicalId) ->
            coEvery { secureItemRepository.findByRemoteItemId(remote.itemId) } returns null
            coEvery { secureItemRepository.getItem(logicalId) } returns null
            coEvery { secureItemDraftRepository.findByRemoteItemId(remote.itemId) } returns null
            coEvery { secureItemDraftRepository.getDraft(logicalId) } returns null
            io.mockk.every {
                secureItemPayloadIdentityReader.readLogicalItemId(remote.payload)
            } returns logicalId
        }
        io.mockk.every {
            secureItemCryptoService.decrypt(any())
        } returns SecureItemDecryptionResult.Success(NoteSecureItemContent("remote"))
        coEvery {
            secureItemRepository.applyRemotePage(
                accountId = accountId,
                items = any(),
                conflictedDrafts = emptyList(),
                draftsToDelete = emptySet(),
                lastAppliedChangeSequence = 10,
                lastSyncedAt = any(),
            )
        } returns true

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Success(
                processedSummaryCount = 2,
                appliedUpsertCount = 2,
                appliedDeleteCount = 0,
                skippedDirtyOrConflictCount = 0,
                checkpointUpdatedTo = 10,
            ),
            result,
        )
        coVerify(exactly = 1) {
            secureItemRemoteRepository.listVaultItemChanges(after = 5, limit = 100)
        }
    }

    @Test
    fun `pull requires account identity and maps remote list failure`() = runBlocking {
        every {
            vaultKeyMaterialLocalRepository.get()
        } returns testVaultKeyMaterial().copy(accountId = null)
        assertEquals(
            PullVaultDeltaResult.Error(PullVaultDeltaError.AccountIdUnavailable),
            target(),
        )

        val accountId = UUID.randomUUID()
        every { vaultKeyMaterialLocalRepository.get() } returns testVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = 0, limit = 25)
        } returns SecureItemRemoteResult.Error(SecureItemRemoteError.Unauthorized)
        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.RemoteListFailed(SecureItemRemoteError.Unauthorized),
            ),
            target(limit = 25),
        )
    }

    @Test
    fun `pull exposes retryable and terminal remote decisions without advancing checkpoint`() = runBlocking {
        val accountId = UUID.randomUUID()
        every { vaultKeyMaterialLocalRepository.get() } returns testVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns 5

        listOf(
            SecureItemRemoteError.HttpError(503, null) to RetryDecision.Retryable,
            SecureItemRemoteError.HttpError(428, null) to RetryDecision.Terminal,
        ).forEach { (error, decision) ->
            coEvery {
                secureItemRemoteRepository.listVaultItemChanges(after = 5, limit = 100)
            } returns SecureItemRemoteResult.Error(error)

            val result = target()

            assertEquals(decision, (result as PullVaultDeltaResult.Error).reason.retryDecision)
            coVerify(exactly = 0) {
                secureItemRepository.applyRemotePage(any(), any(), any(), any(), any(), any())
            }
        }
    }

    @Test
    fun `empty terminal page succeeds without advancing checkpoint`() = runBlocking {
        val accountId = UUID.randomUUID()
        every { vaultKeyMaterialLocalRepository.get() } returns testVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns 4
        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = 4, limit = 100)
        } returns SecureItemRemoteResult.Success(
            RemoteSecureItemChangesPage(emptyList(), nextCursor = 4, hasMore = false),
        )

        assertEquals(
            PullVaultDeltaResult.Success(0, 0, 0, 0, null),
            target(),
        )
    }

    @Test
    fun `pull rejects regressing empty and incorrectly ordered pages`() = runBlocking {
        val accountId = UUID.randomUUID()
        every { vaultKeyMaterialLocalRepository.get() } returns testVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns 5

        val regressing = RemoteSecureItemChangesPage(emptyList(), nextCursor = 4, hasMore = false)
        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = 5, limit = 100)
        } returns SecureItemRemoteResult.Success(regressing)
        assertLocalApplyFailure(target(), "NON_MONOTONIC_CHANGE_CURSOR")

        val emptyMore = RemoteSecureItemChangesPage(emptyList(), nextCursor = 5, hasMore = true)
        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = 5, limit = 100)
        } returns SecureItemRemoteResult.Success(emptyMore)
        assertLocalApplyFailure(target(), "EMPTY_CHANGE_PAGE_WITH_MORE_RESULTS")

        val equalFirst = remoteItem(2, 5, payload = byteArrayOf(1))
        val descendingFirst = remoteItem(2, 8, payload = byteArrayOf(2))
        val descendingSecond = remoteItem(3, 7, payload = byteArrayOf(3))
        val cursorMismatch = remoteItem(4, 9, payload = byteArrayOf(4))
        listOf(
            RemoteSecureItemChangesPage(listOf(equalFirst), nextCursor = 5, hasMore = false),
            RemoteSecureItemChangesPage(
                listOf(descendingFirst, descendingSecond),
                nextCursor = 7,
                hasMore = false,
            ),
            RemoteSecureItemChangesPage(listOf(cursorMismatch), nextCursor = 10, hasMore = false),
        ).forEach { page ->
            coEvery {
                secureItemRemoteRepository.listVaultItemChanges(after = 5, limit = 100)
            } returns SecureItemRemoteResult.Success(page)
            assertLocalApplyFailure(target(), "INVALID_CHANGE_PAGE_ORDER")
        }
    }

    @Test
    fun `pull rejects unknown identity unreadable payload and failed room transaction`() = runBlocking {
        val accountId = UUID.randomUUID()
        val logicalItemId = UUID.randomUUID()
        val remote = remoteItem(2, 6, payload = byteArrayOf(1))
        every { vaultKeyMaterialLocalRepository.get() } returns testVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns 5
        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = 5, limit = 100)
        } returns SecureItemRemoteResult.Success(
            RemoteSecureItemChangesPage(listOf(remote), 6, false),
        )

        val unknownType = remote.copy(itemType = "UNKNOWN")
        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = 5, limit = 100)
        } returns SecureItemRemoteResult.Success(
            RemoteSecureItemChangesPage(listOf(unknownType), 6, false),
        )
        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.UnsupportedRemoteItemType(unknownType.itemId, "UNKNOWN"),
            ),
            target(),
        )

        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = 5, limit = 100)
        } returns SecureItemRemoteResult.Success(
            RemoteSecureItemChangesPage(listOf(remote), 6, false),
        )
        every {
            secureItemPayloadIdentityReader.readLogicalItemId(remote.payload)
        } returns null
        assertLocalApplyFailure(target(), "READ_PAYLOAD_IDENTITY", remote.itemId)

        every {
            secureItemPayloadIdentityReader.readLogicalItemId(remote.payload)
        } returns logicalItemId
        every {
            secureItemCryptoService.decrypt(any())
        } returns SecureItemDecryptionResult.Error(SecureItemCryptoError.CryptographicFailure)
        coEvery { secureItemRepository.findByRemoteItemId(remote.itemId) } returns null
        coEvery { secureItemRepository.getItem(logicalItemId) } returns null
        coEvery { secureItemDraftRepository.findByRemoteItemId(remote.itemId) } returns null
        coEvery { secureItemDraftRepository.getDraft(logicalItemId) } returns null
        assertLocalApplyFailure(target(), "DECRYPT_REMOTE_SNAPSHOT", remote.itemId)
        coVerify(exactly = 0) {
            secureItemRepository.applyRemotePage(any(), any(), any(), any(), any(), any())
        }

        every {
            secureItemCryptoService.decrypt(any())
        } returns SecureItemDecryptionResult.Success(NoteSecureItemContent("remote"))
        coEvery {
            secureItemRepository.applyRemotePage(
                accountId,
                any(),
                emptyList(),
                emptySet(),
                6,
                remote.updatedAt,
            )
        } returns false
        assertLocalApplyFailure(target(), "APPLY_CHANGE_PAGE", remote.itemId)
    }

    @Test
    fun `pull processes multiple pages and persists each cursor`() = runBlocking {
        val accountId = UUID.randomUUID()
        val firstId = UUID.randomUUID()
        val secondId = UUID.randomUUID()
        val first = remoteItem(2, 7, payload = byteArrayOf(1))
        val second = remoteItem(3, 12, payload = byteArrayOf(2))
        every { vaultKeyMaterialLocalRepository.get() } returns testVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns 5
        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = 5, limit = 2)
        } returns SecureItemRemoteResult.Success(
            RemoteSecureItemChangesPage(listOf(first), 7, true),
        )
        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = 7, limit = 2)
        } returns SecureItemRemoteResult.Success(
            RemoteSecureItemChangesPage(listOf(second), 12, false),
        )
        listOf(first to firstId, second to secondId).forEach { (remote, logicalId) ->
            every {
                secureItemPayloadIdentityReader.readLogicalItemId(remote.payload)
            } returns logicalId
            coEvery { secureItemRepository.findByRemoteItemId(remote.itemId) } returns null
            coEvery { secureItemRepository.getItem(logicalId) } returns null
            coEvery { secureItemDraftRepository.findByRemoteItemId(remote.itemId) } returns null
            coEvery { secureItemDraftRepository.getDraft(logicalId) } returns null
        }
        every {
            secureItemCryptoService.decrypt(any())
        } returns SecureItemDecryptionResult.Success(NoteSecureItemContent("remote"))
        coEvery {
            secureItemRepository.applyRemotePage(accountId, any(), emptyList(), emptySet(), 7, any())
        } returns true
        coEvery {
            secureItemRepository.applyRemotePage(accountId, any(), emptyList(), emptySet(), 12, any())
        } returns true

        assertEquals(
            PullVaultDeltaResult.Success(2, 2, 0, 0, 12),
            target(limit = 2),
        )
    }

    @Test
    fun `pull confirms delete draft and counts tombstone`() = runBlocking {
        val accountId = UUID.randomUUID()
        val logicalId = UUID.randomUUID()
        val remote = remoteItem(
            itemRevision = 3,
            changeSequence = 8,
            payload = byteArrayOf(8),
            deletedAt = Instant.parse("2024-04-01T01:00:00Z"),
        )
        val draft = testSecureItemDraft(
            logicalItemId = logicalId,
            remoteItemId = remote.itemId,
            draftType = SecureItemDraftType.DELETE,
            deletedAt = remote.deletedAt,
        )
        everyBasePull(accountId, logicalId, remote, draft, checkpoint = 5)
        val deletedDrafts = slot<Set<UUID>>()
        coEvery {
            secureItemRepository.applyRemotePage(
                accountId,
                any(),
                emptyList(),
                capture(deletedDrafts),
                8,
                remote.updatedAt,
            )
        } returns true

        assertEquals(
            PullVaultDeltaResult.Success(1, 0, 1, 0, 8),
            target(),
        )
        assertEquals(setOf(logicalId), deletedDrafts.captured)
    }

    @Test
    fun `pull keeps same revision draft and emits conflict messages by mutation type`() = runBlocking {
        val sameRevision = conflictScenario(
            draftType = SecureItemDraftType.UPDATE,
            baseRevision = 6,
            remoteRevision = 6,
            deletedAt = null,
        )
        assertTrue(sameRevision.conflicts.isEmpty())

        val updateDelete = conflictScenario(
            draftType = SecureItemDraftType.UPDATE,
            baseRevision = 5,
            remoteRevision = 6,
            deletedAt = Instant.parse("2024-04-01T01:00:00Z"),
        )
        assertEquals(
            "Item was deleted remotely. Save the local proposal as a new item or discard it.",
            updateDelete.conflicts.single().lastSyncError,
        )

        val deleteUpdate = conflictScenario(
            draftType = SecureItemDraftType.DELETE,
            baseRevision = 5,
            remoteRevision = 6,
            deletedAt = null,
        )
        assertEquals(
            "Item changed remotely before the local deletion could be applied.",
            deleteUpdate.conflicts.single().lastSyncError,
        )

        val createConflict = conflictScenario(
            draftType = SecureItemDraftType.CREATE,
            baseRevision = null,
            remoteRevision = 6,
            deletedAt = null,
        )
        assertEquals(
            "Item changed remotely while a local proposal existed.",
            createConflict.conflicts.single().lastSyncError,
        )
    }

    private suspend fun conflictScenario(
        draftType: SecureItemDraftType,
        baseRevision: Long?,
        remoteRevision: Long,
        deletedAt: Instant?,
    ): ConflictCapture {
        val accountId = UUID.randomUUID()
        val logicalId = UUID.randomUUID()
        val remote = remoteItem(
            itemRevision = remoteRevision,
            changeSequence = 21,
            payload = byteArrayOf(remoteRevision.toByte()),
            deletedAt = deletedAt,
        )
        val draft = testSecureItemDraft(
            logicalItemId = logicalId,
            remoteItemId = remote.itemId,
            payload = byteArrayOf(99),
            draftType = draftType,
            deletedAt = nowFor(draftType),
            baseItemRevision = baseRevision,
        )
        everyBasePull(accountId, logicalId, remote, draft)
        val conflicts = slot<List<SecureItemSyncDraft>>()
        coEvery {
            secureItemRepository.applyRemotePage(
                accountId,
                any(),
                capture(conflicts),
                emptySet(),
                21,
                remote.updatedAt,
            )
        } returns true
        assertTrue(target() is PullVaultDeltaResult.Success)
        return ConflictCapture(conflicts.captured)
    }

    private fun nowFor(type: SecureItemDraftType): Instant? =
        Instant.parse("2024-04-01T00:30:00Z").takeIf { type == SecureItemDraftType.DELETE }

    private fun assertLocalApplyFailure(
        result: PullVaultDeltaResult,
        operation: String,
        itemId: UUID = UUID(0, 0),
    ) {
        assertEquals(
            PullVaultDeltaResult.Error(PullVaultDeltaError.LocalApplyFailed(itemId, operation)),
            result,
        )
    }

    private fun everyBasePull(
        accountId: UUID,
        logicalItemId: UUID,
        remoteItem: RemoteSecureItem,
        draft: SecureItemSyncDraft,
        checkpoint: Long = 20,
    ) {
        io.mockk.every { vaultKeyMaterialLocalRepository.get() } returns testVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns checkpoint
        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = checkpoint, limit = 100)
        } returns SecureItemRemoteResult.Success(
            RemoteSecureItemChangesPage(
                items = listOf(remoteItem),
                nextCursor = remoteItem.changeSequence,
                hasMore = false,
            ),
        )
        coEvery { secureItemRepository.findByRemoteItemId(remoteItem.itemId) } returns null
        coEvery { secureItemRepository.getItem(logicalItemId) } returns null
        coEvery { secureItemDraftRepository.findByRemoteItemId(remoteItem.itemId) } returns draft
        io.mockk.every {
            secureItemPayloadIdentityReader.readLogicalItemId(remoteItem.payload)
        } returns logicalItemId
        io.mockk.every {
            secureItemCryptoService.decrypt(any())
        } returns SecureItemDecryptionResult.Success(NoteSecureItemContent("remote"))
    }

    private fun remoteItem(
        itemRevision: Long,
        changeSequence: Long,
        payloadVersion: Long = 3,
        payload: ByteArray,
        deletedAt: Instant? = null,
    ): RemoteSecureItem = RemoteSecureItem(
        itemId = UUID.randomUUID(),
        itemType = "NOTE",
        schemaVersion = 1,
        displayHint = "Remote item",
        payload = payload,
        payloadVersion = payloadVersion,
        itemRevision = itemRevision,
        changeSequence = changeSequence,
        updatedAt = Instant.parse("2024-04-01T00:00:00Z"),
        deletedAt = deletedAt,
    )

    private data class ConflictCapture(
        val conflicts: List<SecureItemSyncDraft>,
    )
}
