package com.miguelrodriguez19.safecube.core.vault.domain.repository

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.Flow

interface SecureItemRepository {
    fun observeActiveItems(): Flow<List<SecureItem>>

    fun observeItem(logicalItemId: UUID): Flow<SecureItem?>

    suspend fun getItem(logicalItemId: UUID): SecureItem?

    suspend fun insert(item: SecureItem)

    suspend fun update(item: SecureItem)

    suspend fun softDelete(
        logicalItemId: UUID,
        deletedAt: Instant,
    ): Boolean

    suspend fun getPendingSyncItemsOrdered(): List<SecureItem>

    suspend fun findByRemoteItemId(remoteItemId: UUID): SecureItem?

    suspend fun markPendingCreate(logicalItemId: UUID): Boolean

    suspend fun markPendingUpdate(logicalItemId: UUID): Boolean

    suspend fun markPendingDelete(
        logicalItemId: UUID,
        deletedAt: Instant,
    ): Boolean

    suspend fun markSynced(
        logicalItemId: UUID,
        remoteItemId: UUID?,
        payloadVersion: Long,
        updatedAt: Instant,
        deletedAt: Instant?,
        lastSyncedAt: Instant,
    ): Boolean

    suspend fun markConflict(
        logicalItemId: UUID,
        lastSyncError: String,
    ): Boolean

    suspend fun applyRemoteUpsert(
        item: SecureItem,
        lastSyncedAt: Instant,
    ): Boolean

    suspend fun applyRemoteDelete(
        remoteItemId: UUID,
        deletedAt: Instant,
        lastSyncedAt: Instant,
    ): Boolean

    suspend fun getSyncCheckpoint(accountId: UUID): Instant?

    suspend fun updateSyncCheckpoint(
        accountId: UUID,
        lastPulledAt: Instant,
    )
}
