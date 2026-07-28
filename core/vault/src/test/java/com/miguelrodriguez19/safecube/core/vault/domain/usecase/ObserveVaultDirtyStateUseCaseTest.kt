package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultDirtyStateUseCase
import com.miguelrodriguez19.safecube.core.vault.test.testSecureItemDraft
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveVaultDirtyStateUseCaseTest {
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()

    @Test
    fun `invoke when all drafts are conflicts then emits false`() = runBlocking {
        every { secureItemDraftRepository.observeDrafts() } returns flowOf(
            listOf(
                testSecureItemDraft(
                    draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
                    lastSyncError = "Conflict",
                ),
            ),
        )

        val result = ObserveVaultDirtyStateUseCase(secureItemDraftRepository).invoke().first()

        assertFalse(result)
    }

    @Test
    fun `invoke when at least one draft is ready then emits true`() = runBlocking {
        every { secureItemDraftRepository.observeDrafts() } returns flowOf(
            listOf(
                testSecureItemDraft(draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT, lastSyncError = "Conflict"),
                testSecureItemDraft(draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC),
            ),
        )

        val result = ObserveVaultDirtyStateUseCase(secureItemDraftRepository).invoke().first()

        assertTrue(result)
    }
}
