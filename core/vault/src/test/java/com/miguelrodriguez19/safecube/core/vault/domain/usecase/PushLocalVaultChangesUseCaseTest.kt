package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.RemoteSecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteCreateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRemoteRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftSyncCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.push.PushLocalVaultChangesUseCase
import com.miguelrodriguez19.safecube.core.vault.test.testSecureItemDraft
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PushLocalVaultChangesUseCaseTest {
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()
    private val secureItemRemoteRepository = mockk<SecureItemRemoteRepository>()
    private val secureItemDraftSyncCoordinator = mockk<SecureItemDraftSyncCoordinator>()

    private val target = PushLocalVaultChangesUseCase(
        secureItemDraftRepository = secureItemDraftRepository,
        secureItemRemoteRepository = secureItemRemoteRepository,
        secureItemDraftSyncCoordinator = secureItemDraftSyncCoordinator,
    )

    @Test
    fun `invoke when create draft is uploaded then officializes and counts synced`() = runBlocking {
        val draft = testSecureItemDraft(remoteItemId = null, draftType = com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType.CREATE)
        val remoteItemId = UUID.randomUUID()
        val createdAt = Instant.parse("2024-03-01T00:00:00Z")
        val remoteResult = RemoteCreateSecureItemResult(
            itemId = remoteItemId,
            mutationId = draft.mutationId,
            payloadVersion = draft.payloadVersion,
            itemRevision = 1,
            changeSequence = 10,
            updatedAt = createdAt,
        )
        coEvery { secureItemDraftRepository.getSyncableDraftsOrdered() } returns listOf(draft)
        coEvery { secureItemRemoteRepository.createVaultItem(any()) } returns SecureItemRemoteResult.Success(
            remoteResult,
        )
        coEvery {
            secureItemDraftSyncCoordinator.officializeCreatedDraft(draft, remoteResult)
        } returns true

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 1,
                conflictCount = 0,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
    }

    @Test
    fun `invoke when update conflicts then keeps draft as conflict and counts it`() = runBlocking {
        val remoteItemId = UUID.randomUUID()
        val draft = testSecureItemDraft(remoteItemId = remoteItemId)
        val remoteOfficial = RemoteSecureItem(
            itemId = remoteItemId,
            itemType = draft.itemType.wireName,
            schemaVersion = 1,
            displayHint = "Remote official",
            payload = byteArrayOf(9, 9, 9),
            payloadVersion = 3,
            itemRevision = 6,
            changeSequence = 11,
            updatedAt = Instant.parse("2024-03-01T01:00:00Z"),
            deletedAt = null,
        )
        coEvery { secureItemDraftRepository.getSyncableDraftsOrdered() } returns listOf(draft)
        coEvery { secureItemRemoteRepository.updateVaultItem(remoteItemId, any()) } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.PreconditionFailed,
        )
        coEvery { secureItemRemoteRepository.getVaultItem(remoteItemId) } returns SecureItemRemoteResult.Success(remoteOfficial)
        coEvery {
            secureItemDraftSyncCoordinator.replaceOfficialWithRemoteAndConflictedDraft(
                draft = draft,
                remoteItem = any(),
                lastSyncedAt = remoteOfficial.updatedAt,
                lastSyncError = "Update conflicted with backend state.",
            )
        } returns true

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Success(
                processedCount = 1,
                syncedCount = 0,
                conflictCount = 1,
                keptPendingCount = 0,
                locallyResolvedDeleteCount = 0,
            ),
            result,
        )
    }

    @Test
    fun `invoke when create is rejected as invalid then keeps draft and stops push`() = runBlocking {
        val draft = testSecureItemDraft(
            remoteItemId = null,
            draftType = com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType.CREATE,
        )
        coEvery { secureItemDraftRepository.getSyncableDraftsOrdered() } returns listOf(draft)
        coEvery {
            secureItemRemoteRepository.createVaultItem(any())
        } returns SecureItemRemoteResult.Error(
            SecureItemRemoteError.ValidationFailed(
                fields = mapOf("payloadVersion" to "must be positive"),
            ),
        )

        val result = target()

        assertEquals(
            PushLocalVaultChangesResult.Error(
                PushLocalVaultChangesError.ProtocolIntegrityFailed(
                    logicalItemId = draft.logicalItemId,
                    operation = "CREATE_VALIDATION",
                ),
            ),
            result,
        )
        coVerify(exactly = 0) {
            secureItemDraftSyncCoordinator.officializeCreatedDraft(any(), any())
        }
    }
}
