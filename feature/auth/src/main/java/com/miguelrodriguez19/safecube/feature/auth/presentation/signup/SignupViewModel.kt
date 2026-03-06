package com.miguelrodriguez19.safecube.feature.auth.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miguelrodriguez19.safecube.core.auth.domain.model.AuthResult
import com.miguelrodriguez19.safecube.core.auth.domain.repository.AuthRepository
import com.miguelrodriguez19.safecube.core.auth.domain.session.SessionManager
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.feature.auth.presentation.AuthUiErrorMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
) : ViewModel() {

    private val mutableUiState = MutableStateFlow(SignupUiState())
    val uiState: StateFlow<SignupUiState> = mutableUiState.asStateFlow()

    fun onEmailChanged(value: String) {
        mutableUiState.update { state ->
            state.copy(
                email = value,
                emailErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    fun onPasswordChanged(value: String) {
        mutableUiState.update { state ->
            state.copy(
                password = value,
                passwordErrorRes = null,
                confirmPasswordErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    fun onConfirmPasswordChanged(value: String) {
        mutableUiState.update { state ->
            state.copy(
                confirmPassword = value,
                confirmPasswordErrorRes = null,
                errorMessageRes = null,
            )
        }
    }

    fun signup() {
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
            mutableUiState.update { state ->
                state.copy(
                    emailErrorRes = emailErrorRes,
                    passwordErrorRes = passwordErrorRes,
                    confirmPasswordErrorRes = confirmPasswordErrorRes,
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
                confirmPasswordErrorRes = null,
                errorMessageRes = null,
            )
        }

        viewModelScope.launch {
            when (val registerResult = authRepository.register(sanitizedEmail, password)) {
                is AuthResult.Success -> loginAfterSignup(
                    email = sanitizedEmail,
                    password = password,
                )

                is AuthResult.Error -> {
                    val uiError = AuthUiErrorMapper.map(registerResult.error)
                    mutableUiState.update { state ->
                        state.copy(
                            isLoading = false,
                            emailErrorRes = uiError.fieldErrors["email"],
                            passwordErrorRes = uiError.fieldErrors["password"],
                            confirmPasswordErrorRes = uiError.fieldErrors["confirmPassword"]
                                ?: uiError.fieldErrors["confirm_password"],
                            errorMessageRes = uiError.messageRes,
                        )
                    }
                }
            }
        }
    }

    fun consumeSignupSuccess() {
        if (!mutableUiState.value.signupSucceeded) return
        mutableUiState.update { state -> state.copy(signupSucceeded = false) }
    }

    private suspend fun loginAfterSignup(
        email: String,
        password: String,
    ) {
        when (val loginResult = authRepository.login(email, password)) {
            is AuthResult.Success -> {
                sessionManager.onLoginSuccess(loginResult.data)
                mutableUiState.update { state ->
                    state.copy(
                        isLoading = false,
                        signupSucceeded = true,
                    )
                }
            }

            is AuthResult.Error -> {
                val uiError = AuthUiErrorMapper.map(loginResult.error)
                mutableUiState.update { state ->
                    state.copy(
                        isLoading = false,
                        emailErrorRes = uiError.fieldErrors["email"],
                        passwordErrorRes = uiError.fieldErrors["password"],
                        errorMessageRes = uiError.messageRes,
                    )
                }
            }
        }
    }
}
