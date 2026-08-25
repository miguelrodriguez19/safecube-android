package com.miguelrodriguez19.safecube.app.session

import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockAccountSessionValidator
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The vault owns the account binding through its local key material. The app only authorizes that
 * binding while the account session remains valid.
 */
@Singleton
class QuickUnlockAccountSessionValidatorImpl @Inject constructor(
    private val sessionManager: SessionManager,
) : QuickUnlockAccountSessionValidator {
    override fun isValid(accountId: UUID): Boolean = sessionManager.isLoggedIn()
}
