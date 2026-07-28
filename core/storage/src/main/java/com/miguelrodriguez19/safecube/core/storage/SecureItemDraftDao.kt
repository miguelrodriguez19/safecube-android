package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.UUID
import kotlinx.coroutines.flow.Flow

@Dao
interface SecureItemDraftDao {
    @Query(
        """
        SELECT * FROM secure_items_draft
        ORDER BY updated_at DESC
        """,
    )
    fun observeDrafts(): Flow<List<SecureItemDraftEntity>>

    @Query(
        """
        SELECT * FROM secure_items_draft
        WHERE logical_item_id = :logicalItemId
        LIMIT 1
        """,
    )
    fun observeDraft(logicalItemId: UUID): Flow<SecureItemDraftEntity?>

    @Query(
        """
        SELECT * FROM secure_items_draft
        WHERE logical_item_id = :logicalItemId
        LIMIT 1
        """,
    )
    suspend fun getDraft(logicalItemId: UUID): SecureItemDraftEntity?

    @Query(
        """
        SELECT * FROM secure_items_draft
        WHERE remote_item_id = :remoteItemId
        LIMIT 1
        """,
    )
    suspend fun findByRemoteItemId(remoteItemId: UUID): SecureItemDraftEntity?

    @Query(
        """
        SELECT * FROM secure_items_draft
        WHERE draft_sync_status = :draftSyncStatus
        ORDER BY updated_at ASC
        """,
    )
    suspend fun getDraftsBySyncStatus(
        draftSyncStatus: SecureItemDraftSyncStatusDb,
    ): List<SecureItemDraftEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(draft: SecureItemDraftEntity)

    @Query(
        """
        UPDATE secure_items_draft
        SET draft_sync_status = :draftSyncStatus,
            last_sync_error = :lastSyncError
        WHERE logical_item_id = :logicalItemId
        """,
    )
    suspend fun updateStatus(
        logicalItemId: UUID,
        draftSyncStatus: SecureItemDraftSyncStatusDb,
        lastSyncError: String?,
    ): Int

    @Query(
        """
        DELETE FROM secure_items_draft
        WHERE logical_item_id = :logicalItemId
        """,
    )
    suspend fun delete(logicalItemId: UUID): Int
}
