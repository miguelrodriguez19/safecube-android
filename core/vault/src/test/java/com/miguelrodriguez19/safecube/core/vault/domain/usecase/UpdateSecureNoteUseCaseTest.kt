package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureNoteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.UpdateSecureNoteUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note.NoteDraftToContentMapper
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.VaultSyncTrigger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class UpdateSecureNoteUseCaseTest {

    private val secureItemMutationCoordinator = mockk<SecureItemMutationCoordinator>()
    private val noteDraftToContentMapper = mockk<NoteDraftToContentMapper>()
    private val vaultSyncTrigger = mockk<VaultSyncTrigger>(relaxed = true)

    private val target = UpdateSecureNoteUseCase(
        secureItemMutationCoordinator = secureItemMutationCoordinator,
        noteDraftToContentMapper = noteDraftToContentMapper,
        vaultSyncTrigger = vaultSyncTrigger,
    )

    @Test
    fun `invoke when draft is valid and local mutation succeeds then triggers opportunistic sync`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val draft = SecureNoteDraft(
            displayHint = "API key",
            body = "secret body",
        )
        val content = NoteSecureItemContent(body = "secret body")
        val expectedResult = SecureItemMutationResult.Success(sampleUpdatedNoteItem(logicalItemId))
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
        verify(exactly = 1) { vaultSyncTrigger.onLocalMutationStored() }
        confirmVerified(secureItemMutationCoordinator, noteDraftToContentMapper, vaultSyncTrigger)
    }

    @Test
    fun `invoke when mapper rejects draft then returns validation error without delegating`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
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
        verify(exactly = 0) { vaultSyncTrigger.onLocalMutationStored() }
        confirmVerified(secureItemMutationCoordinator, noteDraftToContentMapper, vaultSyncTrigger)
    }

    @Test
    fun `invoke when mapper rejects draft without message then returns fallback validation error`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val draft = SecureNoteDraft(
            displayHint = "API key",
            body = "secret body",
        )
        every { noteDraftToContentMapper.map(draft) } throws IllegalArgumentException()

        val result = target(
            logicalItemId = logicalItemId,
            draft = draft,
        )

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Invalid note item."),
            ),
            result,
        )
        verify(exactly = 1) { noteDraftToContentMapper.map(draft) }
        coVerify(exactly = 0) { secureItemMutationCoordinator.update(any(), any(), any(), any()) }
        verify(exactly = 0) { vaultSyncTrigger.onLocalMutationStored() }
        confirmVerified(secureItemMutationCoordinator, noteDraftToContentMapper, vaultSyncTrigger)
    }
}

private fun sampleUpdatedNoteItem(logicalItemId: UUID): SecureItem {
    val now = Instant.now()
    return SecureItem(
        logicalItemId = logicalItemId,
        remoteItemId = UUID.randomUUID(),
        itemType = SecureItemType.NOTE,
        schemaVersion = 1,
        displayHint = "API key",
        payload = byteArrayOf(7, 8, 9),
        payloadVersion = 2,
        createdAt = now,
        updatedAt = now,
        syncState = SecureItemSyncState.PENDING_UPDATE,
    )
}
