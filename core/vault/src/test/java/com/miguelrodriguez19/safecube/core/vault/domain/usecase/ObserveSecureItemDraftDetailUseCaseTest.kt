package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeError
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDraftDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoError
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDraftDetailUseCase
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObserveSecureItemDraftDetailUseCaseTest {

    private val secureItemDraftRepository = mockk<SecureItemDraftRepository>()
    private val secureItemCryptoService = mockk<SecureItemCryptoService>()
    private val vaultState = MutableStateFlow<VaultState>(VaultState.Locked)
    private val vaultSessionManager = object : VaultSessionManager {
        override val vaultState = this@ObserveSecureItemDraftDetailUseCaseTest.vaultState
        override fun isUnlocked(): Boolean = error("Not required in test")
        override suspend fun refreshVaultState() = error("Not required in test")
        override fun unlockWithPassphrase(passphrase: String) = error("Not required in test")
        override fun unlockWithRecoveryKey(recoveryKey: ByteArray) = error("Not required in test")
        override fun lock() = error("Not required in test")
        override fun onLogout() = error("Not required in test")
    }

    private val target = ObserveSecureItemDraftDetailUseCase(
        secureItemDraftRepository = secureItemDraftRepository,
        secureItemCryptoService = secureItemCryptoService,
        vaultSessionManager = vaultSessionManager,
    )

    @Test
    fun `invoke when draft does not exist then emits not found`() = runBlocking {
        every { secureItemDraftRepository.observeDraft(SAMPLE_ITEM_ID) } returns flowOf(null)

        val result = target(SAMPLE_ITEM_ID).first()

        assertEquals(ObserveSecureItemDraftDetailResult.NotFound, result)
        verify(exactly = 1) { secureItemDraftRepository.observeDraft(SAMPLE_ITEM_ID) }
        verify(exactly = 0) { secureItemCryptoService.decrypt(any()) }
        confirmVerified(secureItemDraftRepository, secureItemCryptoService)
    }

    @Test
    fun `invoke when vault is locked then emits vault locked without decrypting`() = runBlocking {
        vaultState.value = VaultState.Locked
        every { secureItemDraftRepository.observeDraft(SAMPLE_ITEM_ID) } returns flowOf(sampleDraft())

        val result = target(SAMPLE_ITEM_ID).first()

        assertEquals(
            ObserveSecureItemDraftDetailResult.Error(SecureItemCrudError.VaultLocked),
            result,
        )
        verify(exactly = 1) { secureItemDraftRepository.observeDraft(SAMPLE_ITEM_ID) }
        verify(exactly = 0) { secureItemCryptoService.decrypt(any()) }
        confirmVerified(secureItemDraftRepository, secureItemCryptoService)
    }

    @Test
    fun `invoke when vault is unlocked and draft decrypts then emits draft detail`() = runBlocking {
        val draft = sampleDraft()
        val content = NoteSecureItemContent(body = "draft note")
        vaultState.value = VaultState.Unlocked
        every { secureItemDraftRepository.observeDraft(SAMPLE_ITEM_ID) } returns flowOf(draft)
        every { secureItemCryptoService.decrypt(any()) } returns SecureItemDecryptionResult.Success(content)

        val result = target(SAMPLE_ITEM_ID).first()

        assertTrue(result is ObserveSecureItemDraftDetailResult.Success)
        result as ObserveSecureItemDraftDetailResult.Success
        assertEquals(draft.logicalItemId, result.detail.logicalItemId)
        assertEquals(draft.remoteItemId, result.detail.remoteItemId)
        assertEquals(draft.draftType, result.detail.draftType)
        assertEquals(draft.displayHint, result.detail.displayHint)
        assertEquals(draft.payloadVersion, result.detail.payloadVersion)
        assertEquals(draft.updatedAt, result.detail.updatedAt)
        assertEquals(draft.lastPublishError, result.detail.lastPublishError)
        assertEquals(content, result.detail.content)
        verify(exactly = 1) { secureItemDraftRepository.observeDraft(SAMPLE_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(any()) }
        confirmVerified(secureItemDraftRepository, secureItemCryptoService)
    }

    @Test
    fun `invoke when decrypt returns vault locked then emits vault locked`() = runBlocking {
        assertDecryptErrorMapsToCrudError(
            decryptionError = SecureItemCryptoError.VaultLocked,
            expectedError = SecureItemCrudError.VaultLocked,
        )
    }

    @Test
    fun `invoke when decrypt returns missing account id then emits vault locked`() = runBlocking {
        assertDecryptErrorMapsToCrudError(
            decryptionError = SecureItemCryptoError.AccountIdUnavailable,
            expectedError = SecureItemCrudError.VaultLocked,
        )
    }

    @Test
    fun `invoke when decrypt returns malformed payload then emits corrupted payload`() = runBlocking {
        assertDecryptErrorMapsToCrudError(
            decryptionError = SecureItemCryptoError.MalformedPayload,
            expectedError = SecureItemCrudError.CorruptedPayload,
        )
    }

    @Test
    fun `invoke when decrypt returns cryptographic failure then emits corrupted payload`() = runBlocking {
        assertDecryptErrorMapsToCrudError(
            decryptionError = SecureItemCryptoError.CryptographicFailure,
            expectedError = SecureItemCrudError.CorruptedPayload,
        )
    }

    @Test
    fun `invoke when decrypt returns content decoding failure then emits corrupted payload`() = runBlocking {
        assertDecryptErrorMapsToCrudError(
            decryptionError = SecureItemCryptoError.ContentDecodingFailed(
                SecureItemContentDecodeError.InvalidPayload("boom"),
            ),
            expectedError = SecureItemCrudError.CorruptedPayload,
        )
    }

    private fun assertDecryptErrorMapsToCrudError(
        decryptionError: SecureItemCryptoError,
        expectedError: SecureItemCrudError,
    ) = runBlocking {
        val draft = sampleDraft()
        vaultState.value = VaultState.Unlocked
        every { secureItemDraftRepository.observeDraft(SAMPLE_ITEM_ID) } returns flowOf(draft)
        every { secureItemCryptoService.decrypt(any()) } returns SecureItemDecryptionResult.Error(decryptionError)

        val result = target(SAMPLE_ITEM_ID).first()

        assertEquals(ObserveSecureItemDraftDetailResult.Error(expectedError), result)
        verify(exactly = 1) { secureItemDraftRepository.observeDraft(SAMPLE_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(any()) }
        confirmVerified(secureItemDraftRepository, secureItemCryptoService)
    }
}

private val SAMPLE_ITEM_ID: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
private val SAMPLE_REMOTE_ID: UUID = UUID.fromString("22222222-2222-2222-2222-222222222222")

private fun sampleDraft(): SecureItemSyncDraft = SecureItemSyncDraft(
    logicalItemId = SAMPLE_ITEM_ID,
    remoteItemId = SAMPLE_REMOTE_ID,
    itemType = SecureItemType.NOTE,
    schemaVersion = 1,
    displayHint = "Draft note",
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 2,
    createdAt = Instant.parse("2026-04-01T08:00:00Z"),
    updatedAt = Instant.parse("2026-04-01T09:00:00Z"),
    draftType = SecureItemDraftType.UPDATE,
    basePayloadVersion = 1,
    baseUpdatedAt = Instant.parse("2026-04-01T07:00:00Z"),
    lastPublishError = "Conflict",
)
