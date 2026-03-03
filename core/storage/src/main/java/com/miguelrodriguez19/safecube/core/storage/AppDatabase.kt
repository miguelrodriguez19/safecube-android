package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SecureItemEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun secureItemDao(): SecureItemDao
}
