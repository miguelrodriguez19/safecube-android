package com.miguelrodriguez19.safecube.app.session

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.auth.domain.model.SessionTerminationReason
import com.miguelrodriguez19.safecube.core.auth.domain.repository.TokenStorage
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManagerImpl
import com.miguelrodriguez19.safecube.core.vault.domain.model.VaultState
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultCleanupResult
import com.miguelrodriguez19.safecube.core.vault.domain.session.LocalVaultDataCleaner
import com.miguelrodriguez19.safecube.core.vault.domain.session.QuickUnlockPromptMode
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultSessionManager
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.GateDestination
import com.miguelrodriguez19.safecube.app.presentation.navigation.gate.resolveGateDestination
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import java.time.Instant
import javax.inject.Provider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertNull
import org.junit.Assert.fail
import org.junit.Test

class AccountSessionLifecycleImplTest {
    private val sessionManager = mockk<SessionManager>()
    private val localVaultDataCleaner = mockk<LocalVaultDataCleaner>()
    private val vaultSessionManager = mockk<VaultSessionManager>()
    private val vaultState = MutableStateFlow<VaultState>(VaultState.Unlocked)
    private val vaultSessionManagerProvider = Provider { vaultSessionManager }

    init {
        every { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) } answers {
            vaultState.value = VaultState.Locked
        }
        every { vaultSessionManager.vaultState } returns vaultState
        every { vaultSessionManager.isUnlocked() } answers {
            vaultState.value == VaultState.Unlocked
        }
    }

    private val target = AccountSessionLifecycleImpl(
        sessionManager = sessionManager,
        localVaultDataCleaner = localVaultDataCleaner,
        vaultSessionManagerProvider = vaultSessionManagerProvider,
    )

    @Test
    fun `fresh activation stores tokens only after local cleanup succeeds`() = runBlocking {
        val tokens = tokens()
        coEvery { localVaultDataCleaner.clear() } returns LocalVaultCleanupResult.Success
        justRun { sessionManager.onLoginSuccess(tokens) }

        val result = target.activateFreshSession(tokens)

        assertEquals(AccountSessionResult.Success, result)
        assertEquals(VaultState.Locked, vaultState.value)
        coVerifyOrder {
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
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
        assertEquals(VaultState.Locked, vaultState.value)
        coVerifyOrder {
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
            localVaultDataCleaner.clear()
        }
        coVerify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        coVerify(exactly = 1) {
            sessionManager.forceLogout(SessionTerminationReason.LocalIntegrityFailure)
        }
    }

    @Test
    fun `fresh activation still attempts cleanup and fails closed when vault lock throws`() = runBlocking {
        val tokens = tokens()
        every {
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
        } throws IllegalStateException("lock unavailable")
        coEvery { localVaultDataCleaner.clear() } returns LocalVaultCleanupResult.Success
        justRun { sessionManager.forceLogout(any()) }

        val result = target.activateFreshSession(tokens)

        assertEquals(AccountSessionResult.LocalVaultCleanupFailed, result)
        coVerify(exactly = 1) { localVaultDataCleaner.clear() }
        coVerify(exactly = 0) { sessionManager.onLoginSuccess(any()) }
        coVerify(exactly = 1) {
            sessionManager.forceLogout(SessionTerminationReason.LocalIntegrityFailure)
        }
    }

    @Test
    fun `fresh activation logs out and rethrows when cleanup is cancelled`() = runBlocking {
        val tokens = tokens()
        val cancellation = CancellationException("cleanup cancelled")
        coEvery { localVaultDataCleaner.clear() } throws cancellation
        justRun { sessionManager.forceLogout(any()) }

        try {
            target.activateFreshSession(tokens)
            fail("Expected cleanup cancellation")
        } catch (actual: CancellationException) {
            assertSame(cancellation, actual)
        }

        assertEquals(VaultState.Locked, vaultState.value)
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

        assertEquals(VaultState.Unlocked, vaultState.value)
        coVerify(exactly = 1) { sessionManager.onLoginSuccess(tokens) }
        coVerify(exactly = 0) { localVaultDataCleaner.clear() }
        coVerify(exactly = 0) { vaultSessionManager.lock(any()) }
    }

    @Test
    fun `terminating previous account before fresh activation routes next account to unlock`() = runBlocking {
        coEvery { localVaultDataCleaner.clear() } returns
            LocalVaultCleanupResult.Success andThen LocalVaultCleanupResult.Success
        justRun { sessionManager.forceLogout(any()) }
        justRun { sessionManager.onLoginSuccess(any()) }

        val terminationResult = target.terminateSession(SessionTerminationReason.ManualLogout)
        val activationResult = target.activateFreshSession(tokens())

        assertEquals(AccountSessionResult.Success, terminationResult)
        assertEquals(AccountSessionResult.Success, activationResult)
        assertEquals(VaultState.Locked, vaultState.value)
        assertEquals(GateDestination.UnlockVault, resolveGateDestination(vaultState.value))
        coVerify(exactly = 2) { vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly) }
        coVerify(exactly = 2) { localVaultDataCleaner.clear() }
    }

    @Test
    fun `termination clears tokens even when local cleanup fails`() = runBlocking {
        coEvery {
            localVaultDataCleaner.clear()
        } returns LocalVaultCleanupResult.Failure
        justRun { sessionManager.forceLogout(any()) }

        val result = target.terminateSession(SessionTerminationReason.SessionExpired)

        assertEquals(AccountSessionResult.LocalVaultCleanupFailed, result)
        assertEquals(VaultState.Locked, vaultState.value)
        coVerifyOrder {
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
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
        assertEquals(VaultState.Locked, vaultState.value)
        coVerifyOrder {
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
            localVaultDataCleaner.clear()
            sessionManager.forceLogout(SessionTerminationReason.SessionExpired)
        }
    }

    @Test
    fun `termination logs out when cleanup throws cancellation`() = runBlocking {
        coEvery { localVaultDataCleaner.clear() } throws CancellationException("cleanup cancelled")
        justRun { sessionManager.forceLogout(any()) }

        val result = target.terminateSession(SessionTerminationReason.SessionExpired)

        assertEquals(AccountSessionResult.LocalVaultCleanupFailed, result)
        assertEquals(VaultState.Locked, vaultState.value)
        coVerifyOrder {
            vaultSessionManager.lock(QuickUnlockPromptMode.ManualOnly)
            localVaultDataCleaner.clear()
            sessionManager.forceLogout(SessionTerminationReason.LocalIntegrityFailure)
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
            vaultSessionManagerProvider = vaultSessionManagerProvider,
        )

        val result = lifecycle.terminateSession(SessionTerminationReason.ManualLogout)

        assertEquals(AccountSessionResult.Success, result)
        assertEquals(VaultState.Locked, vaultState.value)
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
