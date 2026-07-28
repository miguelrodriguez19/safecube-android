package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDraftDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDraftDetailUseCase
import com.miguelrodriguez19.safecube.core.vault.test.FakeVaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.test.testSecureItemDraft
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveSecureItemDraftDetailUseCaseTest {
    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()
    private val secureItemRepository = mockk<SecureItemRepository>()
    private val secureItemCryptoService = mockk<SecureItemCryptoService>()
    private val vaultSessionManager = FakeVaultSessionManager()

    @Test
    fun `invoke when vault is unlocked then exposes draft status and decrypted content`() = runBlocking {
        val draft = testSecureItemDraft(lastSyncError = "Conflict")
        every { secureItemDraftRepository.observeDraft(draft.logicalItemId) } returns flowOf(draft)
        every { secureItemRepository.observeItem(draft.logicalItemId) } returns flowOf(null)
        every { secureItemCryptoService.decrypt(any()) } returns SecureItemDecryptionResult.Success(
            NoteSecureItemContent(body = "hello"),
        )

        val result = ObserveSecureItemDraftDetailUseCase(
            secureItemDraftRepository = secureItemDraftRepository,
            secureItemRepository = secureItemRepository,
            secureItemCryptoService = secureItemCryptoService,
            vaultSessionManager = vaultSessionManager,
        ).invoke(draft.logicalItemId).first()

        result as ObserveSecureItemDraftDetailResult.Success
        assertEquals(draft.draftSyncStatus, result.detail.draftSyncStatus)
        assertEquals(draft.lastSyncError, result.detail.lastSyncError)
        assertEquals("hello", (result.detail.content as NoteSecureItemContent).body)
    }

    @Test
    fun `invoke when vault is locked then returns vault locked error`() = runBlocking {
        val draft = testSecureItemDraft()
        vaultSessionManager.setState(VaultState.Locked)
        every { secureItemDraftRepository.observeDraft(draft.logicalItemId) } returns flowOf(draft)
        every { secureItemRepository.observeItem(draft.logicalItemId) } returns flowOf(null)

        val result = ObserveSecureItemDraftDetailUseCase(
            secureItemDraftRepository = secureItemDraftRepository,
            secureItemRepository = secureItemRepository,
            secureItemCryptoService = secureItemCryptoService,
            vaultSessionManager = vaultSessionManager,
        ).invoke(draft.logicalItemId).first()

        assertEquals(
            ObserveSecureItemDraftDetailResult.Error(SecureItemCrudError.VaultLocked),
            result,
        )
    }
}
