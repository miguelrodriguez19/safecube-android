package com.miguelrodriguez19.safecube.app.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultDataCleaner
import io.mockk.coEvery
import io.mockk.coJustRun
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.justRun
import io.mockk.mockk
import java.time.Instant
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
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
        coVerify(exactly = 0) { sessionManager.forceLogout() }
    }

    @Test
    fun `fresh activation remains logged out when local cleanup fails`() = runBlocking {
        val tokens = tokens()
        val cause = IllegalStateException("Room unavailable")
        coEvery {
            localVaultDataCleaner.clear()
        } returns LocalVaultCleanupResult.Failure(cause)
        justRun { sessionManager.forceLogout() }

        val result = target.activateFreshSession(tokens)

        assertSame(cause, (result as AccountSessionResult.LocalVaultCleanupFailed).cause)
        coVerify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        coVerify(exactly = 1) { sessionManager.forceLogout() }
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
        val cause = IllegalStateException("Room unavailable")
        coEvery {
            localVaultDataCleaner.clear()
        } returns LocalVaultCleanupResult.Failure(cause)
        justRun { sessionManager.forceLogout() }

        val result = target.terminateSession()

        assertSame(cause, (result as AccountSessionResult.LocalVaultCleanupFailed).cause)
        coVerifyOrder {
            localVaultDataCleaner.clear()
            sessionManager.forceLogout()
        }
    }

    private fun tokens() = AuthTokens(
        accessToken = "access-token",
        refreshToken = "refresh-token",
        issuedAt = Instant.parse("2026-07-28T10:00:00Z"),
    )
}
