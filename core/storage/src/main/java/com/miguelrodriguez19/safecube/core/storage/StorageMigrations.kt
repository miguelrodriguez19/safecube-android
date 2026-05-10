package com.miguelrodriguez19.safecube.core.storage

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object StorageMigrations {
    private const val NANOS_PER_MILLI = 1_000_000L
    private const val SECURE_ITEM_SYNC_STATE_CHECK =
        "'SYNCED','PENDING_CREATE','PENDING_UPDATE','PENDING_DELETE','CONFLICT'"
    private const val SECURE_ITEM_DRAFT_TYPE_CHECK = "'UPDATE','DELETE'"
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

    /**
     * v3 introduces sync metadata at item level and incremental checkpoint persistence by account.
     * Existing rows are backfilled as SYNCED to preserve current offline CRUD behavior.
     */
    val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "ALTER TABLE `secure_items` ADD COLUMN `sync_state` TEXT NOT NULL DEFAULT 'SYNCED'",
            )
            db.execSQL(
                "ALTER TABLE `secure_items` ADD COLUMN `last_synced_at` INTEGER",
            )
            db.execSQL(
                "ALTER TABLE `secure_items` ADD COLUMN `last_sync_error` TEXT",
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_secure_items_sync_state` ON `secure_items` (`sync_state`)",
            )
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `secure_item_sync_checkpoints` (
                    `account_id` TEXT NOT NULL,
                    `last_pulled_at` INTEGER NOT NULL,
                    PRIMARY KEY(`account_id`)
                )
                """.trimIndent(),
            )
        }
    }

    /**
     * v4 hardens sync_state as a closed set of values at DB level.
     */
    val MIGRATION_3_4: Migration = object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `secure_items_new` (
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
                    `sync_state` TEXT NOT NULL DEFAULT 'SYNCED' CHECK(`sync_state` IN ($SECURE_ITEM_SYNC_STATE_CHECK)),
                    `last_synced_at` INTEGER,
                    `last_sync_error` TEXT,
                    PRIMARY KEY(`logical_item_id`)
                )
                """.trimIndent(),
            )
            db.execSQL(
                """
                INSERT INTO `secure_items_new` (
                    `logical_item_id`,
                    `remote_item_id`,
                    `item_type`,
                    `schema_version`,
                    `display_hint`,
                    `payload`,
                    `payload_version`,
                    `created_at`,
                    `updated_at`,
                    `deleted_at`,
                    `sync_state`,
                    `last_synced_at`,
                    `last_sync_error`
                )
                SELECT
                    `logical_item_id`,
                    `remote_item_id`,
                    `item_type`,
                    `schema_version`,
                    `display_hint`,
                    `payload`,
                    `payload_version`,
                    `created_at`,
                    `updated_at`,
                    `deleted_at`,
                    CASE
                        WHEN `sync_state` IN ($SECURE_ITEM_SYNC_STATE_CHECK) THEN `sync_state`
                        ELSE 'SYNCED'
                    END,
                    `last_synced_at`,
                    `last_sync_error`
                FROM `secure_items`
                """.trimIndent(),
            )
            db.execSQL("DROP TABLE `secure_items`")
            db.execSQL("ALTER TABLE `secure_items_new` RENAME TO `secure_items`")
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
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_secure_items_sync_state` ON `secure_items` (`sync_state`)",
            )
        }
    }

    /**
     * v5 introduces mirror-style draft storage for non-official local proposals.
     */
    val MIGRATION_4_5: Migration = object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS `secure_items_draft` (
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
                    `last_synced_at` INTEGER,
                    `last_sync_error` TEXT,
                    `draft_type` TEXT NOT NULL CHECK(`draft_type` IN ($SECURE_ITEM_DRAFT_TYPE_CHECK)),
                    `base_payload_version` INTEGER NOT NULL,
                    `base_updated_at` INTEGER NOT NULL,
                    `last_publish_error` TEXT,
                    PRIMARY KEY(`logical_item_id`)
                )
                """.trimIndent(),
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_secure_items_draft_remote_item_id` ON `secure_items_draft` (`remote_item_id`)",
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_secure_items_draft_deleted_at` ON `secure_items_draft` (`deleted_at`)",
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_secure_items_draft_updated_at` ON `secure_items_draft` (`updated_at`)",
            )
            db.execSQL(
                "CREATE INDEX IF NOT EXISTS `index_secure_items_draft_draft_type` ON `secure_items_draft` (`draft_type`)",
            )
        }
    }

    /**
     * v6 preserves sub-millisecond timestamp precision by storing Instants as epoch nanos.
     * Existing rows are migrated from epoch millis deterministically.
     */
    val MIGRATION_5_6: Migration = object : Migration(5, 6) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                UPDATE `secure_items`
                SET `created_at` = `created_at` * $NANOS_PER_MILLI,
                    `updated_at` = `updated_at` * $NANOS_PER_MILLI,
                    `deleted_at` = CASE
                        WHEN `deleted_at` IS NULL THEN NULL
                        ELSE `deleted_at` * $NANOS_PER_MILLI
                    END,
                    `last_synced_at` = CASE
                        WHEN `last_synced_at` IS NULL THEN NULL
                        ELSE `last_synced_at` * $NANOS_PER_MILLI
                    END
                """.trimIndent(),
            )
            db.execSQL(
                """
                UPDATE `secure_item_sync_checkpoints`
                SET `last_pulled_at` = `last_pulled_at` * $NANOS_PER_MILLI
                """.trimIndent(),
            )
            db.execSQL(
                """
                UPDATE `secure_items_draft`
                SET `created_at` = `created_at` * $NANOS_PER_MILLI,
                    `updated_at` = `updated_at` * $NANOS_PER_MILLI,
                    `deleted_at` = CASE
                        WHEN `deleted_at` IS NULL THEN NULL
                        ELSE `deleted_at` * $NANOS_PER_MILLI
                    END,
                    `last_synced_at` = CASE
                        WHEN `last_synced_at` IS NULL THEN NULL
                        ELSE `last_synced_at` * $NANOS_PER_MILLI
                    END,
                    `base_updated_at` = `base_updated_at` * $NANOS_PER_MILLI
                """.trimIndent(),
            )
        }
    }
}
