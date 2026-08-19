package com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDraftDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDraftDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDraftDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.CreateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.UpdateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.DiscardSecureItemDraftUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.PrepareSecureItemDraftForSyncUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.action.NoteEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.event.NoteEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.async
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class NoteEditorViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeSecureItemDetailUseCase = mockk<ObserveSecureItemDetailUseCase>()
    private val observeSecureItemDraftDetailUseCase = mockk<ObserveSecureItemDraftDetailUseCase>()
    private val createSecureNoteUseCase = mockk<CreateSecureNoteUseCase>()
    private val updateSecureNoteUseCase = mockk<UpdateSecureNoteUseCase>()
    private val softDeleteSecureItemUseCase = mockk<SoftDeleteSecureItemUseCase>()
    private val prepareSecureItemDraftForSyncUseCase = mockk<PrepareSecureItemDraftForSyncUseCase>()
    private val discardSecureItemDraftUseCase = mockk<DiscardSecureItemDraftUseCase>()
    private val observeVaultSyncingUseCase = mockk<ObserveVaultSyncingUseCase>()
    private val vaultSessionManager = mockk<VaultSessionManager>()
    private val vaultState = MutableStateFlow<VaultState>(VaultState.Unlocked)

    init {
        every { vaultSessionManager.vaultState } returns vaultState
    }

    @Test
    fun `load when only draft exists then renders draft content and clears not found error`() = runTest {
        val logicalItemId = UUID.randomUUID()
        every { observeVaultSyncingUseCase.invoke() } returns MutableStateFlow(false)
        every { observeSecureItemDetailUseCase.invoke(logicalItemId) } returns flowOf(
            ObserveSecureItemDetailResult.Error(com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError.ItemNotFound),
        )
        every { observeSecureItemDraftDetailUseCase.invoke(logicalItemId) } returns flowOf(
            ObserveSecureItemDraftDetailResult.Success(
                SecureItemDraftDetail(
                    logicalItemId = logicalItemId,
                    remoteItemId = null,
                    draftType = SecureItemDraftType.CREATE,
                    draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
                    itemType = SecureItemType.NOTE,
                    displayHint = "Draft note",
                    payloadVersion = 1,
                    updatedAt = Instant.parse("2024-06-01T00:00:00Z"),
                    lastSyncError = null,
                    content = NoteSecureItemContent("Draft body"),
                ),
            ),
        )

        val target = NoteEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecureNoteUseCase = createSecureNoteUseCase,
            updateSecureNoteUseCase = updateSecureNoteUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
            prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
            discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
            vaultSessionManager = vaultSessionManager,
        )

        target.load(logicalItemId.toString())
        advanceUntilIdle()

        assertEquals("Draft note", target.uiState.value.displayHint)
        assertEquals("Draft body", target.uiState.value.body)
        assertEquals(SecureItemDraftType.CREATE, target.uiState.value.draftType)
        assertNull(target.uiState.value.errorMessage)
    }

    @Test
    fun `publish draft when conflict draft is loaded then prepares it and navigates back`() = runTest {
        val logicalItemId = UUID.randomUUID()
        every { observeVaultSyncingUseCase.invoke() } returns MutableStateFlow(false)
        every { observeSecureItemDetailUseCase.invoke(logicalItemId) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                SecureItemDetail(
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
                ),
            ),
        )
        every { observeSecureItemDraftDetailUseCase.invoke(logicalItemId) } returns flowOf(
            ObserveSecureItemDraftDetailResult.Success(
                SecureItemDraftDetail(
                    logicalItemId = logicalItemId,
                    remoteItemId = UUID.randomUUID(),
                    draftType = SecureItemDraftType.UPDATE,
                    draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
                    itemType = SecureItemType.NOTE,
                    displayHint = "Draft note",
                    payloadVersion = 3,
                    updatedAt = Instant.parse("2024-06-01T01:00:00Z"),
                    lastSyncError = "Conflict",
                    content = NoteSecureItemContent("Draft body"),
                ),
            ),
        )
        coEvery {
            prepareSecureItemDraftForSyncUseCase.invoke(logicalItemId)
        } returns PrepareSecureItemDraftForSyncResult.Success(
            logicalItemId = logicalItemId,
            draftType = SecureItemDraftType.UPDATE,
        )

        val target = NoteEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecureNoteUseCase = createSecureNoteUseCase,
            updateSecureNoteUseCase = updateSecureNoteUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
            prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
            discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
            vaultSessionManager = vaultSessionManager,
        )

        target.load(logicalItemId.toString())
        advanceUntilIdle()
        val event = async { target.events.first() }
        target.onAction(NoteEditorUiAction.PublishDraftClicked)
        advanceUntilIdle()

        assertEquals(NoteEditorUiEvent.NavigateBack, event.await())
    }

    @Test
    fun `load when payload is corrupted then clears note and blocks mutations`() = runTest {
        val logicalItemId = UUID.randomUUID()
        every { observeVaultSyncingUseCase.invoke() } returns MutableStateFlow(false)
        every { observeSecureItemDetailUseCase.invoke(logicalItemId) } returns flowOf(
            ObserveSecureItemDetailResult.Error(SecureItemCrudError.CorruptedPayload),
        )
        every { observeSecureItemDraftDetailUseCase.invoke(logicalItemId) } returns flowOf(
            ObserveSecureItemDraftDetailResult.NotFound,
        )

        val target = NoteEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecureNoteUseCase = createSecureNoteUseCase,
            updateSecureNoteUseCase = updateSecureNoteUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
            prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
            discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
            vaultSessionManager = vaultSessionManager,
        )

        target.load(logicalItemId.toString())
        advanceUntilIdle()
        target.onAction(NoteEditorUiAction.BodyChanged("ignored"))
        target.onAction(NoteEditorUiAction.SaveClicked)
        target.onAction(NoteEditorUiAction.DeleteClicked)
        advanceUntilIdle()

        assertEquals(
            com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.state.SecureItemEditorState.CorruptedPayload,
            target.uiState.value.editorState,
        )
        assertEquals("", target.uiState.value.body)
        coVerify(exactly = 0) { createSecureNoteUseCase.invoke(any()) }
        coVerify(exactly = 0) { updateSecureNoteUseCase.invoke(any(), any()) }
        coVerify(exactly = 0) { softDeleteSecureItemUseCase.invoke(any()) }
    }

    @Test
    fun `lock during load then clears note and navigates to unlock`() = runTest {
        val logicalItemId = UUID.randomUUID()
        val officialFlow = MutableSharedFlow<ObserveSecureItemDetailResult>()
        val draftFlow = MutableSharedFlow<ObserveSecureItemDraftDetailResult>()
        every { observeVaultSyncingUseCase.invoke() } returns MutableStateFlow(false)
        every { observeSecureItemDetailUseCase.invoke(logicalItemId) } returns officialFlow
        every { observeSecureItemDraftDetailUseCase.invoke(logicalItemId) } returns draftFlow

        val target = NoteEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecureNoteUseCase = createSecureNoteUseCase,
            updateSecureNoteUseCase = updateSecureNoteUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
            prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
            discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
            vaultSessionManager = vaultSessionManager,
        )

        target.load(logicalItemId.toString())
        runCurrent()
        val event = async { target.events.first() }
        vaultState.value = VaultState.Locked
        advanceUntilIdle()

        assertEquals(
            com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.state.SecureItemEditorState.VaultLocked,
            target.uiState.value.editorState,
        )
        assertEquals("", target.uiState.value.body)
        assertEquals(NoteEditorUiEvent.NavigateToUnlock, event.await())
    }

    @Test
    fun `lock during save then cancels mutation and clears note`() = runTest {
        val saveResult = CompletableDeferred<SecureItemMutationResult>()
        every { observeVaultSyncingUseCase.invoke() } returns MutableStateFlow(false)
        coEvery { createSecureNoteUseCase.invoke(any()) } coAnswers { saveResult.await() }

        val target = NoteEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecureNoteUseCase = createSecureNoteUseCase,
            updateSecureNoteUseCase = updateSecureNoteUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
            prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
            discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
            vaultSessionManager = vaultSessionManager,
        )

        target.onAction(NoteEditorUiAction.BodyChanged("local-value"))
        target.onAction(NoteEditorUiAction.SaveClicked)
        runCurrent()
        vaultState.value = VaultState.Locked
        advanceUntilIdle()

        assertEquals(
            com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.state.SecureItemEditorState.VaultLocked,
            target.uiState.value.editorState,
        )
        assertEquals("", target.uiState.value.body)
        coVerify(exactly = 1) { createSecureNoteUseCase.invoke(any()) }
    }

    @Test
    fun `load when local observation fails then exposes local storage failure`() = runTest {
        val logicalItemId = UUID.randomUUID()
        every { observeVaultSyncingUseCase.invoke() } returns MutableStateFlow(false)
        every { observeSecureItemDetailUseCase.invoke(logicalItemId) } returns flow {
            throw IllegalStateException("local read failed")
        }
        every { observeSecureItemDraftDetailUseCase.invoke(logicalItemId) } returns flowOf(
            ObserveSecureItemDraftDetailResult.NotFound,
        )

        val target = NoteEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecureNoteUseCase = createSecureNoteUseCase,
            updateSecureNoteUseCase = updateSecureNoteUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
            prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
            discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
            vaultSessionManager = vaultSessionManager,
        )

        target.load(logicalItemId.toString())
        advanceUntilIdle()

        assertEquals(
            com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.state.SecureItemEditorState.LocalStorageFailure,
            target.uiState.value.editorState,
        )
        assertEquals("", target.uiState.value.body)
    }
}
