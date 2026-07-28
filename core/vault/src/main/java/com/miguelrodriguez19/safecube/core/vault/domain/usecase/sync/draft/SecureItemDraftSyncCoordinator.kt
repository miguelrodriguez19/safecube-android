package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteCreateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteDeleteSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.RemoteUpdateSecureItemResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemCryptoService
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemDecryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemEncryptionResult
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.CurrentInstantProvider
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemIdGenerator
import com.miguelrodriguez19.safecube.core.vault.domain.usecase.secureitem.SecureItemMutationIdGenerator
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureItemDraftSyncCoordinator @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
    private val secureItemDraftRepository: SecureItemDraftRepository,
    private val secureItemCryptoService: SecureItemCryptoService,
    private val currentInstantProvider: CurrentInstantProvider,
    private val secureItemMutationIdGenerator: SecureItemMutationIdGenerator,
    private val secureItemIdGenerator: SecureItemIdGenerator,
) {
    suspend fun replaceOfficialWithRemoteAndConflictedDraft(
        draft: SecureItemSyncDraft,
        remoteItem: SecureItem,
        lastSyncedAt: Instant,
        lastSyncError: String,
    ): Boolean = runCatching {
        secureItemRepository.replaceOfficialWithConflictedDraft(
            item = remoteItem,
            draft = draft.copy(
                remoteItemId = remoteItem.remoteItemId,
                draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
                lastSyncError = lastSyncError,
            ),
            lastSyncedAt = lastSyncedAt,
        )
    }.getOrDefault(false)

    suspend fun markDraftConflict(
        logicalItemId: UUID,
        lastSyncError: String,
    ): Boolean = secureItemDraftRepository.updateStatus(
        logicalItemId = logicalItemId,
        draftSyncStatus = SecureItemDraftSyncStatus.CONFLICT,
        lastSyncError = lastSyncError,
    )

    suspend fun officializeCreatedDraft(
        draft: SecureItemSyncDraft,
        result: RemoteCreateSecureItemResult,
    ): Boolean {
        if (
            result.mutationId != draft.mutationId ||
            result.payloadVersion != draft.payloadVersion
        ) {
            return false
        }
        return officialize(
            draft = draft,
            remoteItemId = result.itemId,
            payloadVersion = result.payloadVersion,
            itemRevision = result.itemRevision,
            changeSequence = result.changeSequence,
            updatedAt = result.updatedAt,
            deletedAt = null,
        )
    }

    suspend fun officializeUpdatedDraft(
        draft: SecureItemSyncDraft,
        result: RemoteUpdateSecureItemResult,
    ): Boolean {
        if (
            result.itemId != draft.remoteItemId ||
            result.mutationId != draft.mutationId ||
            result.payloadVersion != draft.payloadVersion
        ) {
            return false
        }
        return officialize(
            draft = draft,
            remoteItemId = result.itemId,
            payloadVersion = result.payloadVersion,
            itemRevision = result.itemRevision,
            changeSequence = result.changeSequence,
            updatedAt = result.updatedAt,
            deletedAt = null,
        )
    }

    suspend fun officializeDeletedDraft(
        draft: SecureItemSyncDraft,
        result: RemoteDeleteSecureItemResult,
    ): Boolean {
        if (
            result.itemId != draft.remoteItemId ||
            result.mutationId != draft.mutationId ||
            result.payloadVersion != draft.payloadVersion
        ) {
            return false
        }
        return officialize(
            draft = draft,
            remoteItemId = result.itemId,
            payloadVersion = result.payloadVersion,
            itemRevision = result.itemRevision,
            changeSequence = result.changeSequence,
            updatedAt = result.deletedAt,
            deletedAt = result.deletedAt,
        )
    }

    suspend fun prepareDraftForSync(logicalItemId: UUID): Boolean {
        val draft = secureItemDraftRepository.getDraft(logicalItemId) ?: return false
        return when (draft.draftType) {
            SecureItemDraftType.CREATE -> false
            SecureItemDraftType.UPDATE -> prepareUpdateDraftForSync(draft)
            SecureItemDraftType.DELETE -> prepareDeleteDraftForSync(draft)
        }
    }

    suspend fun discardDraft(logicalItemId: UUID): Boolean = runCatching {
        secureItemDraftRepository.delete(logicalItemId)
    }.getOrDefault(false)

    suspend fun resolveAlreadyDeletedDraft(draft: SecureItemSyncDraft): Boolean {
        val officialItem = secureItemRepository.getItem(draft.logicalItemId) ?: return false
        return officialItem.deletedAt != null && secureItemDraftRepository.delete(draft.logicalItemId)
    }

    private suspend fun officialize(
        draft: SecureItemSyncDraft,
        remoteItemId: UUID,
        payloadVersion: Long,
        itemRevision: Long,
        changeSequence: Long,
        updatedAt: Instant,
        deletedAt: Instant?,
    ): Boolean = runCatching {
        val candidate = draft.toOfficialItem(
            remoteItemId = remoteItemId,
            payloadVersion = payloadVersion,
            itemRevision = itemRevision,
            changeSequence = changeSequence,
            updatedAt = updatedAt,
            deletedAt = deletedAt,
        )
        if (secureItemCryptoService.decrypt(candidate) !is SecureItemDecryptionResult.Success) {
            return false
        }
        secureItemRepository.officializeDraft(
            item = candidate,
            lastSyncedAt = updatedAt,
        )
    }.getOrDefault(false)

    private suspend fun prepareUpdateDraftForSync(draft: SecureItemSyncDraft): Boolean {
        val officialItem = secureItemRepository.getItem(draft.logicalItemId) ?: return false
        if (officialItem.deletedAt != null) {
            return prepareUpdateAsNew(draft, officialItem)
        }
        if (officialItem.remoteItemId == null) {
            return false
        }

        val draftCandidate = draft.toOfficialItem(
            remoteItemId = draft.remoteItemId,
            payloadVersion = draft.payloadVersion,
            itemRevision = draft.baseItemRevision ?: officialItem.itemRevision,
            changeSequence = officialItem.changeSequence,
            updatedAt = draft.updatedAt,
            deletedAt = draft.deletedAt,
        )
        val content = when (val decryptionResult = secureItemCryptoService.decrypt(draftCandidate)) {
            is SecureItemDecryptionResult.Success -> decryptionResult.content
            is SecureItemDecryptionResult.Error -> return false
        }
        val nextPayloadVersion = maxOf(officialItem.payloadVersion, draft.payloadVersion) + 1
        val encryptedPayload = when (
            val encryptionResult = secureItemCryptoService.encrypt(
                logicalItemId = draft.logicalItemId,
                payloadVersion = nextPayloadVersion,
                content = content,
            )
        ) {
            is SecureItemEncryptionResult.Success -> encryptionResult.payload
            is SecureItemEncryptionResult.Error -> return false
        }

        secureItemDraftRepository.upsert(
            draft.copy(
                remoteItemId = officialItem.remoteItemId,
                itemType = encryptedPayload.itemType,
                schemaVersion = encryptedPayload.schemaVersion,
                payload = encryptedPayload.payload,
                payloadVersion = nextPayloadVersion,
                updatedAt = currentInstantProvider.now(),
                mutationId = secureItemMutationIdGenerator.generate(),
                deletedAt = null,
                draftType = SecureItemDraftType.UPDATE,
                draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
                baseItemRevision = officialItem.itemRevision,
                lastSyncError = null,
            ),
        )
        return true
    }

    private suspend fun prepareUpdateAsNew(
        draft: SecureItemSyncDraft,
        deletedOfficial: SecureItem,
    ): Boolean {
        val draftCandidate = draft.toOfficialItem(
            remoteItemId = draft.remoteItemId,
            payloadVersion = draft.payloadVersion,
            itemRevision = draft.baseItemRevision ?: deletedOfficial.itemRevision,
            changeSequence = deletedOfficial.changeSequence,
            updatedAt = draft.updatedAt,
            deletedAt = null,
        )
        val content = when (val decryptionResult = secureItemCryptoService.decrypt(draftCandidate)) {
            is SecureItemDecryptionResult.Success -> decryptionResult.content
            is SecureItemDecryptionResult.Error -> return false
        }
        val newLogicalItemId = secureItemIdGenerator.generate()
        val encryptedPayload = when (
            val encryptionResult = secureItemCryptoService.encrypt(
                logicalItemId = newLogicalItemId,
                payloadVersion = INITIAL_PAYLOAD_VERSION,
                content = content,
            )
        ) {
            is SecureItemEncryptionResult.Success -> encryptionResult.payload
            is SecureItemEncryptionResult.Error -> return false
        }
        val now = currentInstantProvider.now()
        return secureItemDraftRepository.replace(
            logicalItemId = draft.logicalItemId,
            replacement = draft.copy(
                logicalItemId = newLogicalItemId,
                remoteItemId = null,
                itemType = encryptedPayload.itemType,
                schemaVersion = encryptedPayload.schemaVersion,
                payload = encryptedPayload.payload,
                payloadVersion = INITIAL_PAYLOAD_VERSION,
                createdAt = now,
                updatedAt = now,
                mutationId = secureItemMutationIdGenerator.generate(),
                deletedAt = null,
                lastSyncedAt = null,
                draftType = SecureItemDraftType.CREATE,
                draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
                baseItemRevision = null,
                lastSyncError = null,
            ),
        )
    }

    private suspend fun prepareDeleteDraftForSync(draft: SecureItemSyncDraft): Boolean {
        val officialItem = secureItemRepository.getItem(draft.logicalItemId) ?: return false
        if (officialItem.deletedAt != null || officialItem.remoteItemId == null) {
            return false
        }
        val now = currentInstantProvider.now()

        secureItemDraftRepository.upsert(
            draft.copy(
                remoteItemId = officialItem.remoteItemId,
                updatedAt = now,
                mutationId = secureItemMutationIdGenerator.generate(),
                deletedAt = now,
                draftType = SecureItemDraftType.DELETE,
                draftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
                baseItemRevision = officialItem.itemRevision,
                lastSyncError = null,
            ),
        )
        return true
    }

    private companion object {
        private const val INITIAL_PAYLOAD_VERSION = 1L
    }
}
