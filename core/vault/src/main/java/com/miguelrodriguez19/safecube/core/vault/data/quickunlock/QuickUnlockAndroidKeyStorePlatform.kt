package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import java.util.UUID
import javax.crypto.SecretKey

/** Android boundary; keeping it separate makes the cryptographic operation lifecycle JVM-testable. */
internal interface QuickUnlockAndroidKeyStorePlatform {
    fun isSupported(): Boolean

    fun createKey(accountId: UUID): SecretKey

    fun loadKey(accountId: UUID): SecretKey?

    fun hasAlias(accountId: UUID): Boolean

    fun delete(accountId: UUID): Boolean

    fun deleteAll(): Boolean
}
