package com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem

import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncState
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemCrudError
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud.SecureItemMutationResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoError
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemEncryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SecureItemDraftMutationCoordinator @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
    private val secureItemDraftRepository: SecureItemDraftRepository,
    private val secureItemCryptoService: SecureItemCryptoService,
    private val vaultSessionManager: VaultSessionManager,
    private val secureItemIdGenerator: SecureItemIdGenerator,
    private val secureItemMutationIdGenerator: SecureItemMutationIdGenerator,
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
                val draft = SecureItemSyncDraft(
                    logicalItemId = logicalItemId,
                    itemType = encryptionResult.payload.itemType,
                    schemaVersion = encryptionResult.payload.schemaVersion,
                    displayHint = normalizedDisplayHint,
                    payload = encryptionResult.payload.payload,
                    payloadVersion = INITIAL_PAYLOAD_VERSION,
                    createdAt = createdAt,
                    updatedAt = createdAt,
                    mutationId = secureItemMutationIdGenerator.generate(),
                    draftType = SecureItemDraftType.CREATE,
                    draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
                    baseItemRevision = null,
                )
                secureItemDraftRepository.upsert(draft)
                SecureItemMutationResult.Success(logicalItemId)
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

        val existingDraft = secureItemDraftRepository.getDraft(logicalItemId)
        val officialItem = secureItemRepository.getItem(logicalItemId)
        val currentItemType = existingDraft?.itemType ?: officialItem?.itemType
            ?: return SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        if (currentItemType != expectedItemType) {
            return SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("Secure item type mismatch."),
            )
        }
        if (officialItem?.deletedAt != null) {
            return SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        }

        val normalizedDisplayHint = normalizeDisplayHint(displayHint)
            ?: return SecureItemMutationResult.Error(
                SecureItemCrudError.ValidationError("displayHint must not be blank."),
            )

        val updatedAt = currentInstantProvider.now()
        val draftType = when {
            existingDraft?.draftType == SecureItemDraftType.CREATE -> SecureItemDraftType.CREATE
            officialItem?.remoteItemId == null -> SecureItemDraftType.CREATE
            else -> SecureItemDraftType.UPDATE
        }
        val payloadVersion = maxOf(
            officialItem?.payloadVersion ?: 0,
            existingDraft?.payloadVersion ?: 0,
        ) + 1
        val keepsConflict = existingDraft?.draftSyncStatus == SecureItemDraftSyncStatus.CONFLICT
        val baseItemRevision = if (draftType == SecureItemDraftType.CREATE) {
            null
        } else if (keepsConflict) {
            existingDraft.baseItemRevision
        } else {
            officialItem?.itemRevision ?: existingDraft?.baseItemRevision
        }
        val createdAt = officialItem?.createdAt ?: existingDraft?.createdAt ?: updatedAt

        val updatedDraft = when (
            val encryptionResult = secureItemCryptoService.encrypt(
                logicalItemId = logicalItemId,
                payloadVersion = payloadVersion,
                content = content,
            )
        ) {
            is SecureItemEncryptionResult.Success -> {
                SecureItemSyncDraft(
                    logicalItemId = logicalItemId,
                    remoteItemId = officialItem?.remoteItemId ?: existingDraft?.remoteItemId,
                    itemType = encryptionResult.payload.itemType,
                    schemaVersion = encryptionResult.payload.schemaVersion,
                    displayHint = normalizedDisplayHint,
                    payload = encryptionResult.payload.payload,
                    payloadVersion = payloadVersion,
                    createdAt = createdAt,
                    updatedAt = updatedAt,
                    mutationId = secureItemMutationIdGenerator.generate(),
                    deletedAt = null,
                    lastSyncedAt = officialItem?.lastSyncedAt ?: existingDraft?.lastSyncedAt,
                    draftType = if (existingDraft?.draftType == SecureItemDraftType.DELETE) {
                        SecureItemDraftType.UPDATE
                    } else {
                        draftType
                    },
                    draftSyncStatus = if (keepsConflict) {
                        SecureItemDraftSyncStatus.CONFLICT
                    } else {
                        SecureItemDraftSyncStatus.READY_TO_SYNC
                    },
                    baseItemRevision = baseItemRevision,
                    lastSyncError = existingDraft?.lastSyncError.takeIf { keepsConflict },
                )
            }

            is SecureItemEncryptionResult.Error -> {
                return SecureItemMutationResult.Error(mapEncryptionError(encryptionResult.reason))
            }
        }

        secureItemDraftRepository.upsert(updatedDraft)
        return SecureItemMutationResult.Success(logicalItemId)
    }

    suspend fun softDelete(logicalItemId: UUID): SecureItemMutationResult {
        if (vaultSessionManager.vaultState.value != VaultState.Unlocked) {
            return SecureItemMutationResult.Error(SecureItemCrudError.VaultLocked)
        }

        val existingDraft = secureItemDraftRepository.getDraft(logicalItemId)
        if (existingDraft?.draftType == SecureItemDraftType.CREATE) {
            val deleted = secureItemDraftRepository.delete(logicalItemId)
            return if (deleted) {
                SecureItemMutationResult.Success(logicalItemId)
            } else {
                SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
            }
        }

        val officialItem = secureItemRepository.getItem(logicalItemId)
            ?: return SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        if (officialItem.deletedAt != null) {
            return SecureItemMutationResult.Error(SecureItemCrudError.ItemNotFound)
        }

        val deletedAt = currentInstantProvider.now()
        val deleteDraft = SecureItemSyncDraft(
            logicalItemId = officialItem.logicalItemId,
            remoteItemId = officialItem.remoteItemId,
            itemType = officialItem.itemType,
            schemaVersion = officialItem.schemaVersion,
            displayHint = officialItem.displayHint,
            payload = officialItem.payload,
            payloadVersion = officialItem.payloadVersion,
            createdAt = officialItem.createdAt,
            updatedAt = deletedAt,
            mutationId = secureItemMutationIdGenerator.generate(),
            deletedAt = deletedAt,
            lastSyncedAt = officialItem.lastSyncedAt,
            draftType = SecureItemDraftType.DELETE,
            draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
            baseItemRevision = officialItem.itemRevision,
            lastSyncError = null,
        )
        secureItemDraftRepository.upsert(deleteDraft)
        return SecureItemMutationResult.Success(logicalItemId)
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

    private companion object {
        private const val INITIAL_PAYLOAD_VERSION: Long = 1
    }
}
