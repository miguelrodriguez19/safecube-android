package com.miguelrodriguez19.safecube.feature.auth.presentation.login.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.ContentType
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentType
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButton
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButtonVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTextField
import com.miguelrodriguez19.safecube.core.ui.component.SecretOutlinedTextField
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.feature.auth.R
import com.miguelrodriguez19.safecube.feature.auth.presentation.AuthTestTags
import com.miguelrodriguez19.safecube.feature.auth.presentation.component.AuthFeedback
import com.miguelrodriguez19.safecube.feature.auth.presentation.component.AuthScreenLayout
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.action.LoginUiAction
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.event.LoginUiEvent
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.state.LoginUiState
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.viewmodel.LoginViewModel
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.AuthUiOperationState
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.isRetryable

private const val EMAIL_FIELD_ID = "email"
private const val PASSWORD_FIELD_ID = "password"

@Composable
fun LoginScreen(
    onSignup: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                LoginUiEvent.LoginSucceeded -> onLoginSuccess()
            }
        }
    }

    LoginContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onSignup = onSignup,
    )
}

@Composable
internal fun LoginContent(
    uiState: LoginUiState,
    onAction: (LoginUiAction) -> Unit,
    onSignup: () -> Unit,
    modifier: Modifier = Modifier,
    focusEffectsEnabled: Boolean = true,
) {
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val emailBringIntoViewRequester = remember { BringIntoViewRequester() }
    val passwordBringIntoViewRequester = remember { BringIntoViewRequester() }
    var lastFocusedField by rememberSaveable { mutableStateOf<String?>(null) }
    val restoredFocusedField = remember { lastFocusedField }
    var hasHandledInitialFocus by remember { mutableStateOf(false) }
    val windowFocused = LocalWindowInfo.current.isWindowFocused
    var outcomeFocusHandled by remember(
        uiState.operationState,
        uiState.emailErrorRes,
        uiState.passwordErrorRes,
    ) { mutableStateOf(false) }

    LaunchedEffect(
        focusEffectsEnabled,
        uiState.operationState,
        uiState.emailErrorRes,
        uiState.passwordErrorRes,
        restoredFocusedField,
        windowFocused,
    ) {
        if (!focusEffectsEnabled || !windowFocused || uiState.isLoading || outcomeFocusHandled) {
            return@LaunchedEffect
        }

        val outcomeFieldId = when (uiState.operationState) {
            AuthUiOperationState.ValidationError -> when {
                uiState.emailErrorRes != null -> EMAIL_FIELD_ID
                uiState.passwordErrorRes != null -> PASSWORD_FIELD_ID
                else -> null
            }

            AuthUiOperationState.InvalidCredentials -> PASSWORD_FIELD_ID
            else -> null
        }
        val fieldId = if (!hasHandledInitialFocus && restoredFocusedField != null) {
            restoredFocusedField
        } else {
            outcomeFieldId
        }
        if (fieldId == null) {
            hasHandledInitialFocus = true
            outcomeFocusHandled = true
            return@LaunchedEffect
        }
        withFrameNanos { }
        hasHandledInitialFocus = true
        outcomeFocusHandled = true

        when (fieldId) {
            EMAIL_FIELD_ID -> {
                emailFocusRequester.requestFocus()
                emailBringIntoViewRequester.bringIntoView()
            }

            PASSWORD_FIELD_ID -> {
                passwordFocusRequester.requestFocus()
                passwordBringIntoViewRequester.bringIntoView()
            }
        }
        lastFocusedField = fieldId
    }

    val loading = uiState.operationState == AuthUiOperationState.Loading
    val retryable = uiState.operationState.isRetryable()
    val canSubmit = uiState.operationState == AuthUiOperationState.Idle
    val onSubmit = {
        if (!loading && !retryable && canSubmit) {
            onAction(LoginUiAction.Submit)
        }
    }

    AuthScreenLayout(
        title = stringResource(R.string.auth_login_title),
        subtitle = stringResource(R.string.auth_login_subtitle),
        modifier = modifier.testTag(AuthTestTags.LOGIN_SCREEN),
    ) {
        LoginFields(
            uiState = uiState,
            loading = loading,
            onAction = onAction,
            onSubmit = onSubmit,
            onSignup = onSignup,
            emailFocusRequester = emailFocusRequester,
            passwordFocusRequester = passwordFocusRequester,
            emailBringIntoViewRequester = emailBringIntoViewRequester,
            passwordBringIntoViewRequester = passwordBringIntoViewRequester,
            onFocusedField = { lastFocusedField = it },
            canSubmit = canSubmit,
            retryable = retryable,
        )
    }
}

@Composable
private fun ColumnScope.LoginFields(
    uiState: LoginUiState,
    loading: Boolean,
    onAction: (LoginUiAction) -> Unit,
    onSubmit: () -> Unit,
    onSignup: () -> Unit,
    emailFocusRequester: FocusRequester,
    passwordFocusRequester: FocusRequester,
    emailBringIntoViewRequester: BringIntoViewRequester,
    passwordBringIntoViewRequester: BringIntoViewRequester,
    onFocusedField: (String) -> Unit,
    canSubmit: Boolean,
    retryable: Boolean,
) {
    val emailError = uiState.emailErrorRes?.let { stringResource(it) }
    val passwordError = uiState.passwordErrorRes?.let { stringResource(it) }

    SafeCubeTextField(
        value = uiState.email,
        onValueChange = {
            if (!loading) onAction(LoginUiAction.EmailChanged(it))
        },
        label = stringResource(UiR.string.email_label),
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(emailBringIntoViewRequester)
            .focusRequester(emailFocusRequester)
            .onFocusChanged { if (it.isFocused) onFocusedField(EMAIL_FIELD_ID) }
            .testTag(AuthTestTags.LOGIN_EMAIL)
            .semantics { contentType = ContentType.Username + ContentType.EmailAddress },
        enabled = !loading,
        isError = uiState.emailErrorRes != null,
        supportingText = emailError,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
        ),
        keyboardActions = KeyboardActions(onNext = {
            if (!loading) passwordFocusRequester.requestFocus()
        }),
    )
    SecretOutlinedTextField(
        value = uiState.password,
        onValueChange = {
            if (!loading) onAction(LoginUiAction.PasswordChanged(it))
        },
        label = { androidx.compose.material3.Text(stringResource(R.string.auth_account_password_label)) },
        modifier = Modifier
            .fillMaxWidth()
            .bringIntoViewRequester(passwordBringIntoViewRequester)
            .focusRequester(passwordFocusRequester)
            .onFocusChanged { if (it.isFocused) onFocusedField(PASSWORD_FIELD_ID) }
            .testTag(AuthTestTags.LOGIN_PASSWORD)
            .semantics { contentType = ContentType.Password },
        enabled = !loading,
        isError = uiState.passwordErrorRes != null,
        supportingText = passwordError?.let { error ->
            { androidx.compose.material3.Text(error) }
        },
        allowReveal = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            autoCorrectEnabled = false,
            imeAction = ImeAction.Done,
        ),
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
    )

    val message = uiState.errorMessageRes?.let { stringResource(it) }
    AuthFeedback(
        operationState = uiState.operationState,
        message = message,
        onRetry = { if (!loading && retryable) onAction(LoginUiAction.Retry) },
        modifier = Modifier.padding(top = SafeCubeSpacing.Medium),
    )

    if (!retryable) {
        SafeCubeButton(
            text = stringResource(
                if (loading) UiR.string.logging_in else R.string.auth_login_action,
            ),
            onClick = onSubmit,
            enabled = canSubmit,
            loading = loading,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = SafeCubeSpacing.Large)
                .testTag(AuthTestTags.LOGIN_SUBMIT),
        )
    }
    SafeCubeButton(
        text = stringResource(R.string.auth_switch_to_signup),
        onClick = { if (!loading) onSignup() },
        variant = SafeCubeButtonVariant.Text,
        enabled = !loading,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = SafeCubeSpacing.Small)
            .testTag(AuthTestTags.LOGIN_SIGNUP_ACTION),
    )
}

@Preview(
    name = "Login · EN light",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Login · ES dark",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun LoginScreenDevelopmentPreview() {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.System,
            dynamicColorEnabled = false,
        ),
    ) {
        LoginContent(
            uiState = LoginUiState(),
            onAction = {},
            onSignup = {},
            focusEffectsEnabled = false,
        )
    }
}
