package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import java.util.UUID

@Dao
interface SecureItemSyncCheckpointDao {
    @Query(
        """
        SELECT last_applied_change_sequence
        FROM secure_item_sync_checkpoints
        WHERE account_id = :accountId
        LIMIT 1
        """,
    )
    suspend fun getLastAppliedChangeSequence(accountId: UUID): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(checkpoint: SecureItemSyncCheckpointEntity)

    @Query("DELETE FROM secure_item_sync_checkpoints")
    suspend fun deleteAll()
}
