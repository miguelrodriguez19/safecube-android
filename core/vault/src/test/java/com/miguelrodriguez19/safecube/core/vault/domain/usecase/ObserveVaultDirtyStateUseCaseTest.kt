package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultDirtyStateUseCase
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveVaultDirtyStateUseCaseTest {
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()
    private val itemsFlow = MutableStateFlow<List<SecureItem>>(emptyList())
    private val draftsFlow = MutableStateFlow<List<SecureItemSyncDraft>>(emptyList())

    private val target = ObserveVaultDirtyStateUseCase(
        secureItemRepository = secureItemRepository,
        secureItemDraftRepository = secureItemDraftRepository,
    )

    @Test
    fun `invoke when all items are synced and no drafts then emits false`() = runBlocking {
        every { secureItemRepository.observeActiveItems() } returns itemsFlow
        every { secureItemDraftRepository.observeDrafts() } returns draftsFlow
        itemsFlow.value = listOf(sampleItem(syncState = SecureItemSyncState.SYNCED))

        val result = target().first()

        assertEquals(false, result)
    }

    @Test
    fun `invoke when local item becomes pending then emits false then true`() = runBlocking {
        every { secureItemRepository.observeActiveItems() } returns itemsFlow
        every { secureItemDraftRepository.observeDrafts() } returns draftsFlow
        itemsFlow.value = listOf(sampleItem(syncState = SecureItemSyncState.SYNCED))

        val initialResult = target().first()

        itemsFlow.value = listOf(sampleItem(syncState = SecureItemSyncState.PENDING_UPDATE))
        val pendingResult = target().first()

        assertEquals(false, initialResult)
        assertEquals(true, pendingResult)
    }

    @Test
    fun `invoke when draft exists then emits true even with synced items`() = runBlocking {
        every { secureItemRepository.observeActiveItems() } returns itemsFlow
        every { secureItemDraftRepository.observeDrafts() } returns draftsFlow
        itemsFlow.value = listOf(sampleItem(syncState = SecureItemSyncState.SYNCED))
        draftsFlow.value = listOf(sampleDraft())

        val result = target().first()

        assertEquals(true, result)
    }
}

private fun sampleItem(
    syncState: SecureItemSyncState,
): SecureItem {
    val now = Instant.parse("2026-05-11T09:00:00Z")
    return SecureItem(
        logicalItemId = UUID.randomUUID(),
        remoteItemId = UUID.randomUUID(),
        itemType = SecureItemType.NOTE,
        schemaVersion = 1,
        displayHint = "Server key",
        payload = byteArrayOf(1, 2, 3),
        payloadVersion = 1,
        createdAt = now,
        updatedAt = now,
        syncState = syncState,
    )
}

private fun sampleDraft(): SecureItemSyncDraft {
    val now = Instant.parse("2026-05-11T09:00:00Z")
    return SecureItemSyncDraft(
        logicalItemId = UUID.randomUUID(),
        remoteItemId = UUID.randomUUID(),
        itemType = SecureItemType.NOTE,
        schemaVersion = 1,
        displayHint = "Server key",
        payload = byteArrayOf(1, 2, 3),
        payloadVersion = 2,
        createdAt = now,
        updatedAt = now,
        lastSyncedAt = now,
        draftType = SecureItemDraftType.UPDATE,
        basePayloadVersion = 1,
        baseUpdatedAt = now,
    )
}
