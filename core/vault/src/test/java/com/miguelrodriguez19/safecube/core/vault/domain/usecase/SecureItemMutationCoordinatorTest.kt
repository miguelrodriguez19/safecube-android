package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeError
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.EncryptedSecureItemPayload
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoError
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemEncryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.CurrentInstantProvider
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemIdGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationCoordinator
import io.mockk.confirmVerified
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecureItemMutationCoordinatorTest {

    private val secureItemRepository = mockk<SecureItemRepository>()
    private val secureItemCryptoService = mockk<SecureItemCryptoService>()
    private val vaultState = MutableStateFlow<VaultState>(VaultState.Locked)
    private val vaultSessionManager = object : VaultSessionManager {
        override val vaultState = this@SecureItemMutationCoordinatorTest.vaultState
        override suspend fun refreshVaultState() = error("Not required in test")
        override fun unlockWithPassphrase(passphrase: String) = error("Not required in test")
        override fun unlockWithRecoveryKey(recoveryKey: ByteArray) = error("Not required in test")
        override fun lock() = error("Not required in test")
        override fun onLogout() = error("Not required in test")
    }
    private val secureItemIdGenerator = mockk<SecureItemIdGenerator>()
    private val currentInstantProvider = mockk<CurrentInstantProvider>()

    private val target = SecureItemMutationCoordinator(
        secureItemRepository = secureItemRepository,
        secureItemCryptoService = secureItemCryptoService,
        vaultSessionManager = vaultSessionManager,
        secureItemIdGenerator = secureItemIdGenerator,
        currentInstantProvider = currentInstantProvider,
    )

    @Test
    fun `create when vault is locked then returns vault locked without persisting`() = runBlocking {
        vaultState.value = VaultState.Locked

        val result = target.create(
            displayHint = "Github",
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked),
            result,
        )
        verify(exactly = 0) { secureItemIdGenerator.generate() }
        verify(exactly = 0) { currentInstantProvider.now() }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        coVerify(exactly = 0) { secureItemRepository.insert(any()) }
        confirmVerified(secureItemIdGenerator, currentInstantProvider, secureItemCryptoService, secureItemRepository)
    }

    @Test
    fun `create when display hint is blank then returns validation error`() = runBlocking {
        vaultState.value = VaultState.Unlocked

        val result = target.create(
            displayHint = "   ",
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("displayHint must not be blank."),
            ),
            result,
        )
        verify(exactly = 0) { secureItemIdGenerator.generate() }
        verify(exactly = 0) { currentInstantProvider.now() }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        coVerify(exactly = 0) { secureItemRepository.insert(any()) }
        confirmVerified(secureItemIdGenerator, currentInstantProvider, secureItemCryptoService, secureItemRepository)
    }

    @Test
    fun `create when vault is unlocked then encrypts and inserts payload version one`() = runBlocking {
        val itemSlot = slot<SecureItem>()
        vaultState.value = VaultState.Unlocked
        every { secureItemIdGenerator.generate() } returns SAMPLE_LOGICAL_ITEM_ID
        every { currentInstantProvider.now() } returns CREATED_AT
        every {
            secureItemCryptoService.encrypt(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                payloadVersion = 1,
                content = validPasswordContent(),
            )
        } returns SecureItemEncryptionResult.Success(
            payload = EncryptedSecureItemPayload(
                itemType = SecureItemType.PASSWORD,
                schemaVersion = 1,
                payload = byteArrayOf(9, 8, 7),
            ),
        )
        coEvery { secureItemRepository.insert(capture(itemSlot)) } returns Unit

        val result = target.create(
            displayHint = " Github ",
            content = validPasswordContent(),
        )

        assertTrue(result is SecureItemMutationResult.Success)
        assertEquals("Github", itemSlot.captured.displayHint)
        assertEquals(1, itemSlot.captured.payloadVersion)
        assertEquals(CREATED_AT, itemSlot.captured.createdAt)
        assertEquals(CREATED_AT, itemSlot.captured.updatedAt)
        assertArrayEquals(byteArrayOf(9, 8, 7), itemSlot.captured.payload)
        verify(exactly = 1) { secureItemIdGenerator.generate() }
        verify(exactly = 1) { currentInstantProvider.now() }
        verify(exactly = 1) { secureItemCryptoService.encrypt(SAMPLE_LOGICAL_ITEM_ID, 1, validPasswordContent()) }
        coVerify(exactly = 1) { secureItemRepository.insert(any()) }
        confirmVerified(secureItemIdGenerator, currentInstantProvider, secureItemCryptoService, secureItemRepository)
    }

    @Test
    fun `create when encryption reports vault locked then returns vault locked`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        every { secureItemIdGenerator.generate() } returns SAMPLE_LOGICAL_ITEM_ID
        every { currentInstantProvider.now() } returns CREATED_AT
        every {
            secureItemCryptoService.encrypt(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                payloadVersion = 1,
                content = validPasswordContent(),
            )
        } returns SecureItemEncryptionResult.Error(SecureItemCryptoError.VaultLocked)

        val result = target.create(
            displayHint = "Github",
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked),
            result,
        )
        verify(exactly = 1) { secureItemIdGenerator.generate() }
        verify(exactly = 1) { currentInstantProvider.now() }
        verify(exactly = 1) { secureItemCryptoService.encrypt(SAMPLE_LOGICAL_ITEM_ID, 1, validPasswordContent()) }
        coVerify(exactly = 0) { secureItemRepository.insert(any()) }
        confirmVerified(secureItemIdGenerator, currentInstantProvider, secureItemCryptoService, secureItemRepository)
    }

    @Test
    fun `create when encryption reports missing account id then returns vault locked`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        every { secureItemIdGenerator.generate() } returns SAMPLE_LOGICAL_ITEM_ID
        every { currentInstantProvider.now() } returns CREATED_AT
        every {
            secureItemCryptoService.encrypt(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                payloadVersion = 1,
                content = validPasswordContent(),
            )
        } returns SecureItemEncryptionResult.Error(SecureItemCryptoError.AccountIdUnavailable)

        val result = target.create(
            displayHint = "Github",
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked),
            result,
        )
        verify(exactly = 1) { secureItemIdGenerator.generate() }
        verify(exactly = 1) { currentInstantProvider.now() }
        verify(exactly = 1) { secureItemCryptoService.encrypt(SAMPLE_LOGICAL_ITEM_ID, 1, validPasswordContent()) }
        coVerify(exactly = 0) { secureItemRepository.insert(any()) }
        confirmVerified(secureItemIdGenerator, currentInstantProvider, secureItemCryptoService, secureItemRepository)
    }

    @Test
    fun `create when encryption fails then returns validation error`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        every { secureItemIdGenerator.generate() } returns SAMPLE_LOGICAL_ITEM_ID
        every { currentInstantProvider.now() } returns CREATED_AT
        every {
            secureItemCryptoService.encrypt(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                payloadVersion = 1,
                content = validPasswordContent(),
            )
        } returns SecureItemEncryptionResult.Error(SecureItemCryptoError.CryptographicFailure)

        val result = target.create(
            displayHint = "Github",
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Unable to encrypt secure item."),
            ),
            result,
        )
        verify(exactly = 1) { secureItemIdGenerator.generate() }
        verify(exactly = 1) { currentInstantProvider.now() }
        verify(exactly = 1) { secureItemCryptoService.encrypt(SAMPLE_LOGICAL_ITEM_ID, 1, validPasswordContent()) }
        coVerify(exactly = 0) { secureItemRepository.insert(any()) }
        confirmVerified(secureItemIdGenerator, currentInstantProvider, secureItemCryptoService, secureItemRepository)
    }

    @Test
    fun `create when encryption content cannot be encoded then returns validation error`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        every { secureItemIdGenerator.generate() } returns SAMPLE_LOGICAL_ITEM_ID
        every { currentInstantProvider.now() } returns CREATED_AT
        every {
            secureItemCryptoService.encrypt(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                payloadVersion = 1,
                content = validPasswordContent(),
            )
        } returns SecureItemEncryptionResult.Error(
            SecureItemCryptoError.ContentDecodingFailed(
                SecureItemContentDecodeError.InvalidPayload("boom"),
            ),
        )

        val result = target.create(
            displayHint = "Github",
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Unable to encrypt secure item."),
            ),
            result,
        )
        verify(exactly = 1) { secureItemIdGenerator.generate() }
        verify(exactly = 1) { currentInstantProvider.now() }
        verify(exactly = 1) { secureItemCryptoService.encrypt(SAMPLE_LOGICAL_ITEM_ID, 1, validPasswordContent()) }
        coVerify(exactly = 0) { secureItemRepository.insert(any()) }
        confirmVerified(secureItemIdGenerator, currentInstantProvider, secureItemCryptoService, secureItemRepository)
    }

    @Test
    fun `update when vault is locked then returns vault locked without reading repository`() = runBlocking {
        vaultState.value = VaultState.Locked

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "Github",
            expectedItemType = SecureItemType.PASSWORD,
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked),
            result,
        )
        coVerify(exactly = 0) { secureItemRepository.getItem(any()) }
        verify(exactly = 0) { secureItemCryptoService.decrypt(any()) }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        coVerify(exactly = 0) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `update when item does not exist then returns item not found`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns null

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "Github",
            expectedItemType = SecureItemType.PASSWORD,
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 0) { secureItemCryptoService.decrypt(any()) }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        coVerify(exactly = 0) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `update when item is already deleted then returns item not found`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        coEvery {
            secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID)
        } returns samplePasswordItem().copy(deletedAt = UPDATED_AT)

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "Github",
            expectedItemType = SecureItemType.PASSWORD,
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 0) { secureItemCryptoService.decrypt(any()) }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        coVerify(exactly = 0) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `update when item type does not match then returns validation error`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns sampleNoteItem()

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "Github",
            expectedItemType = SecureItemType.PASSWORD,
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Secure item type mismatch."),
            ),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 0) { secureItemCryptoService.decrypt(any()) }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        coVerify(exactly = 0) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `update when display hint is blank then returns validation error`() = runBlocking {
        vaultState.value = VaultState.Unlocked
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns samplePasswordItem()

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "   ",
            expectedItemType = SecureItemType.PASSWORD,
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("displayHint must not be blank."),
            ),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 0) { secureItemCryptoService.decrypt(any()) }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        coVerify(exactly = 0) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService)
    }

    @Test
    fun `update when decrypted content is unchanged then keeps payload version and updates metadata only`() = runBlocking {
        val updatedItemSlot = slot<SecureItem>()
        val existingItem = samplePasswordItem()
        vaultState.value = VaultState.Unlocked
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns existingItem
        every { secureItemCryptoService.decrypt(existingItem) } returns SecureItemDecryptionResult.Success(validPasswordContent())
        every { currentInstantProvider.now() } returns UPDATED_AT
        coEvery { secureItemRepository.update(capture(updatedItemSlot)) } returns Unit

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "Github updated",
            expectedItemType = SecureItemType.PASSWORD,
            content = validPasswordContent(),
        )

        assertTrue(result is SecureItemMutationResult.Success)
        assertEquals(1, updatedItemSlot.captured.payloadVersion)
        assertArrayEquals(existingItem.payload, updatedItemSlot.captured.payload)
        assertEquals("Github updated", updatedItemSlot.captured.displayHint)
        assertEquals(UPDATED_AT, updatedItemSlot.captured.updatedAt)
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(existingItem) }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        verify(exactly = 1) { currentInstantProvider.now() }
        coVerify(exactly = 1) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService, currentInstantProvider)
    }

    @Test
    fun `update when decrypted content changes then re encrypts and increments payload version`() = runBlocking {
        val updatedItemSlot = slot<SecureItem>()
        val existingItem = samplePasswordItem()
        val newContent = PasswordSecureItemContent(
            username = "user",
            password = "new-secret",
        )
        vaultState.value = VaultState.Unlocked
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns existingItem
        every { secureItemCryptoService.decrypt(existingItem) } returns SecureItemDecryptionResult.Success(validPasswordContent())
        every { currentInstantProvider.now() } returns UPDATED_AT
        every {
            secureItemCryptoService.encrypt(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                payloadVersion = 2,
                content = newContent,
            )
        } returns SecureItemEncryptionResult.Success(
            payload = EncryptedSecureItemPayload(
                itemType = SecureItemType.PASSWORD,
                schemaVersion = 1,
                payload = byteArrayOf(4, 5, 6),
            ),
        )
        coEvery { secureItemRepository.update(capture(updatedItemSlot)) } returns Unit

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "Github",
            expectedItemType = SecureItemType.PASSWORD,
            content = newContent,
        )

        assertTrue(result is SecureItemMutationResult.Success)
        assertEquals(2, updatedItemSlot.captured.payloadVersion)
        assertArrayEquals(byteArrayOf(4, 5, 6), updatedItemSlot.captured.payload)
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(existingItem) }
        verify(exactly = 1) { secureItemCryptoService.encrypt(SAMPLE_LOGICAL_ITEM_ID, 2, newContent) }
        verify(exactly = 1) { currentInstantProvider.now() }
        coVerify(exactly = 1) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService, currentInstantProvider)
    }

    @Test
    fun `update when re encryption reports vault locked then returns vault locked`() = runBlocking {
        val existingItem = samplePasswordItem()
        val newContent = PasswordSecureItemContent(
            username = "user",
            password = "new-secret",
        )
        vaultState.value = VaultState.Unlocked
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns existingItem
        every { secureItemCryptoService.decrypt(existingItem) } returns SecureItemDecryptionResult.Success(validPasswordContent())
        every { currentInstantProvider.now() } returns UPDATED_AT
        every {
            secureItemCryptoService.encrypt(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                payloadVersion = 2,
                content = newContent,
            )
        } returns SecureItemEncryptionResult.Error(SecureItemCryptoError.AccountIdUnavailable)

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "Github",
            expectedItemType = SecureItemType.PASSWORD,
            content = newContent,
        )

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(existingItem) }
        verify(exactly = 1) { secureItemCryptoService.encrypt(SAMPLE_LOGICAL_ITEM_ID, 2, newContent) }
        verify(exactly = 1) { currentInstantProvider.now() }
        coVerify(exactly = 0) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService, currentInstantProvider)
    }

    @Test
    fun `update when existing payload is corrupted then returns corrupted payload`() = runBlocking {
        val existingItem = samplePasswordItem()
        vaultState.value = VaultState.Unlocked
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns existingItem
        every { secureItemCryptoService.decrypt(existingItem) } returns SecureItemDecryptionResult.Error(
            SecureItemCryptoError.MalformedPayload,
        )

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "Github",
            expectedItemType = SecureItemType.PASSWORD,
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.CorruptedPayload),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(existingItem) }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        verify(exactly = 0) { currentInstantProvider.now() }
        coVerify(exactly = 0) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService, currentInstantProvider)
    }

    @Test
    fun `update when existing payload reports vault locked then returns vault locked`() = runBlocking {
        val existingItem = samplePasswordItem()
        vaultState.value = VaultState.Unlocked
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns existingItem
        every { secureItemCryptoService.decrypt(existingItem) } returns SecureItemDecryptionResult.Error(
            SecureItemCryptoError.VaultLocked,
        )

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "Github",
            expectedItemType = SecureItemType.PASSWORD,
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(existingItem) }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        verify(exactly = 0) { currentInstantProvider.now() }
        coVerify(exactly = 0) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService, currentInstantProvider)
    }

    @Test
    fun `update when existing payload cannot be decoded then returns corrupted payload`() = runBlocking {
        val existingItem = samplePasswordItem()
        vaultState.value = VaultState.Unlocked
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns existingItem
        every { secureItemCryptoService.decrypt(existingItem) } returns SecureItemDecryptionResult.Error(
            SecureItemCryptoError.ContentDecodingFailed(
                SecureItemContentDecodeError.InvalidPayload("boom"),
            ),
        )

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "Github",
            expectedItemType = SecureItemType.PASSWORD,
            content = validPasswordContent(),
        )

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.CorruptedPayload),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(existingItem) }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        verify(exactly = 0) { currentInstantProvider.now() }
        coVerify(exactly = 0) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService, currentInstantProvider)
    }

    @Test
    fun `update when re encryption fails cryptographically then returns validation error`() = runBlocking {
        val existingItem = samplePasswordItem()
        val newContent = PasswordSecureItemContent(
            username = "user",
            password = "new-secret",
        )
        vaultState.value = VaultState.Unlocked
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns existingItem
        every { secureItemCryptoService.decrypt(existingItem) } returns SecureItemDecryptionResult.Success(validPasswordContent())
        every { currentInstantProvider.now() } returns UPDATED_AT
        every {
            secureItemCryptoService.encrypt(
                logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                payloadVersion = 2,
                content = newContent,
            )
        } returns SecureItemEncryptionResult.Error(SecureItemCryptoError.CryptographicFailure)

        val result = target.update(
            logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
            displayHint = "Github",
            expectedItemType = SecureItemType.PASSWORD,
            content = newContent,
        )

        assertEquals(
            SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Unable to encrypt secure item."),
            ),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 1) { secureItemCryptoService.decrypt(existingItem) }
        verify(exactly = 1) { secureItemCryptoService.encrypt(SAMPLE_LOGICAL_ITEM_ID, 2, newContent) }
        verify(exactly = 1) { currentInstantProvider.now() }
        coVerify(exactly = 0) { secureItemRepository.update(any()) }
        confirmVerified(secureItemRepository, secureItemCryptoService, currentInstantProvider)
    }

    @Test
    fun `softDelete when item does not exist then returns item not found`() = runBlocking {
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns null

        val result = target.softDelete(SAMPLE_LOGICAL_ITEM_ID)

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 0) { currentInstantProvider.now() }
        coVerify(exactly = 0) { secureItemRepository.softDelete(any(), any()) }
        confirmVerified(secureItemRepository, currentInstantProvider)
    }

    @Test
    fun `softDelete when item is already deleted then returns item not found`() = runBlocking {
        coEvery {
            secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID)
        } returns samplePasswordItem().copy(deletedAt = UPDATED_AT)

        val result = target.softDelete(SAMPLE_LOGICAL_ITEM_ID)

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 0) { currentInstantProvider.now() }
        coVerify(exactly = 0) { secureItemRepository.softDelete(any(), any()) }
        confirmVerified(secureItemRepository, currentInstantProvider)
    }

    @Test
    fun `softDelete when repository does not update tombstone then returns item not found`() = runBlocking {
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns samplePasswordItem()
        every { currentInstantProvider.now() } returns UPDATED_AT
        coEvery { secureItemRepository.softDelete(SAMPLE_LOGICAL_ITEM_ID, UPDATED_AT) } returns false

        val result = target.softDelete(SAMPLE_LOGICAL_ITEM_ID)

        assertEquals(
            SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound),
            result,
        )
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        verify(exactly = 1) { currentInstantProvider.now() }
        coVerify(exactly = 1) { secureItemRepository.softDelete(SAMPLE_LOGICAL_ITEM_ID, UPDATED_AT) }
        confirmVerified(secureItemRepository, currentInstantProvider)
    }

    @Test
    fun `softDelete when item exists then writes tombstone and returns deleted item`() = runBlocking {
        val existingItem = samplePasswordItem()
        coEvery { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) } returns existingItem
        every { currentInstantProvider.now() } returns UPDATED_AT
        coEvery { secureItemRepository.softDelete(SAMPLE_LOGICAL_ITEM_ID, UPDATED_AT) } returns true

        val result = target.softDelete(SAMPLE_LOGICAL_ITEM_ID)

        assertTrue(result is SecureItemMutationResult.Success)
        result as SecureItemMutationResult.Success
        assertEquals(UPDATED_AT, result.item.updatedAt)
        assertEquals(UPDATED_AT, result.item.deletedAt)
        verify(exactly = 1) { currentInstantProvider.now() }
        coVerify(exactly = 1) { secureItemRepository.getItem(SAMPLE_LOGICAL_ITEM_ID) }
        coVerify(exactly = 1) { secureItemRepository.softDelete(SAMPLE_LOGICAL_ITEM_ID, UPDATED_AT) }
        verify(exactly = 0) { secureItemCryptoService.decrypt(any()) }
        verify(exactly = 0) { secureItemCryptoService.encrypt(any(), any(), any()) }
        confirmVerified(secureItemRepository, currentInstantProvider, secureItemCryptoService)
    }
}

private val SAMPLE_LOGICAL_ITEM_ID: UUID = UUID.fromString("11111111-1111-1111-1111-111111111111")
private val CREATED_AT: Instant = Instant.parse("2026-03-27T08:00:00Z")
private val UPDATED_AT: Instant = Instant.parse("2026-03-27T09:00:00Z")

private fun validPasswordContent(): PasswordSecureItemContent = PasswordSecureItemContent(
    username = "user",
    password = "secret",
)

private fun samplePasswordItem(): SecureItem = SecureItem(
    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
    itemType = SecureItemType.PASSWORD,
    schemaVersion = 1,
    displayHint = "Github",
    payload = byteArrayOf(1, 2, 3),
    payloadVersion = 1,
    createdAt = CREATED_AT,
    updatedAt = CREATED_AT,
)

private fun sampleNoteItem(): SecureItem = SecureItem(
    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
    itemType = SecureItemType.NOTE,
    schemaVersion = 1,
    displayHint = "Server note",
    payload = byteArrayOf(7, 8, 9),
    payloadVersion = 1,
    createdAt = CREATED_AT,
    updatedAt = CREATED_AT,
)
