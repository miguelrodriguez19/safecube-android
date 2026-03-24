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
}
