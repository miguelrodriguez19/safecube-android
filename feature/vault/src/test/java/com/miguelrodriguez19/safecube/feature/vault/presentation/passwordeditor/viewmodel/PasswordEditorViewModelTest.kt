package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDraftDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordWebsiteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDraftDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.CreateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.UpdateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.DiscardSecureItemDraftUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.PublishSecureItemDraftUseCase
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
import kotlinx.coroutines.flow.MutableStateFlow
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
    private val observeSecureItemDraftDetailUseCase = mockk<ObserveSecureItemDraftDetailUseCase>()
    private val createSecurePasswordUseCase = mockk<CreateSecurePasswordUseCase>()
    private val updateSecurePasswordUseCase = mockk<UpdateSecurePasswordUseCase>()
    private val softDeleteSecureItemUseCase = mockk<SoftDeleteSecureItemUseCase>()
    private val publishSecureItemDraftUseCase = mockk<PublishSecureItemDraftUseCase>()
    private val discardSecureItemDraftUseCase = mockk<DiscardSecureItemDraftUseCase>()
    private val observeVaultSyncingUseCase = mockk<ObserveVaultSyncingUseCase>()
    private val isSyncingFlow = MutableStateFlow(false)

    private val target by lazy { buildTarget() }

    private fun buildTarget(): PasswordEditorViewModel {
        every { observeVaultSyncingUseCase.invoke() } returns isSyncingFlow
        every { observeSecureItemDraftDetailUseCase.invoke(any()) } returns flowOf(
            ObserveSecureItemDraftDetailResult.NotFound,
        )
        return PasswordEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase = createSecurePasswordUseCase,
            updateSecurePasswordUseCase = updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
            publishSecureItemDraftUseCase = publishSecureItemDraftUseCase,
            discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
        )
    }

    @Test
    fun `load when existing password detail is available then populates editor state`() = runTest {
        val logicalItemId = UUID.randomUUID()
        val now = Instant.now()

        every { observeSecureItemDetailUseCase(logicalItemId) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = logicalItemId,
                    remoteItemId = null,
                    itemType = SecureItemType.PASSWORD,
                    schemaVersion = 1,
                    displayHint = "Github",
                    payloadVersion = 1,
                    createdAt = now,
                    updatedAt = now,
                    syncState = SecureItemSyncState.PENDING_UPDATE,
                    lastSyncError = null,
                    content = PasswordSecureItemContent(
                        username = "miguel",
                        password = "s3cret",
                        website = PasswordWebsiteSecureItemContent(url = "https://github.com"),
                        notes = "personal account",
                    ),
                ),
            ),
        )

        target.load(logicalItemId.toString())
        advanceUntilIdle()

        assertEquals(logicalItemId, target.uiState.value.logicalItemId)
        assertEquals("Github", target.uiState.value.displayHint)
        assertEquals("miguel", target.uiState.value.username)
        assertEquals("s3cret", target.uiState.value.password)
        assertEquals("https://github.com", target.uiState.value.websiteUrl)
        assertEquals("personal account", target.uiState.value.notes)
        assertEquals(SecureItemSyncState.PENDING_UPDATE, target.uiState.value.itemSyncState)
        verify(exactly = 1) { observeSecureItemDetailUseCase(logicalItemId) }
        verify(exactly = 1) { observeSecureItemDraftDetailUseCase(logicalItemId) }
        verify(exactly = 1) { observeVaultSyncingUseCase.invoke() }
        confirmVerified(
            observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase,
            publishSecureItemDraftUseCase,
            discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase,
        )
    }

    @Test
    fun `onAction when save clicked for new password then creates item and emits navigate back`() = runTest {
        coEvery { createSecurePasswordUseCase(any()) } returns SecureItemMutationResult.Success(samplePasswordItem())
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
        verify(exactly = 1) { observeVaultSyncingUseCase.invoke() }
        confirmVerified(
            observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase,
            publishSecureItemDraftUseCase,
            discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase,
        )
    }

    @Test
    fun `onAction when save clicked for existing password then updates item and emits navigate back`() = runTest {
        val logicalItemId = UUID.randomUUID()
        val now = Instant.now()
        every { observeSecureItemDetailUseCase(logicalItemId) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = logicalItemId,
                    remoteItemId = null,
                    itemType = SecureItemType.PASSWORD,
                    schemaVersion = 1,
                    displayHint = "Github",
                    payloadVersion = 1,
                    createdAt = now,
                    updatedAt = now,
                    syncState = SecureItemSyncState.SYNCED,
                    lastSyncError = null,
                    content = PasswordSecureItemContent(
                        username = "miguel",
                        password = "s3cret",
                    ),
                ),
            ),
        )
        coEvery {
            updateSecurePasswordUseCase(
                logicalItemId = logicalItemId,
                draft = any(),
            )
        } returns SecureItemMutationResult.Success(samplePasswordItem(logicalItemId, now))
        val event = CompletableDeferred<PasswordEditorUiEvent>()
        backgroundScope.launch {
            event.complete(target.events.first())
        }

        target.load(logicalItemId.toString())
        advanceUntilIdle()
        target.onAction(PasswordEditorUiAction.PasswordChanged("updated-password"))
        target.onAction(PasswordEditorUiAction.SaveClicked)
        advanceUntilIdle()

        assertEquals(PasswordEditorUiEvent.NavigateBack, event.await())
        verify(exactly = 1) { observeSecureItemDetailUseCase(logicalItemId) }
        verify(exactly = 1) { observeSecureItemDraftDetailUseCase(logicalItemId) }
        coVerify(exactly = 1) {
            updateSecurePasswordUseCase(
                logicalItemId = logicalItemId,
                draft = match { draft ->
                    draft.displayHint == "Github" &&
                            draft.username == "miguel" &&
                            draft.password == "updated-password"
                },
            )
        }
        verify(exactly = 1) { observeVaultSyncingUseCase.invoke() }
        confirmVerified(
            observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase,
            publishSecureItemDraftUseCase,
            discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase,
        )
    }

    @Test
    fun `onAction when delete clicked for existing password then soft deletes item and emits navigate back`() = runTest {
        val logicalItemId = UUID.randomUUID()
        val now = Instant.now()
        every { observeSecureItemDetailUseCase(logicalItemId) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = logicalItemId,
                    remoteItemId = null,
                    itemType = SecureItemType.PASSWORD,
                    schemaVersion = 1,
                    displayHint = "Github",
                    payloadVersion = 1,
                    createdAt = now,
                    updatedAt = now,
                    syncState = SecureItemSyncState.CONFLICT,
                    lastSyncError = "Remote conflict",
                    content = PasswordSecureItemContent(
                        username = "miguel",
                        password = "s3cret",
                    ),
                ),
            ),
        )
        coEvery { softDeleteSecureItemUseCase(logicalItemId) } returns SecureItemMutationResult.Success(
            samplePasswordItem(logicalItemId, now),
        )
        val event = CompletableDeferred<PasswordEditorUiEvent>()
        backgroundScope.launch {
            event.complete(target.events.first())
        }

        target.load(logicalItemId.toString())
        advanceUntilIdle()
        target.onAction(PasswordEditorUiAction.DeleteClicked)
        advanceUntilIdle()

        assertEquals(PasswordEditorUiEvent.NavigateBack, event.await())
        verify(exactly = 1) { observeSecureItemDetailUseCase(logicalItemId) }
        verify(exactly = 1) { observeSecureItemDraftDetailUseCase(logicalItemId) }
        coVerify(exactly = 1) { softDeleteSecureItemUseCase(logicalItemId) }
        verify(exactly = 1) { observeVaultSyncingUseCase.invoke() }
        confirmVerified(
            observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase,
            publishSecureItemDraftUseCase,
            discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase,
        )
    }

    @Test
    fun `load when create flow succeeded previously then entering edit loads fresh state`() = runTest {
        val logicalItemId = UUID.randomUUID()
        val now = Instant.now()
        coEvery { createSecurePasswordUseCase(any()) } returns SecureItemMutationResult.Success(
            samplePasswordItem(logicalItemId, now),
        )
        every { observeSecureItemDetailUseCase(logicalItemId) } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = logicalItemId,
                    remoteItemId = null,
                    itemType = SecureItemType.PASSWORD,
                    schemaVersion = 1,
                    displayHint = "Github",
                    payloadVersion = 1,
                    createdAt = now,
                    updatedAt = now,
                    syncState = SecureItemSyncState.SYNCED,
                    lastSyncError = null,
                    content = PasswordSecureItemContent(
                        username = "miguel",
                        password = "s3cret",
                    ),
                ),
            ),
        )
        backgroundScope.launch {
            target.events.first()
        }

        target.onAction(PasswordEditorUiAction.DisplayHintChanged("Github"))
        target.onAction(PasswordEditorUiAction.UsernameChanged("miguel"))
        target.onAction(PasswordEditorUiAction.PasswordChanged("s3cret"))
        target.onAction(PasswordEditorUiAction.SaveClicked)
        advanceUntilIdle()
        target.load(logicalItemId.toString())
        advanceUntilIdle()

        assertEquals(false, target.uiState.value.isSaving)
        assertEquals(false, target.uiState.value.isLoading)
        assertEquals(logicalItemId, target.uiState.value.logicalItemId)
        assertEquals("Github", target.uiState.value.displayHint)
        verify(exactly = 1) { observeSecureItemDetailUseCase(logicalItemId) }
        verify(exactly = 1) { observeSecureItemDraftDetailUseCase(logicalItemId) }
        coVerify(exactly = 1) { createSecurePasswordUseCase(any()) }
        verify(exactly = 1) { observeVaultSyncingUseCase.invoke() }
        confirmVerified(
            observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase,
            publishSecureItemDraftUseCase,
            discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase,
        )
    }

    private fun samplePasswordItem(
        logicalItemId: UUID = UUID.randomUUID(),
        now: Instant = Instant.now(),
    ) = SecureItem(
        logicalItemId = logicalItemId,
        remoteItemId = null,
        itemType = SecureItemType.PASSWORD,
        schemaVersion = 1,
        displayHint = "Github",
        payload = byteArrayOf(1, 2, 3),
        payloadVersion = 1,
        createdAt = now,
        updatedAt = now,
        deletedAt = null,
    )
}
