package com.miguelrodriguez19.safecube.core.vault.domain.usecase.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.DiscardSecureItemDraftUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftSyncCoordinator
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class DiscardSecureItemDraftUseCaseTest {
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()
    private val secureItemDraftSyncCoordinator = mockk<SecureItemDraftSyncCoordinator>()

    private val target = DiscardSecureItemDraftUseCase(
        secureItemDraftRepository = secureItemDraftRepository,
        secureItemDraftSyncCoordinator = secureItemDraftSyncCoordinator,
    )

    @Test
    fun `invoke when draft exists and discard succeeds then removes draft without remote call`() = runBlocking {
        val draft = sampleDraft()
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemDraftSyncCoordinator.discardDraft(draft.logicalItemId) } returns true

        val result = target(draft.logicalItemId)

        assertEquals(
            DiscardSecureItemDraftResult.Success(draft.logicalItemId),
            result,
        )
        coVerify(exactly = 1) { secureItemDraftSyncCoordinator.discardDraft(draft.logicalItemId) }
    }

    @Test
    fun `invoke when draft does not exist then returns not found`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        coEvery { secureItemDraftRepository.getDraft(logicalItemId) } returns null

        val result = target(logicalItemId)

        assertEquals(
            DiscardSecureItemDraftResult.Error(
                DiscardSecureItemDraftError.DraftNotFound(logicalItemId),
            ),
            result,
        )
    }

    @Test
    fun `invoke when discard cannot update local state then returns local failure`() = runBlocking {
        val draft = sampleDraft()
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemDraftSyncCoordinator.discardDraft(draft.logicalItemId) } returns false

        val result = target(draft.logicalItemId)

        assertEquals(
            DiscardSecureItemDraftResult.Error(
                DiscardSecureItemDraftError.LocalStateUpdateFailed(
                    logicalItemId = draft.logicalItemId,
                    operation = "DISCARD_DRAFT",
                ),
            ),
            result,
        )
    }

    private fun sampleDraft(): SecureItemSyncDraft {
        val updatedAt = Instant.now()
        return SecureItemSyncDraft(
            logicalItemId = UUID.randomUUID(),
            remoteItemId = UUID.randomUUID(),
            itemType = SecureItemType.NOTE,
            schemaVersion = 1,
            displayHint = "draft",
            payload = byteArrayOf(1, 2, 3),
            payloadVersion = 2,
            createdAt = updatedAt.minusSeconds(3600),
            updatedAt = updatedAt,
            deletedAt = null,
            lastSyncedAt = null,
            draftType = SecureItemDraftType.UPDATE,
            draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
            baseItemRevision = 1,
            lastSyncError = null,
        )
    }
}
