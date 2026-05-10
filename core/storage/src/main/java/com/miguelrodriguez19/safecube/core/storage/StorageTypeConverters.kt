package com.miguelrodriguez19.safecube.core.storage

import androidx.room.TypeConverter
import java.time.Instant
import java.util.UUID

class StorageTypeConverters {
    private companion object {
        private const val NANOS_PER_SECOND = 1_000_000_000L
    }

    @TypeConverter
    fun fromUuid(value: UUID?): String? = value?.toString()

    @TypeConverter
    fun toUuid(value: String?): UUID? = value
        ?.takeIf { it.isNotBlank() }
        ?.let { UUID.fromString(it) }

    @TypeConverter
    fun fromInstant(value: Instant?): Long? = value?.let { instant ->
        Math.addExact(
            Math.multiplyExact(instant.epochSecond, NANOS_PER_SECOND),
            instant.nano.toLong(),
        )
    }

    @TypeConverter
    fun toInstant(value: Long?): Instant? = value?.let { epochNanos ->
        val epochSeconds = Math.floorDiv(epochNanos, NANOS_PER_SECOND)
        val nanosAdjustment = Math.floorMod(epochNanos, NANOS_PER_SECOND)
        Instant.ofEpochSecond(epochSeconds, nanosAdjustment)
    }

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
