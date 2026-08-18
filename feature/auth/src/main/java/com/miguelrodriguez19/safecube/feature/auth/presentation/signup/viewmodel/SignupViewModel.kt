package com.miguelrodriguez19.safecube.feature.auth.presentation.signup.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.feature.auth.presentation.mapper.AuthUiErrorMapper
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.action.SignupUiAction
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.event.SignupUiEvent
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.state.SignupUiState
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.AuthUiOperationState
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.isRetryable
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountSessionLifecycle: AccountSessionLifecycle,
) : ViewModel() {

    private enum class RetryOperation {
        Register,
        LoginAfterSignup,
    }

    private val mutableUiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<SignupUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<SignupUiEvent> = mutableEvents.asSharedFlow()

    private var retryOperation: RetryOperation? = null

    fun onAction(action: SignupUiAction) {
        when (action) {
            is SignupUiAction.EmailChanged -> onEmailChanged(action.value)
            is SignupUiAction.PasswordChanged -> onPasswordChanged(action.value)
            is SignupUiAction.ConfirmPasswordChanged -> onConfirmPasswordChanged(action.value)
            SignupUiAction.Submit -> submit()
            SignupUiAction.Retry -> retry()
        }
    }

    private fun onEmailChanged(value: String) {
        mutableUiState.update { state ->
            state.copy(
                email = value,
                operationState = state.operationState.afterInputChange(),
                emailErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    private fun onPasswordChanged(value: String) {
        mutableUiState.update { state ->
            state.copy(
                password = value,
                operationState = state.operationState.afterInputChange(),
                passwordErrorRes = null,
                confirmPasswordErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    private fun onConfirmPasswordChanged(value: String) {
        mutableUiState.update { state ->
            state.copy(
                confirmPassword = value,
                operationState = state.operationState.afterInputChange(),
                confirmPasswordErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    private fun submit() {
        if (mutableUiState.value.isRetryable) {
            retry()
        } else {
            signup()
        }
    }

    private fun retry() {
        if (!mutableUiState.value.isRetryable) return

        when (retryOperation) {
            RetryOperation.Register -> signup()
            RetryOperation.LoginAfterSignup -> retryLoginAfterSignup()
            null -> Unit
        }
    }

    private fun signup() {
        val currentState = mutableUiState.value
        if (currentState.isLoading) return

        val sanitizedEmail = currentState.email.trim()
        val password = currentState.password
        val confirmPassword = currentState.confirmPassword

        val emailErrorRes = if (sanitizedEmail.isBlank()) UiR.string.email_is_required else null
        val passwordErrorRes = if (password.isBlank()) UiR.string.password_is_required else null
        val confirmPasswordErrorRes = when {
            confirmPassword.isBlank() -> UiR.string.confirm_password_is_required
            password != confirmPassword -> UiR.string.passwords_do_not_match
            else -> null
        }
        if (emailErrorRes != null || passwordErrorRes != null || confirmPasswordErrorRes != null) {
            retryOperation = null
            mutableUiState.update { state ->
                state.copy(
                    operationState = AuthUiOperationState.ValidationError,
                    emailErrorRes = emailErrorRes,
                    passwordErrorRes = passwordErrorRes,
                    confirmPasswordErrorRes = confirmPasswordErrorRes,
                    errorMessageRes = null,
                    password = "",
                    confirmPassword = "",
                )
            }
            return
        }

        retryOperation = RetryOperation.Register
        mutableUiState.update { state ->
            state.copy(
                operationState = AuthUiOperationState.Loading,
                emailErrorRes = null,
                passwordErrorRes = null,
                confirmPasswordErrorRes = null,
                errorMessageRes = null,
            )
        }

        viewModelScope.launch {
            when (val registerResult = authRepository.register(sanitizedEmail, password)) {
                is AuthResult.Success -> {
                    retryOperation = RetryOperation.LoginAfterSignup
                    loginAfterSignup(
                        email = sanitizedEmail,
                        password = password,
                    )
                }

                is AuthResult.Error -> applyAuthError(
                    error = registerResult.error,
                    retryOperation = RetryOperation.Register,
                )
            }
        }
    }

    private fun retryLoginAfterSignup() {
        val currentState = mutableUiState.value
        if (currentState.isLoading) return

        val email = currentState.email.trim()
        val password = currentState.password
        if (email.isBlank() || password.isBlank()) return

        mutableUiState.update { state ->
            state.copy(
                operationState = AuthUiOperationState.Loading,
                emailErrorRes = null,
                passwordErrorRes = null,
                confirmPasswordErrorRes = null,
                errorMessageRes = null,
            )
        }

        viewModelScope.launch {
            loginAfterSignup(email = email, password = password)
        }
    }

    private suspend fun loginAfterSignup(
        email: String,
        password: String,
    ) {
        when (val loginResult = authRepository.login(email, password)) {
            is AuthResult.Success -> {
                when (accountSessionLifecycle.activateFreshSession(loginResult.data)) {
                    AccountSessionResult.Success -> {
                        retryOperation = null
                        mutableUiState.update { state ->
                            state.copy(
                                operationState = AuthUiOperationState.Idle,
                                password = "",
                                confirmPassword = "",
                            )
                        }
                        mutableEvents.emit(SignupUiEvent.SignupSucceeded)
                    }

                    AccountSessionResult.LocalVaultCleanupFailed -> {
                        retryOperation = null
                        mutableUiState.update { state ->
                            state.copy(
                                operationState = AuthUiOperationState.TerminalError,
                                password = "",
                                confirmPassword = "",
                                errorMessageRes = UiR.string.generic_error,
                            )
                        }
                    }
                }
            }

            is AuthResult.Error -> applyAuthError(
                error = loginResult.error,
                retryOperation = RetryOperation.LoginAfterSignup,
            )
        }
    }

    private fun applyAuthError(
        error: AuthError,
        retryOperation: RetryOperation,
    ) {
        val uiError = AuthUiErrorMapper.map(error)
        this.retryOperation = retryOperation.takeIf { uiError.operationState.isRetryable() }
        mutableUiState.update { state ->
            state.copy(
                operationState = uiError.operationState,
                password = state.password.takeIf { uiError.operationState.isRetryable() }.orEmpty(),
                confirmPassword = state.confirmPassword
                    .takeIf { uiError.operationState.isRetryable() }
                    .orEmpty(),
                emailErrorRes = uiError.fieldErrors[AuthUiErrorMapper.EMAIL],
                passwordErrorRes = uiError.fieldErrors[AuthUiErrorMapper.PASSWORD],
                confirmPasswordErrorRes = uiError.fieldErrors[AuthUiErrorMapper.CONFIRM_PASSWORD]
                    ?: uiError.fieldErrors["confirmPassword"],
                errorMessageRes = uiError.messageRes,
            )
        }
    }

    private fun AuthUiOperationState.afterInputChange() =
        when {
            this == AuthUiOperationState.Loading -> this
            isRetryable() -> this
            else -> AuthUiOperationState.Idle
        }
}
