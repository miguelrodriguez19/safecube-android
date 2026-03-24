package com.miguelrodriguez19.safecube.core.storage

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object StorageMigrations {
    /**
     * v1 shipped only a placeholder secure_items table with an auto-generated numeric id and no
     * user data contract. v2 introduces the first real offline schema, so the table is recreated
     * explicitly instead of relying on destructive fallback at database level.
     */
    val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("DROP TABLE IF EXISTS `secure_items`")
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `secure_items` (
                    `logical_item_id` TEXT NOT NULL,
                    `remote_item_id` TEXT,
                    `item_type` TEXT NOT NULL,
                    `schema_version` INTEGER NOT NULL,
                    `display_hint` TEXT NOT NULL,
                    `payload` BLOB NOT NULL,
                    `payload_version` INTEGER NOT NULL,
                    `created_at` INTEGER NOT NULL,
                    `updated_at` INTEGER NOT NULL,
                    `deleted_at` INTEGER,
                    PRIMARY KEY(`logical_item_id`)
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_secure_items_remote_item_id` ON `secure_items` (`remote_item_id`)",
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_secure_items_deleted_at` ON `secure_items` (`deleted_at`)",
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_secure_items_updated_at` ON `secure_items` (`updated_at`)",
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_secure_items_item_type` ON `secure_items` (`item_type`)",
            )
        }
    }
}
