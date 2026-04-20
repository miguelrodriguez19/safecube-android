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

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(item: SecureItemEntity)

    @Update
    suspend fun update(item: SecureItemEntity)

    @Query(
        """
        UPDATE secure_items
        SET deleted_at = :deletedAt,
            updated_at = :deletedAt,
            sync_state = 'PENDING_DELETE',
            last_sync_error = NULL
        WHERE logical_item_id = :logicalItemId
        """,
    )
    suspend fun softDelete(
        logicalItemId: UUID,
        deletedAt: Instant
    ): Int
}
