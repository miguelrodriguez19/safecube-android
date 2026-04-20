package com.miguelrodriguez19.safecube.core.storage.local

import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointDao
import com.miguelrodriguez19.safecube.core.storage.SecureItemSyncCheckpointEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.mockk
import java.time.Instant
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SecureItemSyncCheckpointLocalStorageTest {
    private val secureItemSyncCheckpointDao = mockk<SecureItemSyncCheckpointDao>()

    private val target = SecureItemSyncCheckpointLocalStorage(secureItemSyncCheckpointDao)

    @Test
    fun `getLastPulledAt when checkpoint exists then returns persisted instant`() = runBlocking {
        val accountId = UUID.randomUUID()
        val expected = Instant.now()
        coEvery { secureItemSyncCheckpointDao.getLastPulledAt(accountId) } returns expected

        val result = target.getLastPulledAt(accountId)

        assertEquals(expected, result)
        coVerify(exactly = 1) { secureItemSyncCheckpointDao.getLastPulledAt(accountId) }
        confirmVerified(secureItemSyncCheckpointDao)
    }

    @Test
    fun `getLastPulledAt when checkpoint is missing then returns null`() = runBlocking {
        val accountId = UUID.randomUUID()
        coEvery { secureItemSyncCheckpointDao.getLastPulledAt(accountId) } returns null

        val result = target.getLastPulledAt(accountId)

        assertNull(result)
        coVerify(exactly = 1) { secureItemSyncCheckpointDao.getLastPulledAt(accountId) }
        confirmVerified(secureItemSyncCheckpointDao)
    }

    @Test
    fun `updateLastPulledAt when called then upserts checkpoint row`() = runBlocking {
        val accountId = UUID.randomUUID()
        val lastPulledAt = Instant.now()
        coEvery { secureItemSyncCheckpointDao.upsert(any()) } returns Unit

        target.updateLastPulledAt(
            accountId = accountId,
            lastPulledAt = lastPulledAt,
        )

        coVerify(exactly = 1) {
            secureItemSyncCheckpointDao.upsert(
                SecureItemSyncCheckpointEntity(
                    accountId = accountId,
                    lastPulledAt = lastPulledAt,
                ),
            )
        }
        confirmVerified(secureItemSyncCheckpointDao)
    }
}
