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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: SecureItemEntity)

    @Update
    suspend fun update(item: SecureItemEntity)

    @Query(
        """
        UPDATE secure_items
        SET deleted_at = :deletedAt,
            updated_at = :deletedAt,
            item_revision = :itemRevision,
            change_sequence = :changeSequence,
            sync_state = :syncState,
            last_synced_at = :lastSyncedAt,
            last_sync_error = NULL
        WHERE remote_item_id = :remoteItemId
        """,
    )
    suspend fun applyRemoteDelete(
        remoteItemId: UUID,
        deletedAt: Instant,
        itemRevision: Long,
        changeSequence: Long,
        syncState: SecureItemSyncStateDb,
        lastSyncedAt: Instant,
    ): Int

    @Query("DELETE FROM secure_items")
    suspend fun deleteAll()
}
