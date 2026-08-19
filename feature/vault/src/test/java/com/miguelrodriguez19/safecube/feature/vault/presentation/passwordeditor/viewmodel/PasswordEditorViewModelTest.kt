package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDraftDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDraftDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDraftDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.CreateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.UpdateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.ObserveVaultSyncingUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.DiscardSecureItemDraftUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.PrepareSecureItemDraftForSyncUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordWebsiteSecureItemContent
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.action.PasswordEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.event.PasswordEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.async
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.flow.flow
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class PasswordEditorViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeSecureItemDetailUseCase = mockk<ObserveSecureItemDetailUseCase>()
    private val observeSecureItemDraftDetailUseCase = mockk<ObserveSecureItemDraftDetailUseCase>()
    private val createSecurePasswordUseCase = mockk<CreateSecurePasswordUseCase>()
    private val updateSecurePasswordUseCase = mockk<UpdateSecurePasswordUseCase>()
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
    fun `load when draft exists then renders password draft fields`() = runTest {
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
                    itemType = SecureItemType.PASSWORD,
                    displayHint = "Draft password",
                    payloadVersion = 1,
                    updatedAt = Instant.parse("2024-06-02T00:00:00Z"),
                    lastSyncError = null,
                    content = PasswordSecureItemContent(
                        username = "user",
                        email = null,
                        password = "secret",
                        website = PasswordWebsiteSecureItemContent(url = "https://example.com"),
                        notes = "note",
                    ),
                ),
            ),
        )

        val target = PasswordEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase = createSecurePasswordUseCase,
            updateSecurePasswordUseCase = updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
            prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
            discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
            vaultSessionManager = vaultSessionManager,
        )

        target.load(logicalItemId.toString())
        advanceUntilIdle()

        assertEquals("Draft password", target.uiState.value.displayHint)
        assertEquals("user", target.uiState.value.username)
        assertEquals("secret", target.uiState.value.password)
        assertEquals("https://example.com", target.uiState.value.websiteUrl)
        assertEquals("note", target.uiState.value.notes)
        assertNull(target.uiState.value.errorMessage)
    }

    @Test
    fun `discard draft when create draft exists then navigates back`() = runTest {
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
                    itemType = SecureItemType.PASSWORD,
                    displayHint = "Draft password",
                    payloadVersion = 1,
                    updatedAt = Instant.parse("2024-06-02T00:00:00Z"),
                    lastSyncError = null,
                    content = PasswordSecureItemContent(
                        username = "user",
                        email = null,
                        password = "secret",
                        website = null,
                        notes = null,
                    ),
                ),
            ),
        )
        coEvery {
            discardSecureItemDraftUseCase.invoke(logicalItemId)
        } returns DiscardSecureItemDraftResult.Success(logicalItemId)

        val target = PasswordEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase = createSecurePasswordUseCase,
            updateSecurePasswordUseCase = updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
            prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
            discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
            vaultSessionManager = vaultSessionManager,
        )

        target.load(logicalItemId.toString())
        advanceUntilIdle()
        val event = async { target.events.first() }
        target.onAction(PasswordEditorUiAction.DiscardDraftClicked)
        advanceUntilIdle()

        assertEquals(PasswordEditorUiEvent.NavigateBack, event.await())
    }

    @Test
    fun `load when payload is corrupted then clears fields and blocks mutations`() = runTest {
        val logicalItemId = UUID.randomUUID()
        every { observeVaultSyncingUseCase.invoke() } returns MutableStateFlow(false)
        every { observeSecureItemDetailUseCase.invoke(logicalItemId) } returns flowOf(
            ObserveSecureItemDetailResult.Error(SecureItemCrudError.CorruptedPayload),
        )
        every { observeSecureItemDraftDetailUseCase.invoke(logicalItemId) } returns flowOf(
            ObserveSecureItemDraftDetailResult.NotFound,
        )

        val target = PasswordEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase = createSecurePasswordUseCase,
            updateSecurePasswordUseCase = updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
            prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
            discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
            vaultSessionManager = vaultSessionManager,
        )

        target.load(logicalItemId.toString())
        advanceUntilIdle()
        target.onAction(PasswordEditorUiAction.PasswordChanged("ignored"))
        target.onAction(PasswordEditorUiAction.SaveClicked)
        target.onAction(PasswordEditorUiAction.DeleteClicked)
        advanceUntilIdle()

        assertEquals(
            com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.state.SecureItemEditorState.CorruptedPayload,
            target.uiState.value.editorState,
        )
        assertEquals("", target.uiState.value.password)
        assertEquals("", target.uiState.value.username)
        coVerify(exactly = 0) { createSecurePasswordUseCase.invoke(any()) }
        coVerify(exactly = 0) { updateSecurePasswordUseCase.invoke(any(), any()) }
        coVerify(exactly = 0) { softDeleteSecureItemUseCase.invoke(any()) }
    }

    @Test
    fun `lock during load then clears password and navigates to unlock`() = runTest {
        val logicalItemId = UUID.randomUUID()
        val officialFlow = MutableSharedFlow<ObserveSecureItemDetailResult>()
        val draftFlow = MutableSharedFlow<ObserveSecureItemDraftDetailResult>()
        every { observeVaultSyncingUseCase.invoke() } returns MutableStateFlow(false)
        every { observeSecureItemDetailUseCase.invoke(logicalItemId) } returns officialFlow
        every { observeSecureItemDraftDetailUseCase.invoke(logicalItemId) } returns draftFlow

        val target = PasswordEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase = createSecurePasswordUseCase,
            updateSecurePasswordUseCase = updateSecurePasswordUseCase,
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
        assertEquals("", target.uiState.value.password)
        assertEquals(PasswordEditorUiEvent.NavigateToUnlock, event.await())
    }

    @Test
    fun `lock during save then cancels mutation and clears password`() = runTest {
        val saveResult = CompletableDeferred<SecureItemMutationResult>()
        every { observeVaultSyncingUseCase.invoke() } returns MutableStateFlow(false)
        coEvery { createSecurePasswordUseCase.invoke(any()) } coAnswers { saveResult.await() }

        val target = PasswordEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase = createSecurePasswordUseCase,
            updateSecurePasswordUseCase = updateSecurePasswordUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
            prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
            discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
            observeVaultSyncingUseCase = observeVaultSyncingUseCase,
            vaultSessionManager = vaultSessionManager,
        )

        target.onAction(PasswordEditorUiAction.PasswordChanged("local-value"))
        target.onAction(PasswordEditorUiAction.SaveClicked)
        runCurrent()
        vaultState.value = VaultState.Locked
        advanceUntilIdle()

        assertEquals(
            com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.state.SecureItemEditorState.VaultLocked,
            target.uiState.value.editorState,
        )
        assertEquals("", target.uiState.value.password)
        coVerify(exactly = 1) { createSecurePasswordUseCase.invoke(any()) }
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

        val target = PasswordEditorViewModel(
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            observeSecureItemDraftDetailUseCase = observeSecureItemDraftDetailUseCase,
            createSecurePasswordUseCase = createSecurePasswordUseCase,
            updateSecurePasswordUseCase = updateSecurePasswordUseCase,
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
        assertEquals("", target.uiState.value.password)
    }
}
