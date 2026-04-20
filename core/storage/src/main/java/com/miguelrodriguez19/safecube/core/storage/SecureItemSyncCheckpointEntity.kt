package com.miguelrodriguez19.safecube.core.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(tableName = "secure_item_sync_checkpoints")
data class SecureItemSyncCheckpointEntity(
    @PrimaryKey
    @ColumnInfo(name = "account_id")
    val accountId: UUID,
    @ColumnInfo(name = "last_pulled_at")
    val lastPulledAt: Instant,
)
