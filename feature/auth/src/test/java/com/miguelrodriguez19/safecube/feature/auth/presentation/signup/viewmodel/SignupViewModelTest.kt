@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package com.miguelrodriguez19.safecube.feature.auth.presentation.signup.viewmodel

import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthTokens
import com.miguelrodriguez19.safecube.core.auth.domain.model.RegisteredAccount
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import com.miguelrodriguez19.safecube.core.network.domain.model.NetworkFailureClassifier
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.action.SignupUiAction
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.event.SignupUiEvent
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.AuthUiOperationState
import com.miguelrodriguez19.safecube.feature.auth.test.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.util.UUID
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class SignupViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val authRepository = mockk<AuthRepository>()
    private val accountSessionLifecycle = mockk<AccountSessionLifecycle>()

    @Test
    fun `submit when fields are invalid then exposes validation and does not call repository`() = runTest {
        val target = SignupViewModel(authRepository, accountSessionLifecycle)

        enterCredentials(target, emailValue(), sensitiveValue(), sensitiveValue())
        target.onAction(SignupUiAction.Submit)

        assertEquals(AuthUiOperationState.ValidationError, target.uiState.value.operationState)
        assertTrue(target.uiState.value.password.isEmpty())
        assertTrue(target.uiState.value.confirmPassword.isEmpty())
        coVerify(exactly = 0) { authRepository.register(any(), any()) }
    }

    @Test
    fun `submit when account already exists then clears password and confirmation`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        coEvery { authRepository.register(email, password) } returns AuthResult.Error(
            AuthError.AccountAlreadyExists,
        )
        val target = SignupViewModel(authRepository, accountSessionLifecycle)

        enterCredentials(target, email, password, password)
        target.onAction(SignupUiAction.Submit)
        advanceUntilIdle()

        assertEquals(AuthUiOperationState.AccountAlreadyExists, target.uiState.value.operationState)
        assertEquals(email, target.uiState.value.email)
        assertTrue(target.uiState.value.password.isEmpty())
        assertTrue(target.uiState.value.confirmPassword.isEmpty())
    }

    @Test
    fun `submit when register is forbidden then exposes terminal forbidden state`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        coEvery { authRepository.register(email, password) } returns AuthResult.Error(AuthError.Forbidden)
        val target = SignupViewModel(authRepository, accountSessionLifecycle)

        enterCredentials(target, email, password, password)
        target.onAction(SignupUiAction.Submit)
        advanceUntilIdle()

        assertEquals(AuthUiOperationState.Forbidden, target.uiState.value.operationState)
        assertTrue(target.uiState.value.password.isEmpty())
        assertTrue(target.uiState.value.confirmPassword.isEmpty())
    }

    @Test
    fun `submit when register is unavailable then retry repeats register and preserves secrets until terminal result`() =
        runTest {
            val email = emailValue()
            val password = sensitiveValue()
            coEvery { authRepository.register(email, password) } returnsMany listOf(
                AuthResult.Error(
                    AuthError.Unknown(failure = NetworkFailureClassifier.fromHttpStatus(503)),
                ),
                AuthResult.Error(AuthError.AccountAlreadyExists),
            )
            val target = SignupViewModel(authRepository, accountSessionLifecycle)

            enterCredentials(target, email, password, password)
            target.onAction(SignupUiAction.Submit)
            advanceUntilIdle()

            assertEquals(AuthUiOperationState.ServiceUnavailable, target.uiState.value.operationState)
            assertEquals(password, target.uiState.value.password)
            assertEquals(password, target.uiState.value.confirmPassword)

            target.onAction(SignupUiAction.Retry)
            advanceUntilIdle()

            assertEquals(AuthUiOperationState.AccountAlreadyExists, target.uiState.value.operationState)
            assertTrue(target.uiState.value.password.isEmpty())
            assertTrue(target.uiState.value.confirmPassword.isEmpty())
            coVerify(exactly = 2) { authRepository.register(email, password) }
        }

    @Test
    fun `submit when login after successful register is unavailable then retry only repeats login`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        val registeredAccount = RegisteredAccount(UUID.randomUUID(), null)
        val tokens = authTokens()
        coEvery { authRepository.register(email, password) } returns AuthResult.Success(registeredAccount)
        coEvery { authRepository.login(email, password) } returnsMany listOf(
            AuthResult.Error(
                AuthError.Unknown(failure = NetworkFailureClassifier.fromHttpStatus(503)),
            ),
            AuthResult.Success(tokens),
        )
        coEvery { accountSessionLifecycle.activateFreshSession(tokens) } returns AccountSessionResult.Success
        val target = SignupViewModel(authRepository, accountSessionLifecycle)
        val event = async { target.events.first() }

        enterCredentials(target, email, password, password)
        target.onAction(SignupUiAction.Submit)
        advanceUntilIdle()

        assertEquals(AuthUiOperationState.ServiceUnavailable, target.uiState.value.operationState)
        assertEquals(password, target.uiState.value.password)
        assertEquals(password, target.uiState.value.confirmPassword)
        coVerify(exactly = 1) { authRepository.register(email, password) }
        coVerify(exactly = 1) { authRepository.login(email, password) }

        target.onAction(SignupUiAction.Retry)
        advanceUntilIdle()

        assertEquals(SignupUiEvent.SignupSucceeded, event.await())
        assertEquals(AuthUiOperationState.Idle, target.uiState.value.operationState)
        assertTrue(target.uiState.value.password.isEmpty())
        assertTrue(target.uiState.value.confirmPassword.isEmpty())
        coVerify(exactly = 1) { authRepository.register(email, password) }
        coVerify(exactly = 2) { authRepository.login(email, password) }
    }

    @Test
    fun `submit twice while register is loading then sends one request`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        val response = CompletableDeferred<AuthResult<RegisteredAccount>>()
        coEvery { authRepository.register(email, password) } coAnswers { response.await() }
        val target = SignupViewModel(authRepository, accountSessionLifecycle)

        enterCredentials(target, email, password, password)
        target.onAction(SignupUiAction.Submit)
        target.onAction(SignupUiAction.Submit)
        advanceUntilIdle()

        assertEquals(AuthUiOperationState.Loading, target.uiState.value.operationState)
        coVerify(exactly = 1) { authRepository.register(email, password) }

        response.complete(AuthResult.Error(AuthError.AccountAlreadyExists))
        advanceUntilIdle()
    }

    @Test
    fun `submit when signup and login succeed then clears secrets and emits one event`() = runTest {
        val email = emailValue()
        val password = sensitiveValue()
        val registeredAccount = RegisteredAccount(UUID.randomUUID(), null)
        val tokens = authTokens()
        coEvery { authRepository.register(email, password) } returns AuthResult.Success(registeredAccount)
        coEvery { authRepository.login(email, password) } returns AuthResult.Success(tokens)
        coEvery { accountSessionLifecycle.activateFreshSession(tokens) } returns AccountSessionResult.Success
        val target = SignupViewModel(authRepository, accountSessionLifecycle)
        val event = async { target.events.first() }

        enterCredentials(target, email, password, password)
        target.onAction(SignupUiAction.Submit)
        advanceUntilIdle()

        assertEquals(SignupUiEvent.SignupSucceeded, event.await())
        assertEquals(AuthUiOperationState.Idle, target.uiState.value.operationState)
        assertTrue(target.uiState.value.password.isEmpty())
        assertTrue(target.uiState.value.confirmPassword.isEmpty())
    }

    private fun enterCredentials(
        target: SignupViewModel,
        email: String,
        password: String,
        confirmation: String,
    ) {
        target.onAction(SignupUiAction.EmailChanged(email))
        target.onAction(SignupUiAction.PasswordChanged(password))
        target.onAction(SignupUiAction.ConfirmPasswordChanged(confirmation))
    }

    private fun emailValue(): String = "${UUID.randomUUID()}@example.invalid"

    private fun sensitiveValue(): String = UUID.randomUUID().toString()

    private fun authTokens(): AuthTokens = AuthTokens(
        accessToken = sensitiveValue(),
        refreshToken = sensitiveValue(),
        issuedAt = null,
    )
}
