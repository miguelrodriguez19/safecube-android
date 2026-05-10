package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.note

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureNoteDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
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

class CreateSecureNoteUseCaseTest {

    private val secureItemMutationCoordinator = mockk<SecureItemMutationCoordinator>()
    private val noteDraftToContentMapper = mockk<NoteDraftToContentMapper>()
    private val vaultSyncTrigger = mockk<VaultSyncTrigger>(relaxed = true)

    private val target = CreateSecureNoteUseCase(
        secureItemMutationCoordinator = secureItemMutationCoordinator,
        noteDraftToContentMapper = noteDraftToContentMapper,
        vaultSyncTrigger = vaultSyncTrigger,
    )

    @Test
    fun `invoke when draft is valid and local mutation succeeds then triggers opportunistic sync`() = runBlocking {
        val draft = SecureNoteDraft(
            displayHint = "API key",
            body = "secret body",
        )
        val content = NoteSecureItemContent(body = "secret body")
        val expectedResult = SecureItemMutationResult.Success(sampleCreatedNoteItem())
        every { noteDraftToContentMapper.map(draft) } returns content
        coEvery {
            secureItemMutationCoordinator.create(
                displayHint = "API key",
                content = content,
            )
        } returns expectedResult

        val result = target(draft)

        assertEquals(expectedResult, result)
        verify(exactly = 1) { noteDraftToContentMapper.map(draft) }
        coVerify(exactly = 1) {
            secureItemMutationCoordinator.create(
                displayHint = "API key",
                content = content,
            )
        }
        verify(exactly = 1) { vaultSyncTrigger.onLocalMutationStored(expectedResult.item.logicalItemId) }
        confirmVerified(secureItemMutationCoordinator, noteDraftToContentMapper, vaultSyncTrigger)
    }

    @Test
    fun `invoke when mapper rejects draft then returns validation error without delegating`() = runBlocking {
        val draft = SecureNoteDraft(
            displayHint = "API key",
            body = " ",
        )
        every { noteDraftToContentMapper.map(draft) } throws IllegalArgumentException(
            "body must not be blank.",
        )

        val result = target(draft)

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("body must not be blank."),
            ),
            result,
        )
        verify(exactly = 1) { noteDraftToContentMapper.map(draft) }
        coVerify(exactly = 0) { secureItemMutationCoordinator.create(any(), any()) }
        verify(exactly = 0) { vaultSyncTrigger.onLocalMutationStored(any()) }
        confirmVerified(secureItemMutationCoordinator, noteDraftToContentMapper, vaultSyncTrigger)
    }

    @Test
    fun `invoke when mapper rejects draft without message then returns fallback validation error`() = runBlocking {
        val draft = SecureNoteDraft(
            displayHint = "API key",
            body = "secret body",
        )
        every { noteDraftToContentMapper.map(draft) } throws IllegalArgumentException()

        val result = target(draft)

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Invalid note item."),
            ),
            result,
        )
        verify(exactly = 1) { noteDraftToContentMapper.map(draft) }
        coVerify(exactly = 0) { secureItemMutationCoordinator.create(any(), any()) }
        verify(exactly = 0) { vaultSyncTrigger.onLocalMutationStored(any()) }
        confirmVerified(secureItemMutationCoordinator, noteDraftToContentMapper, vaultSyncTrigger)
    }
}

private fun sampleCreatedNoteItem(): SecureItem {
    val now = Instant.now()
    return SecureItem(
        logicalItemId = UUID.randomUUID(),
        itemType = SecureItemType.NOTE,
        schemaVersion = 1,
        displayHint = "API key",
        payload = byteArrayOf(4, 5, 6),
        payloadVersion = 1,
        createdAt = now,
        updatedAt = now,
        syncState = SecureItemSyncState.PENDING_CREATE,
    )
}
