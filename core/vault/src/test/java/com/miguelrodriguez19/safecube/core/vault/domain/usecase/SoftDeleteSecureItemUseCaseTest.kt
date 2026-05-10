package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SoftDeleteSecureItemUseCase
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.VaultSyncTrigger
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class SoftDeleteSecureItemUseCaseTest {

    private val secureItemMutationCoordinator = mockk<SecureItemMutationCoordinator>()
    private val vaultSyncTrigger = mockk<VaultSyncTrigger>(relaxed = true)

    private val target = SoftDeleteSecureItemUseCase(
        secureItemMutationCoordinator = secureItemMutationCoordinator,
        vaultSyncTrigger = vaultSyncTrigger,
    )

    @Test
    fun `invoke when local delete succeeds then triggers opportunistic sync`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val now = Instant.now()
        val expectedResult = SecureItemMutationResult.Success(
            SecureItem(
                logicalItemId = logicalItemId,
                remoteItemId = UUID.randomUUID(),
                itemType = SecureItemType.NOTE,
                schemaVersion = 1,
                displayHint = "Note",
                payload = byteArrayOf(9, 9, 9),
                payloadVersion = 1,
                createdAt = now,
                updatedAt = now,
                deletedAt = now,
                syncState = SecureItemSyncState.PENDING_DELETE,
            ),
        )
        coEvery { secureItemMutationCoordinator.softDelete(logicalItemId) } returns expectedResult

        val result = target(logicalItemId)

        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { secureItemMutationCoordinator.softDelete(logicalItemId) }
        verify(exactly = 1) { vaultSyncTrigger.onLocalMutationStored(logicalItemId) }
        confirmVerified(secureItemMutationCoordinator, vaultSyncTrigger)
    }

    @Test
    fun `invoke when local delete fails then does not trigger opportunistic sync`() = runBlocking {
        val logicalItemId = UUID.randomUUID()
        val expectedResult = SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        coEvery { secureItemMutationCoordinator.softDelete(logicalItemId) } returns expectedResult

        val result = target(logicalItemId)

        assertEquals(expectedResult, result)
        coVerify(exactly = 1) { secureItemMutationCoordinator.softDelete(logicalItemId) }
        verify(exactly = 0) { vaultSyncTrigger.onLocalMutationStored(any()) }
        confirmVerified(secureItemMutationCoordinator, vaultSyncTrigger)
    }
}
