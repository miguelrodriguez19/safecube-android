package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureNoteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.UpdateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.NoteDraftToContentMapper
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateSecureNoteUseCaseTest {

    private val secureItemMutationCoordinator = mockk<SecureItemMutationCoordinator>()
    private val noteDraftToContentMapper = mockk<NoteDraftToContentMapper>()

    private val target = UpdateSecureNoteUseCase(
        secureItemMutationCoordinator = secureItemMutationCoordinator,
        noteDraftToContentMapper = noteDraftToContentMapper,
    )

    @Test
    fun `invoke when draft is valid then maps note content and delegates update`() = runBlocking {
        val logicalItemId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val draft = SecureNoteDraft(
            displayHint = "API key",
            body = "secret body",
        )
        val content = NoteSecureItemContent(body = "secret body")
        val expectedResult = SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        every { noteDraftToContentMapper.map(draft) } returns content
        coEvery {
            secureItemMutationCoordinator.update(
                logicalItemId = logicalItemId,
                displayHint = "API key",
                expectedItemType = SecureItemType.NOTE,
                content = content,
            )
        } returns expectedResult

        val result = target(
            logicalItemId = logicalItemId,
            draft = draft,
        )

        assertEquals(expectedResult, result)
        verify(exactly = 1) { noteDraftToContentMapper.map(draft) }
        coVerify(exactly = 1) {
            secureItemMutationCoordinator.update(
                logicalItemId = logicalItemId,
                displayHint = "API key",
                expectedItemType = SecureItemType.NOTE,
                content = content,
            )
        }
        confirmVerified(secureItemMutationCoordinator, noteDraftToContentMapper)
    }

    @Test
    fun `invoke when mapper rejects draft then returns validation error without delegating`() = runBlocking {
        val logicalItemId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val draft = SecureNoteDraft(
            displayHint = "API key",
            body = " ",
        )
        every { noteDraftToContentMapper.map(draft) } throws IllegalArgumentException(
            "body must not be blank.",
        )

        val result = target(
            logicalItemId = logicalItemId,
            draft = draft,
        )

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("body must not be blank."),
            ),
            result,
        )
        verify(exactly = 1) { noteDraftToContentMapper.map(draft) }
        coVerify(exactly = 0) { secureItemMutationCoordinator.update(any(), any(), any(), any()) }
        confirmVerified(secureItemMutationCoordinator, noteDraftToContentMapper)
    }
}
