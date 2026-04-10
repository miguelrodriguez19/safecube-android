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
import java.time.Instant
import java.util.UUID

@OptIn(ExperimentalCoroutinesApi::class)
class NoteEditorViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeSecureItemDetailUseCase = mockk<ObserveSecureItemDetailUseCase>()
    private val createSecureNoteUseCase = mockk<CreateSecureNoteUseCase>()
    private val updateSecureNoteUseCase = mockk<UpdateSecureNoteUseCase>()
    private val softDeleteSecureItemUseCase = mockk<SoftDeleteSecureItemUseCase>()

    private val target = NoteEditorViewModel(
        observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
        createSecureNoteUseCase = createSecureNoteUseCase,
        updateSecureNoteUseCase = updateSecureNoteUseCase,
        softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
    )

    @Test
    fun `load when existing note detail is available then populates editor state`() = runTest {
        val logicalItemId = UUID.randomUUID()
        val now = Instant.now()
        every { observeSecureItemDetailUseCase(logicalItemId) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = logicalItemId,
                    remoteItemId = null,
                    itemType = SecureItemType.NOTE,
                    schemaVersion = 1,
                    displayHint = "Server keys",
                    payloadVersion = 1,
                    createdAt = now,
                    updatedAt = now,
                    content = NoteSecureItemContent(body = "ssh-rsa ..."),
                ),
            ),
        )

        target.load(logicalItemId.toString())
        advanceUntilIdle()

        assertEquals(logicalItemId, target.uiState.value.logicalItemId)
        assertEquals("Server keys", target.uiState.value.displayHint)
        assertEquals("ssh-rsa ...", target.uiState.value.body)
        verify(exactly = 1) { observeSecureItemDetailUseCase(logicalItemId) }
        confirmVerified(
            observeSecureItemDetailUseCase,
            createSecureNoteUseCase,
            updateSecureNoteUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `onAction when save clicked for new note then creates item and emits navigate back`() =
        runTest {
            coEvery { createSecureNoteUseCase(any()) } returns SecureItemMutationResult.Success(
                sampleNoteItem()
            )
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
    fun `onAction when save clicked for existing note then updates item and emits navigate back`() =
        runTest {
            val logicalItemId = UUID.randomUUID()
            val now = Instant.now()

            every { observeSecureItemDetailUseCase(logicalItemId) } returns flowOf(
                ObserveSecureItemDetailResult.Success(
                    detail = SecureItemDetail(
                        logicalItemId = logicalItemId,
                        remoteItemId = null,
                        itemType = SecureItemType.NOTE,
                        schemaVersion = 1,
                        displayHint = "Server keys",
                        payloadVersion = 1,
                        createdAt = now,
                        updatedAt = now,
                        content = NoteSecureItemContent(body = "ssh-rsa ..."),
                    ),
                ),
            )
            coEvery {
                updateSecureNoteUseCase(
                    logicalItemId = logicalItemId,
                    draft = any(),
                )
            } returns SecureItemMutationResult.Success(sampleNoteItem())
            val event = CompletableDeferred<NoteEditorUiEvent>()
            backgroundScope.launch {
                event.complete(target.events.first())
            }

            target.load(logicalItemId.toString())
            advanceUntilIdle()
            target.onAction(NoteEditorUiAction.BodyChanged("updated body"))
            target.onAction(NoteEditorUiAction.SaveClicked)
            advanceUntilIdle()

            assertEquals(NoteEditorUiEvent.NavigateBack, event.await())
            verify(exactly = 1) { observeSecureItemDetailUseCase(logicalItemId) }
            coVerify(exactly = 1) {
                updateSecureNoteUseCase(
                    logicalItemId = logicalItemId,
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
    fun `onAction when delete clicked for existing note then soft deletes item and emits navigate back`() =
        runTest {
            val logicalItemId = UUID.randomUUID()
            val now = Instant.now()
            every { observeSecureItemDetailUseCase(logicalItemId) } returns flowOf(
                ObserveSecureItemDetailResult.Success(
                    detail = SecureItemDetail(
                        logicalItemId = logicalItemId,
                        remoteItemId = null,
                        itemType = SecureItemType.NOTE,
                        schemaVersion = 1,
                        displayHint = "Server keys",
                        payloadVersion = 1,
                        createdAt = now,
                        updatedAt = now,
                        content = NoteSecureItemContent(body = "ssh-rsa ..."),
                    ),
                ),
            )
            coEvery { softDeleteSecureItemUseCase(logicalItemId) } returns SecureItemMutationResult.Success(
                sampleNoteItem(logicalItemId),
            )
            val event = CompletableDeferred<NoteEditorUiEvent>()
            backgroundScope.launch {
                event.complete(target.events.first())
            }

            target.load(logicalItemId.toString())
            advanceUntilIdle()
            target.onAction(NoteEditorUiAction.DeleteClicked)
            advanceUntilIdle()

            assertEquals(NoteEditorUiEvent.NavigateBack, event.await())
            verify(exactly = 1) { observeSecureItemDetailUseCase(logicalItemId) }
            coVerify(exactly = 1) { softDeleteSecureItemUseCase(logicalItemId) }
            confirmVerified(
                observeSecureItemDetailUseCase,
                createSecureNoteUseCase,
                updateSecureNoteUseCase,
                softDeleteSecureItemUseCase,
            )
        }

    @Test
    fun `load when create flow succeeded previously then entering edit loads fresh state`() =
        runTest {
            val logicalItemId = UUID.randomUUID()
            val now = Instant.now()
            coEvery { createSecureNoteUseCase(any()) } returns SecureItemMutationResult.Success(
                sampleNoteItem()
            )
            every { observeSecureItemDetailUseCase(logicalItemId) } returns flowOf(
                ObserveSecureItemDetailResult.Success(
                    detail = SecureItemDetail(
                        logicalItemId = logicalItemId,
                        remoteItemId = null,
                        itemType = SecureItemType.NOTE,
                        schemaVersion = 1,
                        displayHint = "Server keys",
                        payloadVersion = 1,
                        createdAt = now,
                        updatedAt = now,
                        content = NoteSecureItemContent(body = "ssh-rsa ..."),
                    ),
                ),
            )
            backgroundScope.launch {
                target.events.first()
            }

            target.onAction(NoteEditorUiAction.DisplayHintChanged("Server keys"))
            target.onAction(NoteEditorUiAction.BodyChanged("ssh-rsa ..."))
            target.onAction(NoteEditorUiAction.SaveClicked)
            advanceUntilIdle()
            target.load(logicalItemId.toString())
            advanceUntilIdle()

            assertEquals(false, target.uiState.value.isSaving)
            assertEquals(false, target.uiState.value.isLoading)
            assertEquals(logicalItemId, target.uiState.value.logicalItemId)
            assertEquals("Server keys", target.uiState.value.displayHint)
            verify(exactly = 1) { observeSecureItemDetailUseCase(logicalItemId) }
            coVerify(exactly = 1) { createSecureNoteUseCase(any()) }
            confirmVerified(
                observeSecureItemDetailUseCase,
                createSecureNoteUseCase,
                updateSecureNoteUseCase,
                softDeleteSecureItemUseCase,
            )
        }

    private fun sampleNoteItem(logicalItemId: UUID = UUID.randomUUID()): SecureItem {
        val now = Instant.now()

        return SecureItem(
            logicalItemId = logicalItemId,
            remoteItemId = null,
            itemType = SecureItemType.NOTE,
            schemaVersion = 1,
            displayHint = "Server keys",
            payload = byteArrayOf(1, 2, 3),
            payloadVersion = 1,
            createdAt = now,
            updatedAt = now,
            deletedAt = null,
        )
    }
}