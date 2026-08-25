package com.miguelrodriguez19.safecube.core.vault.domain.quickunlock

import java.util.UUID

/** Port implemented by the app layer; it intentionally has no Android or auth-module dependency. */
interface QuickUnlockAccountSessionValidator {
    fun isValid(accountId: UUID): Boolean
}
