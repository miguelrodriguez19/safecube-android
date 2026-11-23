package com.miguelrodriguez19.safecube.app.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultDataCleaner
import com.miguelrodriguez19.safecube.core.vault.domain.session.QuickUnlockPromptMode
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext

@Singleton
class AccountSessionLifecycleImpl @Inject constructor(
    private val sessionManager: SessionManager,
    private val localVaultDataCleaner: LocalVaultDataCleaner,
    private val vaultSessionManagerProvider: Provider<VaultSessionManager>,
) : AccountSessionLifecycle {
    override suspend fun activateFreshSession(tokens: AuthTokens): AccountSessionResult {
        val vaultLocked = lockVaultForAccountBoundary()
        val cleanupResult = try {
            clearLocalVault()
        } catch (cancellationException: CancellationException) {
            sessionManager.forceLogout(SessionTerminationReason.LocalIntegrityFailure)
            throw cancellationException
        }

        return when {
            vaultLocked && cleanupResult == LocalVaultCleanupResult.Success -> {
                sessionManager.onLoginSuccess(tokens)
                AccountSessionResult.Success
            }

            else -> {
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
        val vaultLocked = lockVaultForAccountBoundary()
        val cleanupResult = try {
            clearLocalVault()
        } catch (_: CancellationException) {
            LocalVaultCleanupResult.Failure
        }
        val result = if (vaultLocked && cleanupResult == LocalVaultCleanupResult.Success) {
            AccountSessionResult.Success
        } else {
            AccountSessionResult.LocalVaultCleanupFailed
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

    private fun lockVaultForAccountBoundary(): Boolean = try {
        vaultSessionManagerProvider.get().lock(QuickUnlockPromptMode.ManualOnly)
        true
    } catch (_: Throwable) {
        false
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
