package com.miguelrodriguez19.safecube.core.vault.data.session

import java.util.UUID
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PendingQuickUnlockEnrollmentStoreTest {
    private lateinit var target: PendingQuickUnlockEnrollmentStore

    @Before
    fun setUp() {
        target = PendingQuickUnlockEnrollmentStore()
    }

    @Test
    fun `request for account then consume same account succeeds once`() {
        val accountId = UUID.randomUUID()
        target.request(accountId)

        assertTrue(target.consume(accountId))
        assertFalse(target.consume(accountId))
    }

    @Test
    fun `request for account then consume different account rejects and clears request`() {
        val requestedAccountId = UUID.randomUUID()
        val activeAccountId = UUID.randomUUID()
        target.request(requestedAccountId)

        assertFalse(target.consume(activeAccountId))
        assertFalse(target.consume(requestedAccountId))
    }

    @Test
    fun `clear removes pending request without persistence`() {
        target.request(UUID.randomUUID())

        target.clear()

        assertFalse(target.consume(UUID.randomUUID()))
    }
}
