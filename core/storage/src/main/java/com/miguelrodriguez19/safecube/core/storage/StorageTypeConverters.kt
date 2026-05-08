package com.miguelrodriguez19.safecube.core.storage

import androidx.room.TypeConverter
import java.time.Instant
import java.util.UUID

class StorageTypeConverters {
    @TypeConverter
    fun fromUuid(value: UUID?): String? = value?.toString()

    @TypeConverter
    fun toUuid(value: String?): UUID? = value
        ?.takeIf { it.isNotBlank() }
        ?.let { UUID.fromString(it) }

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.toEpochMilli()

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let(Instant::ofEpochMilli)

    @TypeConverter
    fun fromSecureItemSyncStateDb(value: SecureItemSyncStateDb): String = value.storageValue

    @TypeConverter
    fun toSecureItemSyncStateDb(value: String): SecureItemSyncStateDb = requireNotNull(
        SecureItemSyncStateDb.fromStorageValue(value),
    ) {
        "Unsupported SecureItemSyncStateDb value: $value"
    }

    @TypeConverter
    fun fromSecureItemDraftTypeDb(value: SecureItemDraftTypeDb): String = value.storageValue

    @TypeConverter
    fun toSecureItemDraftTypeDb(value: String): SecureItemDraftTypeDb = requireNotNull(
        SecureItemDraftTypeDb.fromStorageValue(value),
    ) {
        "Unsupported SecureItemDraftTypeDb value: $value"
    }
}
