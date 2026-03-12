package com.miguelrodriguez19.safecube.feature.auth.presentation.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.feature.auth.presentation.mapper.AuthUiErrorMapper
import com.miguelrodriguez19.safecube.feature.auth.presentation.mapper.AuthUiErrorMapper.EMAIL
import com.miguelrodriguez19.safecube.feature.auth.presentation.mapper.AuthUiErrorMapper.PASSWORD
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.action.LoginUiAction
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.event.LoginUiEvent
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.state.LoginUiState
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
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val mutableUiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = mutableUiState.asStateFlow()

    private val mutableEvents = MutableSharedFlow<LoginUiEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<LoginUiEvent> = mutableEvents.asSharedFlow()

    fun onAction(action: LoginUiAction) {
        when (action) {
            is LoginUiAction.EmailChanged -> onEmailChanged(action.value)
            is LoginUiAction.PasswordChanged -> onPasswordChanged(action.value)
            LoginUiAction.Submit -> login()
        }
    }

    private fun onEmailChanged(value: String) {
        mutableUiState.update { state ->
            state.copy(
                email = value,
                emailErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    private fun onPasswordChanged(value: String) {
        mutableUiState.update { state ->
            state.copy(
                password = value,
                passwordErrorRes = null,
                errorMessageRes = null,
            )
        }
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
                    emailErrorRes = emailErrorRes,
                    passwordErrorRes = passwordErrorRes,
                    errorMessageRes = null,
                )
            }
            return
        }

        mutableUiState.update { state ->
            state.copy(
                isLoading = true,
                emailErrorRes = null,
                passwordErrorRes = null,
                errorMessageRes = null,
            )
        }

        viewModelScope.launch {
            when (val result = authRepository.login(sanitizedEmail, password)) {
                is AuthResult.Success -> {
                    sessionManager.onLoginSuccess(result.data)
                    mutableUiState.update { state ->
                        state.copy(isLoading = false)
                    }
                    mutableEvents.emit(LoginUiEvent.LoginSucceeded)
                }

                is AuthResult.Error -> {
                    val uiError = AuthUiErrorMapper.map(result.error)
                    mutableUiState.update { state ->
                        state.copy(
                            isLoading = false,
                            emailErrorRes = uiError.fieldErrors[EMAIL],
                            passwordErrorRes = uiError.fieldErrors[PASSWORD],
                            errorMessageRes = uiError.messageRes,
                        )
                    }
                }
            }
        }
    }
}
