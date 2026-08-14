package com.miguelrodriguez19.safecube.app.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultDataCleaner
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

@Singleton
class AccountSessionLifecycleImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val localVaultDataCleaner: LocalVaultDataCleaner,
) : AccountSessionLifecycle {
    override suspend fun activateFreshSession(tokens: AuthTokens): AccountSessionResult {
        return when (val cleanupResult = clearLocalVault()) {
            LocalVaultCleanupResult.Success -> {
                sessionManager.onLoginSuccess(tokens)
                AccountSessionResult.Success
            }

            LocalVaultCleanupResult.Failure -> {
                sessionManager.forceLogout(SessionTerminationReason.LocalIntegrityFailure)
                AccountSessionResult.LocalVaultCleanupFailed
            }
        }
    }

    override suspend fun refreshSession(tokens: AuthTokens) {
        sessionManager.onLoginSuccess(tokens)
    }

    override suspend fun terminateSession(
        reason: SessionTerminationReason,
    ): AccountSessionResult = withContext(NonCancellable) {
        val result = when (val cleanupResult = clearLocalVault()) {
            LocalVaultCleanupResult.Success -> AccountSessionResult.Success
            LocalVaultCleanupResult.Failure -> AccountSessionResult.LocalVaultCleanupFailed
        }
        sessionManager.forceLogout(
            reason = if (result == AccountSessionResult.LocalVaultCleanupFailed) {
                SessionTerminationReason.LocalIntegrityFailure
            } else {
                reason
            },
        )
        result
    }

    private suspend fun clearLocalVault(): LocalVaultCleanupResult =
        try {
            localVaultDataCleaner.clear()
        } catch (cancellationException: CancellationException) {
            throw cancellationException
        } catch (throwable: Throwable) {
            LocalVaultCleanupResult.Failure
        }
}
