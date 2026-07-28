package com.miguelrodriguez19.safecube.core.vault.domain.repository

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemSyncDraft
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface SecureItemRepository {
    fun observeActiveItems(): Flow<List<SecureItem>>

    fun observeItem(logicalItemId: UUID): Flow<SecureItem?>

    suspend fun getItem(logicalItemId: UUID): SecureItem?

    suspend fun findByRemoteItemId(remoteItemId: UUID): SecureItem?

    suspend fun applyRemoteUpsert(
        item: SecureItem,
        lastSyncedAt: Instant,
    ): Boolean

    suspend fun applyRemoteDelete(
        remoteItemId: UUID,
        deletedAt: Instant,
        itemRevision: Long,
        changeSequence: Long,
        lastSyncedAt: Instant,
    ): Boolean

    suspend fun getSyncCheckpoint(accountId: UUID): Long?

    suspend fun updateSyncCheckpoint(
        accountId: UUID,
        lastAppliedChangeSequence: Long,
    )

    suspend fun officializeDraft(
        item: SecureItem,
        lastSyncedAt: Instant,
    ): Boolean

    suspend fun replaceOfficialWithConflictedDraft(
        item: SecureItem,
        draft: SecureItemSyncDraft,
        lastSyncedAt: Instant,
    ): Boolean

    suspend fun applyRemotePage(
        accountId: UUID,
        items: List<SecureItem>,
        conflictedDrafts: List<SecureItemSyncDraft>,
        draftsToDelete: Set<UUID>,
        lastAppliedChangeSequence: Long,
        lastSyncedAt: Instant,
    ): Boolean

    suspend fun clearAllLocalData()
}
