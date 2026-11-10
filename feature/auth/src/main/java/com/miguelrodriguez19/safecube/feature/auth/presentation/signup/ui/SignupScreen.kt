package com.miguelrodriguez19.safecube.feature.auth.presentation.signup.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.action.SignupUiAction
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.event.SignupUiEvent
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.state.SignupUiState
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.viewmodel.SignupViewModel
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.AuthUiOperationState

@Composable
fun SignupScreen(
    onLogin: () -> Unit,
    onSignupSuccess: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                SignupUiEvent.SignupSucceeded -> onSignupSuccess()
            }
        }
    }

    SignupContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onLogin = onLogin,
        focusEffectsEnabled = true,
    )
}

@Composable
internal fun SignupContent(
    uiState: SignupUiState,
    onAction: (SignupUiAction) -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
    focusEffectsEnabled: Boolean = true,
) {
    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    val emailBringIntoViewRequester = remember { BringIntoViewRequester() }
    val passwordBringIntoViewRequester = remember { BringIntoViewRequester() }
    val confirmBringIntoViewRequester = remember { BringIntoViewRequester() }
    val lastFocusedField = rememberSaveable { mutableStateOf<String?>(null) }
    val hadFocusBeforeRecreation = rememberSaveable { mutableStateOf(false) }
    val restoredFocusField = remember {
        lastFocusedField.value.takeIf { hadFocusBeforeRecreation.value }
    }
    var initialFocusHandled by remember { mutableStateOf(false) }
    val windowFocused = LocalWindowInfo.current.isWindowFocused
    var outcomeFocusHandled by remember(
        uiState.operationState,
        uiState.emailErrorRes,
        uiState.passwordErrorRes,
        uiState.confirmPasswordErrorRes,
    ) { mutableStateOf(false) }
    val canSubmit = uiState.operationState == AuthUiOperationState.Idle
    val isRetryable = uiState.isRetryable
    val isLoading = uiState.isLoading
    val errorText = uiState.errorMessageRes?.let { stringResource(it) }

    LaunchedEffect(
        focusEffectsEnabled,
        uiState.operationState,
        uiState.emailErrorRes,
        uiState.passwordErrorRes,
        uiState.confirmPasswordErrorRes,
        isLoading,
        windowFocused,
    ) {
        if (!focusEffectsEnabled || !windowFocused || isLoading || outcomeFocusHandled) {
            return@LaunchedEffect
        }
        val errorTarget = when {
            uiState.operationState == AuthUiOperationState.InvalidCredentials ->
                AuthTestTags.SIGNUP_PASSWORD
            uiState.operationState == AuthUiOperationState.ValidationError &&
                uiState.emailErrorRes != null -> AuthTestTags.SIGNUP_EMAIL
            uiState.operationState == AuthUiOperationState.ValidationError &&
                uiState.passwordErrorRes != null -> AuthTestTags.SIGNUP_PASSWORD
            uiState.operationState == AuthUiOperationState.ValidationError &&
                uiState.confirmPasswordErrorRes != null -> AuthTestTags.SIGNUP_CONFIRM_PASSWORD
            else -> null
        }
        val target = if (!initialFocusHandled && restoredFocusField != null) {
            restoredFocusField
        } else {
            errorTarget
        }
        if (target == null) {
            initialFocusHandled = true
            outcomeFocusHandled = true
            return@LaunchedEffect
        }
        withFrameNanos { }
        initialFocusHandled = true
        outcomeFocusHandled = true
        when (target) {
            AuthTestTags.SIGNUP_EMAIL -> {
                emailFocusRequester.requestFocus()
                emailBringIntoViewRequester.bringIntoView()
            }
            AuthTestTags.SIGNUP_PASSWORD -> {
                passwordFocusRequester.requestFocus()
                passwordBringIntoViewRequester.bringIntoView()
            }
            else -> {
                confirmPasswordFocusRequester.requestFocus()
                confirmBringIntoViewRequester.bringIntoView()
            }
        }
    }

    AuthScreenLayout(
        title = stringResource(R.string.auth_signup_title),
        subtitle = stringResource(R.string.auth_signup_subtitle),
        modifier = modifier.testTag(AuthTestTags.SIGNUP_SCREEN),
    ) {
        SignupFields(
            uiState = uiState,
            onAction = onAction,
            isLoading = isLoading,
            emailFocusRequester = emailFocusRequester,
            passwordFocusRequester = passwordFocusRequester,
            confirmPasswordFocusRequester = confirmPasswordFocusRequester,
            emailBringIntoViewRequester = emailBringIntoViewRequester,
            passwordBringIntoViewRequester = passwordBringIntoViewRequester,
            confirmBringIntoViewRequester = confirmBringIntoViewRequester,
            onFocusedField = { field ->
                lastFocusedField.value = field
                hadFocusBeforeRecreation.value = true
            },
            onSubmit = {
                if (canSubmit) onAction(SignupUiAction.Submit)
            },
        )

        AuthFeedback(
            operationState = uiState.operationState,
            message = errorText,
            onRetry = { if (isRetryable) onAction(SignupUiAction.Retry) },
            modifier = Modifier.padding(top = SafeCubeSpacing.Medium),
        )

        if (!isRetryable) {
            SafeCubeButton(
                text = stringResource(
                    if (isLoading) UiR.string.creating_account else R.string.auth_signup_action,
                ),
                onClick = { if (!isLoading && canSubmit) onAction(SignupUiAction.Submit) },
                enabled = canSubmit && !isLoading,
                loading = isLoading,
                modifier = Modifier
                    .padding(top = SafeCubeSpacing.Large)
                    .fillMaxWidth()
                    .testTag(AuthTestTags.SIGNUP_SUBMIT),
            )
        }
        SafeCubeButton(
            text = stringResource(R.string.auth_switch_to_login),
            onClick = { if (!isLoading) onLogin() },
            variant = SafeCubeButtonVariant.Text,
            enabled = !isLoading,
            modifier = Modifier
                .padding(top = SafeCubeSpacing.Small)
                .fillMaxWidth()
                .testTag(AuthTestTags.SIGNUP_LOGIN_ACTION),
        )
    }
}

@Composable
private fun ColumnScope.SignupFields(
    uiState: SignupUiState,
    onAction: (SignupUiAction) -> Unit,
    isLoading: Boolean,
    emailFocusRequester: FocusRequester,
    passwordFocusRequester: FocusRequester,
    confirmPasswordFocusRequester: FocusRequester,
    emailBringIntoViewRequester: BringIntoViewRequester,
    passwordBringIntoViewRequester: BringIntoViewRequester,
    confirmBringIntoViewRequester: BringIntoViewRequester,
    onFocusedField: (String) -> Unit,
    onSubmit: () -> Unit,
) {
    SafeCubeTextField(
        value = uiState.email,
        onValueChange = { if (!isLoading) onAction(SignupUiAction.EmailChanged(it)) },
        label = stringResource(UiR.string.email_label),
        modifier = Modifier
            .padding(top = SafeCubeSpacing.Large)
            .fillMaxWidth()
            .focusRequester(emailFocusRequester)
            .bringIntoViewRequester(emailBringIntoViewRequester)
            .onFocusChanged { if (it.isFocused) onFocusedField(AuthTestTags.SIGNUP_EMAIL) }
            .semantics { contentType = ContentType.Username + ContentType.EmailAddress }
            .testTag(AuthTestTags.SIGNUP_EMAIL),
        enabled = !isLoading,
        isError = uiState.emailErrorRes != null,
        supportingText = uiState.emailErrorRes?.let { stringResource(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next,
            capitalization = KeyboardCapitalization.None,
            autoCorrectEnabled = false,
        ),
        keyboardActions = KeyboardActions(
            onNext = { if (!isLoading) passwordFocusRequester.requestFocus() },
        ),
    )
    SecretOutlinedTextField(
        value = uiState.password,
        onValueChange = { if (!isLoading) onAction(SignupUiAction.PasswordChanged(it)) },
        label = { Text(stringResource(R.string.auth_account_password_label)) },
        modifier = Modifier
            .padding(top = SafeCubeSpacing.Small)
            .fillMaxWidth()
            .focusRequester(passwordFocusRequester)
            .bringIntoViewRequester(passwordBringIntoViewRequester)
            .onFocusChanged { if (it.isFocused) onFocusedField(AuthTestTags.SIGNUP_PASSWORD) }
            .semantics { contentType = ContentType.NewPassword }
            .testTag(AuthTestTags.SIGNUP_PASSWORD),
        enabled = !isLoading,
        isError = uiState.passwordErrorRes != null,
        supportingText = uiState.passwordErrorRes?.let { { Text(stringResource(it)) } },
        allowReveal = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Next,
            autoCorrectEnabled = false,
        ),
        keyboardActions = KeyboardActions(
            onNext = { if (!isLoading) confirmPasswordFocusRequester.requestFocus() },
        ),
    )
    SecretOutlinedTextField(
        value = uiState.confirmPassword,
        onValueChange = { if (!isLoading) onAction(SignupUiAction.ConfirmPasswordChanged(it)) },
        label = { Text(stringResource(R.string.auth_confirm_account_password_label)) },
        modifier = Modifier
            .padding(top = SafeCubeSpacing.Small)
            .fillMaxWidth()
            .focusRequester(confirmPasswordFocusRequester)
            .bringIntoViewRequester(confirmBringIntoViewRequester)
            .onFocusChanged { if (it.isFocused) onFocusedField(AuthTestTags.SIGNUP_CONFIRM_PASSWORD) }
            .semantics { contentType = ContentType.NewPassword }
            .testTag(AuthTestTags.SIGNUP_CONFIRM_PASSWORD),
        enabled = !isLoading,
        isError = uiState.confirmPasswordErrorRes != null,
        supportingText = uiState.confirmPasswordErrorRes?.let { { Text(stringResource(it)) } },
        allowReveal = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done,
            autoCorrectEnabled = false,
        ),
        keyboardActions = KeyboardActions(onDone = { onSubmit() }),
    )
}

@Preview(
    name = "Signup · EN light",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Signup · ES dark",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun SignupScreenDevelopmentPreview() {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.System,
            dynamicColorEnabled = false,
        ),
    ) {
        SignupContent(
            uiState = SignupUiState(),
            onAction = {},
            onLogin = {},
            focusEffectsEnabled = false,
        )
    }
}
