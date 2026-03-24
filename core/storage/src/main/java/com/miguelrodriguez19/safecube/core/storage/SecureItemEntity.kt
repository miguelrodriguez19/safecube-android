package com.miguelrodriguez19.safecube.core.storage

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Instant
import java.util.UUID

@Entity(
    tableName = "secure_items",
    indices = [
        Index(value = ["remote_item_id"]),
        Index(value = ["deleted_at"]),
        Index(value = ["updated_at"]),
        Index(value = ["item_type"]),
    ],
)
data class SecureItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "logical_item_id")
    val logicalItemId: UUID,
    @ColumnInfo(name = "remote_item_id")
    val remoteItemId: UUID? = null,
    @ColumnInfo(name = "item_type")
    val itemType: String,
    @ColumnInfo(name = "schema_version")
    val schemaVersion: Int,
    @ColumnInfo(name = "display_hint")
    val displayHint: String,
    @ColumnInfo(name = "payload")
    val payload: ByteArray,
    @ColumnInfo(name = "payload_version")
    val payloadVersion: Long,
    @ColumnInfo(name = "created_at")
    val createdAt: Instant,
    @ColumnInfo(name = "updated_at")
    val updatedAt: Instant,
    @ColumnInfo(name = "deleted_at")
    val deletedAt: Instant? = null,
)
