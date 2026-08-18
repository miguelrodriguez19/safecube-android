@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.miguelrodriguez19.safecube.feature.vault.presentation.home.viewmodel

import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemDraftSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.VaultSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultDraftSummariesUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultItemSummariesUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultDirtyStateUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.SyncVaultNowUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultHomeContentState
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultHomeLocalReadError
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync.VaultSyncUiErrorCategory
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun `initialization before Room emits then exposes loading instead of empty`() = runTest {
        val itemFlow = MutableSharedFlow<List<VaultItemSummary>>()
        val draftFlow = MutableSharedFlow<List<VaultItemDraftSummary>>()
        stubObservers(itemFlow = itemFlow, draftFlow = draftFlow)

        val target = createTarget()

        runCurrent()

        assertEquals(VaultHomeContentState.InitialLoading, target.uiState.value.contentState)
        assertTrue(target.uiState.value.items.isEmpty())
        assertFalse(target.uiState.value.isEmpty)
    }

    @Test
    fun `initial sync with empty Room emission then exposes empty after sync`() = runTest {
        stubObservers()
        coEvery { syncVaultNowUseCase.invoke() } returns VaultSyncResult.Success(0, 0, 0)
        val target = createTarget()

        target.onVaultScreenShown()
        advanceUntilIdle()

        assertEquals(VaultHomeContentState.Empty, target.uiState.value.contentState)
        assertTrue(target.uiState.value.isEmpty)
        assertFalse(target.uiState.value.hasLocalReadError)
    }

    @Test
    fun `initial sync with empty Room emission then keeps loading until remote content arrives`() = runTest {
        val itemFlow = MutableStateFlow<List<VaultItemSummary>>(emptyList())
        stubObservers(itemFlow = itemFlow)
        val releaseSync = CompletableDeferred<VaultSyncResult>()
        coEvery { syncVaultNowUseCase.invoke() } coAnswers { releaseSync.await() }
        val target = createTarget()

        target.onVaultScreenShown()
        runCurrent()

        assertEquals(VaultHomeContentState.InitialLoading, target.uiState.value.contentState)
        assertFalse(target.uiState.value.isEmpty)

        itemFlow.value = listOf(itemSummary())
        releaseSync.complete(VaultSyncResult.Success(0, 1, 0))
        advanceUntilIdle()

        assertEquals(VaultHomeContentState.Content, target.uiState.value.contentState)
        assertEquals(1, target.uiState.value.items.size)
    }

    @Test
    fun `local summaries with drafts then exposes content and overlays draft data`() = runTest {
        val officialId = UUID.randomUUID()
        val createDraftId = UUID.randomUUID()
        val officialUpdatedAt = Instant.now()
        val createUpdatedAt = officialUpdatedAt.plusSeconds(60)
        val officialDisplayHint = UUID.randomUUID().toString()
        val draftDisplayHint = UUID.randomUUID().toString()
        val createDisplayHint = UUID.randomUUID().toString()
        every { observeVaultItemSummariesUseCase.invoke() } returns flowOf(
            listOf(
                VaultItemSummary(
                    logicalItemId = officialId,
                    itemType = SecureItemType.NOTE,
                    displayHint = officialDisplayHint,
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
                    displayHint = draftDisplayHint,
                    updatedAt = officialUpdatedAt.plusSeconds(30),
                    draftType = SecureItemDraftType.UPDATE,
                    draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
                    lastSyncError = null,
                ),
                VaultItemDraftSummary(
                    logicalItemId = createDraftId,
                    itemType = SecureItemType.PASSWORD,
                    displayHint = createDisplayHint,
                    updatedAt = createUpdatedAt,
                    draftType = SecureItemDraftType.CREATE,
                    draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
                    lastSyncError = UUID.randomUUID().toString(),
                ),
            ),
        )
        stubSyncObservers()

        val target = createTarget()

        advanceUntilIdle()

        assertEquals(VaultHomeContentState.Content, target.uiState.value.contentState)
        assertEquals(2, target.uiState.value.items.size)
        val overlayItem = target.uiState.value.items.single { it.logicalItemId == officialId }
        assertEquals(draftDisplayHint, overlayItem.displayHint)
        assertTrue(overlayItem.hasDraft)
        assertTrue(overlayItem.isDraftPendingSync)
        val createDraftItem = target.uiState.value.items.single { it.logicalItemId == createDraftId }
        assertEquals(SecureItemDraftType.CREATE, createDraftItem.draftType)
        assertTrue(createDraftItem.isDraftConflict)
    }

    @Test
    fun `local read failure after content then keeps items and exposes storage error`() = runTest {
        val item = itemSummary()
        stubObservers(
            itemFlow = flow {
                emit(listOf(item))
                throw IllegalStateException()
            },
        )

        val target = createTarget()

        advanceUntilIdle()

        assertEquals(VaultHomeContentState.Error, target.uiState.value.contentState)
        assertEquals(VaultHomeLocalReadError.StorageOrCrypto, target.uiState.value.localReadError)
        assertEquals(listOf(item.logicalItemId), target.uiState.value.items.map { it.logicalItemId })
        assertFalse(target.uiState.value.isEmpty)
    }

    @Test
    fun `retryable sync failure then keeps local content and exposes retry`() = runTest {
        val item = itemSummary()
        stubObservers(itemFlow = flowOf(listOf(item)))
        coEvery { syncVaultNowUseCase.invoke() } returns VaultSyncResult.Error(
            reason = VaultSyncError.PushFailed(
                com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError.RemoteFailure(
                    logicalItemId = UUID.randomUUID(),
                    operation = UUID.randomUUID().toString(),
                    failure = NetworkFailureClassifier.fromHttpStatus(503),
                ),
            ),
            downloadedCount = 2,
            conflictCount = 1,
        )
        val target = createTarget()

        advanceUntilIdle()
        target.syncNow()
        advanceUntilIdle()

        assertEquals(VaultHomeContentState.Content, target.uiState.value.contentState)
        assertEquals(listOf(item.logicalItemId), target.uiState.value.items.map { it.logicalItemId })
        assertEquals(VaultSyncUiErrorCategory.ServiceUnavailable, target.uiState.value.syncErrorCategory)
        assertTrue(target.uiState.value.isSyncRetryable)
        assertEquals(2, (target.uiState.value.lastSyncResult as VaultSyncResult.Error).downloadedCount)

        target.syncNow()
        advanceUntilIdle()

        coVerify(exactly = 2) { syncVaultNowUseCase.invoke() }
    }

    @Test
    fun `sync failures then expose sanitized categories and retry only when retryable`() = runTest {
        stubObservers()
        val outcomes = listOf(
            VaultSyncResult.Error(
                reason = VaultSyncError.PushFailed(
                    remoteFailure(NetworkFailureClassifier.fromThrowable(IOException())),
                ),
            ) to VaultSyncUiErrorCategory.OfflineOrTimeout,
            VaultSyncResult.Error(
                reason = VaultSyncError.PushFailed(
                    remoteFailure(NetworkFailureClassifier.fromHttpStatus(408)),
                ),
            ) to VaultSyncUiErrorCategory.OfflineOrTimeout,
            VaultSyncResult.Error(
                reason = VaultSyncError.PushFailed(
                    remoteFailure(NetworkFailureClassifier.fromHttpStatus(503)),
                ),
            ) to VaultSyncUiErrorCategory.ServiceUnavailable,
            VaultSyncResult.Error(
                reason = VaultSyncError.InvalidVaultState(VaultState.Locked),
            ) to VaultSyncUiErrorCategory.SessionRequired,
            VaultSyncResult.Error(
                reason = VaultSyncError.PushFailed(
                    remoteFailure(NetworkFailureClassifier.fromHttpStatus(409)),
                ),
            ) to VaultSyncUiErrorCategory.Conflict,
            VaultSyncResult.Error(
                reason = VaultSyncError.PushFailed(
                    com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError.ProtocolIntegrityFailed(
                        logicalItemId = UUID.randomUUID(),
                        operation = UUID.randomUUID().toString(),
                    ),
                ),
            ) to VaultSyncUiErrorCategory.ProtocolIntegrity,
            VaultSyncResult.Error(
                reason = VaultSyncError.PushFailed(
                    com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError.LocalStateUpdateFailed(
                        logicalItemId = UUID.randomUUID(),
                        operation = UUID.randomUUID().toString(),
                    ),
                ),
            ) to VaultSyncUiErrorCategory.StorageOrCrypto,
        )
        coEvery { syncVaultNowUseCase.invoke() } returnsMany outcomes.map { it.first }
        val target = createTarget()

        advanceUntilIdle()
        outcomes.forEach { (expectedResult, expectedCategory) ->
            target.syncNow()
            advanceUntilIdle()

            assertEquals(expectedResult.reason, target.uiState.value.lastSyncError)
            assertEquals(expectedCategory, target.uiState.value.syncErrorCategory)
            assertEquals(expectedResult.retryDecision == com.miguelrodriguez19.safecube.core.network.domain.model.RetryDecision.Retryable, target.uiState.value.isSyncRetryable)
        }
    }

    @Test
    fun `two sync requests while active then execute only one`() = runTest {
        stubObservers()
        val release = CompletableDeferred<VaultSyncResult>()
        coEvery { syncVaultNowUseCase.invoke() } coAnswers { release.await() }
        val target = createTarget()

        target.syncNow()
        target.syncNow()
        runCurrent()

        assertTrue(target.uiState.value.isSyncing)
        coVerify(exactly = 1) { syncVaultNowUseCase.invoke() }

        release.complete(VaultSyncResult.Success(0, 0, 0))
        advanceUntilIdle()

        assertFalse(target.uiState.value.isSyncing)
    }

    @Test
    fun `screen shown more than once then triggers only one initial sync`() = runTest {
        stubObservers()
        coEvery { syncVaultNowUseCase.invoke() } returns VaultSyncResult.Success(0, 0, 0)
        val target = createTarget()

        target.onVaultScreenShown()
        target.onVaultScreenHidden()
        target.onVaultScreenShown()
        advanceUntilIdle()

        coVerify(exactly = 1) { syncVaultNowUseCase.invoke() }
    }

    @Test
    fun `new home entry after logout then triggers a new initial sync`() = runTest {
        stubObservers()
        coEvery { syncVaultNowUseCase.invoke() } returns VaultSyncResult.Success(0, 0, 0)
        val firstTarget = createTarget()

        firstTarget.onVaultScreenShown()
        advanceUntilIdle()
        firstTarget.onVaultScreenHidden()
        val secondTarget = createTarget()
        secondTarget.onVaultScreenShown()
        advanceUntilIdle()

        coVerify(exactly = 2) { syncVaultNowUseCase.invoke() }
    }

    private fun createTarget(): VaultHomeViewModel = VaultHomeViewModel(
        observeVaultItemSummariesUseCase = observeVaultItemSummariesUseCase,
        observeVaultDraftSummariesUseCase = observeVaultDraftSummariesUseCase,
        observeVaultDirtyStateUseCase = observeVaultDirtyStateUseCase,
        observeVaultSyncingUseCase = observeVaultSyncingUseCase,
        syncVaultNowUseCase = syncVaultNowUseCase,
    )

    private fun stubObservers(
        itemFlow: Flow<List<VaultItemSummary>> = flowOf(emptyList()),
        draftFlow: Flow<List<VaultItemDraftSummary>> = flowOf(emptyList()),
    ) {
        every { observeVaultItemSummariesUseCase.invoke() } returns itemFlow
        every { observeVaultDraftSummariesUseCase.invoke() } returns draftFlow
        stubSyncObservers()
    }

    private fun stubSyncObservers() {
        every { observeVaultDirtyStateUseCase.invoke() } returns flowOf(false)
        every { observeVaultSyncingUseCase.invoke() } returns MutableStateFlow(false)
    }

    private fun itemSummary(): VaultItemSummary = VaultItemSummary(
        logicalItemId = UUID.randomUUID(),
        itemType = SecureItemType.NOTE,
        displayHint = UUID.randomUUID().toString(),
        updatedAt = Instant.now(),
        syncState = SecureItemSyncState.SYNCED,
        lastSyncError = null,
    )

    private fun remoteFailure(
        failure: com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailure,
    ) = com.miguelrodriguez19.safecube.core.vault.domain.model.sync.push.PushLocalVaultChangesError.RemoteFailure(
        logicalItemId = UUID.randomUUID(),
        operation = UUID.randomUUID().toString(),
        failure = failure,
    )
}
