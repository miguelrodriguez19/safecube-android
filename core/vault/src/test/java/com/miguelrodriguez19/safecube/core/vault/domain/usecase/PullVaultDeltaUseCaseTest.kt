package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultKeyMaterial
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteListVaultItemsRequestParams
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.pull.PullVaultDeltaResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.VaultKeyMaterialLocalRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftPolicyCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.pull.PullVaultDeltaUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PullVaultDeltaUseCaseTest {
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val secureItemRemoteRepository = mockk<SecureItemRemoteRepository>()
    private val vaultKeyMaterialLocalRepository = mockk<VaultKeyMaterialLocalRepository>()
    private val secureItemDraftPolicyCoordinator = mockk<SecureItemDraftPolicyCoordinator>()

    private val target = PullVaultDeltaUseCase(
        secureItemRepository = secureItemRepository,
        secureItemRemoteRepository = secureItemRemoteRepository,
        vaultKeyMaterialLocalRepository = vaultKeyMaterialLocalRepository,
        secureItemDraftPolicyCoordinator = secureItemDraftPolicyCoordinator,
    )

    @Test
    fun `invoke when remote delta is consistent then applies upsert and tombstone and updates checkpoint`() = runBlocking {
        val accountId = UUID.randomUUID()
        val checkpoint = Instant.now().minusSeconds(7200)
        val itemId = UUID.randomUUID()
        val tombstoneId = UUID.randomUUID()
        val itemUpdatedAt = Instant.now().minusSeconds(3600)
        val tombstoneUpdatedAt = Instant.now().minusSeconds(1800)
        val upsertedItem = slot<SecureItem>()
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns checkpoint
        coEvery {
            secureItemRemoteRepository.listVaultItems(
                requestParams = RemoteListVaultItemsRequestParams(
                    updatedAfter = checkpoint,
                    includeDeleted = true,
                    limit = null,
                ),
            )
        } returns SecureItemRemoteResult.Success(
            listOf(
                sampleSummary(
                    itemId = itemId,
                    updatedAt = itemUpdatedAt,
                    deletedAt = null,
                ),
                sampleSummary(
                    itemId = itemId,
                    updatedAt = checkpoint.plusSeconds(1),
                    deletedAt = null,
                ),
                sampleSummary(
                    itemId = tombstoneId,
                    updatedAt = tombstoneUpdatedAt,
                    deletedAt = tombstoneUpdatedAt,
                ),
            ),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            sampleRemoteItem(
                itemId = itemId,
                updatedAt = itemUpdatedAt,
            ),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns null
        coEvery { secureItemRepository.findByRemoteItemId(tombstoneId) } returns null
        coEvery { secureItemRepository.applyRemoteUpsert(capture(upsertedItem), itemUpdatedAt) } returns true
        coEvery {
            secureItemRepository.applyRemoteDelete(
                remoteItemId = tombstoneId,
                deletedAt = tombstoneUpdatedAt,
                lastSyncedAt = tombstoneUpdatedAt,
            )
        } returns true
        coEvery { secureItemRepository.updateSyncCheckpoint(accountId, tombstoneUpdatedAt) } returns Unit
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Success(
                processedSummaryCount = 2,
                appliedUpsertCount = 1,
                appliedDeleteCount = 1,
                skippedDirtyOrConflictCount = 0,
                checkpointUpdatedTo = tombstoneUpdatedAt,
            ),
            result,
        )
        assertEquals(itemId, upsertedItem.captured.remoteItemId)
        assertEquals(SecureItemType.NOTE, upsertedItem.captured.itemType)
    }

    @Test
    fun `invoke when local item is pending update and remote item is active then stores update draft and applies official remote`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        val localItem = sampleLocalSecureItem(
            remoteItemId = itemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(sampleSummary(itemId = itemId, updatedAt = updatedAt, deletedAt = null)),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            sampleRemoteItem(itemId = itemId, updatedAt = updatedAt),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns localItem
        coEvery {
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = localItem,
                remoteItem = any(),
                draftType = SecureItemDraftType.UPDATE,
                lastSyncedAt = updatedAt,
            )
        } returns true
        coEvery { secureItemRepository.updateSyncCheckpoint(accountId, updatedAt) } returns Unit

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Success(
                processedSummaryCount = 1,
                appliedUpsertCount = 1,
                appliedDeleteCount = 0,
                skippedDirtyOrConflictCount = 0,
                checkpointUpdatedTo = updatedAt,
            ),
            result,
        )
        coVerify(exactly = 1) {
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = localItem,
                remoteItem = any(),
                draftType = SecureItemDraftType.UPDATE,
                lastSyncedAt = updatedAt,
            )
        }
        coVerify(exactly = 0) { secureItemRepository.markConflict(any(), any()) }
    }

    @Test
    fun `invoke when local item is pending delete and remote item is active then stores delete draft and applies official remote`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        val localItem = sampleLocalSecureItem(
            remoteItemId = itemId,
            syncState = SecureItemSyncState.PENDING_DELETE,
            deletedAt = updatedAt.minusSeconds(10),
        )
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(sampleSummary(itemId = itemId, updatedAt = updatedAt, deletedAt = null)),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            sampleRemoteItem(itemId = itemId, updatedAt = updatedAt),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns localItem
        coEvery {
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = localItem,
                remoteItem = any(),
                draftType = SecureItemDraftType.DELETE,
                lastSyncedAt = updatedAt,
            )
        } returns true
        coEvery { secureItemRepository.updateSyncCheckpoint(accountId, updatedAt) } returns Unit

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Success(
                processedSummaryCount = 1,
                appliedUpsertCount = 1,
                appliedDeleteCount = 0,
                skippedDirtyOrConflictCount = 0,
                checkpointUpdatedTo = updatedAt,
            ),
            result,
        )
        coVerify(exactly = 1) {
            secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(
                localItem = localItem,
                remoteItem = any(),
                draftType = SecureItemDraftType.DELETE,
                lastSyncedAt = updatedAt,
            )
        }
    }

    @Test
    fun `invoke when remote tombstone arrives for local pending update then applies delete and discards local changes`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val deletedAt = Instant.now()
        val localItem = sampleLocalSecureItem(
            remoteItemId = itemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(sampleSummary(itemId = itemId, updatedAt = deletedAt, deletedAt = deletedAt)),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns localItem
        coEvery {
            secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                logicalItemId = localItem.logicalItemId,
                remoteItemId = itemId,
                deletedAt = deletedAt,
                lastSyncedAt = deletedAt,
            )
        } returns true
        coEvery { secureItemRepository.updateSyncCheckpoint(accountId, deletedAt) } returns Unit

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Success(
                processedSummaryCount = 1,
                appliedUpsertCount = 0,
                appliedDeleteCount = 1,
                skippedDirtyOrConflictCount = 0,
                checkpointUpdatedTo = deletedAt,
            ),
            result,
        )
    }

    @Test
    fun `invoke when remote tombstone arrives for synced local item then applies official tombstone`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val deletedAt = Instant.now()
        val localItem = sampleLocalSecureItem(
            remoteItemId = itemId,
            syncState = SecureItemSyncState.SYNCED,
        )
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(sampleSummary(itemId = itemId, updatedAt = deletedAt, deletedAt = deletedAt)),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns localItem
        coEvery {
            secureItemRepository.applyRemoteDelete(
                remoteItemId = itemId,
                deletedAt = deletedAt,
                lastSyncedAt = deletedAt,
            )
        } returns true
        coEvery { secureItemRepository.updateSyncCheckpoint(accountId, deletedAt) } returns Unit

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Success(
                processedSummaryCount = 1,
                appliedUpsertCount = 0,
                appliedDeleteCount = 1,
                skippedDirtyOrConflictCount = 0,
                checkpointUpdatedTo = deletedAt,
            ),
            result,
        )
    }

    @Test
    fun `invoke when remote tombstone arrives for pending create then marks conflict and skips delete`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val deletedAt = Instant.now()
        val localItem = sampleLocalSecureItem(
            remoteItemId = itemId,
            syncState = SecureItemSyncState.PENDING_CREATE,
        )
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(sampleSummary(itemId = itemId, updatedAt = deletedAt, deletedAt = deletedAt)),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns localItem
        coEvery { secureItemRepository.markConflict(localItem.logicalItemId, any()) } returns true
        coEvery { secureItemRepository.updateSyncCheckpoint(accountId, deletedAt) } returns Unit

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Success(
                processedSummaryCount = 1,
                appliedUpsertCount = 0,
                appliedDeleteCount = 0,
                skippedDirtyOrConflictCount = 1,
                checkpointUpdatedTo = deletedAt,
            ),
            result,
        )
    }

    @Test
    fun `invoke when remote tombstone arrives for pending update and local delete resolution fails then returns local delete failure`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val deletedAt = Instant.now()
        val localItem = sampleLocalSecureItem(
            remoteItemId = itemId,
            syncState = SecureItemSyncState.PENDING_UPDATE,
        )
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(sampleSummary(itemId = itemId, updatedAt = deletedAt, deletedAt = deletedAt)),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns localItem
        coEvery {
            secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                logicalItemId = localItem.logicalItemId,
                remoteItemId = itemId,
                deletedAt = deletedAt,
                lastSyncedAt = deletedAt,
            )
        } returns false

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.LocalApplyFailed(
                    itemId = itemId,
                    operation = "DELETE",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when local item is in conflict then keeps remote change skipped`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        val localItem = sampleLocalSecureItem(
            remoteItemId = itemId,
            syncState = SecureItemSyncState.CONFLICT,
        )
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(sampleSummary(itemId = itemId, updatedAt = updatedAt, deletedAt = null)),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            sampleRemoteItem(itemId = itemId, updatedAt = updatedAt),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns localItem
        coEvery { secureItemRepository.markConflict(localItem.logicalItemId, any()) } returns true
        coEvery { secureItemRepository.updateSyncCheckpoint(accountId, updatedAt) } returns Unit

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Success(
                processedSummaryCount = 1,
                appliedUpsertCount = 0,
                appliedDeleteCount = 0,
                skippedDirtyOrConflictCount = 1,
                checkpointUpdatedTo = updatedAt,
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemDraftPolicyCoordinator.replaceOfficialItemWithRemoteAndDraft(any(), any(), any(), any()) }
    }

    @Test
    fun `invoke when remote active item arrives for synced local item then applies official upsert`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        val localItem = sampleLocalSecureItem(
            remoteItemId = itemId,
            syncState = SecureItemSyncState.SYNCED,
        )
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(sampleSummary(itemId = itemId, updatedAt = updatedAt, deletedAt = null)),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            sampleRemoteItem(itemId = itemId, updatedAt = updatedAt),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns localItem
        coEvery { secureItemRepository.applyRemoteUpsert(any(), updatedAt) } returns true
        coEvery { secureItemRepository.updateSyncCheckpoint(accountId, updatedAt) } returns Unit

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Success(
                processedSummaryCount = 1,
                appliedUpsertCount = 1,
                appliedDeleteCount = 0,
                skippedDirtyOrConflictCount = 0,
                checkpointUpdatedTo = updatedAt,
            ),
            result,
        )
    }

    @Test
    fun `invoke when remote active item arrives for synced local item and upsert fails then returns local apply failed`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        val localItem = sampleLocalSecureItem(
            remoteItemId = itemId,
            syncState = SecureItemSyncState.SYNCED,
        )
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(sampleSummary(itemId = itemId, updatedAt = updatedAt, deletedAt = null)),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            sampleRemoteItem(itemId = itemId, updatedAt = updatedAt),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns localItem
        coEvery { secureItemRepository.applyRemoteUpsert(any(), updatedAt) } returns false

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.LocalApplyFailed(
                    itemId = itemId,
                    operation = "UPSERT",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when detail fetch fails then returns remote detail error and does not update checkpoint`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(sampleSummary(itemId = itemId, updatedAt = Instant.now(), deletedAt = null)),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Unauthorized,
        )

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.RemoteDetailFailed(
                    itemId = itemId,
                    error = SecureItemRemoteError.Unauthorized,
                ),
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRepository.updateSyncCheckpoint(any(), any()) }
    }

    @Test
    fun `invoke when account id is unavailable then returns account unavailable error`() = runBlocking {
        every { vaultKeyMaterialLocalRepository.get() } returns null

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(PullVaultDeltaError.AccountIdUnavailable),
            result,
        )
    }

    @Test
    fun `invoke when remote list fails then returns remote list error`() = runBlocking {
        val accountId = UUID.randomUUID()
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.Unauthorized,
        )

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.RemoteListFailed(SecureItemRemoteError.Unauthorized),
            ),
            result,
        )
    }

    @Test
    fun `invoke when remote item type is unsupported then returns unsupported type error`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(
                RemoteSecureItemSummary(
                    itemId = itemId,
                    itemType = "CARD",
                    schemaVersion = 1,
                    displayHint = "bad type",
                    payloadVersion = 1,
                    updatedAt = updatedAt,
                    deletedAt = null,
                ),
            ),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            RemoteSecureItem(
                itemId = itemId,
                itemType = "CARD",
                schemaVersion = 1,
                displayHint = "bad type",
                payload = byteArrayOf(4, 5, 6),
                payloadVersion = 1,
                updatedAt = updatedAt,
                deletedAt = null,
            ),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns null

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.UnsupportedRemoteItemType(
                    itemId = itemId,
                    wireType = "CARD",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when local upsert fails then returns local apply failed error`() = runBlocking {
        val accountId = UUID.randomUUID()
        val itemId = UUID.randomUUID()
        val updatedAt = Instant.now()
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(
            listOf(sampleSummary(itemId = itemId, updatedAt = updatedAt, deletedAt = null)),
        )
        coEvery { secureItemRemoteRepository.getVaultItem(itemId) } returns SecureItemRemoteResult.Success(
            sampleRemoteItem(itemId = itemId, updatedAt = updatedAt),
        )
        coEvery { secureItemRepository.findByRemoteItemId(itemId) } returns null
        coEvery { secureItemRepository.applyRemoteUpsert(any(), any()) } returns false

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Error(
                PullVaultDeltaError.LocalApplyFailed(
                    itemId = itemId,
                    operation = "UPSERT",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when remote list is empty then returns success without updating checkpoint`() = runBlocking {
        val accountId = UUID.randomUUID()
        every { vaultKeyMaterialLocalRepository.get() } returns sampleVaultKeyMaterial(accountId)
        coEvery { secureItemRepository.getSyncCheckpoint(accountId) } returns null
        coEvery { secureItemRemoteRepository.listVaultItems(any()) } returns SecureItemRemoteResult.Success(emptyList())

        val result = target()

        assertEquals(
            PullVaultDeltaResult.Success(
                processedSummaryCount = 0,
                appliedUpsertCount = 0,
                appliedDeleteCount = 0,
                skippedDirtyOrConflictCount = 0,
                checkpointUpdatedTo = null,
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemRepository.updateSyncCheckpoint(any(), any()) }
    }

    private fun sampleVaultKeyMaterial(accountId: UUID): VaultKeyMaterial = VaultKeyMaterial(
        accountId = accountId,
        kekEncMaster = byteArrayOf(1),
        kekEncRecovery = byteArrayOf(2),
        kdfAlgorithm = "argon2id",
        kdfSalt = byteArrayOf(3),
        kdfMemoryKib = 1,
        kdfIterations = 1,
        kdfParallelism = 1,
        kdfOutputLen = 1,
        cryptoVersion = "v1",
    )

    private fun sampleLocalSecureItem(
        remoteItemId: UUID,
        syncState: SecureItemSyncState,
        deletedAt: Instant? = null,
    ): SecureItem {
        val updatedAt = Instant.now().minusSeconds(60)
        return SecureItem(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = remoteItemId,
            itemType = SecureItemType.NOTE,
            schemaVersion = 1,
            displayHint = "local",
            payload = byteArrayOf(7, 7, 7),
            payloadVersion = 1,
            createdAt = updatedAt.minusSeconds(3600),
            updatedAt = updatedAt,
            deletedAt = deletedAt,
            syncState = syncState,
            lastSyncedAt = null,
            lastSyncError = null,
        )
    }

    private fun sampleSummary(
        itemId: UUID,
        updatedAt: Instant,
        deletedAt: Instant?,
    ): RemoteSecureItemSummary = RemoteSecureItemSummary(
        itemId = itemId,
        itemType = SecureItemType.NOTE.wireName,
        schemaVersion = 1,
        displayHint = "remote",
        payloadVersion = 1,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
    )

    private fun sampleRemoteItem(
        itemId: UUID,
        updatedAt: Instant,
    ): RemoteSecureItem = RemoteSecureItem(
        itemId = itemId,
        itemType = SecureItemType.NOTE.wireName,
        schemaVersion = 1,
        displayHint = "remote",
        payload = byteArrayOf(1, 1, 1),
        payloadVersion = 2,
        updatedAt = updatedAt,
        deletedAt = null,
    )
}
