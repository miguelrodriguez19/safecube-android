package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
interface SecureItemDao {
    @Query(
        """
        SELECT * FROM secure_items
        WHERE deleted_at IS NULL
        ORDER BY updated_at DESC
        """,
    )
    fun observeActiveItems(): Flow<List<SecureItemEntity>>

    @Query(
        """
        SELECT * FROM secure_items
        WHERE logical_item_id = :logicalItemId
        LIMIT 1
        """,
    )
    fun observeItem(logicalItemId: UUID): Flow<SecureItemEntity?>

    @Query(
        """
        SELECT * FROM secure_items
        WHERE logical_item_id = :logicalItemId
        LIMIT 1
        """,
    )
    suspend fun getItem(logicalItemId: UUID): SecureItemEntity?

    @Query(
        """
        SELECT * FROM secure_items
        WHERE remote_item_id = :remoteItemId
        LIMIT 1
        """,
    )
    suspend fun findByRemoteItemId(remoteItemId: UUID): SecureItemEntity?

    @Query(
        """
        SELECT * FROM secure_items
        WHERE sync_state IN (:pendingCreateState, :pendingUpdateState, :pendingDeleteState)
        ORDER BY updated_at ASC
        """,
    )
    suspend fun getPendingSyncItemsOrdered(
        pendingCreateState: SecureItemSyncStateDb = SecureItemSyncStateDb.PENDING_CREATE,
        pendingUpdateState: SecureItemSyncStateDb = SecureItemSyncStateDb.PENDING_UPDATE,
        pendingDeleteState: SecureItemSyncStateDb = SecureItemSyncStateDb.PENDING_DELETE,
    ): List<SecureItemEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: SecureItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: SecureItemEntity)

    @Update
    suspend fun update(item: SecureItemEntity)

    @Query(
        """
        UPDATE secure_items
        SET deleted_at = :deletedAt,
            updated_at = :deletedAt,
            sync_state = :syncState,
            last_sync_error = NULL
        WHERE logical_item_id = :logicalItemId
        """,
    )
    suspend fun softDelete(
        logicalItemId: UUID,
        deletedAt: Instant,
        syncState: SecureItemSyncStateDb = SecureItemSyncStateDb.PENDING_DELETE,
    ): Int

    @Query(
        """
        UPDATE secure_items
        SET sync_state = :syncState,
            last_sync_error = NULL
        WHERE logical_item_id = :logicalItemId
        """,
    )
    suspend fun markPendingCreate(
        logicalItemId: UUID,
        syncState: SecureItemSyncStateDb = SecureItemSyncStateDb.PENDING_CREATE,
    ): Int

    @Query(
        """
        UPDATE secure_items
        SET sync_state = :syncState,
            last_sync_error = NULL
        WHERE logical_item_id = :logicalItemId
        """,
    )
    suspend fun markPendingUpdate(
        logicalItemId: UUID,
        syncState: SecureItemSyncStateDb = SecureItemSyncStateDb.PENDING_UPDATE
    ): Int

    @Query(
        """
        UPDATE secure_items
        SET deleted_at = :deletedAt,
            updated_at = :deletedAt,
            sync_state = :syncState,
            last_sync_error = NULL
        WHERE logical_item_id = :logicalItemId
        """,
    )
    suspend fun markPendingDelete(
        logicalItemId: UUID,
        deletedAt: Instant,
        syncState: SecureItemSyncStateDb = SecureItemSyncStateDb.PENDING_DELETE,
    ): Int

    @Query(
        """
        UPDATE secure_items
        SET remote_item_id = :remoteItemId,
            payload_version = :payloadVersion,
            updated_at = :updatedAt,
            deleted_at = :deletedAt,
            sync_state = :syncState,
            last_synced_at = :lastSyncedAt,
            last_sync_error = NULL
        WHERE logical_item_id = :logicalItemId
        """,
    )
    suspend fun markSynced(
        logicalItemId: UUID,
        remoteItemId: UUID?,
        payloadVersion: Long,
        updatedAt: Instant,
        deletedAt: Instant?,
        syncState: SecureItemSyncStateDb = SecureItemSyncStateDb.SYNCED,
        lastSyncedAt: Instant,
    ): Int

    @Query(
        """
        UPDATE secure_items
        SET sync_state = :syncState,
            last_sync_error = :lastSyncError
        WHERE logical_item_id = :logicalItemId
        """,
    )
    suspend fun markConflict(
        logicalItemId: UUID,
        syncState: SecureItemSyncStateDb = SecureItemSyncStateDb.CONFLICT,
        lastSyncError: String,
    ): Int

    @Query(
        """
        UPDATE secure_items
        SET deleted_at = :deletedAt,
            updated_at = :deletedAt,
            sync_state = :syncState,
            last_synced_at = :lastSyncedAt,
            last_sync_error = NULL
        WHERE remote_item_id = :remoteItemId
        """,
    )
    suspend fun applyRemoteDelete(
        remoteItemId: UUID,
        deletedAt: Instant,
        syncState: SecureItemSyncStateDb,
        lastSyncedAt: Instant,
    ): Int
}
