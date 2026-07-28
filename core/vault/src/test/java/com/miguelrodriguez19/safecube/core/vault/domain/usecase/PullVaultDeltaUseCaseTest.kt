package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemChangesPage
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemPayloadIdentityReader
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull.PullVaultDeltaUseCase
import com.miguelrodriguez19.safecube.core.vault.test.testSecureItemDraft
import com.miguelrodriguez19.safecube.core.vault.test.testVaultKeyMaterial
import io.mockk.coEvery
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

    private fun everyBasePull(
        accountId: UUID,
        logicalItemId: UUID,
        remoteItem: RemoteSecureItem,
        draft: SecureItemSyncDraft,
    ) {
        io.mockk.every { vaultKeyMaterialLocalRepository.get() } returns testVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns 20
        coEvery {
            secureItemRemoteRepository.listVaultItemChanges(after = 20, limit = 100)
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
        deletedAt = null,
    )
}
