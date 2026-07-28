package com.miguelrodriguez19.safecube.core.vault.domain.usecase.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.PrepareSecureItemDraftForSyncUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.SecureItemDraftSyncCoordinator
import com.miguelrodriguez19.safecube.core.vault.test.testSecureItemDraft
import io.mockk.coEvery
import io.mockk.mockk
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class PrepareSecureItemDraftForSyncUseCaseTest {
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()
    private val secureItemDraftSyncCoordinator = mockk<SecureItemDraftSyncCoordinator>()

    private val target = PrepareSecureItemDraftForSyncUseCase(
        secureItemDraftRepository = secureItemDraftRepository,
        secureItemDraftSyncCoordinator = secureItemDraftSyncCoordinator,
    )

    @Test
    fun `invoke when draft is create then returns not publishable`() = runBlocking {
        val draft = testSecureItemDraft(
            remoteItemId = null,
            draftType = SecureItemDraftType.CREATE,
            draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
        )
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft

        val result = target(draft.logicalItemId)

        assertEquals(
            PrepareSecureItemDraftForSyncResult.Error(
                PrepareSecureItemDraftForSyncError.DraftNotPublishable(draft.logicalItemId, SecureItemDraftType.CREATE),
            ),
            result,
        )
    }

    @Test
    fun `invoke when conflict update draft can be prepared then returns success`() = runBlocking {
        val draft = testSecureItemDraft(
            draftType = SecureItemDraftType.UPDATE,
            draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
            lastSyncError = "Conflict",
        )
        coEvery { secureItemDraftRepository.getDraft(draft.logicalItemId) } returns draft
        coEvery { secureItemDraftSyncCoordinator.prepareDraftForSync(draft.logicalItemId) } returns true

        val result = target(draft.logicalItemId)

        assertEquals(
            PrepareSecureItemDraftForSyncResult.Success(
                logicalItemId = draft.logicalItemId,
                draftType = SecureItemDraftType.UPDATE,
            ),
            result,
        )
    }
}
