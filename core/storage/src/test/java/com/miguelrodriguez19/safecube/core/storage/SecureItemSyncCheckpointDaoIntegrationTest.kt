package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Room
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
import java.time.temporal.ChronoUnit

@RunWith(RobolectricTestRunner::class)
class SecureItemSyncCheckpointDaoIntegrationTest {
    private lateinit var database: AppDatabase
    private lateinit var target: SecureItemSyncCheckpointDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
        target = database.secureItemSyncCheckpointDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `getLastPulledAt when checkpoint is missing then returns null`() = runBlocking {
        val accountId = UUID.randomUUID()

        val result = target.getLastPulledAt(accountId)

        assertNull(result)
    }

    @Test
    fun `upsert when called then stores and replaces checkpoint`() = runBlocking {
        val accountId = UUID.randomUUID()
        val firstPulledAt = Instant.now()
        val secondPulledAt = firstPulledAt.plus(1, ChronoUnit.HOURS)
        target.upsert(
            SecureItemSyncCheckpointEntity(
                accountId = accountId,
                lastPulledAt = firstPulledAt,
            ),
        )

        target.upsert(
            SecureItemSyncCheckpointEntity(
                accountId = accountId,
                lastPulledAt = secondPulledAt,
            ),
        )

        val result = target.getLastPulledAt(accountId)

        assertEquals(secondPulledAt, result)
    }
}
