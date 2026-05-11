package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoError
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemEncryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SecureItemMutationCoordinator @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
    private val secureItemCryptoService: SecureItemCryptoService,
    private val vaultSessionManager: VaultSessionManager,
    private val secureItemIdGenerator: SecureItemIdGenerator,
    private val currentInstantProvider: CurrentInstantProvider,
) {
    suspend fun create(
        displayHint: String,
        content: SecureItemContent,
    ): SecureItemMutationResult {
        if (vaultSessionManager.vaultState.value != VaultState.Unlocked) {
            return SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked)
        }

        val normalizedDisplayHint = normalizeDisplayHint(displayHint)
            ?: return SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("displayHint must not be blank."),
            )
        val logicalItemId = secureItemIdGenerator.generate()
        val createdAt = currentInstantProvider.now()

        return when (
            val encryptionResult = secureItemCryptoService.encrypt(
                logicalItemId = logicalItemId,
                payloadVersion = INITIAL_PAYLOAD_VERSION,
                content = content,
            )
        ) {
            is SecureItemEncryptionResult.Success -> {
                val item = SecureItem(
                    logicalItemId = logicalItemId,
                    itemType = encryptionResult.payload.itemType,
                    schemaVersion = encryptionResult.payload.schemaVersion,
                    displayHint = normalizedDisplayHint,
                    payload = encryptionResult.payload.payload,
                    payloadVersion = INITIAL_PAYLOAD_VERSION,
                    createdAt = createdAt,
                    updatedAt = createdAt,
                    syncState = SecureItemSyncState.PENDING_CREATE,
                )
                secureItemRepository.insert(item)
                SecureItemMutationResult.Success(item)
            }

            is SecureItemEncryptionResult.Error -> {
                SecureItemMutationResult.Error(mapEncryptionError(encryptionResult.reason))
            }
        }
    }

    suspend fun update(
        logicalItemId: UUID,
        displayHint: String,
        expectedItemType: SecureItemType,
        content: SecureItemContent,
    ): SecureItemMutationResult {
        if (vaultSessionManager.vaultState.value != VaultState.Unlocked) {
            return SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked)
        }

        val existingItem = secureItemRepository.getItem(logicalItemId)
            ?: return SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        if (existingItem.deletedAt != null) {
            return SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        }
        if (existingItem.itemType != expectedItemType) {
            return SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Secure item type mismatch."),
            )
        }

        val normalizedDisplayHint = normalizeDisplayHint(displayHint)
            ?: return SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("displayHint must not be blank."),
            )

        when (val decryptionResult = secureItemCryptoService.decrypt(existingItem)) {
            is SecureItemDecryptionResult.Success -> decryptionResult.content
            is SecureItemDecryptionResult.Error -> {
                return SecureItemMutationResult.Error(mapDecryptionError(decryptionResult.reason))
            }
        }

        val updatedAt = currentInstantProvider.now()
        val updatedItem = when (
            val encryptionResult = secureItemCryptoService.encrypt(
                logicalItemId = logicalItemId,
                payloadVersion = existingItem.payloadVersion + 1,
                content = content,
            )
        ) {
            is SecureItemEncryptionResult.Success -> {
                existingItem.copy(
                    itemType = encryptionResult.payload.itemType,
                    schemaVersion = encryptionResult.payload.schemaVersion,
                    displayHint = normalizedDisplayHint,
                    payload = encryptionResult.payload.payload,
                    payloadVersion = existingItem.payloadVersion + 1,
                    updatedAt = updatedAt,
                    syncState = existingItem.pendingSyncStateForMutation(),
                    lastSyncError = null,
                )
            }

            is SecureItemEncryptionResult.Error -> {
                return SecureItemMutationResult.Error(mapEncryptionError(encryptionResult.reason))
            }
        }

        secureItemRepository.update(updatedItem)
        return SecureItemMutationResult.Success(updatedItem)
    }

    suspend fun softDelete(logicalItemId: UUID): SecureItemMutationResult {
        val existingItem = secureItemRepository.getItem(logicalItemId)
            ?: return SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        if (existingItem.deletedAt != null) {
            return SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        }

        val deletedAt = currentInstantProvider.now()
        val softDeleted = secureItemRepository.softDelete(
            logicalItemId = logicalItemId,
            deletedAt = deletedAt,
        )
        if (!softDeleted) {
            return SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        }

        return SecureItemMutationResult.Success(
            existingItem.copy(
                updatedAt = deletedAt,
                deletedAt = deletedAt,
                syncState = SecureItemSyncState.PENDING_DELETE,
                lastSyncError = null,
            ),
        )
    }

    private fun normalizeDisplayHint(displayHint: String): String? = displayHint.trim().takeIf(String::isNotEmpty)

    private fun mapEncryptionError(error: SecureItemCryptoError): SecureItemCrudError = when (error) {
        SecureItemCryptoError.VaultLocked,
        SecureItemCryptoError.AccountIdUnavailable,
        -> SecureItemCrudError.VaultLocked

        SecureItemCryptoError.MalformedPayload,
        SecureItemCryptoError.CryptographicFailure,
        is SecureItemCryptoError.ContentDecodingFailed,
        -> SecureItemCrudError.ValidationError("Unable to encrypt secure item.")
    }

    private fun mapDecryptionError(error: SecureItemCryptoError): SecureItemCrudError = when (error) {
        SecureItemCryptoError.VaultLocked,
        SecureItemCryptoError.AccountIdUnavailable,
        -> SecureItemCrudError.VaultLocked

        SecureItemCryptoError.MalformedPayload,
        SecureItemCryptoError.CryptographicFailure,
        is SecureItemCryptoError.ContentDecodingFailed,
        -> SecureItemCrudError.CorruptedPayload
    }

    private companion object {
        private const val INITIAL_PAYLOAD_VERSION: Long = 1
    }
}

private fun SecureItem.pendingSyncStateForMutation(): SecureItemSyncState =
    if (remoteItemId == null) {
        SecureItemSyncState.PENDING_CREATE
    } else {
        SecureItemSyncState.PENDING_UPDATE
    }
