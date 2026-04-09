package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordWebsiteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.CreateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.UpdateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.action.PasswordEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.event.PasswordEditorUiEvent
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
class PasswordEditorViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeSecureItemDetailUseCase = mockk<ObserveSecureItemDetailUseCase>()
    private val createSecurePasswordUseCase = mockk<CreateSecurePasswordUseCase>()
    private val updateSecurePasswordUseCase = mockk<UpdateSecurePasswordUseCase>()
    private val softDeleteSecureItemUseCase = mockk<SoftDeleteSecureItemUseCase>()

    private lateinit var target: PasswordEditorViewModel

    @Test
    fun `load when existing password detail is available then populates editor state`() = runTest {
        every { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                    remoteItemId = null,
                    itemType = SecureItemType.PASSWORD,
                    schemaVersion = 1,
                    displayHint = "Github",
                    payloadVersion = 1,
                    createdAt = UPDATED_AT,
                    updatedAt = UPDATED_AT,
                    content = PasswordSecureItemContent(
                        username = "miguel",
                        password = "s3cret",
                        website = PasswordWebsiteSecureItemContent(url = "https://github.com"),
                        notes = "personal account",
                    ),
                ),
            ),
        )
        createTarget()

        target.load(SAMPLE_LOGICAL_ITEM_ID.toString())
        advanceUntilIdle()

        assertEquals(SAMPLE_LOGICAL_ITEM_ID, target.uiState.value.logicalItemId)
        assertEquals("Github", target.uiState.value.displayHint)
        assertEquals("miguel", target.uiState.value.username)
        assertEquals("s3cret", target.uiState.value.password)
        assertEquals("https://github.com", target.uiState.value.websiteUrl)
        assertEquals("personal account", target.uiState.value.notes)
        verify(exactly = 1) { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        confirmVerified(
            observeSecureItemDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `onAction when save clicked for new password then creates item and emits navigate back`() = runTest {
        coEvery { createSecurePasswordUseCase(any()) } returns SecureItemMutationResult.Success(samplePasswordItem())
        createTarget()
        val event = CompletableDeferred<PasswordEditorUiEvent>()
        backgroundScope.launch {
            event.complete(target.events.first())
        }

        target.onAction(PasswordEditorUiAction.DisplayHintChanged("Github"))
        target.onAction(PasswordEditorUiAction.UsernameChanged("miguel"))
        target.onAction(PasswordEditorUiAction.PasswordChanged("s3cret"))
        target.onAction(PasswordEditorUiAction.WebsiteUrlChanged("https://github.com"))
        target.onAction(PasswordEditorUiAction.NotesChanged("personal account"))
        target.onAction(PasswordEditorUiAction.SaveClicked)
        advanceUntilIdle()

        assertEquals(PasswordEditorUiEvent.NavigateBack, event.await())
        coVerify(exactly = 1) {
            createSecurePasswordUseCase(
                match { draft ->
                    draft.displayHint == "Github" &&
                        draft.username == "miguel" &&
                        draft.email == null &&
                        draft.password == "s3cret" &&
                        draft.website?.url == "https://github.com" &&
                        draft.notes == "personal account"
                },
            )
        }
        confirmVerified(
            observeSecureItemDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `onAction when save clicked for existing password then updates item and emits navigate back`() = runTest {
        every { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                    remoteItemId = null,
                    itemType = SecureItemType.PASSWORD,
                    schemaVersion = 1,
                    displayHint = "Github",
                    payloadVersion = 1,
                    createdAt = UPDATED_AT,
                    updatedAt = UPDATED_AT,
                    content = PasswordSecureItemContent(
                        username = "miguel",
                        password = "s3cret",
                    ),
                ),
            ),
        )
        coEvery {
            updateSecurePasswordUseCase(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                draft = any(),
            )
        } returns SecureItemMutationResult.Success(samplePasswordItem())
        createTarget()
        val event = CompletableDeferred<PasswordEditorUiEvent>()
        backgroundScope.launch {
            event.complete(target.events.first())
        }

        target.load(SAMPLE_LOGICAL_ITEM_ID.toString())
        advanceUntilIdle()
        target.onAction(PasswordEditorUiAction.PasswordChanged("updated-password"))
        target.onAction(PasswordEditorUiAction.SaveClicked)
        advanceUntilIdle()

        assertEquals(PasswordEditorUiEvent.NavigateBack, event.await())
        verify(exactly = 1) { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        coVerify(exactly = 1) {
            updateSecurePasswordUseCase(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                draft = match { draft ->
                    draft.displayHint == "Github" &&
                        draft.username == "miguel" &&
                        draft.password == "updated-password"
                },
            )
        }
        confirmVerified(
            observeSecureItemDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `onAction when delete clicked for existing password then soft deletes item and emits navigate back`() = runTest {
        every { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                    remoteItemId = null,
                    itemType = SecureItemType.PASSWORD,
                    schemaVersion = 1,
                    displayHint = "Github",
                    payloadVersion = 1,
                    createdAt = UPDATED_AT,
                    updatedAt = UPDATED_AT,
                    content = PasswordSecureItemContent(
                        username = "miguel",
                        password = "s3cret",
                    ),
                ),
            ),
        )
        coEvery { softDeleteSecureItemUseCase(SAMPLE_LOGICAL_ITEM_ID) } returns SecureItemMutationResult.Success(
            samplePasswordItem(),
        )
        createTarget()
        val event = CompletableDeferred<PasswordEditorUiEvent>()
        backgroundScope.launch {
            event.complete(target.events.first())
        }

        target.load(SAMPLE_LOGICAL_ITEM_ID.toString())
        advanceUntilIdle()
        target.onAction(PasswordEditorUiAction.DeleteClicked)
        advanceUntilIdle()

        assertEquals(PasswordEditorUiEvent.NavigateBack, event.await())
        verify(exactly = 1) { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        coVerify(exactly = 1) { softDeleteSecureItemUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        confirmVerified(
            observeSecureItemDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `load when create flow succeeded previously then entering edit loads fresh state`() = runTest {
        coEvery { createSecurePasswordUseCase(any()) } returns SecureItemMutationResult.Success(samplePasswordItem())
        every { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                    remoteItemId = null,
                    itemType = SecureItemType.PASSWORD,
                    schemaVersion = 1,
                    displayHint = "Github",
                    payloadVersion = 1,
                    createdAt = UPDATED_AT,
                    updatedAt = UPDATED_AT,
                    content = PasswordSecureItemContent(
                        username = "miguel",
                        password = "s3cret",
                    ),
                ),
            ),
        )
        createTarget()
        backgroundScope.launch {
            target.events.first()
        }

        target.onAction(PasswordEditorUiAction.DisplayHintChanged("Github"))
        target.onAction(PasswordEditorUiAction.UsernameChanged("miguel"))
        target.onAction(PasswordEditorUiAction.PasswordChanged("s3cret"))
        target.onAction(PasswordEditorUiAction.SaveClicked)
        advanceUntilIdle()
        target.load(SAMPLE_LOGICAL_ITEM_ID.toString())
        advanceUntilIdle()

        assertEquals(false, target.uiState.value.isSaving)
        assertEquals(false, target.uiState.value.isLoading)
        assertEquals(SAMPLE_LOGICAL_ITEM_ID, target.uiState.value.logicalItemId)
        assertEquals("Github", target.uiState.value.displayHint)
        verify(exactly = 1) { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        coVerify(exactly = 1) { createSecurePasswordUseCase(any()) }
        confirmVerified(
            observeSecureItemDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    private fun createTarget() {
        target = PasswordEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            createSecurePasswordUseCase = createSecurePasswordUseCase,
            updateSecurePasswordUseCase = updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
        )
    }
}

private val SAMPLE_LOGICAL_ITEM_ID: UUID = UUID.fromString("ab41dc34-bf46-4e11-8fa4-d183f621ab7b")
private val UPDATED_AT: Instant = Instant.parse("2025-01-10T10:15:30Z")

private fun samplePasswordItem() = SecureItem(
    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
    remoteItemId = null,
    itemType = SecureItemType.PASSWORD,
    schemaVersion = 1,
    displayHint = "Github",
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 1,
    createdAt = UPDATED_AT,
    updatedAt = UPDATED_AT,
    deletedAt = null,
)
