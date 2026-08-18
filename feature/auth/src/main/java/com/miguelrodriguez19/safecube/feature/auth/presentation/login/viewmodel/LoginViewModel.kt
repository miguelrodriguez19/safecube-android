package com.miguelrodriguez19.safecube.feature.auth.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthError
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionLifecycle
import com.miguelrodriguez19.safecube.core.auth.domain.session.AccountSessionResult
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.feature.auth.presentation.mapper.AuthUiErrorMapper
import com.miguelrodriguez19.safecube.feature.auth.presentation.mapper.AuthUiErrorMapper.EMAIL
import com.miguelrodriguez19.safecube.feature.auth.presentation.mapper.AuthUiErrorMapper.PASSWORD
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.action.LoginUiAction
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.event.LoginUiEvent
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.state.LoginUiState
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
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val accountSessionLifecycle: AccountSessionLifecycle,
) : ViewModel() {

    private val mutableUiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<LoginUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<LoginUiEvent> = mutableEvents.asSharedFlow()

    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.EmailChanged -> onEmailChanged(action.value)
            is LoginUiAction.PasswordChanged -> onPasswordChanged(action.value)
            LoginUiAction.Submit -> submit()
            LoginUiAction.Retry -> retry()
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
                errorMessageRes = null,
            )
        }
    }

    private fun submit() {
        if (mutableUiState.value.isRetryable) {
            retry()
        } else {
            login()
        }
    }

    private fun retry() {
        if (!mutableUiState.value.isRetryable) return
        login()
    }

    private fun login() {
        val currentState = mutableUiState.value
        if (currentState.isLoading) return

        val sanitizedEmail = currentState.email.trim()
        val password = currentState.password

        val emailErrorRes = if (sanitizedEmail.isBlank()) UiR.string.email_is_required else null
        val passwordErrorRes = if (password.isBlank()) UiR.string.password_is_required else null
        if (emailErrorRes != null || passwordErrorRes != null) {
            mutableUiState.update { state ->
                state.copy(
                    operationState = AuthUiOperationState.ValidationError,
                    emailErrorRes = emailErrorRes,
                    passwordErrorRes = passwordErrorRes,
                    errorMessageRes = null,
                    password = "",
                )
            }
            return
        }

        mutableUiState.update { state ->
            state.copy(
                operationState = AuthUiOperationState.Loading,
                emailErrorRes = null,
                passwordErrorRes = null,
                errorMessageRes = null,
            )
        }

        viewModelScope.launch {
            when (val result = authRepository.login(sanitizedEmail, password)) {
                is AuthResult.Success -> {
                    when (accountSessionLifecycle.activateFreshSession(result.data)) {
                        AccountSessionResult.Success -> {
                            mutableUiState.update { state ->
                                state.copy(
                                    operationState = AuthUiOperationState.Idle,
                                    password = "",
                                )
                            }
                            mutableEvents.emit(LoginUiEvent.LoginSucceeded)
                        }

                        AccountSessionResult.LocalVaultCleanupFailed -> {
                            mutableUiState.update { state ->
                                state.copy(
                                    operationState = AuthUiOperationState.TerminalError,
                                    password = "",
                                    errorMessageRes = UiR.string.generic_error,
                                )
                            }
                        }
                    }
                }

                is AuthResult.Error -> {
                    applyAuthError(result.error)
                }
            }
        }
    }

    private fun applyAuthError(error: AuthError) {
        val uiError = AuthUiErrorMapper.map(error)
        mutableUiState.update { state ->
            state.copy(
                operationState = uiError.operationState,
                password = state.password.takeIf { uiError.operationState.isRetryable() }.orEmpty(),
                emailErrorRes = uiError.fieldErrors[EMAIL],
                passwordErrorRes = uiError.fieldErrors[PASSWORD],
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
