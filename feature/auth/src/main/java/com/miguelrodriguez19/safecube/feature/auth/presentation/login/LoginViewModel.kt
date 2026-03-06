package com.miguelrodriguez19.safecube.feature.auth.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.feature.auth.presentation.AuthUiErrorMapper
import com.miguelrodriguez19.safecube.feature.auth.presentation.AuthUiErrorMapper.EMAIL
import com.miguelrodriguez19.safecube.feature.auth.presentation.AuthUiErrorMapper.PASSWORD
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun onEmailChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                email = value,
                emailErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    fun onPasswordChanged(value: String) {
        _uiState.update { state ->
            state.copy(
                password = value,
                passwordErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    fun login() {
        val currentState = _uiState.value
        if (currentState.isLoading) return

        val sanitizedEmail = currentState.email.trim()
        val password = currentState.password

        val emailErrorRes = if (sanitizedEmail.isBlank()) UiR.string.email_is_required else null
        val passwordErrorRes = if (password.isBlank()) UiR.string.password_is_required else null
        if (emailErrorRes != null || passwordErrorRes != null) {
            _uiState.update { state ->
                state.copy(
                    emailErrorRes = emailErrorRes,
                    passwordErrorRes = passwordErrorRes,
                    errorMessageRes = null,
                )
            }
            return
        }

        _uiState.update { state ->
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
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            loginSucceeded = true,
                        )
                    }
                }

                is AuthResult.Error -> {
                    val uiError = AuthUiErrorMapper.map(result.error)
                    _uiState.update { state ->
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

    fun consumeLoginSuccess() {
        if (!_uiState.value.loginSucceeded) return
        _uiState.update { state -> state.copy(loginSucceeded = false) }
    }
}
