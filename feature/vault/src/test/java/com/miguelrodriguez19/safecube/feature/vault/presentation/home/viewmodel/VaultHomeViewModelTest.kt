package com.miguelrodriguez19.safecube.feature.vault.presentation.home.viewmodel

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemDetail
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.VaultItemSummary
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveVaultItemSummariesUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.CreateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.UpdateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.CreateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.UpdateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.action.VaultHomeUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultEditorUiState
import com.miguelrodriguez19.safecube.feature.vault.test.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VaultHomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val observeVaultItemSummariesUseCase = mockk<ObserveVaultItemSummariesUseCase>()
    private val observeSecureItemDetailUseCase = mockk<ObserveSecureItemDetailUseCase>()
    private val createSecurePasswordUseCase = mockk<CreateSecurePasswordUseCase>()
    private val updateSecurePasswordUseCase = mockk<UpdateSecurePasswordUseCase>()
    private val createSecureNoteUseCase = mockk<CreateSecureNoteUseCase>()
    private val updateSecureNoteUseCase = mockk<UpdateSecureNoteUseCase>()
    private val softDeleteSecureItemUseCase = mockk<SoftDeleteSecureItemUseCase>()

    private val summariesFlow = MutableStateFlow<List<VaultItemSummary>>(emptyList())

    private lateinit var target: VaultHomeViewModel

    @Test
    fun `init when summaries flow emits then exposes local vault items`() = runTest {
        createTarget()
        summariesFlow.value = listOf(
            VaultItemSummary(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                itemType = SecureItemType.PASSWORD,
                displayHint = "Github",
                updatedAt = UPDATED_AT,
            ),
        )

        advanceUntilIdle()

        assertEquals(1, target.uiState.value.items.size)
        assertEquals("Github", target.uiState.value.items.first().displayHint)
        assertEquals(SecureItemType.PASSWORD, target.uiState.value.items.first().itemType)
        verify(exactly = 1) { observeVaultItemSummariesUseCase.invoke() }
        confirmVerified(
            observeVaultItemSummariesUseCase,
            observeSecureItemDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            createSecureNoteUseCase,
            updateSecureNoteUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `onAction when create password clicked then opens password editor`() = runTest {
        createTarget()
        advanceUntilIdle()

        target.onAction(VaultHomeUiAction.CreatePasswordClicked)

        assertTrue(target.uiState.value.editor is VaultEditorUiState.Password)
        verify(exactly = 1) { observeVaultItemSummariesUseCase.invoke() }
        confirmVerified(
            observeVaultItemSummariesUseCase,
            observeSecureItemDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            createSecureNoteUseCase,
            updateSecureNoteUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `onAction when save clicked for new note then creates item and dismisses editor`() = runTest {
        createTarget()
        coEvery { createSecureNoteUseCase(any()) } returns SecureItemMutationResult.Success(sampleSecureItem())

        target.onAction(VaultHomeUiAction.CreateNoteClicked)
        target.onAction(VaultHomeUiAction.NoteDisplayHintChanged("API key"))
        target.onAction(VaultHomeUiAction.NoteBodyChanged("secret body"))
        target.onAction(VaultHomeUiAction.SaveEditorClicked)
        advanceUntilIdle()

        assertEquals(null, target.uiState.value.editor)
        verify(exactly = 1) { observeVaultItemSummariesUseCase.invoke() }
        coVerify(exactly = 1) {
            createSecureNoteUseCase(
                match { draft ->
                    draft.displayHint == "API key" && draft.body == "secret body"
                },
            )
        }
        confirmVerified(
            observeVaultItemSummariesUseCase,
            observeSecureItemDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            createSecureNoteUseCase,
            updateSecureNoteUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `onAction when edit item clicked for note then loads detail into note editor`() = runTest {
        createTarget()
        every {
            observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID)
        } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                    remoteItemId = null,
                    itemType = SecureItemType.NOTE,
                    schemaVersion = 1,
                    displayHint = "API key",
                    payloadVersion = 1,
                    createdAt = UPDATED_AT,
                    updatedAt = UPDATED_AT,
                    content = NoteSecureItemContent(body = "secret body"),
                ),
            ),
        )

        target.onAction(
            VaultHomeUiAction.EditItemClicked(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                itemType = SecureItemType.NOTE,
            ),
        )
        advanceUntilIdle()

        assertEquals(
            VaultEditorUiState.Note(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                displayHint = "API key",
                body = "secret body",
            ),
            target.uiState.value.editor,
        )
        verify(exactly = 1) { observeVaultItemSummariesUseCase.invoke() }
        verify(exactly = 1) { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        confirmVerified(
            observeVaultItemSummariesUseCase,
            observeSecureItemDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            createSecureNoteUseCase,
            updateSecureNoteUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    @Test
    fun `onAction when delete clicked for existing note then soft deletes and dismisses editor`() = runTest {
        createTarget()
        coEvery { softDeleteSecureItemUseCase(SAMPLE_LOGICAL_ITEM_ID) } returns SecureItemMutationResult.Success(
            sampleSecureItem(),
        )
        every {
            observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID)
        } returns flowOf(
            ObserveSecureItemDetailResult.Success(
                detail = SecureItemDetail(
                    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                    remoteItemId = null,
                    itemType = SecureItemType.NOTE,
                    schemaVersion = 1,
                    displayHint = "API key",
                    payloadVersion = 1,
                    createdAt = UPDATED_AT,
                    updatedAt = UPDATED_AT,
                    content = NoteSecureItemContent(body = "secret body"),
                ),
            ),
        )

        target.onAction(
            VaultHomeUiAction.EditItemClicked(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                itemType = SecureItemType.NOTE,
            ),
        )
        advanceUntilIdle()
        target.onAction(VaultHomeUiAction.DeleteItemClicked)
        advanceUntilIdle()

        assertEquals(null, target.uiState.value.editor)
        verify(exactly = 1) { observeVaultItemSummariesUseCase.invoke() }
        verify(exactly = 1) { observeSecureItemDetailUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        coVerify(exactly = 1) { softDeleteSecureItemUseCase(SAMPLE_LOGICAL_ITEM_ID) }
        confirmVerified(
            observeVaultItemSummariesUseCase,
            observeSecureItemDetailUseCase,
            createSecurePasswordUseCase,
            updateSecurePasswordUseCase,
            createSecureNoteUseCase,
            updateSecureNoteUseCase,
            softDeleteSecureItemUseCase,
        )
    }

    private fun createTarget() {
        every { observeVaultItemSummariesUseCase.invoke() } returns summariesFlow
        target = VaultHomeViewModel(
            observeVaultItemSummariesUseCase = observeVaultItemSummariesUseCase,
            observeSecureItemDetailUseCase = observeSecureItemDetailUseCase,
            createSecurePasswordUseCase = createSecurePasswordUseCase,
            updateSecurePasswordUseCase = updateSecurePasswordUseCase,
            createSecureNoteUseCase = createSecureNoteUseCase,
            updateSecureNoteUseCase = updateSecureNoteUseCase,
            softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
        )
    }

    private fun sampleSecureItem() = SecureItem(
        logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
        remoteItemId = null,
        itemType = SecureItemType.NOTE,
        schemaVersion = 1,
        displayHint = "API key",
        payload = byteArrayOf(1, 2, 3),
        payloadVersion = 1,
        createdAt = UPDATED_AT,
        updatedAt = UPDATED_AT,
        deletedAt = null,
    )

    private companion object {
        val SAMPLE_LOGICAL_ITEM_ID: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val UPDATED_AT: Instant = Instant.parse("2026-03-29T10:15:30Z")
    }
}
