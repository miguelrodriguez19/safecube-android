@file:OptIn(ExperimentalCoroutinesApi::class)

package com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDraftDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordWebsiteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftResult
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.action.PasswordEditorUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.event.PasswordEditorUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.passwordeditor.mutation.PasswordEditorMutationCoordinator
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.lifecycle.SecureItemEditorLifecycleCoordinator
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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

class PasswordEditorViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observationCoordinator = mockk<SecureItemEditorObservationCoordinator>()
    private val mutationCoordinator = mockk<PasswordEditorMutationCoordinator>()
    private val lifecycleCoordinator = mockk<SecureItemEditorLifecycleCoordinator>()

    private val syncing = MutableStateFlow(false)
    private val vaultLockEvents = MutableSharedFlow<Unit>()
    private var vaultLocked = false

    init {
        every { lifecycleCoordinator.observeSyncing() } returns syncing
        every { lifecycleCoordinator.observeVaultLocked() } returns vaultLockEvents
        every { lifecycleCoordinator.isVaultLocked() } answers { vaultLocked }
    }

    private fun target(): PasswordEditorViewModel = PasswordEditorViewModel(
        observationCoordinator = observationCoordinator,
        mutationCoordinator = mutationCoordinator,
        lifecycleCoordinator = lifecycleCoordinator,
    )

    @Test
    fun `load when draft exists then renders password draft fields`() = runTest {
        val logicalItemId = UUID.randomUUID()
        val draftDetail = passwordDraftDetail(logicalItemId)
        every { observationCoordinator.observe(logicalItemId) } returns flowOf(
            SecureItemEditorObservationResult.Content(
                officialDetail = null,
                draftDetail = draftDetail,
            ),
        )

        val target = target()

        target.load(logicalItemId.toString())
        advanceUntilIdle()

        val content = draftDetail.content as PasswordSecureItemContent
        assertEquals(draftDetail.displayHint, target.uiState.value.displayHint)
        assertEquals(content.username, target.uiState.value.username)
        assertEquals(content.password, target.uiState.value.password)
        assertEquals(content.website?.url, target.uiState.value.websiteUrl)
        assertEquals(content.notes, target.uiState.value.notes)
        assertNull(target.uiState.value.errorMessage)
    }

    @Test
    fun `discard draft when create draft exists then navigates back`() = runTest {
        val logicalItemId = UUID.randomUUID()
        every { observationCoordinator.observe(logicalItemId) } returns flowOf(
            SecureItemEditorObservationResult.Content(
                officialDetail = null,
                draftDetail = passwordDraftDetail(
                    logicalItemId = logicalItemId,
                    website = null,
                    notes = null,
                ),
            ),
        )
        coEvery { mutationCoordinator.discard(logicalItemId) } returns
            DiscardSecureItemDraftResult.Success(logicalItemId)

        val target = target()

        target.load(logicalItemId.toString())
        advanceUntilIdle()
        val event = async { target.events.first() }

        target.onAction(PasswordEditorUiAction.DiscardDraftClicked)
        advanceUntilIdle()

        assertEquals(PasswordEditorUiEvent.NavigateBack, event.await())
        coVerify(exactly = 1) { mutationCoordinator.discard(logicalItemId) }
    }

    @Test
    fun `load when payload is corrupted then clears fields and blocks mutations`() = runTest {
        val logicalItemId = UUID.randomUUID()
        every { observationCoordinator.observe(logicalItemId) } returns flowOf(
            SecureItemEditorObservationResult.Error(SecureItemCrudError.CorruptedPayload),
        )

        val target = target()

        target.load(logicalItemId.toString())
        advanceUntilIdle()
        target.onAction(PasswordEditorUiAction.PasswordChanged("ignored"))
        target.onAction(PasswordEditorUiAction.SaveClicked)
        target.onAction(PasswordEditorUiAction.DeleteClicked)
        advanceUntilIdle()

        assertEquals(SecureItemEditorState.CorruptedPayload, target.uiState.value.editorState)
        assertEquals("", target.uiState.value.password)
        assertEquals("", target.uiState.value.username)
        coVerify(exactly = 0) {
            mutationCoordinator.save(any(), any(), any<SecureItemContent>())
        }
        coVerify(exactly = 0) { mutationCoordinator.delete(any()) }
    }

    @Test
    fun `lock during load then clears password and navigates to unlock`() = runTest {
        val logicalItemId = UUID.randomUUID()
        every { observationCoordinator.observe(logicalItemId) } returns
            MutableSharedFlow<SecureItemEditorObservationResult>()

        val target = target()

        target.load(logicalItemId.toString())
        runCurrent()
        val event = async { target.events.first() }
        vaultLocked = true
        vaultLockEvents.emit(Unit)
        advanceUntilIdle()

        assertEquals(SecureItemEditorState.VaultLocked, target.uiState.value.editorState)
        assertEquals("", target.uiState.value.password)
        assertEquals(PasswordEditorUiEvent.NavigateToUnlock, event.await())
    }

    @Test
    fun `lock during save then cancels mutation and clears password`() = runTest {
        val saveResult = CompletableDeferred<SecureItemMutationResult>()
        coEvery {
            mutationCoordinator.save(any(), any(), any<SecureItemContent>())
        } coAnswers { saveResult.await() }

        val target = target()

        target.onAction(PasswordEditorUiAction.UsernameChanged("local-user"))
        target.onAction(PasswordEditorUiAction.PasswordChanged("local-value"))
        target.onAction(PasswordEditorUiAction.SaveClicked)
        runCurrent()
        vaultLocked = true
        vaultLockEvents.emit(Unit)
        advanceUntilIdle()

        assertEquals(SecureItemEditorState.VaultLocked, target.uiState.value.editorState)
        assertEquals("", target.uiState.value.password)
        coVerify(exactly = 1) {
            mutationCoordinator.save(any(), any(), any<SecureItemContent>())
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
        assertEquals("", target.uiState.value.password)
    }

    private fun passwordDraftDetail(
        logicalItemId: UUID,
        website: PasswordWebsiteSecureItemContent? =
            PasswordWebsiteSecureItemContent(url = "https://example.com"),
        notes: String? = "note",
    ) = SecureItemDraftDetail(
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
            website = website,
            notes = notes,
        ),
    )
}
