package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultDraftSummariesUseCase
import com.miguelrodriguez19.safecube.core.vault.test.testSecureItemDraft
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveVaultDraftSummariesUseCaseTest {
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()

    @Test
    fun `invoke maps draft sync metadata into summaries`() = runBlocking {
        val draft = testSecureItemDraft(lastSyncError = "Conflict")
        every { secureItemDraftRepository.observeDrafts() } returns flowOf(listOf(draft))

        val result = ObserveVaultDraftSummariesUseCase(secureItemDraftRepository).invoke().first()

        assertEquals(1, result.size)
        assertEquals(draft.logicalItemId, result.single().logicalItemId)
        assertEquals(draft.draftType, result.single().draftType)
        assertEquals(draft.draftSyncStatus, result.single().draftSyncStatus)
        assertEquals(draft.lastSyncError, result.single().lastSyncError)
    }
}
