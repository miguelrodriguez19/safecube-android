package com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.CreateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.UpdateSecureNoteUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.action.NoteEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.noteeditor.event.NoteEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class NoteEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeSecureItemDetailUseCase = mockk<ObserveSecureItemDetailUseCase>()
    private val createSecureNoteUseCase = mockk<CreateSecureNoteUseCase>()
    private val updateSecureNoteUseCase = mockk<UpdateSecureNoteUseCase>()
    private val softDeleteSecureItemUseCase = mockk<SoftDeleteSecureItemUseCase>()

    private lateinit var target: NoteEditorViewModel

    @Test
    fun `load when existing note detail is available then populates editor state`() = runTest {
        every { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                    remoteItemId = null,
                    itemType = SecureItemType.NOTE,
                    schemaVersion = 1,
                    displayHint = "Server keys",
                    payloadVersion = 1,
                    createdAt = UPDATED_AT,
                    updatedAt = UPDATED_AT,
                    content = NoteSecureItemContent(body = "ssh-rsa ..."),
                ),
            ),
        )
        createTarget()

        target.load(SAMPLE_LOGICAL_ITEM_ID.toString())
        advanceUntilIdle()

        assertEquals(SAMPLE_LOGICAL_ITEM_ID, target.uiState.value.logicalItemId)
        assertEquals("Server keys", target.uiState.value.displayHint)
        assertEquals("ssh-rsa ...", target.uiState.value.body)
        verify(exactly = 1) { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        confirmVerified(
            observeSecureItemDetailUseCase,
            createSecureNoteUseCase,
            updateSecureNoteUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `onAction when save clicked for new note then creates item and emits navigate back`() = runTest {
        coEvery { createSecureNoteUseCase(any()) } returns SecureItemMutationResult.Success(sampleNoteItem())
        createTarget()
        val event = CompletableDeferred<NoteEditorUiEvent>()
        backgroundScope.launch {
            event.complete(target.events.first())
        }

        target.onAction(NoteEditorUiAction.DisplayHintChanged("Server keys"))
        target.onAction(NoteEditorUiAction.BodyChanged("ssh-rsa ..."))
        target.onAction(NoteEditorUiAction.SaveClicked)
        advanceUntilIdle()

        assertEquals(NoteEditorUiEvent.NavigateBack, event.await())
        coVerify(exactly = 1) {
            createSecureNoteUseCase(
                match { draft ->
                    draft.displayHint == "Server keys" &&
                        draft.body == "ssh-rsa ..."
                },
            )
        }
        confirmVerified(
            observeSecureItemDetailUseCase,
            createSecureNoteUseCase,
            updateSecureNoteUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `onAction when save clicked for existing note then updates item and emits navigate back`() = runTest {
        every { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                    remoteItemId = null,
                    itemType = SecureItemType.NOTE,
                    schemaVersion = 1,
                    displayHint = "Server keys",
                    payloadVersion = 1,
                    createdAt = UPDATED_AT,
                    updatedAt = UPDATED_AT,
                    content = NoteSecureItemContent(body = "ssh-rsa ..."),
                ),
            ),
        )
        coEvery {
            updateSecureNoteUseCase(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                draft = any(),
            )
        } returns SecureItemMutationResult.Success(sampleNoteItem())
        createTarget()
        val event = CompletableDeferred<NoteEditorUiEvent>()
        backgroundScope.launch {
            event.complete(target.events.first())
        }

        target.load(SAMPLE_LOGICAL_ITEM_ID.toString())
        advanceUntilIdle()
        target.onAction(NoteEditorUiAction.BodyChanged("updated body"))
        target.onAction(NoteEditorUiAction.SaveClicked)
        advanceUntilIdle()

        assertEquals(NoteEditorUiEvent.NavigateBack, event.await())
        verify(exactly = 1) { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        coVerify(exactly = 1) {
            updateSecureNoteUseCase(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                draft = match { draft ->
                    draft.displayHint == "Server keys" &&
                        draft.body == "updated body"
                },
            )
        }
        confirmVerified(
            observeSecureItemDetailUseCase,
            createSecureNoteUseCase,
            updateSecureNoteUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `onAction when delete clicked for existing note then soft deletes item and emits navigate back`() = runTest {
        every { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                    remoteItemId = null,
                    itemType = SecureItemType.NOTE,
                    schemaVersion = 1,
                    displayHint = "Server keys",
                    payloadVersion = 1,
                    createdAt = UPDATED_AT,
                    updatedAt = UPDATED_AT,
                    content = NoteSecureItemContent(body = "ssh-rsa ..."),
                ),
            ),
        )
        coEvery { softDeleteSecureItemUseCase(SAMPLE_LOGICAL_ITEM_ID) } returns SecureItemMutationResult.Success(
            sampleNoteItem(),
        )
        createTarget()
        val event = CompletableDeferred<NoteEditorUiEvent>()
        backgroundScope.launch {
            event.complete(target.events.first())
        }

        target.load(SAMPLE_LOGICAL_ITEM_ID.toString())
        advanceUntilIdle()
        target.onAction(NoteEditorUiAction.DeleteClicked)
        advanceUntilIdle()

        assertEquals(NoteEditorUiEvent.NavigateBack, event.await())
        verify(exactly = 1) { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        coVerify(exactly = 1) { softDeleteSecureItemUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        confirmVerified(
            observeSecureItemDetailUseCase,
            createSecureNoteUseCase,
            updateSecureNoteUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `load when create flow succeeded previously then entering edit loads fresh state`() = runTest {
        coEvery { createSecureNoteUseCase(any()) } returns SecureItemMutationResult.Success(sampleNoteItem())
        every { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                    remoteItemId = null,
                    itemType = SecureItemType.NOTE,
                    schemaVersion = 1,
                    displayHint = "Server keys",
                    payloadVersion = 1,
                    createdAt = UPDATED_AT,
                    updatedAt = UPDATED_AT,
                    content = NoteSecureItemContent(body = "ssh-rsa ..."),
                ),
            ),
        )
        createTarget()
        backgroundScope.launch {
            target.events.first()
        }

        target.onAction(NoteEditorUiAction.DisplayHintChanged("Server keys"))
        target.onAction(NoteEditorUiAction.BodyChanged("ssh-rsa ..."))
        target.onAction(NoteEditorUiAction.SaveClicked)
        advanceUntilIdle()
        target.load(SAMPLE_LOGICAL_ITEM_ID.toString())
        advanceUntilIdle()

        assertEquals(false, target.uiState.value.isSaving)
        assertEquals(false, target.uiState.value.isLoading)
        assertEquals(SAMPLE_LOGICAL_ITEM_ID, target.uiState.value.logicalItemId)
        assertEquals("Server keys", target.uiState.value.displayHint)
        verify(exactly = 1) { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        coVerify(exactly = 1) { createSecureNoteUseCase(any()) }
        confirmVerified(
            observeSecureItemDetailUseCase,
            createSecureNoteUseCase,
            updateSecureNoteUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    private fun createTarget() {
        target = NoteEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            createSecureNoteUseCase = createSecureNoteUseCase,
            updateSecureNoteUseCase = updateSecureNoteUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
        )
    }
}

private val SAMPLE_LOGICAL_ITEM_ID: UUID = UUID.fromString("9606b332-f4eb-45ce-bd91-79554a5ce995")
private val UPDATED_AT: Instant = Instant.parse("2025-01-10T10:15:30Z")

private fun sampleNoteItem() = SecureItem(
    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
    remoteItemId = null,
    itemType = SecureItemType.NOTE,
    schemaVersion = 1,
    displayHint = "Server keys",
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 1,
    createdAt = UPDATED_AT,
    updatedAt = UPDATED_AT,
    deletedAt = null,
)
