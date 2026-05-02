package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.test.core.app.ApplicationProvider
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
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
    fun `migration 2 to 4 when applied then keeps secure items and adds sync columns plus checkpoint table`() =
        runBlocking {
            createVersion2Database()

            val migratedDatabase = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                TEST_DATABASE_NAME,
            )
                .addMigrations(StorageMigrations.MIGRATION_2_3)
                .addMigrations(StorageMigrations.MIGRATION_3_4)
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

    private companion object {
        private const val TEST_DATABASE_NAME = "storage-migration-test.db"
        private val SAMPLE_LOGICAL_ITEM_ID = UUID.randomUUID()
        private val SAMPLE_REMOTE_ITEM_ID = UUID.randomUUID()
        private val SAMPLE_ACCOUNT_ID = UUID.randomUUID()
        private val SAMPLE_CREATED_AT = Instant.parse("2026-04-15T10:00:00Z")
        private val SAMPLE_UPDATED_AT = Instant.parse("2026-04-16T10:00:00Z")
        private val SAMPLE_LAST_PULLED_AT = Instant.parse("2026-04-17T12:45:00Z")
    }
}
