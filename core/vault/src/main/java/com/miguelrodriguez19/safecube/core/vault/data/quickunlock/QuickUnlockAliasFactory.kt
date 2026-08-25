package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import java.security.MessageDigest
import java.util.UUID

internal object QuickUnlockAliasFactory {
    const val ALIAS_PREFIX = "safecube.quick_unlock."

    fun aliasFor(accountId: UUID): String = ALIAS_PREFIX + accountHash(accountId)

    fun accountHash(accountId: UUID): String = MessageDigest.getInstance("SHA-256")
        .digest(accountId.toString().toByteArray(Charsets.UTF_8))
        .joinToString(separator = "") { byte -> "%02x".format(byte) }

    fun aadFor(accountId: UUID): ByteArray =
        "accountId:$accountId|purpose:kek".toByteArray(Charsets.UTF_8)
}
