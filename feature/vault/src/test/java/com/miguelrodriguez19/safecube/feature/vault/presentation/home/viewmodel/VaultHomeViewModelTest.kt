package com.miguelrodriguez19.safecube.feature.vault.presentation.home.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemDraftSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultDraftSummariesUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultItemSummariesUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultDirtyStateUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.SyncVaultNowUseCase
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class VaultHomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeVaultItemSummariesUseCase = mockk<ObserveVaultItemSummariesUseCase>()
    private val observeVaultDraftSummariesUseCase = mockk<ObserveVaultDraftSummariesUseCase>()
    private val observeVaultDirtyStateUseCase = mockk<ObserveVaultDirtyStateUseCase>()
    private val observeVaultSyncingUseCase = mockk<ObserveVaultSyncingUseCase>()
    private val syncVaultNowUseCase = mockk<SyncVaultNowUseCase>()

    @Test
    fun `init overlays official items with matching drafts and appends create drafts`() = runTest {
        val officialId = UUID.randomUUID()
        val createDraftId = UUID.randomUUID()
        val officialUpdatedAt = Instant.parse("2024-06-01T00:00:00Z")
        val createUpdatedAt = officialUpdatedAt.plusSeconds(60)
        every { observeVaultItemSummariesUseCase.invoke() } returns flowOf(
            listOf(
                VaultItemSummary(
                    logicalItemId = officialId,
                    itemType = SecureItemType.NOTE,
                    displayHint = "Official",
                    updatedAt = officialUpdatedAt,
                    syncState = SecureItemSyncState.SYNCED,
                    lastSyncError = null,
                ),
            ),
        )
        every { observeVaultDraftSummariesUseCase.invoke() } returns flowOf(
            listOf(
                VaultItemDraftSummary(
                    logicalItemId = officialId,
                    itemType = SecureItemType.NOTE,
                    displayHint = "Draft overlay",
                    updatedAt = officialUpdatedAt.plusSeconds(30),
                    draftType = SecureItemDraftType.UPDATE,
                    draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
                    lastSyncError = null,
                ),
                VaultItemDraftSummary(
                    logicalItemId = createDraftId,
                    itemType = SecureItemType.PASSWORD,
                    displayHint = "New local item",
                    updatedAt = createUpdatedAt,
                    draftType = SecureItemDraftType.CREATE,
                    draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
                    lastSyncError = "Conflict",
                ),
            ),
        )
        every { observeVaultDirtyStateUseCase.invoke() } returns flowOf(false)
        every { observeVaultSyncingUseCase.invoke() } returns MutableStateFlow(false)
        coEvery { syncVaultNowUseCase.invoke() } throws AssertionError("sync should not run in this test")

        val target = VaultHomeViewModel(
            observeVaultItemSummariesUseCase = observeVaultItemSummariesUseCase,
            observeVaultDraftSummariesUseCase = observeVaultDraftSummariesUseCase,
            observeVaultDirtyStateUseCase = observeVaultDirtyStateUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
            syncVaultNowUseCase = syncVaultNowUseCase,
        )

        advanceUntilIdle()

        assertEquals(2, target.uiState.value.items.size)
        val overlayItem = target.uiState.value.items.single { it.logicalItemId == officialId }
        assertEquals("Draft overlay", overlayItem.displayHint)
        assertTrue(overlayItem.hasDraft)
        assertTrue(overlayItem.isDraftPendingSync)

        val createDraftItem = target.uiState.value.items.single { it.logicalItemId == createDraftId }
        assertEquals(SecureItemDraftType.CREATE, createDraftItem.draftType)
        assertTrue(createDraftItem.isDraftConflict)
        assertEquals("Conflict", createDraftItem.lastDraftError)
    }
}
