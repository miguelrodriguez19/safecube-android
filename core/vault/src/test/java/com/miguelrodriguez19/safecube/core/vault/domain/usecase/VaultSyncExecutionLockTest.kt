package com.miguelrodriguez19.safecube.core.vault.domain.usecase

import com.miguelrodriguez19.safecube.core.vault.domain.usecase.sync.VaultSyncExecutionLock
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultSyncExecutionLockTest {
    private val target = VaultSyncExecutionLock()

    @Test
    fun `withLock when block starts then exposes syncing true and resets to false when done`() = runBlocking {
        val entered = CompletableDeferred<Unit>()
        val release = CompletableDeferred<Unit>()

        val job = async {
            target.withLock {
                entered.complete(Unit)
                release.await()
            }
        }

        entered.await()
        assertTrue(target.isSyncing.value)
        release.complete(Unit)
        job.await()
        assertFalse(target.isSyncing.value)
    }

    @Test
    fun `withLock when block throws then resets syncing state to false`() = runBlocking {
        runCatching {
            target.withLock<Unit> {
                assertTrue(target.isSyncing.value)
                error("boom")
            }
        }

        assertFalse(target.isSyncing.value)
    }

    @Test
    fun `tryLock when lock is free then acquires lock and marks syncing until unlock`() {
        val acquired = target.tryLock()

        assertTrue(acquired)
        assertTrue(target.isSyncing.value)

        target.unlock()
        assertFalse(target.isSyncing.value)
    }

    @Test
    fun `tryLock when lock is already held then returns false and keeps syncing true`() {
        val firstAcquire = target.tryLock()
        assertTrue(firstAcquire)

        val secondAcquire = target.tryLock()
        assertFalse(secondAcquire)
        assertTrue(target.isSyncing.value)

        target.unlock()
        assertFalse(target.isSyncing.value)
    }

    @Test
    fun `withLock when called concurrently then serializes executions`() = runBlocking {
        val enteredFirst = CompletableDeferred<Unit>()
        val releaseFirst = CompletableDeferred<Unit>()
        var enteredCount = 0

        val first = async {
            target.withLock {
                enteredCount++
                enteredFirst.complete(Unit)
                releaseFirst.await()
            }
        }

        enteredFirst.await()
        val second = async {
            target.withLock {
                enteredCount++
            }
        }

        delay(50)
        assertEquals(1, enteredCount)

        releaseFirst.complete(Unit)
        first.await()
        second.await()
        assertEquals(2, enteredCount)
        assertFalse(target.isSyncing.value)
    }
}
