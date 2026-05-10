package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.CreateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.PasswordDraftToContentMapper
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

class CreateSecurePasswordUseCaseTest {

    private val secureItemMutationCoordinator = mockk<SecureItemMutationCoordinator>()
    private val passwordDraftToContentMapper = mockk<PasswordDraftToContentMapper>()
    private val vaultSyncTrigger = mockk<VaultSyncTrigger>(relaxed = true)

    private val target = CreateSecurePasswordUseCase(
        secureItemMutationCoordinator = secureItemMutationCoordinator,
        passwordDraftToContentMapper = passwordDraftToContentMapper,
        vaultSyncTrigger = vaultSyncTrigger,
    )

    @Test
    fun `invoke when draft is valid and local mutation succeeds then triggers opportunistic sync`() = runBlocking {
        val draft = SecurePasswordDraft(
            displayHint = "Github",
            username = "user",
            password = "secret",
        )
        val content = PasswordSecureItemContent(
            username = "user",
            email = null,
            password = "secret",
        )
        val expectedResult = SecureItemMutationResult.Success(sampleSecureItem())
        every { passwordDraftToContentMapper.map(draft) } returns content
        coEvery {
            secureItemMutationCoordinator.create(
                displayHint = "Github",
                content = content,
            )
        } returns expectedResult

        val result = target(draft)

        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { secureItemMutationCoordinator.create("Github", content) }
        verify(exactly = 1) { passwordDraftToContentMapper.map(draft) }
        verify(exactly = 1) { vaultSyncTrigger.onLocalMutationStored(expectedResult.item.logicalItemId) }
        confirmVerified(secureItemMutationCoordinator, passwordDraftToContentMapper, vaultSyncTrigger)
    }

    @Test
    fun `invoke when mapper rejects draft then returns validation error without delegating`() = runBlocking {
        val draft = SecurePasswordDraft(
            displayHint = "Github",
            password = " ",
        )
        every { passwordDraftToContentMapper.map(draft) } throws IllegalArgumentException(
            "password must not be blank.",
        )

        val result = target(draft)

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("password must not be blank."),
            ),
            result,
        )
        verify(exactly = 1) { passwordDraftToContentMapper.map(draft) }
        coVerify(exactly = 0) { secureItemMutationCoordinator.create(any(), any()) }
        verify(exactly = 0) { vaultSyncTrigger.onLocalMutationStored(any()) }
        confirmVerified(secureItemMutationCoordinator, passwordDraftToContentMapper, vaultSyncTrigger)
    }

    @Test
    fun `invoke when mapper rejects draft without message then returns fallback validation error`() = runBlocking {
        val draft = SecurePasswordDraft(
            displayHint = "Github",
            password = "secret",
        )
        every { passwordDraftToContentMapper.map(draft) } throws IllegalArgumentException()

        val result = target(draft)

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Invalid password item."),
            ),
            result,
        )
        verify(exactly = 1) { passwordDraftToContentMapper.map(draft) }
        coVerify(exactly = 0) { secureItemMutationCoordinator.create(any(), any()) }
        verify(exactly = 0) { vaultSyncTrigger.onLocalMutationStored(any()) }
        confirmVerified(secureItemMutationCoordinator, passwordDraftToContentMapper, vaultSyncTrigger)
    }
}

private fun sampleSecureItem(): SecureItem {
    val now = Instant.now()
    return SecureItem(
        logicalItemId = UUID.randomUUID(),
        itemType = SecureItemType.PASSWORD,
        schemaVersion = 1,
        displayHint = "Github",
        payload = byteArrayOf(1, 2, 3),
        payloadVersion = 1,
        createdAt = now,
        updatedAt = now,
        syncState = SecureItemSyncState.PENDING_CREATE,
    )
}
