package com.miguelrodriguez19.safecube.core.vault.data.session

import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/** Process-local request used to continue a Settings enrolment after passphrase unlock. */
@Singleton
internal class PendingQuickUnlockEnrollmentStore @Inject constructor() {
    private var pendingAccountId: UUID? = null

    @Synchronized
    fun request(accountId: UUID) {
        pendingAccountId = accountId
    }

    @Synchronized
    fun consume(accountId: UUID): Boolean {
        val matches = pendingAccountId == accountId
        pendingAccountId = null
        return matches
    }

    @Synchronized
    fun clear() {
        pendingAccountId = null
    }
}
