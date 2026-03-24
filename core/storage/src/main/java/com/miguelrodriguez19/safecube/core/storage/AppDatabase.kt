package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [SecureItemEntity::class],
    version = 2,
    exportSchema = true,
)
@TypeConverters(StorageTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun secureItemDao(): SecureItemDao
}
