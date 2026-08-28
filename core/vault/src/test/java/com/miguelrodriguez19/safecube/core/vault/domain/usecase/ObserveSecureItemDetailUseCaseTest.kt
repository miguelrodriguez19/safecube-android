package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeError
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.ObserveSecureItemDetailResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoError
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.QuickUnlockPromptMode
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultLockReason
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.ObserveSecureItemDetailUseCase
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

class ObserveSecureItemDetailUseCaseTest {

    private val secureItemRepository = mockk<SecureItemRepository>()
    private val secureItemCryptoService = mockk<SecureItemCryptoService>()
    private val vaultState = MutableStateFlow<VaultState>(VaultState.Locked)
    private val vaultSessionManager = object : VaultSessionManager {
        override val vaultState = this@ObserveSecureItemDetailUseCaseTest.vaultState
        override fun quickUnlockPromptMode() = QuickUnlockPromptMode.AutomaticOnUnlockEntry
        override fun requestQuickUnlockEnrollmentAfterPassphrase() = false
        override fun consumeQuickUnlockEnrollmentAfterPassphrase() = false
        override fun clearPendingQuickUnlockEnrollment() = Unit
        override fun isUnlocked(): Boolean = error("Not required in test")
        override suspend fun refreshVaultState() = error("Not required in test")
        override fun unlockWithPassphrase(passphrase: String) = error("Not required in test")
        override fun unlockWithRecoveryKey(recoveryKey: ByteArray) = error("Not required in test")
        override fun lock() = error("Not required in test")
        override fun lock(promptMode: QuickUnlockPromptMode) = error("Not required in test")
        override fun lock(promptMode: QuickUnlockPromptMode, reason: VaultLockReason) =
            error("Not required in test")
        override fun consumeLockReason(): VaultLockReason? = null
    }

    private val target = ObserveSecureItemDetailUseCase(
        secureItemRepository = secureItemRepository,
        secureItemCryptoService = secureItemCryptoService,
        vaultSessionManager = vaultSessionManager,
    )

    @Test
    fun `invoke when item does not exist then emits item not found`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        every { secureItemRepository.observeItem(SAMPLE_ITEM_ID) } returns flowOf(null)

        val result = target(SAMPLE_ITEM_ID).first()

        assertEquals(
            ObserveSecureItemDetailResult.Error(SecureItemCrudError.ItemNotFound),
            result,
        )
        verify(exactly = 1) { secureItemRepository.observeItem(SAMPLE_ITEM_ID) }
        verify(exactly = 0) { secureItemCryptoService.decrypt(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `invoke when vault is locked then emits vault locked without decrypting`() = runBlocking {
        vaultState.value = VaultState.Locked
        every { secureItemRepository.observeItem(SAMPLE_ITEM_ID) } returns flowOf(sampleSecureItem())

        val result = target(SAMPLE_ITEM_ID).first()

        assertEquals(
            ObserveSecureItemDetailResult.Error(SecureItemCrudError.VaultLocked),
            result,
        )
        verify(exactly = 1) { secureItemRepository.observeItem(SAMPLE_ITEM_ID) }
        verify(exactly = 0) { secureItemCryptoService.decrypt(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `invoke when item is soft deleted then emits item not found without decrypting`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        every { secureItemRepository.observeItem(SAMPLE_ITEM_ID) } returns flowOf(
            sampleSecureItem().copy(deletedAt = Instant.parse("2026-03-27T10:00:00Z")),
        )

        val result = target(SAMPLE_ITEM_ID).first()

        assertEquals(
            ObserveSecureItemDetailResult.Error(SecureItemCrudError.ItemNotFound),
            result,
        )
        verify(exactly = 1) { secureItemRepository.observeItem(SAMPLE_ITEM_ID) }
        verify(exactly = 0) { secureItemCryptoService.decrypt(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `invoke when vault is unlocked and payload decrypts then emits secure item detail`() = runBlocking {
        val item = sampleSecureItem()
        val content = NoteSecureItemContent(body = "secret note")
        vaultState.value = VaultState.Unlocked
        every { secureItemRepository.observeItem(SAMPLE_ITEM_ID) } returns flowOf(item)
        every { secureItemCryptoService.decrypt(item) } returns SecureItemDecryptionResult.Success(content)

        val result = target(SAMPLE_ITEM_ID).first()

        assertTrue(result is ObserveSecureItemDetailResult.Success)
        result as ObserveSecureItemDetailResult.Success
        assertEquals(item.logicalItemId, result.detail.logicalItemId)
        assertEquals(item.displayHint, result.detail.displayHint)
        assertEquals(content, result.detail.content)
        verify(exactly = 1) { secureItemRepository.observeItem(SAMPLE_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(item) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `invoke when payload cannot be decrypted then emits corrupted payload`() = runBlocking {
        val item = sampleSecureItem()
        vaultState.value = VaultState.Unlocked
        every { secureItemRepository.observeItem(SAMPLE_ITEM_ID) } returns flowOf(item)
        every { secureItemCryptoService.decrypt(item) } returns SecureItemDecryptionResult.Error(
            SecureItemCryptoError.MalformedPayload,
        )

        val result = target(SAMPLE_ITEM_ID).first()

        assertEquals(
            ObserveSecureItemDetailResult.Error(SecureItemCrudError.CorruptedPayload),
            result,
        )
        verify(exactly = 1) { secureItemRepository.observeItem(SAMPLE_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(item) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `invoke when decrypt returns vault locked then emits vault locked`() = runBlocking {
        val item = sampleSecureItem()
        vaultState.value = VaultState.Unlocked
        every { secureItemRepository.observeItem(SAMPLE_ITEM_ID) } returns flowOf(item)
        every { secureItemCryptoService.decrypt(item) } returns SecureItemDecryptionResult.Error(
            SecureItemCryptoError.VaultLocked,
        )

        val result = target(SAMPLE_ITEM_ID).first()

        assertEquals(
            ObserveSecureItemDetailResult.Error(SecureItemCrudError.VaultLocked),
            result,
        )
        verify(exactly = 1) { secureItemRepository.observeItem(SAMPLE_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(item) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `invoke when decrypt returns missing account id then emits vault locked`() = runBlocking {
        val item = sampleSecureItem()
        vaultState.value = VaultState.Unlocked
        every { secureItemRepository.observeItem(SAMPLE_ITEM_ID) } returns flowOf(item)
        every { secureItemCryptoService.decrypt(item) } returns SecureItemDecryptionResult.Error(
            SecureItemCryptoError.AccountIdUnavailable,
        )

        val result = target(SAMPLE_ITEM_ID).first()

        assertEquals(
            ObserveSecureItemDetailResult.Error(SecureItemCrudError.VaultLocked),
            result,
        )
        verify(exactly = 1) { secureItemRepository.observeItem(SAMPLE_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(item) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `invoke when payload fails cryptographic verification then emits corrupted payload`() = runBlocking {
        val item = sampleSecureItem()
        vaultState.value = VaultState.Unlocked
        every { secureItemRepository.observeItem(SAMPLE_ITEM_ID) } returns flowOf(item)
        every { secureItemCryptoService.decrypt(item) } returns SecureItemDecryptionResult.Error(
            SecureItemCryptoError.CryptographicFailure,
        )

        val result = target(SAMPLE_ITEM_ID).first()

        assertEquals(
            ObserveSecureItemDetailResult.Error(SecureItemCrudError.CorruptedPayload),
            result,
        )
        verify(exactly = 1) { secureItemRepository.observeItem(SAMPLE_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(item) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `invoke when payload content cannot be decoded then emits corrupted payload`() = runBlocking {
        val item = sampleSecureItem()
        vaultState.value = VaultState.Unlocked
        every { secureItemRepository.observeItem(SAMPLE_ITEM_ID) } returns flowOf(item)
        every { secureItemCryptoService.decrypt(item) } returns SecureItemDecryptionResult.Error(
            SecureItemCryptoError.ContentDecodingFailed(
                SecureItemContentDecodeError.InvalidPayload,
            ),
        )

        val result = target(SAMPLE_ITEM_ID).first()

        assertEquals(
            ObserveSecureItemDetailResult.Error(SecureItemCrudError.CorruptedPayload),
            result,
        )
        verify(exactly = 1) { secureItemRepository.observeItem(SAMPLE_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(item) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }
}

private val SAMPLE_ITEM_ID: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")

private fun sampleSecureItem(): SecureItem = SecureItem(
    logicalItemId = SAMPLE_ITEM_ID,
    itemType = SecureItemType.NOTE,
    schemaVersion = 1,
    displayHint = "Server note",
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 1,
    createdAt = Instant.parse("2026-03-27T08:00:00Z"),
    updatedAt = Instant.parse("2026-03-27T09:00:00Z"),
)
