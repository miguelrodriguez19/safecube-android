package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [
        SecureItemEntity::class,
        SecureItemDraftEntity::class,
        SecureItemSyncCheckpointEntity::class,
    ],
    version = 6,
    exportSchema = true,
)
@TypeConverters(StorageTypeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun secureItemDao(): SecureItemDao

    abstract fun secureItemDraftDao(): SecureItemDraftDao

    abstract fun secureItemSyncCheckpointDao(): SecureItemSyncCheckpointDao
}
