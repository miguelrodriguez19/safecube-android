package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class QuickUnlockAliasFactoryTest {
    @Test
    fun `alias is deterministic opaque and aad binds the canonical account purpose`() {
        val accountId = UUID.randomUUID()
        val alias = QuickUnlockAliasFactory.aliasFor(accountId)

        assertEquals(alias, QuickUnlockAliasFactory.aliasFor(accountId))
        assertFalse(alias.contains(accountId.toString()))
        assertEquals(
            "accountId:$accountId|purpose:kek",
            QuickUnlockAliasFactory.aadFor(accountId).toString(Charsets.UTF_8),
        )
    }
}
