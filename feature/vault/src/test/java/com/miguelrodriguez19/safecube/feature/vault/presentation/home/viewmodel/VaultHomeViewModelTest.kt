package com.miguelrodriguez19.safecube.feature.vault.presentation.home.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultItemSummariesUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.SyncVaultNowUseCase
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.Instant
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class VaultHomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeVaultItemSummariesUseCase = mockk<ObserveVaultItemSummariesUseCase>()
    private val observeVaultSyncingUseCase = mockk<ObserveVaultSyncingUseCase>()
    private val syncVaultNowUseCase = mockk<SyncVaultNowUseCase>()
    private val summariesFlow = MutableStateFlow<List<VaultItemSummary>>(emptyList())
    private val isSyncingFlow = MutableStateFlow(false)

    private fun buildTarget(): VaultHomeViewModel = VaultHomeViewModel(
        observeVaultItemSummariesUseCase = observeVaultItemSummariesUseCase,
        observeVaultSyncingUseCase = observeVaultSyncingUseCase,
        syncVaultNowUseCase = syncVaultNowUseCase,
    )

    @Test
    fun `init when summaries flow emits then exposes local vault items`() = runTest {
        every { observeVaultItemSummariesUseCase.invoke() } returns summariesFlow
        every { observeVaultSyncingUseCase.invoke() } returns isSyncingFlow
        val updatedAt = Instant.parse("2026-04-10T10:15:30Z")
        val target = buildTarget()

        summariesFlow.value = listOf(
            VaultItemSummary(
                logicalItemId = UUID.randomUUID(),
                itemType = SecureItemType.PASSWORD,
                displayHint = "Github",
                updatedAt = updatedAt,
                syncState = SecureItemSyncState.PENDING_UPDATE,
                lastSyncError = null,
            ),
        )

        advanceUntilIdle()

        assertEquals(1, target.uiState.value.items.size)
        assertEquals("Github", target.uiState.value.items.first().displayHint)
        assertEquals(SecureItemType.PASSWORD, target.uiState.value.items.first().itemType)
        assertEquals(true, target.uiState.value.items.first().isPendingSync)
        verify(exactly = 1) { observeVaultItemSummariesUseCase.invoke() }
        verify(exactly = 1) { observeVaultSyncingUseCase.invoke() }
        confirmVerified(observeVaultItemSummariesUseCase, observeVaultSyncingUseCase, syncVaultNowUseCase)
    }

    @Test
    fun `syncNow when called and not syncing then stores last sync result`() = runTest {
        every { observeVaultItemSummariesUseCase.invoke() } returns summariesFlow
        every { observeVaultSyncingUseCase.invoke() } returns isSyncingFlow
        coEvery { syncVaultNowUseCase.invoke() } returns VaultSyncResult.Success(
            uploadedCount = 2,
            downloadedCount = 1,
            conflictCount = 0,
        )
        val target = buildTarget()

        target.syncNow()
        advanceUntilIdle()

        assertEquals(false, target.uiState.value.isSyncing)
        assertEquals(
            VaultSyncResult.Success(
                uploadedCount = 2,
                downloadedCount = 1,
                conflictCount = 0,
            ),
            target.uiState.value.lastSyncResult,
        )
        coVerify(exactly = 1) { syncVaultNowUseCase.invoke() }
        verify(exactly = 1) { observeVaultItemSummariesUseCase.invoke() }
        verify(exactly = 1) { observeVaultSyncingUseCase.invoke() }
        confirmVerified(observeVaultItemSummariesUseCase, observeVaultSyncingUseCase, syncVaultNowUseCase)
    }
}
