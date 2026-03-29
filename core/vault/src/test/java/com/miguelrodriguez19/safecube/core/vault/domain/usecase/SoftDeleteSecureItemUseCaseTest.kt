package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SoftDeleteSecureItemUseCaseTest {

    private val secureItemMutationCoordinator = mockk<SecureItemMutationCoordinator>()

    private val target = SoftDeleteSecureItemUseCase(
        secureItemMutationCoordinator = secureItemMutationCoordinator,
    )

    @Test
    fun `invoke when logical item id is provided then delegates soft delete`() = runBlocking {
        val logicalItemId = UUID.fromString("11111111-1111-1111-1111-111111111111")
        val expectedResult = SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        coEvery { secureItemMutationCoordinator.softDelete(logicalItemId) } returns expectedResult

        val result = target(logicalItemId)

        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { secureItemMutationCoordinator.softDelete(logicalItemId) }
        confirmVerified(secureItemMutationCoordinator)
    }
}
