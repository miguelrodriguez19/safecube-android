package com.miguelrodriguez19.safecube.core.vault.domain.repository

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemDraftSyncStatus
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface SecureItemDraftRepository {
    fun observeDrafts(): Flow<List<SecureItemSyncDraft>>

    fun observeDraft(logicalItemId: UUID): Flow<SecureItemSyncDraft?>

    suspend fun getDraft(logicalItemId: UUID): SecureItemSyncDraft?

    suspend fun findByRemoteItemId(remoteItemId: UUID): SecureItemSyncDraft?

    suspend fun getSyncableDraftsOrdered(
        draftSyncStatus: SecureItemDraftSyncStatus = SecureItemDraftSyncStatus.READY_TO_SYNC,
    ): List<SecureItemSyncDraft>

    suspend fun upsert(draft: SecureItemSyncDraft)

    suspend fun updateStatus(
        logicalItemId: UUID,
        draftSyncStatus: SecureItemDraftSyncStatus,
        lastSyncError: String?,
    ): Boolean

    suspend fun delete(logicalItemId: UUID): Boolean

    suspend fun replace(
        logicalItemId: UUID,
        replacement: SecureItemSyncDraft,
    ): Boolean
}
