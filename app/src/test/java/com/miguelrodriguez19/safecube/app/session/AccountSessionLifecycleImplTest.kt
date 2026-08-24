package com.miguelrodriguez19.safecube.app.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.auth.domain.repository.TokenStorage
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManagerImpl
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultDataCleaner
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.justRun
import io.mockk.mockk
import java.time.Instant
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AccountSessionLifecycleImplTest {
    private val sessionManager = mockk<SessionManager>()
    private val localVaultDataCleaner = mockk<LocalVaultDataCleaner>()
    private val target = AccountSessionLifecycleImpl(
        sessionManager = sessionManager,
        localVaultDataCleaner = localVaultDataCleaner,
    )

    @Test
    fun `fresh activation stores tokens only after local cleanup succeeds`() = runBlocking {
        val tokens = tokens()
        coEvery { localVaultDataCleaner.clear() } returns LocalVaultCleanupResult.Success
        justRun { sessionManager.onLoginSuccess(tokens) }

        val result = target.activateFreshSession(tokens)

        assertEquals(AccountSessionResult.Success, result)
        coVerifyOrder {
            localVaultDataCleaner.clear()
            sessionManager.onLoginSuccess(tokens)
        }
        coVerify(exactly = 0) { sessionManager.forceLogout(any()) }
    }

    @Test
    fun `fresh activation remains logged out when local cleanup fails`() = runBlocking {
        val tokens = tokens()
        coEvery {
            localVaultDataCleaner.clear()
        } returns LocalVaultCleanupResult.Failure
        justRun { sessionManager.forceLogout(any()) }

        val result = target.activateFreshSession(tokens)

        assertEquals(AccountSessionResult.LocalVaultCleanupFailed, result)
        coVerify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        coVerify(exactly = 1) {
            sessionManager.forceLogout(SessionTerminationReason.LocalIntegrityFailure)
        }
    }

    @Test
    fun `token refresh does not clear local vault data`() = runBlocking {
        val tokens = tokens()
        justRun { sessionManager.onLoginSuccess(tokens) }

        target.refreshSession(tokens)

        coVerify(exactly = 1) { sessionManager.onLoginSuccess(tokens) }
        coVerify(exactly = 0) { localVaultDataCleaner.clear() }
    }

    @Test
    fun `termination clears tokens even when local cleanup fails`() = runBlocking {
        coEvery {
            localVaultDataCleaner.clear()
        } returns LocalVaultCleanupResult.Failure
        justRun { sessionManager.forceLogout(any()) }

        val result = target.terminateSession(SessionTerminationReason.SessionExpired)

        assertEquals(AccountSessionResult.LocalVaultCleanupFailed, result)
        coVerifyOrder {
            localVaultDataCleaner.clear()
            sessionManager.forceLogout(SessionTerminationReason.LocalIntegrityFailure)
        }
    }

    @Test
    fun `termination exposes the requested reason after successful cleanup`() = runBlocking {
        coEvery { localVaultDataCleaner.clear() } returns LocalVaultCleanupResult.Success
        justRun {
            sessionManager.forceLogout(SessionTerminationReason.SessionExpired)
        }

        val result = target.terminateSession(SessionTerminationReason.SessionExpired)

        assertEquals(AccountSessionResult.Success, result)
        coVerifyOrder {
            localVaultDataCleaner.clear()
            sessionManager.forceLogout(SessionTerminationReason.SessionExpired)
        }
    }

    @Test
    fun `termination clears tokens after vault cleanup contract succeeds`() = runBlocking {
        val tokenStorage = InMemoryTokenStorage(
            accessToken = "access-token",
            refreshToken = "refresh-token",
        )
        val realSessionManager = SessionManagerImpl(tokenStorage)
        val cleaner = mockk<LocalVaultDataCleaner>()
        coEvery { cleaner.clear() } returns LocalVaultCleanupResult.Success
        val lifecycle = AccountSessionLifecycleImpl(
            sessionManager = realSessionManager,
            localVaultDataCleaner = cleaner,
        )

        val result = lifecycle.terminateSession(SessionTerminationReason.ManualLogout)

        assertEquals(AccountSessionResult.Success, result)
        assertNull(tokenStorage.getAccessToken())
        assertNull(tokenStorage.getRefreshToken())
        coVerify(exactly = 1) { cleaner.clear() }
    }

    private fun tokens() = AuthTokens(
        accessToken = "access-token",
        refreshToken = "refresh-token",
        issuedAt = Instant.now(),
    )

    private class InMemoryTokenStorage(
        private var accessToken: String?,
        private var refreshToken: String?,
    ) : TokenStorage {
        override fun saveTokens(
            accessToken: String,
            refreshToken: String,
            issuedAt: Instant?,
        ) {
            this.accessToken = accessToken
            this.refreshToken = refreshToken
        }

        override fun getAccessToken(): String? = accessToken

        override fun getRefreshToken(): String? = refreshToken

        override fun getIssuedAt(): Instant? = null

        override fun clear() {
            accessToken = null
            refreshToken = null
        }
    }
}
