package com.miguelrodriguez19.safecube.core.storage

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
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
    fun `get cursor when checkpoint is missing then returns null`() = runBlocking {
        val accountId = UUID.randomUUID()

        val result = target.getLastAppliedChangeSequence(accountId)

        assertNull(result)
    }

    @Test
    fun `upsert when called then stores and replaces checkpoint`() = runBlocking {
        val accountId = UUID.randomUUID()
        val firstSequence = 17L
        val secondSequence = 23L
        target.upsert(
            SecureItemSyncCheckpointEntity(
                accountId = accountId,
                lastAppliedChangeSequence = firstSequence,
            ),
        )

        target.upsert(
            SecureItemSyncCheckpointEntity(
                accountId = accountId,
                lastAppliedChangeSequence = secondSequence,
            ),
        )

        val result = target.getLastAppliedChangeSequence(accountId)

        assertEquals(secondSequence, result)
    }
}
