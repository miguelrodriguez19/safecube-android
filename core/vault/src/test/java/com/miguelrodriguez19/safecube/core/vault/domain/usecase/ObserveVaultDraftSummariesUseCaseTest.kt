package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultDraftSummariesUseCase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveVaultDraftSummariesUseCaseTest {

    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()

    private val target = ObserveVaultDraftSummariesUseCase(
        secureItemDraftRepository = secureItemDraftRepository,
    )

    @Test
    fun `invoke when repository emits drafts then maps them to draft summaries`() = runBlocking {
        val firstId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val secondId = UUID.fromString("22222222-2222-2222-2222-222222222222")
        every { secureItemDraftRepository.observeDrafts() } returns flowOf(
            listOf(
                sampleDraft(
                    logicalItemId = firstId,
                    draftType = SecureItemDraftType.UPDATE,
                    lastPublishError = "Update rejected",
                ),
                sampleDraft(
                    logicalItemId = secondId,
                    draftType = SecureItemDraftType.DELETE,
                    lastPublishError = null,
                ),
            ),
        )

        val result = target().first()

        assertEquals(2, result.size)
        assertEquals(firstId, result[0].logicalItemId)
        assertEquals(SecureItemDraftType.UPDATE, result[0].draftType)
        assertEquals("Update rejected", result[0].lastPublishError)
        assertEquals(secondId, result[1].logicalItemId)
        assertEquals(SecureItemDraftType.DELETE, result[1].draftType)
        assertEquals(null, result[1].lastPublishError)
        verify(exactly = 1) { secureItemDraftRepository.observeDrafts() }
        confirmVerified(secureItemDraftRepository)
    }
}

private fun sampleDraft(
    logicalItemId: UUID,
    draftType: SecureItemDraftType,
    lastPublishError: String?,
): SecureItemSyncDraft = SecureItemSyncDraft(
    logicalItemId = logicalItemId,
    itemType = SecureItemType.NOTE,
    schemaVersion = 1,
    displayHint = "Draft",
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 2,
    createdAt = Instant.parse("2026-04-01T08:00:00Z"),
    updatedAt = Instant.parse("2026-04-01T09:00:00Z"),
    draftType = draftType,
    basePayloadVersion = 1,
    baseUpdatedAt = Instant.parse("2026-04-01T07:00:00Z"),
    lastPublishError = lastPublishError,
)
