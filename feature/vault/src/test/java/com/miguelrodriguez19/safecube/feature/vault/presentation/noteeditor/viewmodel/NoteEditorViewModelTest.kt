@file:OptIn(ExperimentalCoroutinesApi::class)

package com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDraftDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncResult
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.action.NoteEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.event.NoteEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.lifecycle.SecureItemEditorLifecycleCoordinator
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.contract.SecureItemEditorMutationOperations
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.model.SecureItemEditorMutationRequest
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.observation.SecureItemEditorObservationCoordinator
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.observation.model.SecureItemEditorObservationResult
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.state.SecureItemEditorState
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class NoteEditorViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observationCoordinator = mockk<SecureItemEditorObservationCoordinator>()
    private val mutationOperations = mockk<SecureItemEditorMutationOperations>()
    private val lifecycleCoordinator = mockk<SecureItemEditorLifecycleCoordinator>()

    private val syncing = MutableStateFlow(false)
    private val vaultLockEvents = MutableSharedFlow<Unit>()
    private var vaultLocked = false

    init {
        every { lifecycleCoordinator.observeSyncing() } returns syncing
        every { lifecycleCoordinator.observeVaultLocked() } returns vaultLockEvents
        every { lifecycleCoordinator.isVaultLocked() } answers { vaultLocked }
    }

    private fun target(): NoteEditorViewModel = NoteEditorViewModel(
        observationCoordinator = observationCoordinator,
        mutationOperations = mutationOperations,
        lifecycleCoordinator = lifecycleCoordinator,
    )

    @Test
    fun `load when only draft exists then renders draft content and clears not found error`() =
        runTest {
            val logicalItemId = UUID.randomUUID()
            val draftDetail = noteDraftDetail(logicalItemId)
            every { observationCoordinator.observe(logicalItemId) } returns flowOf(
                SecureItemEditorObservationResult.Content(
                    officialDetail = null,
                    draftDetail = draftDetail,
                ),
            )

            val target = target()

            target.load(logicalItemId.toString())
            advanceUntilIdle()

            assertEquals(draftDetail.displayHint, target.uiState.value.displayHint)
            assertEquals(
                (draftDetail.content as NoteSecureItemContent).body,
                target.uiState.value.body
            )
            assertEquals(draftDetail.draftType, target.uiState.value.draftType)
            assertNull(target.uiState.value.errorMessage)
        }

    @Test
    fun `publish draft when conflict draft is loaded then prepares it and navigates back`() =
        runTest {
            val logicalItemId = UUID.randomUUID()
            val displayHint = "Draft note"
            val body = "Draft body"
            every { observationCoordinator.observe(logicalItemId) } returns flowOf(
                SecureItemEditorObservationResult.Content(
                    officialDetail = officialDetail(logicalItemId),
                    draftDetail = noteDraftDetail(
                        logicalItemId = logicalItemId,
                        draftType = SecureItemDraftType.UPDATE,
                        draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
                        displayHint = displayHint,
                        body = body,
                        lastSyncError = "Conflict",
                    ),
                ),
            )
            coEvery { mutationOperations.publish(logicalItemId) } returns
                    PrepareSecureItemDraftForSyncResult.Success(
                        logicalItemId = logicalItemId,
                        draftType = SecureItemDraftType.UPDATE,
                    )

            val target = target()

            target.load(logicalItemId.toString())
            advanceUntilIdle()

            assertEquals(displayHint, target.uiState.value.displayHint)
            assertEquals(body, target.uiState.value.body)
            val event = async { target.events.first() }

            target.onAction(NoteEditorUiAction.PublishDraftClicked)
            advanceUntilIdle()

            assertEquals(NoteEditorUiEvent.NavigateBack, event.await())

            coVerify(exactly = 1) { mutationOperations.publish(logicalItemId) }
        }

    @Test
    fun `load when payload is corrupted then clears note and blocks mutations`() = runTest {
        val logicalItemId = UUID.randomUUID()
        every { observationCoordinator.observe(logicalItemId) } returns flowOf(
            SecureItemEditorObservationResult.Error(SecureItemCrudError.CorruptedPayload),
        )

        val target = target()

        target.load(logicalItemId.toString())
        advanceUntilIdle()
        target.onAction(NoteEditorUiAction.BodyChanged("ignored"))
        target.onAction(NoteEditorUiAction.SaveClicked)
        target.onAction(NoteEditorUiAction.DeleteClicked)
        advanceUntilIdle()

        assertEquals(SecureItemEditorState.CorruptedPayload, target.uiState.value.editorState)
        assertEquals("", target.uiState.value.body)

        coVerify(exactly = 0) {
            mutationOperations.save(any(), any<SecureItemEditorMutationRequest>())
        }
        coVerify(exactly = 0) { mutationOperations.delete(any()) }
    }

    @Test
    fun `lock during load then clears note and navigates to unlock`() = runTest {
        val logicalItemId = UUID.randomUUID()
        every { observationCoordinator.observe(logicalItemId) } returns
                MutableSharedFlow()

        val target = target()

        target.load(logicalItemId.toString())
        runCurrent()
        val event = async { target.events.first() }
        vaultLocked = true
        vaultLockEvents.emit(Unit)
        advanceUntilIdle()

        assertEquals(SecureItemEditorState.VaultLocked, target.uiState.value.editorState)
        assertEquals("", target.uiState.value.body)
        assertEquals(NoteEditorUiEvent.NavigateToUnlock, event.await())
    }

    @Test
    fun `lock during save then cancels mutation and clears note`() = runTest {
        val saveResult = CompletableDeferred<SecureItemMutationResult>()
        coEvery {
            mutationOperations.save(any(), any<SecureItemEditorMutationRequest>())
        } coAnswers { saveResult.await() }

        val target = target()

        target.onAction(NoteEditorUiAction.BodyChanged("local-value"))
        target.onAction(NoteEditorUiAction.SaveClicked)
        runCurrent()
        vaultLocked = true
        vaultLockEvents.emit(Unit)
        advanceUntilIdle()

        assertEquals(SecureItemEditorState.VaultLocked, target.uiState.value.editorState)
        assertEquals("", target.uiState.value.body)
        coVerify(exactly = 1) {
            mutationOperations.save(any(), any<SecureItemEditorMutationRequest>())
        }
    }

    @Test
    fun `load when local observation fails then exposes local storage failure`() = runTest {
        val logicalItemId = UUID.randomUUID()
        every { observationCoordinator.observe(logicalItemId) } returns flow {
            throw IllegalStateException("local read failed")
        }

        val target = target()

        target.load(logicalItemId.toString())
        advanceUntilIdle()

        assertEquals(SecureItemEditorState.LocalStorageFailure, target.uiState.value.editorState)
        assertEquals("", target.uiState.value.body)
    }

    private fun noteDraftDetail(
        logicalItemId: UUID,
        draftType: SecureItemDraftType = SecureItemDraftType.CREATE,
        draftSyncStatus: SecureItemDraftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
        displayHint: String = "Draft note",
        body: String = "Draft body",
        lastSyncError: String? = null,
    ) = SecureItemDraftDetail(
        logicalItemId = logicalItemId,
        remoteItemId = null,
        draftType = draftType,
        draftSyncStatus = draftSyncStatus,
        itemType = SecureItemType.NOTE,
        displayHint = displayHint,
        payloadVersion = 1,
        updatedAt = Instant.parse("2024-06-01T00:00:00Z"),
        lastSyncError = lastSyncError,
        content = NoteSecureItemContent(body),
    )

    private fun officialDetail(logicalItemId: UUID) = SecureItemDetail(
        logicalItemId = logicalItemId,
        remoteItemId = UUID.randomUUID(),
        itemType = SecureItemType.NOTE,
        schemaVersion = 1,
        displayHint = "Official note",
        payloadVersion = 2,
        createdAt = Instant.parse("2024-06-01T00:00:00Z"),
        updatedAt = Instant.parse("2024-06-01T00:00:00Z"),
        syncState = SecureItemSyncState.SYNCED,
        lastSyncError = null,
        content = NoteSecureItemContent("Official body"),
    )

}
