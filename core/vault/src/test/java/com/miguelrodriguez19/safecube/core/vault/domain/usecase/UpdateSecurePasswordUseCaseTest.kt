package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecurePasswordDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.UpdateSecurePasswordUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.password.PasswordDraftToContentMapper
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

class UpdateSecurePasswordUseCaseTest {

    private val secureItemMutationCoordinator = mockk<SecureItemMutationCoordinator>()
    private val passwordDraftToContentMapper = mockk<PasswordDraftToContentMapper>()

    private val target = UpdateSecurePasswordUseCase(
        secureItemMutationCoordinator = secureItemMutationCoordinator,
        passwordDraftToContentMapper = passwordDraftToContentMapper,
    )

    @Test
    fun `invoke when draft is valid then maps password content and delegates update`() = runBlocking {
        val logicalItemId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val draft = SecurePasswordDraft(
            displayHint = "Github",
            username = "user",
            password = "secret",
        )
        val content = PasswordSecureItemContent(
            username = "user",
            email = null,
            password = "secret",
            website = null,
            notes = null,
            totp = null,
        )
        val expectedResult = SecureItemMutationResult.Error(
            SecureItemCrudError.ItemNotFound,
        )
        every { passwordDraftToContentMapper.map(draft) } returns content
        coEvery {
            secureItemMutationCoordinator.update(
                logicalItemId = logicalItemId,
                displayHint = "Github",
                expectedItemType = SecureItemType.PASSWORD,
                content = content,
            )
        } returns expectedResult

        val result = target(logicalItemId, draft)

        assertEquals(expectedResult, result)
        verify(exactly = 1) { passwordDraftToContentMapper.map(draft) }
        coVerify(exactly = 1) {
            secureItemMutationCoordinator.update(
                logicalItemId = logicalItemId,
                displayHint = "Github",
                expectedItemType = SecureItemType.PASSWORD,
                content = content,
            )
        }
        confirmVerified(secureItemMutationCoordinator, passwordDraftToContentMapper)
    }

    @Test
    fun `invoke when mapper rejects draft then returns validation error without delegating`() = runBlocking {
        val logicalItemId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val draft = SecurePasswordDraft(
            displayHint = "Github",
            password = " ",
        )
        every { passwordDraftToContentMapper.map(draft) } throws IllegalArgumentException(
            "password must not be blank.",
        )

        val result = target(
            logicalItemId = logicalItemId,
            draft = draft,
        )

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("password must not be blank."),
            ),
            result,
        )
        verify(exactly = 1) { passwordDraftToContentMapper.map(draft) }
        coVerify(exactly = 0) { secureItemMutationCoordinator.update(any(), any(), any(), any()) }
        confirmVerified(secureItemMutationCoordinator, passwordDraftToContentMapper)
    }

    @Test
    fun `invoke when mapper rejects draft without message then returns fallback validation error`() = runBlocking {
        val logicalItemId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val draft = SecurePasswordDraft(
            displayHint = "Github",
            password = "secret",
        )
        every { passwordDraftToContentMapper.map(draft) } throws IllegalArgumentException()

        val result = target(
            logicalItemId = logicalItemId,
            draft = draft,
        )

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Invalid password item."),
            ),
            result,
        )
        verify(exactly = 1) { passwordDraftToContentMapper.map(draft) }
        coVerify(exactly = 0) { secureItemMutationCoordinator.update(any(), any(), any(), any()) }
        confirmVerified(secureItemMutationCoordinator, passwordDraftToContentMapper)
    }
}
