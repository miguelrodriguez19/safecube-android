package com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.draft

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemDraftRepository
import com.miguelrodriguez19.safecube.core.vault.domain.repository.SecureItemRepository
import java.time.Instant
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SecureItemDraftPolicyCoordinator @Inject constructor(
    private val secureItemRepository: SecureItemRepository,
    private val secureItemDraftRepository: SecureItemDraftRepository,
) {
    suspend fun replaceOfficialItemWithRemoteAndDraft(
        localItem: SecureItem,
        remoteItem: SecureItem,
        draftType: SecureItemDraftType,
        lastSyncedAt: Instant,
    ): Boolean = runCatching {
        secureItemDraftRepository.upsert(localItem.toSyncDraft(draftType))
        secureItemRepository.applyRemoteUpsert(
            item = remoteItem,
            lastSyncedAt = lastSyncedAt,
        )
    }.getOrDefault(false)

    suspend fun applyRemoteDeleteAndDiscardLocalChanges(
        logicalItemId: UUID,
        remoteItemId: UUID,
        deletedAt: Instant,
        lastSyncedAt: Instant,
    ): Boolean = runCatching {
        val remoteDeleteApplied = secureItemRepository.applyRemoteDelete(
            remoteItemId = remoteItemId,
            deletedAt = deletedAt,
            lastSyncedAt = lastSyncedAt,
        )
        if (!remoteDeleteApplied) {
            return false
        }

        val existingDraft = secureItemDraftRepository.getDraft(logicalItemId)

        return if (existingDraft != null) {
            secureItemDraftRepository.delete(logicalItemId)
        } else {
            true
        }
    }.getOrDefault(false)

    suspend fun finalizePublishedUpdate(
        draft: SecureItemSyncDraft,
        remotePayloadVersion: Long,
        remoteUpdatedAt: Instant,
    ): Boolean = runCatching {
        val remoteApplySucceeded = secureItemRepository.applyRemoteUpsert(
            item = draft.toPublishedOfficialItem(
                payloadVersion = remotePayloadVersion,
                updatedAt = remoteUpdatedAt,
            ),
            lastSyncedAt = remoteUpdatedAt,
        )
        if (!remoteApplySucceeded) {
            return false
        }

        secureItemDraftRepository.delete(draft.logicalItemId)
    }.getOrDefault(false)

    suspend fun finalizePublishedDelete(
        draft: SecureItemSyncDraft,
        deletedAt: Instant,
    ): Boolean {
        val remoteItemId = draft.remoteItemId ?: return false

        return runCatching {
            val remoteDeleteApplied = secureItemRepository.applyRemoteDelete(
                remoteItemId = remoteItemId,
                deletedAt = deletedAt,
                lastSyncedAt = deletedAt,
            )
            if (!remoteDeleteApplied) {
                return false
            }

            secureItemDraftRepository.delete(draft.logicalItemId)
        }.getOrDefault(false)
    }

    suspend fun discardDraft(logicalItemId: UUID): Boolean = runCatching {
        secureItemDraftRepository.delete(logicalItemId)
    }.getOrDefault(false)
}
