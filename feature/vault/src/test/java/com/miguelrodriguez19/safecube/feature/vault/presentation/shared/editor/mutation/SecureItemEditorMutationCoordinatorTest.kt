package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.DiscardSecureItemDraftUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft.PrepareSecureItemDraftForSyncUseCase
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.contract.SecureItemEditorMutationGateway
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.factory.SecureItemEditorMutationGatewayFactory
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.editor.mutation.model.SecureItemEditorMutationRequest
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.util.UUID
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class SecureItemEditorMutationCoordinatorTest {
    private val mutationGatewayFactory = mockk<SecureItemEditorMutationGatewayFactory>()
    private val softDeleteSecureItemUseCase = mockk<SoftDeleteSecureItemUseCase>()
    private val prepareSecureItemDraftForSyncUseCase = mockk<PrepareSecureItemDraftForSyncUseCase>()
    private val discardSecureItemDraftUseCase = mockk<DiscardSecureItemDraftUseCase>()
    private val noteGateway = mockk<SecureItemEditorMutationGateway>()
    private val target = SecureItemEditorMutationCoordinator(
        mutationGatewayFactory = mutationGatewayFactory,
        softDeleteSecureItemUseCase = softDeleteSecureItemUseCase,
        prepareSecureItemDraftForSyncUseCase = prepareSecureItemDraftForSyncUseCase,
        discardSecureItemDraftUseCase = discardSecureItemDraftUseCase,
    )

    @Test
    fun save_whenCreatingNote_thenDispatchesToGatewayResolvedByItemType() = runTest {
        val request = noteRequest()
        val expected = SecureItemMutationResult.Success(UUID.randomUUID())
        every { mutationGatewayFactory.gatewayFor(SecureItemType.NOTE) } returns noteGateway
        coEvery { noteGateway.create(request) } returns expected

        val actual = target.save(logicalItemId = null, request = request)

        assertEquals(expected, actual)
        coVerify(exactly = 1) { noteGateway.create(request) }
        coVerify(exactly = 0) { noteGateway.update(any(), any()) }
    }

    @Test
    fun save_whenUpdatingNote_thenDispatchesToResolvedGateway() = runTest {
        val logicalItemId = UUID.randomUUID()
        val request = noteRequest()
        val expected = SecureItemMutationResult.Success(logicalItemId)
        every { mutationGatewayFactory.gatewayFor(SecureItemType.NOTE) } returns noteGateway
        coEvery { noteGateway.update(logicalItemId, request) } returns expected

        val actual = target.save(logicalItemId, request)

        assertEquals(expected, actual)
        coVerify(exactly = 1) { noteGateway.update(logicalItemId, request) }
        coVerify(exactly = 0) { noteGateway.create(any()) }
    }

    @Test
    fun save_whenItemTypeHasNoGateway_thenReturnsValidationErrorWithoutMutation() = runTest {
        val request = noteRequest()
        every { mutationGatewayFactory.gatewayFor(SecureItemType.NOTE) } returns null

        val actual = target.save(logicalItemId = null, request = request)

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Unsupported secure item type."),
            ),
            actual,
        )
        coVerify(exactly = 0) { noteGateway.create(any()) }
        coVerify(exactly = 0) { noteGateway.update(any(), any()) }
    }

    private fun noteRequest() = SecureItemEditorMutationRequest(
        displayHint = "note",
        content = NoteSecureItemContent("content"),
    )
}
