package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StorageMigrationsIntegrationTest {
    private val context = ApplicationProvider.getApplicationContext<android.content.Context>()

    @Before
    fun setUp() {
        context.deleteDatabase(TEST_DATABASE_NAME)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(TEST_DATABASE_NAME)
    }

    @Test
    fun `migration 6 to 7 clears rows whose server revisions cannot be reconstructed`() = runBlocking {
        createVersion6Database()

        val migratedDatabase = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            TEST_DATABASE_NAME,
        ).addMigrations(StorageMigrations.MIGRATION_6_7).build()

        val migratedItem = migratedDatabase.secureItemDao().getItem(UUID.fromString(SAMPLE_LOGICAL_ITEM_ID))
        val migratedDraft = migratedDatabase.secureItemDraftDao().getDraft(UUID.fromString(SAMPLE_LOGICAL_ITEM_ID))

        val migratedCheckpoint = migratedDatabase.secureItemSyncCheckpointDao()
            .getLastAppliedChangeSequence(UUID.randomUUID())

        assertNull(migratedItem)
        assertNull(migratedDraft)
        assertNull(migratedCheckpoint)

        migratedDatabase.close()
    }

    private fun createVersion6Database() {
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(TEST_DATABASE_NAME)
                .callback(
                    object : SupportSQLiteOpenHelper.Callback(6) {
                        override fun onCreate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
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
                                    `sync_state` TEXT NOT NULL DEFAULT 'SYNCED' CHECK(`sync_state` IN ('SYNCED','PENDING_CREATE','PENDING_UPDATE','PENDING_DELETE','CONFLICT')),
                                    `last_synced_at` INTEGER,
                                    `last_sync_error` TEXT,
                                    PRIMARY KEY(`logical_item_id`)
                                )
                                """.trimIndent(),
                            )
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
                                    `draft_type` TEXT NOT NULL,
                                    `base_payload_version` INTEGER NOT NULL,
                                    `base_updated_at` INTEGER NOT NULL,
                                    `last_publish_error` TEXT,
                                    PRIMARY KEY(`logical_item_id`)
                                )
                                """.trimIndent(),
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
                            db.execSQL(
                                """
                                INSERT INTO `secure_items` (
                                    `logical_item_id`, `remote_item_id`, `item_type`, `schema_version`,
                                    `display_hint`, `payload`, `payload_version`, `created_at`, `updated_at`,
                                    `deleted_at`, `sync_state`, `last_synced_at`, `last_sync_error`
                                ) VALUES (
                                    '$SAMPLE_LOGICAL_ITEM_ID', '$SAMPLE_REMOTE_ITEM_ID', 'NOTE', 1,
                                    'Official item', X'010203', 1, 1, 2,
                                    NULL, 'PENDING_UPDATE', 3, 'old error'
                                )
                                """.trimIndent(),
                            )
                            db.execSQL(
                                """
                                INSERT INTO `secure_items_draft` (
                                    `logical_item_id`, `remote_item_id`, `item_type`, `schema_version`,
                                    `display_hint`, `payload`, `payload_version`, `created_at`, `updated_at`,
                                    `deleted_at`, `last_synced_at`, `last_sync_error`, `draft_type`,
                                    `base_payload_version`, `base_updated_at`, `last_publish_error`
                                ) VALUES (
                                    '$SAMPLE_LOGICAL_ITEM_ID', '$SAMPLE_REMOTE_ITEM_ID', 'NOTE', 1,
                                    'Draft item', X'040506', 2, 1, 4,
                                    NULL, 3, NULL, 'UPDATE',
                                    1, 2, 'publish failed'
                                )
                                """.trimIndent(),
                            )
                        }

                        override fun onUpgrade(
                            db: androidx.sqlite.db.SupportSQLiteDatabase,
                            oldVersion: Int,
                            newVersion: Int,
                        ) = Unit
                    },
                )
                .build(),
        )

        helper.writableDatabase.close()
        helper.close()
    }

    private companion object {
        private const val TEST_DATABASE_NAME = "storage-migration-test.db"
        private const val SAMPLE_LOGICAL_ITEM_ID = "11111111-1111-1111-1111-111111111111"
        private const val SAMPLE_REMOTE_ITEM_ID = "22222222-2222-2222-2222-222222222222"
    }
}
