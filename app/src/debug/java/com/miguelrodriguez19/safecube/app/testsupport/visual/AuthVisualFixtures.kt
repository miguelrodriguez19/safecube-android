package com.miguelrodriguez19.safecube.app.testsupport.visual

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.miguelrodriguez19.safecube.core.ui.R as CoreR
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.state.LoginUiState
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.state.SignupUiState
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.AuthUiOperationState
import com.miguelrodriguez19.safecube.feature.auth.testsupport.AuthLoginContent
import com.miguelrodriguez19.safecube.feature.auth.testsupport.AuthSignupContent
import com.miguelrodriguez19.safecube.feature.auth.presentation.welcome.ui.WelcomeScreen

private const val SYNTHETIC_EMAIL = "person@example.com"
private const val INERT_MASK = "••••••••"

@Composable
fun AuthVisualTheme(mode: AppearanceMode, content: @Composable () -> Unit) {
    SafeCubeTheme(
        appearance = AppearanceSettings(mode = mode, dynamicColorEnabled = false),
        content = content,
    )
}

@Composable
fun AuthWelcomeVisualSurface() {
    WelcomeScreen(onLogin = {}, onSignup = {})
}

@Composable
fun AuthLoginVisualSurface(state: LoginVisualState = LoginVisualState.Idle) {
    AuthLoginContent(
        uiState = state.toUiState(),
        onAction = {},
        onSignup = {},
    )
}

@Composable
fun AuthSignupVisualSurface(state: SignupVisualState = SignupVisualState.Validation) {
    AuthSignupContent(
        uiState = state.toUiState(),
        onAction = {},
        onLogin = {},
    )
}

enum class LoginVisualState { Idle, Loading, Retryable, Terminal }
enum class SignupVisualState { Idle, Loading, Validation, AccountExists, Retryable, Terminal }

private fun LoginVisualState.toUiState() = when (this) {
    LoginVisualState.Idle -> LoginUiState(email = SYNTHETIC_EMAIL)
    LoginVisualState.Loading -> LoginUiState(
        email = SYNTHETIC_EMAIL,
        operationState = AuthUiOperationState.Loading,
    )
    LoginVisualState.Retryable -> LoginUiState(
        email = SYNTHETIC_EMAIL,
        password = INERT_MASK,
        operationState = AuthUiOperationState.OfflineOrTimeout,
        errorMessageRes = CoreR.string.auth_error_network_retryable,
    )
    LoginVisualState.Terminal -> LoginUiState(
        email = SYNTHETIC_EMAIL,
        operationState = AuthUiOperationState.TerminalError,
        errorMessageRes = CoreR.string.generic_error,
    )
}

private fun SignupVisualState.toUiState() = when (this) {
    SignupVisualState.Idle -> SignupUiState()
    SignupVisualState.Loading -> SignupUiState(
        email = SYNTHETIC_EMAIL,
        operationState = AuthUiOperationState.Loading,
    )
    SignupVisualState.Validation -> SignupUiState(
        operationState = AuthUiOperationState.ValidationError,
        passwordErrorRes = CoreR.string.password_is_required,
        confirmPasswordErrorRes = CoreR.string.confirm_password_is_required,
    )
    SignupVisualState.AccountExists -> SignupUiState(
        email = SYNTHETIC_EMAIL,
        operationState = AuthUiOperationState.AccountAlreadyExists,
        errorMessageRes = CoreR.string.auth_error_account_exists,
    )
    SignupVisualState.Retryable -> SignupUiState(
        email = SYNTHETIC_EMAIL,
        operationState = AuthUiOperationState.OfflineOrTimeout,
        errorMessageRes = CoreR.string.auth_error_network_retryable,
    )
    SignupVisualState.Terminal -> SignupUiState(
        email = SYNTHETIC_EMAIL,
        operationState = AuthUiOperationState.TerminalError,
        errorMessageRes = CoreR.string.generic_error,
    )
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Preview(name = "Auth light English", group = "SafeCube auth", widthDp = 360, heightDp = 640, locale = "en", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Auth dark English", group = "SafeCube auth", widthDp = 360, heightDp = 640, locale = "en", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(name = "Auth light Spanish", group = "SafeCube auth", widthDp = 360, heightDp = 640, locale = "es", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Auth dark Spanish", group = "SafeCube auth", widthDp = 360, heightDp = 640, locale = "es", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class AuthThemeDevelopmentPreviews

@AuthThemeDevelopmentPreviews
@Composable
private fun WelcomeDevelopmentPreview() {
    AuthVisualTheme(AppearanceMode.System) { AuthWelcomeVisualSurface() }
}

@AuthThemeDevelopmentPreviews
@Composable
private fun LoginIdleDevelopmentPreview() {
    AuthVisualTheme(AppearanceMode.System) { AuthLoginVisualSurface(LoginVisualState.Idle) }
}

@AuthThemeDevelopmentPreviews
@Composable
private fun SignupValidationDevelopmentPreview() {
    AuthVisualTheme(AppearanceMode.System) { AuthSignupVisualSurface() }
}

@AuthThemeDevelopmentPreviews
@Composable
private fun SignupIdleDevelopmentPreview() {
    AuthVisualTheme(AppearanceMode.System) { AuthSignupVisualSurface(SignupVisualState.Idle) }
}

@AuthThemeDevelopmentPreviews
@Composable
private fun SignupLoadingDevelopmentPreview() {
    AuthVisualTheme(AppearanceMode.System) { AuthSignupVisualSurface(SignupVisualState.Loading) }
}

@AuthThemeDevelopmentPreviews
@Composable
private fun SignupAccountExistsDevelopmentPreview() {
    AuthVisualTheme(AppearanceMode.System) { AuthSignupVisualSurface(SignupVisualState.AccountExists) }
}

@AuthThemeDevelopmentPreviews
@Composable
private fun SignupRetryableDevelopmentPreview() {
    AuthVisualTheme(AppearanceMode.System) { AuthSignupVisualSurface(SignupVisualState.Retryable) }
}

@AuthThemeDevelopmentPreviews
@Composable
private fun SignupTerminalDevelopmentPreview() {
    AuthVisualTheme(AppearanceMode.System) { AuthSignupVisualSurface(SignupVisualState.Terminal) }
}

@Preview(name = "Login loading", group = "SafeCube auth states", widthDp = 360, heightDp = 640, locale = "en", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun LoginLoadingDevelopmentPreview() {
    AuthVisualTheme(AppearanceMode.System) { AuthLoginVisualSurface(LoginVisualState.Loading) }
}

@Preview(name = "Login retryable", group = "SafeCube auth states", widthDp = 360, heightDp = 640, locale = "es", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
private fun LoginRetryableDevelopmentPreview() {
    AuthVisualTheme(AppearanceMode.System) { AuthLoginVisualSurface(LoginVisualState.Retryable) }
}

@Preview(name = "Login terminal", group = "SafeCube auth states", widthDp = 360, heightDp = 640, locale = "en", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun LoginTerminalDevelopmentPreview() {
    AuthVisualTheme(AppearanceMode.System) { AuthLoginVisualSurface(LoginVisualState.Terminal) }
}
