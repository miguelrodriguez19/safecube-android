package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertNotNull
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
    fun `migration 2 to 6 when applied then keeps secure items and adds sync metadata checkpoint and draft tables`() =
        runBlocking {
            createVersion2Database()

            val migratedDatabase = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                TEST_DATABASE_NAME,
            )
                .addMigrations(StorageMigrations.MIGRATION_2_3)
                .addMigrations(StorageMigrations.MIGRATION_3_4)
                .addMigrations(StorageMigrations.MIGRATION_4_5)
                .addMigrations(StorageMigrations.MIGRATION_5_6)
                .build()

            val migratedItem = migratedDatabase.secureItemDao().getItem(SAMPLE_LOGICAL_ITEM_ID)
            requireNotNull(migratedItem)
            assertEquals(SAMPLE_REMOTE_ITEM_ID, migratedItem.remoteItemId)
            assertEquals(SAMPLE_UPDATED_AT, migratedItem.updatedAt)
            assertEquals(SecureItemSyncStateDb.SYNCED, migratedItem.syncState)
            assertNull(migratedItem.lastSyncedAt)
            assertNull(migratedItem.lastSyncError)

            val checkpointDao = migratedDatabase.secureItemSyncCheckpointDao()
            assertNull(checkpointDao.getLastPulledAt(SAMPLE_ACCOUNT_ID))
            checkpointDao.upsert(
                SecureItemSyncCheckpointEntity(
                    accountId = SAMPLE_ACCOUNT_ID,
                    lastPulledAt = SAMPLE_LAST_PULLED_AT,
                ),
            )
            assertEquals(SAMPLE_LAST_PULLED_AT, checkpointDao.getLastPulledAt(SAMPLE_ACCOUNT_ID))

            val draftDao = migratedDatabase.secureItemDraftDao()
            assertNull(draftDao.getDraft(SAMPLE_LOGICAL_ITEM_ID))
            draftDao.upsert(
                SecureItemDraftEntity(
                    logicalItemId = SAMPLE_LOGICAL_ITEM_ID,
                    remoteItemId = SAMPLE_REMOTE_ITEM_ID,
                    itemType = "PASSWORD",
                    schemaVersion = 1,
                    displayHint = "Migrated draft",
                    payload = byteArrayOf(9, 8, 7),
                    payloadVersion = 3,
                    createdAt = SAMPLE_CREATED_AT,
                    updatedAt = SAMPLE_UPDATED_AT.plusSeconds(60),
                    deletedAt = null,
                    lastSyncedAt = SAMPLE_UPDATED_AT,
                    lastSyncError = null,
                    draftType = SecureItemDraftTypeDb.UPDATE,
                    basePayloadVersion = 2,
                    baseUpdatedAt = SAMPLE_UPDATED_AT,
                    lastPublishError = null,
                ),
            )
            val storedDraft = draftDao.getDraft(SAMPLE_LOGICAL_ITEM_ID)
            assertNotNull(storedDraft)
            assertEquals(SecureItemDraftTypeDb.UPDATE, storedDraft?.draftType)
            assertEquals(2L, storedDraft?.basePayloadVersion)

            migratedDatabase.close()
        }

    @Test
    fun `migration 5 to 6 when applied then upgrades persisted timestamps from millis to nanos`() =
        runBlocking {
            createVersion5Database()

            val migratedDatabase = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                TEST_DATABASE_NAME,
            )
                .addMigrations(StorageMigrations.MIGRATION_5_6)
                .build()

            val migratedItem = migratedDatabase.secureItemDao().getItem(SAMPLE_LOGICAL_ITEM_ID)
            requireNotNull(migratedItem)
            assertEquals(SAMPLE_CREATED_AT, migratedItem.createdAt)
            assertEquals(SAMPLE_UPDATED_AT, migratedItem.updatedAt)
            assertEquals(SAMPLE_LAST_SYNCED_AT, migratedItem.lastSyncedAt)

            val migratedCheckpoint =
                migratedDatabase.secureItemSyncCheckpointDao().getLastPulledAt(SAMPLE_ACCOUNT_ID)
            assertEquals(SAMPLE_LAST_PULLED_AT, migratedCheckpoint)

            val migratedDraft = migratedDatabase.secureItemDraftDao().getDraft(SAMPLE_LOGICAL_ITEM_ID)
            requireNotNull(migratedDraft)
            assertEquals(SAMPLE_CREATED_AT, migratedDraft.createdAt)
            assertEquals(SAMPLE_DRAFT_UPDATED_AT, migratedDraft.updatedAt)
            assertEquals(SAMPLE_LAST_SYNCED_AT, migratedDraft.lastSyncedAt)
            assertEquals(SAMPLE_BASE_UPDATED_AT, migratedDraft.baseUpdatedAt)

            migratedDatabase.close()
        }

    private fun createVersion2Database() {
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(TEST_DATABASE_NAME)
                .callback(
                    object : SupportSQLiteOpenHelper.Callback(2) {
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
                            db.execSQL(
                                """
                                INSERT INTO `secure_items` (
                                    `logical_item_id`,
                                    `remote_item_id`,
                                    `item_type`,
                                    `schema_version`,
                                    `display_hint`,
                                    `payload`,
                                    `payload_version`,
                                    `created_at`,
                                    `updated_at`,
                                    `deleted_at`
                                ) VALUES (
                                    '${SAMPLE_LOGICAL_ITEM_ID}',
                                    '${SAMPLE_REMOTE_ITEM_ID}',
                                    'PASSWORD',
                                    1,
                                    'Migrated item',
                                    X'010203',
                                    1,
                                    ${SAMPLE_CREATED_AT.toEpochMilli()},
                                    ${SAMPLE_UPDATED_AT.toEpochMilli()},
                                    NULL
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

    private fun createVersion5Database() {
        val helper = FrameworkSQLiteOpenHelperFactory().create(
            SupportSQLiteOpenHelper.Configuration.builder(context)
                .name(TEST_DATABASE_NAME)
                .callback(
                    object : SupportSQLiteOpenHelper.Callback(5) {
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
                                    `draft_type` TEXT NOT NULL CHECK(`draft_type` IN ('UPDATE','DELETE')),
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
                            db.execSQL(
                                """
                                INSERT INTO `secure_items` (
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
                                ) VALUES (
                                    '${SAMPLE_LOGICAL_ITEM_ID}',
                                    '${SAMPLE_REMOTE_ITEM_ID}',
                                    'PASSWORD',
                                    1,
                                    'Migrated item',
                                    X'010203',
                                    1,
                                    ${SAMPLE_CREATED_AT.toEpochMilli()},
                                    ${SAMPLE_UPDATED_AT.toEpochMilli()},
                                    NULL,
                                    'SYNCED',
                                    ${SAMPLE_LAST_SYNCED_AT.toEpochMilli()},
                                    NULL
                                )
                                """.trimIndent(),
                            )
                            db.execSQL(
                                """
                                INSERT INTO `secure_item_sync_checkpoints` (
                                    `account_id`,
                                    `last_pulled_at`
                                ) VALUES (
                                    '${SAMPLE_ACCOUNT_ID}',
                                    ${SAMPLE_LAST_PULLED_AT.toEpochMilli()}
                                )
                                """.trimIndent(),
                            )
                            db.execSQL(
                                """
                                INSERT INTO `secure_items_draft` (
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
                                    `last_synced_at`,
                                    `last_sync_error`,
                                    `draft_type`,
                                    `base_payload_version`,
                                    `base_updated_at`,
                                    `last_publish_error`
                                ) VALUES (
                                    '${SAMPLE_LOGICAL_ITEM_ID}',
                                    '${SAMPLE_REMOTE_ITEM_ID}',
                                    'PASSWORD',
                                    1,
                                    'Migrated draft',
                                    X'090807',
                                    3,
                                    ${SAMPLE_CREATED_AT.toEpochMilli()},
                                    ${SAMPLE_DRAFT_UPDATED_AT.toEpochMilli()},
                                    NULL,
                                    ${SAMPLE_LAST_SYNCED_AT.toEpochMilli()},
                                    NULL,
                                    'UPDATE',
                                    2,
                                    ${SAMPLE_BASE_UPDATED_AT.toEpochMilli()},
                                    NULL
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
        private val NOW = Instant.now().truncatedTo(ChronoUnit.MILLIS)
        private val SAMPLE_LOGICAL_ITEM_ID = UUID.randomUUID()
        private val SAMPLE_REMOTE_ITEM_ID = UUID.randomUUID()
        private val SAMPLE_ACCOUNT_ID = UUID.randomUUID()
        private val SAMPLE_CREATED_AT = NOW.minus(3, ChronoUnit.DAYS)
        private val SAMPLE_UPDATED_AT = NOW.minus(2, ChronoUnit.DAYS)
        private val SAMPLE_DRAFT_UPDATED_AT = NOW.minus(90, ChronoUnit.MINUTES)
        private val SAMPLE_BASE_UPDATED_AT = NOW.minus(3, ChronoUnit.HOURS)
        private val SAMPLE_LAST_SYNCED_AT = NOW.minus(6, ChronoUnit.HOURS)
        private val SAMPLE_LAST_PULLED_AT = NOW.minus(1, ChronoUnit.DAYS)
    }
}
