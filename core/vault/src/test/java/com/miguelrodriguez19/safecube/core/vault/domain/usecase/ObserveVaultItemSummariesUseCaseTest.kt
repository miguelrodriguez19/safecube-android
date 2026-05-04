package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultItemSummariesUseCase
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

class ObserveVaultItemSummariesUseCaseTest {

    private val secureItemRepository = mockk<SecureItemRepository>()

    private val target = ObserveVaultItemSummariesUseCase(
        secureItemRepository = secureItemRepository,
    )

    @Test
    fun `invoke when repository emits active items then maps them to summaries`() = runBlocking {
        every { secureItemRepository.observeActiveItems() } returns flowOf(
            listOf(
                sampleSecureItem(
                    logicalItemId = UUID.fromString("11111111-1111-1111-1111-111111111111"),
                    itemType = SecureItemType.PASSWORD,
                    displayHint = "Github",
                    updatedAt = Instant.parse("2026-03-27T10:00:00Z"),
                    syncState = SecureItemSyncState.PENDING_UPDATE,
                    lastSyncError = null,
                ),
                sampleSecureItem(
                    logicalItemId = UUID.fromString("22222222-2222-2222-2222-222222222222"),
                    itemType = SecureItemType.NOTE,
                    displayHint = "WiFi",
                    updatedAt = Instant.parse("2026-03-27T09:00:00Z"),
                    syncState = SecureItemSyncState.CONFLICT,
                    lastSyncError = "Conflict detected",
                ),
            ),
        )

        val result = target().first()

        assertEquals(2, result.size)
        assertEquals("Github", result[0].displayHint)
        assertEquals(SecureItemType.PASSWORD, result[0].itemType)
        assertEquals(Instant.parse("2026-03-27T10:00:00Z"), result[0].updatedAt)
        assertEquals(SecureItemSyncState.PENDING_UPDATE, result[0].syncState)
        assertEquals("WiFi", result[1].displayHint)
        assertEquals(SecureItemType.NOTE, result[1].itemType)
        assertEquals(SecureItemSyncState.CONFLICT, result[1].syncState)
        assertEquals("Conflict detected", result[1].lastSyncError)
        verify(exactly = 1) { secureItemRepository.observeActiveItems() }
        confirmVerified(secureItemRepository)
    }
}

private fun sampleSecureItem(
    logicalItemId: UUID,
    itemType: SecureItemType,
    displayHint: String,
    updatedAt: Instant,
    syncState: SecureItemSyncState,
    lastSyncError: String?,
): SecureItem = SecureItem(
    logicalItemId = logicalItemId,
    itemType = itemType,
    schemaVersion = 1,
    displayHint = displayHint,
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 1,
    createdAt = Instant.parse("2026-03-27T08:00:00Z"),
    updatedAt = updatedAt,
    syncState = syncState,
    lastSyncError = lastSyncError,
)
