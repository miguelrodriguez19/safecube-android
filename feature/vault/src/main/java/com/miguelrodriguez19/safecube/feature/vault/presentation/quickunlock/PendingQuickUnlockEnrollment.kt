package com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock

/**
 * Deliberately process-local. A requested enrollment must not survive process death because it
 * requires a fresh passphrase unlock in the same process.
 */
object PendingQuickUnlockEnrollment {
    private var pending = false

    @Synchronized
    fun request() {
        pending = true
    }

    @Synchronized
    fun consume(): Boolean = pending.also { pending = false }

    @Synchronized
    fun clear() {
        pending = false
    }
}
