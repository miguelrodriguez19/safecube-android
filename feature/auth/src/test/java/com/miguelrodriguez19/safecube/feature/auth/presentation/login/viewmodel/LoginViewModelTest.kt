@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.miguelrodriguez19.safecube.feature.auth.presentation.login.viewmodel

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.action.LoginUiAction
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.event.LoginUiEvent
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.AuthUiOperationState
import com.miguelrodriguez19.safecube.feature.auth.test.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository = mockk<AuthRepository>()
    private val accountSessionLifecycle = mockk<AccountSessionLifecycle>()

    @Test
    fun `submit when fields are invalid then exposes validation and does not call repository`() = runTest {
        val target = LoginViewModel(authRepository, accountSessionLifecycle)
        val password = sensitiveValue()

        target.onAction(LoginUiAction.EmailChanged(""))
        target.onAction(LoginUiAction.PasswordChanged(password))
        target.onAction(LoginUiAction.Submit)

        assertEquals(AuthUiOperationState.ValidationError, target.uiState.value.operationState)
        assertFalse(target.uiState.value.password.isNotEmpty())
        coVerify(exactly = 0) { authRepository.login(any(), any()) }
    }

    @Test
    fun `submit when credentials are invalid then clears password and preserves email`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        coEvery { authRepository.login(email, password) } returns AuthResult.Error(
            AuthError.InvalidCredentials,
        )
        val target = LoginViewModel(authRepository, accountSessionLifecycle)

        enterCredentials(target, email, password)
        target.onAction(LoginUiAction.Submit)
        advanceUntilIdle()

        assertEquals(AuthUiOperationState.InvalidCredentials, target.uiState.value.operationState)
        assertEquals(email, target.uiState.value.email)
        assertTrue(target.uiState.value.password.isEmpty())
        coVerify(exactly = 1) { authRepository.login(email, password) }
    }

    @Test
    fun `submit when transport fails then exposes offline retry and retry repeats once`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        coEvery { authRepository.login(email, password) } returnsMany listOf(
            AuthResult.Error(
                AuthError.Unknown(
                    failure = NetworkFailureClassifier.fromThrowable(IOException()),
                ),
            ),
            AuthResult.Error(AuthError.InvalidCredentials),
        )
        val target = LoginViewModel(authRepository, accountSessionLifecycle)

        enterCredentials(target, email, password)
        target.onAction(LoginUiAction.Submit)
        advanceUntilIdle()

        assertEquals(AuthUiOperationState.OfflineOrTimeout, target.uiState.value.operationState)
        assertEquals(email, target.uiState.value.email)
        assertEquals(password, target.uiState.value.password)

        target.onAction(LoginUiAction.Retry)
        advanceUntilIdle()

        assertEquals(AuthUiOperationState.InvalidCredentials, target.uiState.value.operationState)
        assertTrue(target.uiState.value.password.isEmpty())
        coVerify(exactly = 2) { authRepository.login(email, password) }
    }

    @Test
    fun `submit when service is unavailable then exposes service retry`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        coEvery { authRepository.login(email, password) } returns AuthResult.Error(
            AuthError.Unknown(failure = NetworkFailureClassifier.fromHttpStatus(503)),
        )
        val target = LoginViewModel(authRepository, accountSessionLifecycle)

        enterCredentials(target, email, password)
        target.onAction(LoginUiAction.Submit)
        advanceUntilIdle()

        assertEquals(AuthUiOperationState.ServiceUnavailable, target.uiState.value.operationState)
        assertTrue(target.uiState.value.isRetryable)
        assertEquals(password, target.uiState.value.password)
    }

    @Test
    fun `submit when timeout fails then exposes offline retry`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        coEvery { authRepository.login(email, password) } returns AuthResult.Error(
            AuthError.Unknown(
                failure = NetworkFailureClassifier.fromThrowable(SocketTimeoutException()),
            ),
        )
        val target = LoginViewModel(authRepository, accountSessionLifecycle)

        enterCredentials(target, email, password)
        target.onAction(LoginUiAction.Submit)
        advanceUntilIdle()

        assertEquals(AuthUiOperationState.OfflineOrTimeout, target.uiState.value.operationState)
    }

    @Test
    fun `submit when terminal unexpected error then clears password`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        coEvery { authRepository.login(email, password) } returns AuthResult.Error(
            AuthError.Unknown(failure = NetworkFailureClassifier.unknown()),
        )
        val target = LoginViewModel(authRepository, accountSessionLifecycle)

        enterCredentials(target, email, password)
        target.onAction(LoginUiAction.Submit)
        advanceUntilIdle()

        assertEquals(AuthUiOperationState.TerminalError, target.uiState.value.operationState)
        assertTrue(target.uiState.value.password.isEmpty())
    }

    @Test
    fun `submit twice while loading then sends one request`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        val response = CompletableDeferred<AuthResult<AuthTokens>>()
        coEvery { authRepository.login(email, password) } coAnswers { response.await() }
        val target = LoginViewModel(authRepository, accountSessionLifecycle)

        enterCredentials(target, email, password)
        target.onAction(LoginUiAction.Submit)
        target.onAction(LoginUiAction.Submit)
        advanceUntilIdle()

        assertEquals(AuthUiOperationState.Loading, target.uiState.value.operationState)
        coVerify(exactly = 1) { authRepository.login(email, password) }

        response.complete(AuthResult.Error(AuthError.InvalidCredentials))
        advanceUntilIdle()
    }

    @Test
    fun `submit when login succeeds then clears password and emits one event`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        val tokens = authTokens()
        coEvery { authRepository.login(email, password) } returns AuthResult.Success(tokens)
        coEvery { accountSessionLifecycle.activateFreshSession(tokens) } returns AccountSessionResult.Success
        val target = LoginViewModel(authRepository, accountSessionLifecycle)
        val event = async { target.events.first() }

        enterCredentials(target, email, password)
        target.onAction(LoginUiAction.Submit)
        advanceUntilIdle()

        assertEquals(LoginUiEvent.LoginSucceeded, event.await())
        assertEquals(AuthUiOperationState.Idle, target.uiState.value.operationState)
        assertTrue(target.uiState.value.password.isEmpty())
        coVerify(exactly = 1) { accountSessionLifecycle.activateFreshSession(tokens) }
    }

    private fun enterCredentials(target: LoginViewModel, email: String, password: String) {
        target.onAction(LoginUiAction.EmailChanged(email))
        target.onAction(LoginUiAction.PasswordChanged(password))
    }

    private fun emailValue(): String = "${UUID.randomUUID()}@example.invalid"

    private fun sensitiveValue(): String = UUID.randomUUID().toString()

    private fun authTokens(): AuthTokens = AuthTokens(
        accessToken = sensitiveValue(),
        refreshToken = sensitiveValue(),
        issuedAt = null,
    )
}
