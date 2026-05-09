package com.miguelrodriguez19.safecube.core.vault.domain.usecase.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteDeleteSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteUpdateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PublishSecureItemDraftError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PublishSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.PublishSecureItemDraftUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftPolicyCoordinator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PublishSecureItemDraftUseCaseTest {
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()
    private val secureItemRemoteRepository = mockk<SecureItemRemoteRepository>()
    private val secureItemDraftPolicyCoordinator = mockk<SecureItemDraftPolicyCoordinator>()

    private val target = PublishSecureItemDraftUseCase(
        secureItemDraftRepository = secureItemDraftRepository,
        secureItemRemoteRepository = secureItemRemoteRepository,
        secureItemDraftPolicyCoordinator = secureItemDraftPolicyCoordinator,
    )

    @Test
    fun `invoke when draft does not exist then returns not found`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDraftRepository.getDraft(logicalItemId) } returns null

        val result = target(logicalItemId)

        assertEquals(
            PublishSecureItemDraftResult.Error(
                PublishSecureItemDraftError.DraftNotFound(logicalItemId),
            ),
            result,
        )
    }

    @Test
    fun `invoke when draft has no remote item id then returns missing remote id error`() = runBlocking {
        val draft = sampleDraft(
            draftType = SecureItemDraftType.UPDATE,
            remoteItemId = null,
        )
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft

        val result = target(draft.logicalItemId)

        assertEquals(
            PublishSecureItemDraftResult.Error(
                PublishSecureItemDraftError.MissingRemoteItemId(
                    logicalItemId = draft.logicalItemId,
                    draftType = SecureItemDraftType.UPDATE,
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when update draft publish succeeds then uses put and finalizes local state`() = runBlocking {
        val draft = sampleDraft(draftType = SecureItemDraftType.UPDATE)
        val remoteUpdatedAt = Instant.now()
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRemoteRepository.updateVaultItem(requireNotNull(draft.remoteItemId), any()) } returns
            SecureItemRemoteResult.Success(
                RemoteUpdateSecureItemResult(
                    itemId = requireNotNull(draft.remoteItemId),
                    payloadVersion = 7,
                    updatedAt = remoteUpdatedAt,
                ),
            )
        coEvery {
            secureItemDraftPolicyCoordinator.finalizePublishedUpdate(
                draft = draft,
                remotePayloadVersion = 7,
                remoteUpdatedAt = remoteUpdatedAt,
            )
        } returns true

        val result = target(draft.logicalItemId)

        assertEquals(
            PublishSecureItemDraftResult.Success(
                logicalItemId = draft.logicalItemId,
                draftType = SecureItemDraftType.UPDATE,
            ),
            result,
        )
    }

    @Test
    fun `invoke when update draft publish succeeds but local official state cannot be finalized then returns local failure`() = runBlocking {
        val draft = sampleDraft(draftType = SecureItemDraftType.UPDATE)
        val remoteUpdatedAt = Instant.now()
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRemoteRepository.updateVaultItem(requireNotNull(draft.remoteItemId), any()) } returns
            SecureItemRemoteResult.Success(
                RemoteUpdateSecureItemResult(
                    itemId = requireNotNull(draft.remoteItemId),
                    payloadVersion = 7,
                    updatedAt = remoteUpdatedAt,
                ),
            )
        coEvery {
            secureItemDraftPolicyCoordinator.finalizePublishedUpdate(
                draft = draft,
                remotePayloadVersion = 7,
                remoteUpdatedAt = remoteUpdatedAt,
            )
        } returns false

        val result = target(draft.logicalItemId)

        assertEquals(
            PublishSecureItemDraftResult.Error(
                PublishSecureItemDraftError.LocalStateUpdateFailed(
                    logicalItemId = draft.logicalItemId,
                    operation = "PUBLISH_DRAFT_UPDATE",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when delete draft publish succeeds then uses delete and finalizes local state`() = runBlocking {
        val draft = sampleDraft(draftType = SecureItemDraftType.DELETE)
        val deletedAt = Instant.now()
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRemoteRepository.deleteVaultItem(requireNotNull(draft.remoteItemId)) } returns
            SecureItemRemoteResult.Success(
                RemoteDeleteSecureItemResult(
                    itemId = requireNotNull(draft.remoteItemId),
                    deletedAt = deletedAt,
                ),
            )
        coEvery {
            secureItemDraftPolicyCoordinator.finalizePublishedDelete(
                draft = draft,
                deletedAt = deletedAt,
            )
        } returns true

        val result = target(draft.logicalItemId)

        assertEquals(
            PublishSecureItemDraftResult.Success(
                logicalItemId = draft.logicalItemId,
                draftType = SecureItemDraftType.DELETE,
            ),
            result,
        )
    }

    @Test
    fun `invoke when update draft remote item is missing then applies tombstone and discards draft`() = runBlocking {
        val draft = sampleDraft(draftType = SecureItemDraftType.UPDATE)
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRemoteRepository.updateVaultItem(requireNotNull(draft.remoteItemId), any()) } returns
            SecureItemRemoteResult.Error(SecureItemRemoteError.ItemNotFound)
        coEvery {
            secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                logicalItemId = draft.logicalItemId,
                remoteItemId = requireNotNull(draft.remoteItemId),
                deletedAt = draft.updatedAt,
                lastSyncedAt = draft.updatedAt,
            )
        } returns true

        val result = target(draft.logicalItemId)

        assertEquals(
            PublishSecureItemDraftResult.Success(
                logicalItemId = draft.logicalItemId,
                draftType = SecureItemDraftType.UPDATE,
            ),
            result,
        )
    }

    @Test
    fun `invoke when update draft not found resolution fails then returns local failure`() = runBlocking {
        val draft = sampleDraft(draftType = SecureItemDraftType.UPDATE)
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRemoteRepository.updateVaultItem(requireNotNull(draft.remoteItemId), any()) } returns
            SecureItemRemoteResult.Error(SecureItemRemoteError.ItemNotFound)
        coEvery {
            secureItemDraftPolicyCoordinator.applyRemoteDeleteAndDiscardLocalChanges(
                logicalItemId = draft.logicalItemId,
                remoteItemId = requireNotNull(draft.remoteItemId),
                deletedAt = draft.updatedAt,
                lastSyncedAt = draft.updatedAt,
            )
        } returns false

        val result = target(draft.logicalItemId)

        assertEquals(
            PublishSecureItemDraftResult.Error(
                PublishSecureItemDraftError.LocalStateUpdateFailed(
                    logicalItemId = draft.logicalItemId,
                    operation = "PUBLISH_DRAFT_UPDATE_NOT_FOUND_RESOLUTION",
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when remote failure happens then keeps draft and returns remote error`() = runBlocking {
        val draft = sampleDraft(draftType = SecureItemDraftType.DELETE)
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRemoteRepository.deleteVaultItem(requireNotNull(draft.remoteItemId)) } returns
            SecureItemRemoteResult.Error(SecureItemRemoteError.Unauthorized)

        val result = target(draft.logicalItemId)

        assertEquals(
            PublishSecureItemDraftResult.Error(
                PublishSecureItemDraftError.RemoteOperationFailed(
                    logicalItemId = draft.logicalItemId,
                    error = SecureItemRemoteError.Unauthorized,
                ),
            ),
            result,
        )
        coVerify(exactly = 0) { secureItemDraftPolicyCoordinator.finalizePublishedDelete(any(), any()) }
    }

    @Test
    fun `invoke when update draft remote failure happens then keeps draft and returns remote error`() = runBlocking {
        val draft = sampleDraft(draftType = SecureItemDraftType.UPDATE)
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRemoteRepository.updateVaultItem(requireNotNull(draft.remoteItemId), any()) } returns
            SecureItemRemoteResult.Error(SecureItemRemoteError.Unauthorized)

        val result = target(draft.logicalItemId)

        assertEquals(
            PublishSecureItemDraftResult.Error(
                PublishSecureItemDraftError.RemoteOperationFailed(
                    logicalItemId = draft.logicalItemId,
                    error = SecureItemRemoteError.Unauthorized,
                ),
            ),
            result,
        )
    }

    @Test
    fun `invoke when delete draft remote item is missing then finalizes local tombstone`() = runBlocking {
        val draft = sampleDraft(draftType = SecureItemDraftType.DELETE)
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRemoteRepository.deleteVaultItem(requireNotNull(draft.remoteItemId)) } returns
            SecureItemRemoteResult.Error(SecureItemRemoteError.ItemNotFound)
        coEvery {
            secureItemDraftPolicyCoordinator.finalizePublishedDelete(
                draft = draft,
                deletedAt = draft.updatedAt,
            )
        } returns true

        val result = target(draft.logicalItemId)

        assertEquals(
            PublishSecureItemDraftResult.Success(
                logicalItemId = draft.logicalItemId,
                draftType = SecureItemDraftType.DELETE,
            ),
            result,
        )
    }

    @Test
    fun `invoke when delete draft finalization fails then returns local failure`() = runBlocking {
        val draft = sampleDraft(draftType = SecureItemDraftType.DELETE)
        val deletedAt = Instant.now()
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemRemoteRepository.deleteVaultItem(requireNotNull(draft.remoteItemId)) } returns
            SecureItemRemoteResult.Success(
                RemoteDeleteSecureItemResult(
                    itemId = requireNotNull(draft.remoteItemId),
                    deletedAt = deletedAt,
                ),
            )
        coEvery {
            secureItemDraftPolicyCoordinator.finalizePublishedDelete(
                draft = draft,
                deletedAt = deletedAt,
            )
        } returns false

        val result = target(draft.logicalItemId)

        assertEquals(
            PublishSecureItemDraftResult.Error(
                PublishSecureItemDraftError.LocalStateUpdateFailed(
                    logicalItemId = draft.logicalItemId,
                    operation = "PUBLISH_DRAFT_DELETE",
                ),
            ),
            result,
        )
    }

    private fun sampleDraft(
        draftType: SecureItemDraftType,
        remoteItemId: UUID? = UUID.randomUUID(),
    ): SecureItemSyncDraft {
        val updatedAt = Instant.now()
        return SecureItemSyncDraft(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = remoteItemId,
            itemType = SecureItemType.NOTE,
            schemaVersion = 1,
            displayHint = "draft",
            payload = byteArrayOf(1, 2, 3),
            payloadVersion = 2,
            createdAt = updatedAt.minusSeconds(3600),
            updatedAt = updatedAt,
            deletedAt = null,
            lastSyncedAt = null,
            lastSyncError = null,
            draftType = draftType,
            basePayloadVersion = 1,
            baseUpdatedAt = updatedAt.minusSeconds(60),
            lastPublishError = null,
        )
    }
}
