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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(draft: SecureItemDraftEntity)

    @Query(
        """
        DELETE FROM secure_items_draft
        WHERE logical_item_id = :logicalItemId
        """,
    )
    suspend fun delete(logicalItemId: UUID): Int
}
