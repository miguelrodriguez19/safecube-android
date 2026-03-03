package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "secure_items")
data class SecureItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
)
